/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ali Local
 */
public class PropertyCombiner
{
    public static void combine(String entity, Vector<Indexer.ER> ll, HashMap<String, Set<String>> map11, Vector<String> idToEntity)
    {
        try
        {
            if(ll == null)
            {
                System.out.println("ll is null for entity " + entity);
                return;
            }
            //for(Indexer.ER er: ll)
            for(int i=0;i<ll.size();i++)
            {
                Indexer.ER er = ll.get(i);
                
                //for(Indexer.ER er2: ll)
                for(int j=i+1;j<ll.size();j++)
                {
                    Indexer.ER er2 = ll.get(j);
                    
                    if(match(idToEntity.get(er.nextEntity), idToEntity.get(er2.nextEntity)))
                    {
                        //System.out.println("entity = " + entity);
                        //System.out.println("    " + er.property + " => " + er.nextEntity);
                        //System.out.println("    " + er2.property + " => " + er2.nextEntity);
                        if(!map11.containsKey(er.property))
                            map11.put(er.property, new HashSet<String>());

                        Set<String> set = map11.get(er.property);
                        set.add(er2.property);

                        if(!map11.containsKey(er2.property))
                            map11.put(er2.property, new HashSet<String>());

                        Set<String> set2 = map11.get(er2.property);
                        set2.add(er.property);


                    }

                    //System.out.println("    " + er.property + " => " + er.nextEntity);
                    //System.out.println("    " + er.property + " => " + er.nextEntity);
                }
            }
        }
        catch(Exception e)
        {
            //ignore
            
            System.err.println("Error on entity: " + entity);
            e.printStackTrace();
        }
        //System.out.println("------------");
    }
    
    public static boolean match(String s1, String s2)
    {
        if(s1.trim().isEmpty() || s2.trim().isEmpty())
            return false;
        
        if(s1.equals(s2))
            return true;
        
        //if(s1.contains(s2) || s2.contains(s1)) 
        //    return true;
        
        //match integers
        String s1ints = s1.replaceAll("^[0-9]", "");
        String s2ints = s2.replaceAll("^[0-9]", "");
        
        
        String regex = "\\$?(?=\\(.*\\)|[^()]*$)\\(?\\d{1,3}(,?\\d{3})*(\\.\\d\\d?)?\\)?";
        String str = "$2,100,123.98";
        
        String metersRegex = "(\\d{1,3}[,?\\d{3}]*[\\.\\d\\d?]?)[\\s]*(m|ft|in)\\b";
        String str2 = "(100 m and 1,000,000,000,000 ft.";
        //String metersRegex = "(a+)[\\s]*(b+)";
        //String str2 = "aaa bb";
         
        String simpleRegex = "([0-9\\.]+)";
        Pattern p = Pattern.compile(simpleRegex);
        Matcher m1 = p.matcher(s1.replaceAll(",", ""));
        Matcher m2 = p.matcher(s2.replaceAll(",", ""));
        
        //first ints
        boolean f1 = m1.find();
        boolean f2 = m2.find();
        
        if(f1 && f2)
        {
            String ss1 = m1.group(0);
            String ss2 = m2.group(0);
            
            //System.out.println("found ss1 = " + ss1 + " from s1 = " + s1); 
            //System.out.println("found ss2 = " + ss2 + " from s2 = " + s2); 
            
            try
            {
                int i1 = Integer.parseInt(ss1);
                int i2 = Integer.parseInt(ss2);
                
                if(i1 == i2)
                    return (i1 > 100);
            }
            catch(Exception e)
            {
                
            }
        }
        
        return false;
    }
    
    public static void main(String[] args)
    {
        boolean x = match("7,213", "7213|m|ft|0|sortable=on}}");
        System.out.println("x = " + x);
    }
}
