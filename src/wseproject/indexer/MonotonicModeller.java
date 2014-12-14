/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import wseproject.parser.ListTitleParser;
import wseproject.parser.objects.ListTitleFields;

/**
 *
 * @author Ali Local
 */
public class MonotonicModeller
{
    
    private static HashMap<String, HashMap<String,Vector<String>>> model = new HashMap<String, HashMap<String,Vector<String>>>();
    private static boolean inited = false;
    
    //private MonotonicModeller()
    public static void init()
    {
        try
        {
            String currArticle = null;
            String jjs = null;
            String type = null;
            
            Scanner scanner = new Scanner(new File("data/monotonicCols-data"));
            while(scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                
                if(line.contains("Page:"))
                {
                    currArticle = line.replace("Page:", "");
                    //System.out.println("curr article = " + currArticle);
                    
                    ListTitleParser ltp = new ListTitleParser(null);
                    ltp.setTaggedTitle(currArticle);
                    ListTitleFields f = ltp.parseTitle();
                    
                    //System.out.println("type = " + f.getType());
                    //System.out.println("constraint = " + f.getConstraint());
                    //System.out.println("jjs = " + f.getJjs());
                    //System.out.println("order = " + f.getOrder());
                    type = f.getType();

                }
                else if(line.contains("Type:"))
                {
                }     
                else if(line.contains("JJS:"))
                {
                    jjs = line.replace("JJS:", "");
                }     
                else if(line.contains("Order:"))
                {
                }     
                else if(line.equals("-----------"))
                {
                    
                }     
                else if(line.equals("Count:"))
                {
                    //ignore
                }     
                else 
                {
                    //this is a count
                    //System.out.println(line);
                    String tokens[] = line.split("\t");
                    if(tokens.length != 2)
                        continue;
                    
                    if(jjs!= null && !jjs.trim().equals(""))
                    {
                        System.out.println("type = " + type + ", jjs = " + jjs + ", prop = " + tokens[0]);
                        if(!model.containsKey(type))
                            model.put(type, new HashMap<String,Vector<String>>());
                        
                        HashMap<String,Vector<String>> internalMap = model.get(type);
                        
                        if(!internalMap.containsKey(jjs))
                            internalMap.put(jjs, new Vector<String>());
                        
                        Vector<String> vec = internalMap.get(jjs);
                        
                        vec.add(tokens[0].toLowerCase());
                    }
                    
                }     
                
                
            }
            
            inited = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public static Vector<String> getProps(String type, String ssj)
    {
        if(!inited)
            init();
        
        if(!model.containsKey(type))
            return null;
        
        HashMap<String,Vector<String>> ssjMap = model.get(type);
        if(!ssjMap.containsKey(ssj))
            return null;
        
        return ssjMap.get(ssj);
                
        
    }

    
    
}
