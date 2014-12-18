/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.crawler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import wseproject.parser.LinkParser;

/**
 * Basic idea of crawler is that it will look for Wiki links in the page, and at the same time maintain
 * a "depth" of how far it is away from the seed page. We should stop is after one or two hops, i.e.
 * seed -> one page -> next. Initially 1 can be fine.
 *
 * @author Ali Local
 */


public class Crawler
{
    public static Set<String> crawledLinks = new HashSet<String>();
    
    public static void crawl(String article, int depth)
    {
        crawl(article, depth, true);
    }
    
    public static void crawl(String article, int depth, boolean ignoreCategory)
    {
        //System.out.println("ignor cats = " + ignoreCategory);
        try
        {
            if(crawledLinks.contains(article))
                return;
            //if(depth > 2)
            //    return;
            if(article.toLowerCase().contains("category:") && ignoreCategory)
                return;

            crawledLinks.add(article);


            //System.out.println("fetching text for " + article);
            for(int i=1;i<=depth;i++) System.out.print("  ");
            System.err.println("fetching text for " + article);

            String text;// = WikiJsonReader.getWikiText(article);
            
            if(ignoreCategory)
            {
                text = WikiJsonReader.getWikiText(article, false);
            
                //System.out.println(text);
                if(depth < 1)
                {
                    //scan for links:
                    List<String> links = LinkParser.parseLinkBoxes(text);

                    for(String s: links)
                    {

                        //System.out.println(s);
                        crawl(s, depth + 1);
                    }
                }
            }
            else
            {
                Vector<String> links = WikiJsonReader.getWikiCategoryText(article);
                for(String s: links)
                {

                    //System.out.println(s);
                    crawl(s, depth + 1);
                }
            }

        }
        catch(Exception e)
        {
            //ignore
        }
            
    }
    
    public static void startFromSeedList(String seed)
    {
        
        startFromSeedList(seed, true);
    }
    public static void startFromSeedList(String seed, boolean ignoreCategory)
    {
        ListOfListsDownloader lldl = new ListOfListsDownloader();
        Vector<String> lists = lldl.downloadLists(seed.replaceAll("\\s","_"));
        
        for(String list: lists)
        {
            crawl(list, 0, ignoreCategory);
        }
    }
    
    public static void main(String[] args)
    {
        startFromSeedList("Lists_of_books");
        startFromSeedList("Lists_of_rivers");
        startFromSeedList("Lists_of_mountains");
        startFromSeedList("Lists_of_universities_and_colleges");
        
        
    }
}
