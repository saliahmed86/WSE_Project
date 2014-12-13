/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import wseproject.nlp.Regexer;

/**
 *
 * @author Ali Local
 */
public class LinkParser
{
    public static String parseLinkBox(String text)
    {
        Matcher m = Regexer.matchLinkBox(text);//linkBoxPattern.matcher(str);
        
        if(m.find())
        {
            //System.out.println(m.group(1));
            String ss[] = m.group(1).split("\\|");
            if(!ss[0].contains("Image:"))
                return ss[0];
            //what to return for 3??
        }
        return text;
    }
    
    public static List<String> parseLinkBoxes(String text)
    {
        Matcher m = Regexer.matchLinkBox(text);//linkBoxPattern.matcher(str);
        
              
        List<String> ll = new Vector<String>();
        while(m.find())
        {
            //System.out.println(m.group(1));
            String ss[] = m.group(1).split("\\|");
            if(!ss[0].contains("Image:"))
                ll.add(ss[0]);
            //what to return for 3??
        }
        return ll;
    }
    
    public static String parseTemplate(String text)
    {
        Matcher m = Regexer.matchTemplate(text);//linkBoxPattern.matcher(str);
        
              
        List<String> ll = new Vector<String>();
        if(m.find())
        {
            //System.out.println(m.group(1));
            String ss[] = m.group(1).split("\\|");
            int last = ss.length-1;
            if(!ss[last].contains("Image:"))
                return ss[last];
            //what to return for 3??
        }
        return text;
    }
}
