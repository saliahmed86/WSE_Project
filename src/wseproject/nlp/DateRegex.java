/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ali Local
 */
public class DateRegex
{
    public static void main(String[] args)
    {
        String date = "13st   JanUary 1709";
        date = "13st JanUary 1";
        
        String date2 = "March 13th 2002 sdsds";
        //String date = "1709";
        
        //months
        
        String dr1 = "(\\d{1,2})(?:th|st|nd)*\\s*((?i)January|February|March|April|May|June|July|August|September|October|November|December)\\s*(\\d{1,4})";
        String dr3 = "(\\d{1,2})(?:th|st|nd)*\\s*((?i)Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)\\s*(\\d{1,4})";
        
        String dr2 = "((?i)January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{1,2})(?:th|st|nd){0,1}(?:\\s|\\,)+(\\d{1,4})";
        String dr4 = "((?i)Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)\\s+(\\d{1,2})(?:th|st|nd){0,1}(?:\\s|\\,)+(\\d{1,4})";
        //
        
        
        //String dr1 = "(\\d{4})";
        Pattern p = Pattern.compile(dr2);
        Matcher m = p.matcher(date2);
        
        if(m.find())
        {
            System.out.println("date = " + m.group());
            System.out.println("date = " + m.group(1));
            System.out.println("date = " + m.group(2));
            System.out.println("date = " + m.group(3));
        }
        else
            System.out.println("not found");
    }

}

