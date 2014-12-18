/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import wseproject.parser.objects.NoMainColumnException;
import wseproject.parser.objects.MonotonicLog;
import wseproject.parser.objects.RowProps;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wseproject.nlp.StanfordManager;
import wseproject.parser.objects.WikiTable;

/**
 *
 * @author Ali Local
 */
public class TableAnalyzer
{
    private WikiTable table;

    public TableAnalyzer(WikiTable table)
    {
        this.table = table;
    }
    
    
    public Vector<MonotonicLog> getMonoticColumns()
    {
        return getMonoticColumns_FromCols(table.getTranspose());
    }
    
    private Vector<MonotonicLog>  getMonoticColumns_FromCols(Vector<Vector<String>> cols)
    {
        try
        {
            //String r = "([^\\(]*)\\((.*?)\\)";
            String r = "([0-9\\.]*)";
            Pattern p = Pattern.compile(r);
            Matcher m = p.matcher("100 mins (200 ft)");

    //        if(m.find())
    //        {
    //            System.out.println("=> " + m.group(0));
    //            System.out.println("=> " + m.group(1));
    //            System.out.println("=> " + m.group(2));
    //        }
    //        else
    //            System.out.println("not found");
    //        if(true) return;

            Vector<MonotonicLog> monotonicColumns = new Vector<MonotonicLog>();

            for(Vector<String> column: cols)
            {
                String header = column.get(0);

                if(header.toLowerCase().contains("rank"))
                    continue;
                //System.out.println("col = " + header); 
                Vector<String> parsedVals = new Vector<String>();

                for(int i=1;i<column.size();i++)
                {
                    String val = column.get(i);
                    Matcher m2 = p.matcher(val);
                    String s;
                    if(m2.find())
                    {
                        s = m2.group(1);
                        s = s.replaceAll("[^0-9.\\s-]*", "");
                        //System.out.println("=> " + s);
                        for(int j=1;j<=m2.groupCount();j++)
                        {
                            //System.out.println("=> " + m2.group(j));

                        }
                    }
                    else
                    {
                        s = val.replaceAll("[^0-9.\\s-]*", "");
                        //System.out.println("=> " + s);
                    }
                    //System.out.println("=> " + s);
                    parsedVals.add(s);
                }

                boolean gotExeception = false;
                Vector<Double> numbers = new Vector<Double>();
                for(String val: parsedVals)
                {
                    try
                    {
                        double n = Double.parseDouble(val);
                        numbers.add(n);
                    }
                    catch(NumberFormatException nfe) {
                        gotExeception = true;
                    }
                    catch(Exception e) {
                        gotExeception = true;
                    }
                }
                if(gotExeception)
                    continue;

                int totalDir = 0;
                int distinct = 0;
                for(int j=0;j<numbers.size()-1;j++)
                {
                    int dir = 0; //dir = 1, increasing.  =-1, decreasing
                    //System.out.println("a = " + numbers.get(j) + "  ,  b = " + numbers.get(j+1));
                    distinct++;
                    if(numbers.get(j) < numbers.get(j+1))
                        dir = 1;
                    else if(numbers.get(j) > numbers.get(j+1))
                        dir = -1;
                    else
                    {
                        dir = 0;
                        distinct--;
                    }
                    totalDir += dir;
                }
                int newDir = totalDir;
                if(newDir < 0)
                    newDir = -1*newDir;

                double percent = ((newDir*100)/distinct);
                if(percent >= 85)
                {
                    monotonicColumns.add(new MonotonicLog(header, (totalDir < 0 ? -1 : 1)));
                    //System.out.println("newDIr = " + newDir);
                    //System.out.println("percent = " + percent);
                }


            }
            return monotonicColumns;
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            return null;
        }
        
    }
    
    public List<RowProps> getPropertiesFromRows() throws Exception
    {
        Vector<String> headers = table.getRow(0);
        
        //first determine which column is hte main one, with entities
        //examine headers only?
        //use stanford to parse title//get nouns
        //List of <adj>? <nouns> in|of <noun>
        Vector<String> titleTags = StanfordManager.getPOSTags(table.getPageTitle());
        int flag = 0;   //if flag is 1 or 0, add noun else not
        String noun = "";
        for(int i=0;i<titleTags.size();i++)
        {
            String wordTag = titleTags.get(i);
            String arr[] = wordTag.split("/");
            String tag = arr[arr.length-1];

            //System.out.println("word = " + arr[0] + "  ,  tag = " + tag + "<");
            
            if(tag.equalsIgnoreCase("NN") || tag.equalsIgnoreCase("NNS") || tag.equalsIgnoreCase("NNP") || tag.equalsIgnoreCase("NNPS") || tag.equalsIgnoreCase("JJ") )
            {
                //System.out.println("tag is noun");
                if(arr[0].equalsIgnoreCase("list"))
                {
                    //System.out.println("word id list");
                    continue;
                }
                if(flag == 0 || flag == 1)
                {
                    //System.out.println("should add");
                    noun += arr[0] + " ";
                    flag = 1;
                }
                
            }
            else
            {
                if(flag == 1)
                    flag = -1;      //This means that previously we had a noun, but not now, so do not read more nounds
            }

        }
        
        noun = noun.trim();
        //System.out.println("Noun = " + noun);
        int mainCol = -1; int cc=0;
        /*
        //System.out.println("Stemmer.stemmedToken(noun.toLowerCase()) = " + Stemmer.stemmedToken(noun.toLowerCase()));
        for(String header: headers)
        {
            String toks[] = header.split("\\s|/");
            for(String headerTok: toks)
            {
                //System.out.println("headerTok = " + headerTok);
                //System.out.println("Stemmer.stemmedToken(headerTok.toLowerCase()) = " + Stemmer.stemmedToken(headerTok.toLowerCase()));
                if(Stemmer.stemmedToken(headerTok.toLowerCase()).contains(Stemmer.stemmedToken(noun.toLowerCase())))
                {
                    mainCol = cc;
                    break;
                }
            }
            cc++;
        }
        //doing this so that first preference goes to noun
        if(mainCol == -1)
            for(String header: headers)
            {
                if(header.toLowerCase().contains("name"))
                {
                    mainCol = cc;
                    break;
                }
                cc++;
            }
        */
        String titleToks[] = noun.split("\\s|/");
        for(String header: headers)
        {
            if(header.toLowerCase().contains("name") || header.toLowerCase().contains("title"))
            {
                mainCol = cc;
                //System.out.println("found title or name");
                continue;
            }
            
            
            
            String toks[] = header.split("\\s|/");
            int headerLen = toks.length;
            int found = 0;
            for(String headerTok: toks)
            {
                for(String titleTok: titleToks)
                {
                    //System.out.println("headerTok = " + headerTok);
                    //System.out.println("Stemmer.stemmedToken(headerTok.toLowerCase()) = " + Stemmer.stemmedToken(headerTok.toLowerCase()));
                    try
                    {
                        if(StanfordManager.getLemma(headerTok.toLowerCase()).contains(StanfordManager.getLemma(titleTok.toLowerCase())))
                        {
                            found += 1;
                        }
                    }
                    catch(Exception e)
                    {
                        //move on
                    }
                        
                }
            }
            if(found == headerLen)
            {
                mainCol = cc;
                //break;
            }
            cc++;
        }
        //System.out.println("here 1");
        cc = 0;
        if(table.getPageTitle().toLowerCase().contains("filmography"))
        {
            for(String header: headers)
            {
                //System.out.println("header = " + header);
                if(header.toLowerCase().contains("film") || header.toLowerCase().contains("show") || header.toLowerCase().contains("title"))
                {
                    //System.out.println("found");
                    mainCol = cc;
                    break;
                }
                cc++;
            }
        }
        
        if(mainCol == -1)
            throw new NoMainColumnException(table.getPageTitle());
        
        List<RowProps> allEntityProps = new ArrayList<RowProps>();
        for(int i=1;i<table.numRows();i++)
        {
            Vector<String> row = table.getRow(i);
            String entity = LinkParser.parseLinkBox(row.get(mainCol));
            String type = noun;
            
            //System.out.println("i = " + 1 + "    entity = " + entity);
            if(table.getPageTitle().toLowerCase().contains("filmography"))
                type = "film";
            
            RowProps props = new RowProps(entity, type);
            
            for(int j=mainCol+1;j<row.size();j++)
            {
                String prop = headers.get(j);
                if(prop.toLowerCase().contains("notes") || prop.toLowerCase().contains("comments"))
                    continue;
                props.addProperty(prop, row.get(j));
            }
            allEntityProps.add(props);
        }
        
        return allEntityProps;
    }
    
    
}

