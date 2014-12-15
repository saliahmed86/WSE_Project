/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import wseproject.processor.PageProcessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class Tester2
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
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("F:\\cs2580\\Project\\entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("F:\\cs2580\\Project\\filmography_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/uni-srclist_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/geo-srclist_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/companies-srclist_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/data-srclist_entity_prop_out.txt"), true));
            
            BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/data-srclist_entity_prop_out.txt")));
            //BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/test-srclist_entity_prop_out.txt"),true));
            
            
            BufferedWriter monotonicColWriter = new BufferedWriter(new FileWriter(new File("data/monotonicCols-data")));
            //BufferedWriter monotonicColWriter = new BufferedWriter(new FileWriter(new File("data/monotonicCols-temp")));

            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\small_list.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\companies-srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\geo-srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\books-srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("data/mountain")));
            Vector<String> listOfLists = new Vector<String>();
            Vector<String> listOfArticles = new Vector<String>();

            /*
            String line;
            while ((line = br.readLine()) != null)
            {
                listOfLists.add(line);
            }
            */
            File dir = new File("data/corpus_cache/");
            //File dir = new File("data/temp_corpus/");
            File files[] = dir.listFiles();
            
            System.out.println("dd");
            for(File file: files)
            {
                
                String fname = URLDecoder.decode(file.getName().replaceAll("_", " "), "utf-8").trim();
                //System.out.println(fname);
                if(fname.startsWith("List of"))
                {
                   // if(fname.contains("Pakistan") && fname.contains("rivers"))
                    //System.out.println(fname);
                        listOfLists.add(fname);
                }
                else
                {/*
                    if(fname.toLowerCase().contains("sutlej") ||
                        fname.toLowerCase().contains("indus") || 
                        fname.toLowerCase().contains("jhelum") || 
                        fname.toLowerCase().contains("dasht") || 
                        fname.toLowerCase().contains("chenab")) */
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
            
            //if(true) return;
            
            i = 0;
            for (String pgTitle : listOfArticles)
            {
                //(new PageProcessor()).processListPage(pgTitle, bigWriter, monotonicColWriter);
                PageProcessor.processArticlePage(pgTitle, bigWriter);
                if(i%100 == 0)
                    System.err.println("Processed " + i + " articles");
                //return;
                i++;
            }

            
            bigWriter.close();

            monotonicColWriter.close();
            //tp.findTable(sb.toString());
            /*
             String pgTitle = "List of oldest people by year of birth";
             pgTitle = "List of largest optical refracting telescopes";
             pgTitle = "List of cities in New York";
             pgTitle = "List of Byzantine emperors";
             pgTitle = "List of presidents of FIFA";
            
             //wikiText = WikiJsonReader.getWikiText("List_of_largest_optical_refracting_telescopes");
             */
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void main_from_string(String[] args)
    {

        String pgTitle = "Salman Rushdie";
        //pgTitle = "Joseph_Anton:_A_Memoir";
                
        (new PageProcessor()).processArticlePage(pgTitle, null);
        

    }
}
