package wseproject.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class WikiInfoboxReader {
    
    public static String getByArticleName(String article) throws MalformedURLException, IOException{
        //http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=dump&titles=Brad%20Pitt&rvsection=0
        article = article.replaceAll(" ", "%20");
        String url = "http://en.wikipedia.org/w/api.php?action=query&prop="
                + "revisions&rvprop=content&format=dump&titles="+article
                + "&rvsection=0";
        System.out.println(url);
        
        InputStream is = new URL(url).openStream();
        Scanner sc = new Scanner(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        boolean print = false;
        int curlyCount = 0;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            //System.out.println(line);
            if(!print && line.matches(".*\\{\\{[^ ]*box.*")){
                line = line.substring(line.indexOf("{"));
                print = true;
                curlyCount++;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
            }else if(print && line.matches("\\{\\{.*")){
                curlyCount++;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
            }else if(print && line.matches("}}")){
                curlyCount--;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
                if(curlyCount==0)
                    break;
            }
            if(print && line.matches("( )*\\|.*|( ){1,}\\{\\{.*"))
                sb.append(line).append("\n");
        }
        return sb.toString();
    }
    public static String extractInfobox(String content){
        Scanner sc = new Scanner(content);
        StringBuilder sb = new StringBuilder();
        boolean print = false;
        int curlyCount = 0;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            //System.out.println(line);
            if(!print && line.matches(".*\\{\\{[^ ]*box.*")){
                line = line.substring(line.indexOf("{"));
                print = true;
                curlyCount++;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
            }else if(print && line.matches("\\{\\{.*")){
                curlyCount++;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
            }else if(print && line.matches("}}")){
                curlyCount--;
                sb.append(line).append("\n");
                //System.out.println(line);
                //System.out.println(curlyCount);
                if(curlyCount==0)
                    break;
            }
            if(print && line.matches("( )*\\|.*|( ){1,}\\{\\{.*"))
                sb.append(line).append("\n");
        }
        return sb.toString();
    }
    
    public static void main(String[] args){
        try {
            /*
            System.out.println(WikiInfoboxReader.getByArticleName("Brad Pitt"));
            System.out.println(WikiInfoboxReader.getByArticleName("Amazon River"));
            System.out.println(WikiInfoboxReader.getByArticleName("Shanghai"));
            */
            System.out.println(WikiInfoboxReader.getByArticleName("United States"));
            /*
            System.out.println(WikiInfoboxReader.getByArticleName("Barack Obama"));
            System.out.println(WikiInfoboxReader.getByArticleName("Mount Shasta"));
            System.out.println(WikiInfoboxReader.getByArticleName("English"));
            System.out.println(WikiInfoboxReader.getByArticleName("Law Courts of Brussels"));
            System.out.println(WikiInfoboxReader.getByArticleName("Alabama"));
            String s = WikiInfoboxReader.getByArticleName("Tianjin Grand Bridge");
            System.out.println("============");
            System.out.println(s);
            */
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
