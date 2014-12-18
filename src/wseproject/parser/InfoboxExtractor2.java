package wseproject.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import edu.jhu.nlp.wikipedia.*;
import wseproject.indexer.Indexer;

public class InfoboxExtractor2 {
    BufferedReader _br;
    BufferedWriter _bw;
    WikiInfoboxReader reader = new WikiInfoboxReader();
    
    Set<String> boxType = new HashSet<String>();
    Map<String, String> _translator = new HashMap<String, String>();
    Map<String, String> _dependentRelation = new HashMap<String, String>();
    String dataPath = "data/";
    
    boolean keep = false;
    boolean notdone = false;
    String propStore;
    StringBuilder buffer = new StringBuilder();
    
    public void setTranslator(String filePath) throws IOException{
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null){
            String[] token = line.split(",");
            this._translator.put(token[0], token[1]);
        }
    }
    public void output() throws IOException{
        File file = new File(this.dataPath + "/tin/box_types");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for(String s : boxType){
            bw.write(s + "\n");
        }
        bw.close();
    }
    public void startBatch(String outputPath, String articleListPath) throws IOException{
        File outputFile = new File(outputPath);
        outputFile.delete();
        this._bw = new BufferedWriter(new FileWriter(outputFile,true));
        File articleListFile = new File(articleListPath);
        this._br = new BufferedReader(new FileReader(articleListFile));
        String article;
        int count = 0;
        while((article = _br.readLine()) != null){
            String infobox = reader.getByArticleName(article);
            if(infobox.length()==0)
                continue;
            //System.out.println(article);
            //System.out.println(infobox);
            processInfobox(article, infobox);
            count++;
        }
        this._bw.close();
        this._br.close();
        this.output();
        //System.out.println("DONE "+count);
    }    
    public String startAFile(BufferedWriter bwNew, String articleName, String content) throws IOException{
        //File outputFile = new File(outputPath);
        //outputFile.delete();
        //this._bw = new BufferedWriter(new FileWriter(outputFile,true));
	this._bw = bwNew;
        String infobox = reader.extractInfobox(content);
        if(infobox.length()==0){
            return null;
        }
        //System.out.println(articleName);
        //System.out.println(infobox);
        return processInfobox(articleName, infobox);
        //this._bw.close();
    }
	
	public String startAFile(String outputPath, String articleName, String content) throws IOException{
        //File outputFile = new File(outputPath);
        //outputFile.delete();
        //this._bw = new BufferedWriter(new FileWriter(outputFile,true));
            
        String infobox = reader.extractInfobox(content);
        if(infobox.length()==0){
            return "!!! no infobox extracted";
        }
        //System.out.println(articleName);
        //System.out.println(infobox);
        String type = processInfobox(articleName, infobox);
        this._bw.close();
        return type;
    }
    
    public String processInfobox(String source, String box) throws IOException{
        Scanner sc = new Scanner(box);
        this.keep = false;
        /* get inbox type */
        String line = sc.nextLine();
        //System.out.println(line);
        int index = line.indexOf("box")+3;
        String type = line.substring(index).trim().toLowerCase();
        boxType.add(type);
        //System.out.println("type: "+type);
        /* process rest lines */
        while(sc.hasNextLine()){
            line = sc.nextLine();
            //System.out.println(line);
            processLine(source, type, line);
        }
        return type;
    }
    public void write(String entity, String type, String property, 
            String content, String source){
        if(content.equals(""))
            return;
        StringBuilder sb = new StringBuilder();
        sb.append(entity).append(Indexer.DELIM);
        sb.append(type).append(Indexer.DELIM);
        sb.append(property).append(Indexer.DELIM);
        sb.append(content).append(Indexer.DELIM);
        sb.append(source).append("\n");
        try {
            this._bw.write(sb.toString());
            //System.out.print(": " + sb.toString());
        } catch (NullPointerException e){
            //this._bw is not initialized
            //System.out.print(": " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean validProperty(String prop){
        //useless property
        //System.out.println(prop);
        if(prop.equals("name")
                || prop.matches(".*(module|translit).*") //parameter
                || prop.matches(".*(symbol|photo|seal|shield|logo|display|caption|alt|emblem|seat|footnote|signature).*") //image or sound
                || prop.matches(".*(label|map|coordinates|pushpin|coor).*")
                || prop.matches(".*(ref).*")
                || prop.matches(".*(blank).*")
            )
            return false;
        return true;
    }
    public String processContent(String ori){
        String result = ori.replaceAll("<ref.*</ref>","").
                replaceAll("<ref.*/>","").replaceAll("<[^b>][^r>][^>]*>", "").
                replaceAll("</.*>", "").replaceAll("<!--.*-->", "");
        result = result.replaceAll("&nbsp;-", "").replaceAll("&nbsp;", " ").
                replaceAll("&ndash;","-").replaceAll("&nbsp;"," ").
                replaceAll("&#.*;"," ");
        return result;
    }
    public int isValidContent(String content){
        //System.out.println("content: "+content);
        Stack<Integer> op = new Stack<Integer>();
        int left, right;
        if(content.contains("{{") || content.contains("}}")){
            left = content.lastIndexOf("{{");
            right = content.lastIndexOf("}}");
            while(left!=-1 || right!=-1){
                //System.out.println("left: "+left);
                //System.out.println("right: "+right);
                if(right>left){
                    op.push(right);
                    right = content.lastIndexOf("}}", right-2);
                }else{
                    if(op.isEmpty())
                        return 0;
                    op.pop();
                    left = content.lastIndexOf("{{", left-2);
                }
            }
            if(op.size()!=0)
                return -1;
        }
        op.clear();
        if(content.contains("[[") || content.contains("]]")){
            left = content.lastIndexOf("[[");
            right = content.lastIndexOf("]]");
            while(left!=-1 || right!=-1){
                if(right>left){
                    op.push(right);
                    right = content.lastIndexOf("]]", right-2);
                }else{
                    if(op.isEmpty())
                        return 0;
                    op.pop();
                    left = content.lastIndexOf("[[", left-2);
                }
            }
            if(op.size()!=0)
                return -1;
        }
        //System.out.println("Valid!");
        return 1;
    }
    public void processLine(String source, String type, String line) throws IOException{
        //System.out.println("line: "+line);
        String prop = null;
        String content = null;
        if(this.notdone){
            if(!line.contains("=")){
                this.keep = true;
            }
            this.notdone = false;
        }
        //System.out.println(keep);
        if(this.keep){
            this.buffer.append(line);
            if(isValidContent(this.buffer.toString())<1) return;
            content = this.buffer.toString();
            this.buffer.setLength(0);
            this.buffer.trimToSize();
            prop = this.propStore;
            this.keep = false;
        }else{
            if(!line.contains("="))
                return;
            //System.out.println("test1");
            prop = line.substring(0, line.indexOf("=")).trim();
            content = line.substring(line.indexOf("=")+1).trim();
            //System.out.println("test2");
            prop = prop.replaceAll("\\|", "").trim().toLowerCase();
            //System.out.println("prop: "+prop);
            if(!validProperty(prop)) return;
            if(content.equals("")){
                this.propStore = prop;
                this.notdone = true;
                return;
            }
            //System.out.println("before: "+content);
            content = processContent(content);
            //System.out.println("after: "+content);
            if(content.matches("[^\\}\\{]*\\}\\}")){
                content = content.substring(0,content.indexOf("}}"));
            }
            int test = isValidContent(content);
            //System.out.println("test: "+test);
            if(test==0){
                this.buffer.append(content);
                this.keep = true;
                this.propStore = prop;
                //throw new IOException();
                return;
            }else if(test==-1){
                return;
            }
        }
        
        Scanner sc = new Scanner(content);
        sc.useDelimiter(" *<br(.){0,2}> *|( ){2,}|;");
        List<String> temp = new ArrayList<String>();
        while(sc.hasNext()){
            String piece = sc.next();
            while(isValidContent(piece)<1){
                piece = piece.concat(sc.next());
            }
            temp.add(piece);
            //System.out.println("temp: "+piece);
        }
        String[] props = new String[temp.size()];
        temp.toArray(props);
        //String[] props = content.split(" *<br(.){0,2}> *|( ){2,}|;");
        int left;
        int right;
        for(String p : props){
            //System.out.println("piece: " +p);
            if(p.contains("{{") && p.contains("}}")){
                p = analyzeCurlyBraces(p);
            }
            checkAndWrite(source, type, prop, p.trim());
        }
    }
    private void processSettlement(String prop, String content, Info info){
        //System.out.println("prop: "+prop);
        if(prop.contains("leader_title")){
            this._dependentRelation.put(prop, content);
            info.prop = null;
        }
        if(prop.contains("leader_name")){
            char num = prop.charAt(11);
            String key = "leader_title" + num;
            prop = this._dependentRelation.get(key);
            this._dependentRelation.remove(key);
            info.prop = prop;
            info.content = content;
            info.reverseType = prop;
        }
        if(prop.contains("subdivision_type")){
            this._dependentRelation.put(prop, content);
            info.prop = null;
        }
        if(prop.contains("subdivision_name")){
            if(prop.equals("subdivision_name")){
                //if(this._dependentRelation.containsKey("subdivision_type"))
                    prop = this._dependentRelation.get("subdivision_type");
                    this._dependentRelation.remove("subdivision_type");
            }else{
                String key = "subdivision_type" + prop.charAt(16);
                //if(this._dependentRelation.containsKey(key))
                    prop = this._dependentRelation.get(key);
                    this._dependentRelation.remove(key);
            }
            info.prop = prop;
            info.content = content;
            info.reverseType = prop;
        }
        
        if(prop.contains("established_title")){
            this._dependentRelation.put(prop, content);
            info.prop = null;
        }
        if(prop.contains("established_date")){
            if(prop.equals("established_date")){
                prop = this._dependentRelation.get("established_title");
                this._dependentRelation.remove("established_title");
            }else{
                String key = "established_title" + prop.charAt(16);
                prop = this._dependentRelation.get(key);
                this._dependentRelation.remove(key);
            }
            info.prop = prop;
            info.content = content;
            info.reverseType = prop;
        }
        
        if(prop.matches("demographics[1-9]_title[1-9]")){
            this._dependentRelation.put(prop, "demographics_"+content);
            info.prop = null;
        }
        if(prop.matches("demographics[1-9]_info[1-9]")){
            char num1 = prop.charAt(12);
            char num2 = prop.charAt(18);
            String key = "demographics" + num1 + "_title" + num2;
            if(this._dependentRelation.containsKey(key)){
                prop = this._dependentRelation.get(key);
                this._dependentRelation.remove(key);
                info.prop = prop;
                info.content = content;
                info.reverseType = prop;
            }
        }
        return;
    }
    private void processCountry(String prop, String content, Info info){
        if(prop.contains("leader_title")){
            this._dependentRelation.put(prop, content);
            info.prop = null;
        }
        if(prop.contains("leader_name")){
            char num = prop.charAt(11);
            String key = "leader_title" + num;
            prop = this._dependentRelation.get(key);
            this._dependentRelation.remove(key);
            info.prop = prop;
            info.content = content;
            info.reverseType = prop;
        }
        if(prop.contains("established_event")){
            this._dependentRelation.put(prop, content);
            info.prop = null;
        }
        if(prop.contains("established_date")){
            String num = prop.substring(16);
            String key = "established_event" + num;
            prop = this._dependentRelation.get(key);
            this._dependentRelation.remove(key);
            info.prop = prop;
            info.content = content;
            info.reverseType = prop;
        }
        return;
    }
    public void unitProcess(Info info){
        if(info.prop.contains("_sq_mi")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_sq_mi"));
            info.content = info.content.concat(" square mile");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_km2")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_km2"));
            info.content = info.content.concat(" square kilometer");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_acre")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_acre"));
            info.content = info.content.concat(" acre");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_km")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_km"));
            info.content = info.content.concat(" kilometer(s)");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_mi")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_mi"));
            info.content = info.content.concat(" miles");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_ft")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_ft"));
            info.content = info.content.concat(" feet");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_m")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_m"));
            info.content = info.content.concat(" meter(s)");
            info.reverseType = info.prop;
        }else if(info.prop.contains("_sqmi")){
            info.prop = info.prop.substring(0,info.prop.indexOf("_sqmi"));
            info.content = info.content.concat(" square miles");
            info.reverseType = info.prop;
        }
    }
    class Info{
        String prop;
        String content;
        String reverseType;
        String reverseProp;
    }
    public void checkAndWrite(String source, String type, String prop, String content) throws IOException{
        //System.out.println("content: "+content);
        if(this._translator.containsKey(prop))
            prop = this._translator.get(prop);
        
        //default values
        Info info = new Info();
        info.prop = prop;
        info.content = content;
        info.reverseProp = type;
        info.reverseType = prop;
        
        //special treatment
        if(type.equals("settlement"))
            processSettlement(prop,content,info);
        if(type.equals("country"))
            processCountry(prop,content,info);
        
        List<String> reverseEntity = new ArrayList<String>();
        if(content.contains("[[") && content.contains("]]")){
            reverseEntity = this.processSquareBrackets(content, prop, type, source);
        }
            
        if(info.prop!=null){
            if(info.prop.matches(".*(_sq_mi|_km2|_acre|_km|_mi|_ft|_m|_sqmi)"))
                unitProcess(info);
            this.write(source, type, info.prop, info.content, source);
            //System.out.println(reverseEntity.size());
            for(String entity : reverseEntity){
                this.write(entity, info.reverseType, info.reverseProp, source, source);
            }
        }
    }
    
    public List<String> processSquareBrackets(String target, String prop, String type, String article) throws IOException{
        //System.out.println(target);
        int left = target.indexOf("[")+2;
        int right = target.indexOf("]");
        List<String> result = new ArrayList<String>();
        String entityName;
        while(left!=-1 && right!=-1){
            //System.out.println(target);
            //System.out.println(left);
            //System.out.println(right);
            entityName = target.substring(left,right);
            if(entityName.contains("|"))
                entityName = entityName.substring(0,entityName.indexOf("|"));
            result.add(entityName);
            //this.write(entityName, prop, type, article, article);
            left = target.indexOf("[",left+2)+2;
            right = target.indexOf("]",right+2);
        }
        return result;
    }
    public String analyzeCurlyBraces(String phrase){
        
        Stack<Integer> stack = new Stack<Integer>();
        if(phrase.contains("{{") && phrase.contains("}}")){
            int left = phrase.indexOf("{{");
            int right = phrase.indexOf("}}")+2;
            int temp;
            String cu,re;
            while(left!=-1 || right!=-1){
                //System.out.println("left:"+left);
                //System.out.println("right:"+right);
                if(left!=-1 && left<right){
                    stack.push(left);
                    left = phrase.indexOf("{{", left+2);
                }else{
                    if(stack.isEmpty())
                        break;
                    temp = stack.pop();
                    cu = phrase.substring(temp,right);
                    //System.out.println("cu: "+cu);
                    //System.out.println("go: "+phrase.substring(temp+2,right-2));
                    re = processCurlyBraces(phrase.substring(temp+2,right-2));
                    //System.out.println("re: "+re);
                    phrase = phrase.replace(cu, re);
                    //System.out.println("phrase: "+ phrase);
                    return analyzeCurlyBraces(phrase);
                }
            }
        }
        return phrase;
    }
    public String processCurlyBraces(String phrase){
        Scanner sc = new Scanner(phrase);
        sc.useDelimiter("\\|");
        List<String> temp = new ArrayList<String>();
        while(sc.hasNext()){
            String str = sc.next();
            while(isValidContent(str)<1){
                str = str.concat(sc.next());
            }
            temp.add(str);
        }
        
        String[] token = new String[temp.size()];
        temp.toArray(token);
        
        int start = -1;
        int count = 0;
        if(token.length<=1){
            start=0;count=1;
        }else if(token[0].toLowerCase().contains("date")){
            start=1;count=3;
        }else if(token[0].toLowerCase().contains("sfn")){
            count=0;
        }else if(token[0].toLowerCase().contains("convert")){
            start=1;count=2;
        }else if(token[0].toLowerCase().contains("coord")){
            start=1;count=6;
        }else if(token[0].toLowerCase().contains("bar")){
            count=0;
        }else{
            start=1;count=token.length-1;
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=start;i<token.length && count>0;i++){
            if(token[i].contains("="))
                continue;
            sb.append(token[i]).append(" ");
            count--;
        }
        return sb.toString();

    }
    
}
