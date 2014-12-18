/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.crawler;

import wseproject.crawler.WikiJsonReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import wseproject.parser.objects.WikiList;
import wseproject.parser.LinkParser;
import wseproject.parser.ListParser;

/**
 *
 * @author Ali Local
 */
public class ListOfListsDownloader
{

    private Set<String> visited = new HashSet<String>();
    private static final String listArticleDestFolder = "data/listArticleCollection/";
    
    public ListOfListsDownloader()
    {
    }
    
    public Vector<String> downloadLists(String fromList)
    {
        return downloadLists(fromList, 0);
    }
    
    private Vector<String> downloadLists(String fromList, int depth)
    {
        Vector<String> lists = new Vector<String>();
        //fromList = LinkParser.parseLinkBox(fromList);
        if(depth > 1)
            return lists;
        
        try
        {
            //if(visited.contains(fromList))
            //    return;
            
            visited.add(fromList);
            
            String pageText = WikiJsonReader.getWikiText(fromList);
            //System.out.println("** " + fromList);
            //These are lists, which in turn contain links to other lists
            Vector<WikiList> listsOnPage = ListParser.getLists(fromList, pageText);

            for(WikiList listing: listsOnPage)
            {
                Vector<String> innerlist = listing.getList();

                for(String item: innerlist)
                {
                    item = LinkParser.parseLinkBox(item);
                    //for(int j=0;j<depth;j++)
                    //    System.out.print("  ");
                    //if(item.contains("Lists of") || item.contains("List of") )
                    if(item.contains("List of") )
                    {
                        System.out.println(item);
                        lists.add(item);
                    }
                    //System.out.println(item);
                    if(item.contains("Lists of") || item.contains("List of") )
                    {
                        if(depth <= 2)
                            downloadLists(item.trim().replaceAll("\\[\\[","").replaceAll("\\]\\]",""), depth+1);
                        //downloadLists(item.trim());
                    }
                }
            }
            return lists;
        }
        catch(Exception e)
        {
            return new Vector<String>();
        }
    }
    
   
}
