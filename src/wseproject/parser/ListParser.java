/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import wseproject.crawler.WikiJsonReader;
import java.util.Scanner;
import java.util.Vector;
import wseproject.parser.objects.WikiList;

/**
 *
 * @author Ali Local
 */
public class ListParser
{
    private ListParser()
    {
        
    }
    
    public static Vector<WikiList> getLists(String article, String pageText)
    {
        Scanner scanner = new Scanner(pageText);
                
        //boolean readingSection = true;
        String currentSection = "";
        boolean readingList = false;
        Vector<WikiList> listOfLists = new Vector<WikiList> ();
        
        listOfLists.add(new WikiList(article, currentSection));
        
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine().trim();
            if(line.length() == 0) continue;
            
            if( line.length() >= 5 && 
                line.substring(0,2).equals("==") && 
                line.substring(line.length()-2,line.length()).equals("=="))
            {
                currentSection = line.replaceAll("=", "");
                //System.out.println("Reading section: " + currentSection);
                readingList = false;
                
            }
            else if(line.length() >= 1 && 
                    (line.substring(0,1).equals("*")
                     ||
                     line.substring(0,1).equals("#")))
            {
                if(currentSection.toLowerCase().contains("external links") ||
                   currentSection.equalsIgnoreCase("see also") ||
                   currentSection.equalsIgnoreCase("notes"))
                    continue;
                
                if(!readingList)
                {
                    listOfLists.add(new WikiList(article, currentSection));
                }
                
                listOfLists.lastElement().addElemToList(line.replaceAll("\\*|\\#", ""));
                
                readingList = true;
            }
            else
            {
                readingList = false;
            }
            
        }
        return listOfLists;
    }
    
    public void getListElems(String listText)
    {
        
    }
    
    
}
