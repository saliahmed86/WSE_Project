/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject;

import wseproject.processor.PageProcessor;
import wseproject.nlp.StanfordManager;
import wseproject.parser.objects.WikiTable;
import wseproject.parser.objects.WikiList;
import wseproject.parser.ListParser;
import wseproject.parser.objects.MonotonicLog;
import wseproject.parser.objects.RowProps;
import wseproject.parser.TableAnalyzer;
import wseproject.parser.objects.ColRowSpanException;
import wseproject.parser.TableParser;
import wseproject.crawler.WikiJsonReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class Tester
{

    public static void main(String[] args)
    {
       //main_from_file(args);
       main_from_string(args);
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
            BufferedWriter bigWriter = new BufferedWriter(new FileWriter(new File("data/relations/books-srclist_entity_prop_out.txt")));
            
            
            BufferedWriter monotonicColWriter = new BufferedWriter(new FileWriter(new File("data/monotonicCols-books")));

            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\small_list.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\companies-srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\geo-srclist.txt")));
            BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\WSEProject\\data\\books-srclist.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("data/mountain")));
            Vector<String> listOfLists = new Vector<String>();

            String line;
            while ((line = br.readLine()) != null)
            {
                listOfLists.add(line);
            }

            

            for (String pgTitle : listOfLists)
            {
                (new PageProcessor()).processListPage(pgTitle, bigWriter, monotonicColWriter);
                //return;
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

        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\hfile.txt")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\Alfred+Hitchcock+filmography")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\cs2580\\Project\\List+of+longest+films")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+longest-running+United+States+television+series")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+longest+placenames+in+Ireland")));
            //BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\clean\\texts\\List+of+tallest+buildings+and+structures+in+London")));

            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }

            TableParser tp = TableParser.getInstance();
            //tp.findTable(sb.toString());
            String wikiText = sb.toString();

            String pgTitle = "List of oldest people by year of birth";
            pgTitle = "List of largest optical refracting telescopes";
            pgTitle = "List of cities in New York";
            pgTitle = "List of Byzantine emperors";
            pgTitle = "List of presidents of FIFA";
                   //pgTitle = "List of national anthems";
            //pgTitle = "List of fictional guidebooks";
            pgTitle = "Laurel and Hardy filmography";
            pgTitle = "List of highest mountains";
            pgTitle = "List_of_tallest_structures_in_the_world".replaceAll("_", " ");
            pgTitle = "List of rivers of Pakistan";
            pgTitle = "List of rivers of Norway";
            pgTitle = "List of capitals by population density";
            pgTitle = "World's largest municipalities by population";
            pgTitle = "Pakistan";
            //pgTitle = "Salman Rushdie";
            //pgTitle = "Gabriel_García_Márquez";

            //wikiText = WikiJsonReader.getWikiText("List_of_largest_optical_refracting_telescopes");
            wikiText = WikiJsonReader.getWikiText(pgTitle);
            Vector<String> tables = tp.findTable(wikiText);

            int t = 0;
            for (String table : tables)
            {
                t++;
                //if(t == 1)
                //    continue;
                //System.out.println("Table::");
                //System.out.println(table);

                try
                {
                    WikiTable wikiTable = tp.parseTable2(table);
                    if (wikiTable != null)
                    {
                        wikiTable.setPageTitle(pgTitle);
                        Vector<Vector<String>> rows = wikiTable.getTable();

                        System.out.println("rows = ");
                        //System.out.println(wikiTable.getTable());
                        for (Vector<String> row : rows)
                        {
                            for (String col : row)
                            {
                                System.out.print("<" + col + ">" + "\t");
                            }

                            System.out.println(row.size());
                        }
                        System.out.println("<<<END>>>");
                        //Vector<Vector<String>>  transpose = wikiTable.getTranspose();
                        //System.out.println("transpose = ");
                        //System.out.println(transpose);
                        //if(true)return;
                        //Vector<MonotonicLog> monotonicColumns = (new TableAnalyzer()).getMonoticColumns_FromCols(transpose);

                        TableAnalyzer analyzer = new TableAnalyzer(wikiTable);

                        List<RowProps> allEntityProps = analyzer.getPropertiesFromRows();
                        if (allEntityProps == null || allEntityProps.size() == 0)
                        {
                            System.out.println("allEntitiy props is empty or null");
                        }

                        for (RowProps props : allEntityProps)
                        {
                            System.out.println("entity = " + props.getEntity() + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                            for (Map.Entry e : props.getProps().entrySet())
                            {
                                System.out.println(e.getKey() + " => " + e.getValue());
                            }
                        }

                        Vector<MonotonicLog> monotonicColumns = analyzer.getMonoticColumns();

                        for (MonotonicLog l : monotonicColumns)
                        {
                            System.out.println("mono col = " + l.getColumnName() + "   ,  dir = " + l.getDir());

                        }
                    } else
                    {
                        System.out.println("rows null");
                    }
                } catch (ColRowSpanException crse)
                {
                    System.out.println(crse.getMessage());
                    System.out.println("Ignore and move on");
                } catch (Exception e)
                {
                    System.err.println("other exception: " + e.getMessage());
                    e.printStackTrace();
                }

            }
            
            
            
            
            
            //Now work with lists, instead of tables
                    try
                    {
                    //System.out.println("here 1");
                        Vector<String> titleTags = StanfordManager.getPOSTags(pgTitle);
                        int flag = 0;   //if flag is 1 or 0, add noun else not
                        //      System.out.println("here 2");
                        String noun = "";
                        for (int i = 0; i < titleTags.size(); i++)
                        {
                            String wordTag = titleTags.get(i);
                            String arr[] = wordTag.split("/");
                            String tag = arr[arr.length - 1];

            //System.out.println("word = " + arr[0] + "  ,  tag = " + tag + "<");
                            if (tag.equalsIgnoreCase("NN") || tag.equalsIgnoreCase("NNS") || tag.equalsIgnoreCase("NNP") || tag.equalsIgnoreCase("NNPS") || tag.equalsIgnoreCase("JJ"))
                            {
                                //System.out.println("tag is noun");
                                if (arr[0].equalsIgnoreCase("list"))
                                {
                                    //System.out.println("word id list");
                                    continue;
                                }
                                if (flag == 0 || flag == 1)
                                {
                                    //System.out.println("should add");
                                    noun += arr[0] + " ";
                                    flag = 1;
                                }

                            } else
                            {
                                if (flag == 1)
                                {
                                    flag = -1;      //This means that previously we had a noun, but not now, so do not read more nounds
                                }
                            }

                        }

                        noun = noun.trim();

                        Vector<WikiList> lists = ListParser.getLists(pgTitle, wikiText);
                        for (WikiList list : lists)
                        {
                            for (String item : list.getList())
                            {
                                //w.printList();
                                String entity = item;
                                String type = StanfordManager.getLemma(noun);
                                System.out.println("section = " + list.getSectionTitle());
                                System.out.println("entity = " + entity + "  ,  type = " + StanfordManager.getLemma(noun));
                                

                            }
                        }
                    } catch (Exception e)
                    {
                        System.out.println("error in lists: " + e.getMessage());
                    }

        } catch (Exception e)
        {
            e.printStackTrace();
            
        }

    }
}
