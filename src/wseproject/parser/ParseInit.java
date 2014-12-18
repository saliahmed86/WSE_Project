/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import wseproject.processor.PageProcessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.util.Vector;
import wseproject.Options;

/**
 *
 * @author Ali Local
 */
public class ParseInit
{

    public static void main(String[] args)
    {
        main_from_file(args);
        //main_from_string(args);
    }

    public static void main_from_file(String[] args)
    {
        try
        {
            BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File(Options.indexFilePath)));
            BufferedWriter monotonicColWriter = new BufferedWriter(new FileWriter(new File(Options.monoColFilePath)));
            
            Vector<String> listOfLists = new Vector<String>();
            Vector<String> listOfArticles = new Vector<String>();

            
            File dir = new File(Options.cachePath);
            File files[] = dir.listFiles();
            
            
            for(File file: files)
            {
                
                String fname = URLDecoder.decode(file.getName().replaceAll("_", " "), "utf-8").trim();
                if(fname.startsWith("List of"))
                {
                    listOfLists.add(fname);
                }
                else
                {
                    listOfArticles.add(fname);
                }
                            
            }
            
            System.out.println("Loaded: " + listOfArticles.size() + " articles and " + listOfLists.size() + " lists");

            
            
            int i = 0;
            for (String pgTitle : listOfLists)
            {
                (new PageProcessor()).processListPage(pgTitle, bigWriter, monotonicColWriter);
                i++;
                System.err.println("Processed " + i + " lists");
                //return;
            }

            System.out.println("");
            System.out.println("--------------");
            System.out.println("Finished Lists");
            System.out.println("--------------");
            System.out.println("");
            
            
            i = 0;
            for (String pgTitle : listOfArticles)
            {
                PageProcessor.processArticlePage(pgTitle, bigWriter);
                if(i%100 == 0)
                    System.err.println("Processed " + i + " articles");
                i++;
            }

            
            bigWriter.close();

            monotonicColWriter.close();
            
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    
}
