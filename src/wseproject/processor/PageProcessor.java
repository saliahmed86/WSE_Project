/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.processor;

import wseproject.nlp.StanfordManager;
import wseproject.indexer.Indexer;
import wseproject.parser.objects.WikiTable;
import wseproject.parser.objects.WikiList;
import wseproject.parser.InfoboxExtractor2;
import wseproject.parser.ListParser;
import wseproject.parser.objects.NoMainColumnException;
import wseproject.parser.objects.MonotonicLog;
import wseproject.parser.objects.ListTitleFields;
import wseproject.parser.objects.RowProps;
import wseproject.parser.TableAnalyzer;
import wseproject.parser.ListTitleParser;
import wseproject.parser.LinkParser;
import wseproject.parser.objects.ColRowSpanException;
import wseproject.parser.TableParser;
import wseproject.crawler.WikiJsonReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class PageProcessor
{
    private static TableParser tp = TableParser.getInstance();
    
    public static Set<String> processListPage(String pgTitle, BufferedWriter bigWriter, BufferedWriter monotonicColWriter)
    {
        pgTitle = LinkParser.parseLinkBox(pgTitle); //in case input file is messed up

        Set<String> possibleEntities = new HashSet<String>();
        
        try
        {
            System.out.println("Title :: " + pgTitle);

            Vector<String> titleTags = StanfordManager.getPOSTags(pgTitle);
            String taggedTitle = "";
            for(String token: titleTags) taggedTitle += token + " ";
            taggedTitle = taggedTitle.trim();

            //Java 8 stuff-> //String taggedTitle = String.join(" ", titleTags);
            //parse title to see what this is about:
            
            //list of X of/in/by Y

            ListTitleFields f = (new ListTitleParser(taggedTitle)).parseTitle();

            //System.out.println("type = " + f.getType());
            //System.out.println("constraint = " + f.getConstraint());
            //System.out.println("order = " + f.getOrder());

            String type = f.getType();
            String jjs = f.getJjs();
            String constraint = f.getConstraint();
            String order = f.getOrder();
            
            //from type, get nouns and adjective
            
            

            String wikiText = WikiJsonReader.getWikiText(pgTitle);
            Vector<String> tables = tp.findTable(wikiText);

            StringBuilder orderFinderSB = new StringBuilder();
            //monotonicColWriter.write(pgTitle);
            //monotonicColWriter.write("\n");
            //monotonicColWriter.write(type);
            //monotonicColWriter.write("\n");
            //monotonicColWriter.write(jjs);
            //monotonicColWriter.write("\n");
            orderFinderSB.append("Page:").append(pgTitle).append("\n");
            orderFinderSB.append("Type:").append(type).append("\n");
            orderFinderSB.append("JJS:").append(jjs).append("\n");
            orderFinderSB.append("Order:").append(order).append("\n");
            String orderFinderInitString = orderFinderSB.toString();
            
            orderFinderSB.setLength(0);
            int orderedColCount = 0;
            
            for (String table : tables)
            {
                try
                {
                    WikiTable wikiTable = tp.parseTable2(table);
                    if (wikiTable != null)
                    {
                        wikiTable.setPageTitle(pgTitle);
                        //Vector<Vector<String>> rows = wikiTable.getTable();
                        //System.out.println("rows = ");
                        //System.out.println(wikiTable.getTable());
                        /*
                         for(Vector<String> row: rows)
                         {
                         for(String col: row)
                         {
                         System.out.print("<" + col + ">" + "\t");
                         }

                         System.out.println(row.size());
                         }
                         System.out.println("<<<END>>>");
                         */
                        //Vector<Vector<String>>  transpose = wikiTable.getTranspose();
                        //System.out.println("transpose = ");
                        //System.out.println(transpose);

                        //Vector<MonotonicLog> monotonicColumns = (new TableAnalyzer()).getMonoticColumns_FromCols(transpose);
                        TableAnalyzer analyzer = new TableAnalyzer(wikiTable);

                        try
                        {
                            List<RowProps> allEntityProps = analyzer.getPropertiesFromRows();

                            for (RowProps props : allEntityProps)
                            {
                                //writer.write("entity = ");
                                //writer.write("entity = ");
                                String entity = props.getEntity().replaceAll("\n", "");
                                
                                //System.out.println("entity = " + props.getEntity() + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                                for (Map.Entry e : props.getProps().entrySet())
                                {
                                    //System.out.println(e.getKey() + " => " + e.getValue());
                                    /*
                                    bigWriter.write(props.getEntity().replaceAll("\n", ""));// + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                                    bigWriter.write(Indexer.DELIM);
                                    //bigWriter.write(StanfordManager.getLemma(props.getEntityType()).replaceAll("\n", ""));
                                    bigWriter.write(type);
                                    bigWriter.write(Indexer.DELIM);
                                    bigWriter.write(((String) e.getKey()).replaceAll("\n", ""));
                                    bigWriter.write(Indexer.DELIM);
                                    bigWriter.write(((String) e.getValue()).replaceAll("\n", ""));
                                    bigWriter.write(Indexer.DELIM);
                                    bigWriter.write(pgTitle.replaceAll("\n", ""));
                                    bigWriter.write("\n");
                                    bigWriter.write(Indexer.DELIM);
                                    bigWriter.write(constraint.replaceAll("\n", ""));
                                    bigWriter.write("\n");
                                    */
                                    String str = joinWithDelim
                                    (
                                        entity,//+ "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                                        type,
                                        ((String) e.getKey()).replaceAll("\n", ""),
                                        ((String) e.getValue()).replaceAll("\n", ""),
                                        pgTitle.replaceAll("\n", ""),
                                        constraint.replaceAll("\n", "")
                                    );
                                    
                                    bigWriter.write(str);
                                    bigWriter.write("\n");
                                    

                                    possibleEntities.add(entity);
                                }
                                
                                //for reverse association:
                                if(constraint != null && !constraint.trim().equals(""))
                                {
                                    String str = joinWithDelim(constraint,"", type, entity, pgTitle, "");
                                    bigWriter.write(str);
                                    bigWriter.write("\n");
                                    
                                    possibleEntities.add(constraint);
                                }

                            }
                        }
                        catch(NoMainColumnException e)
                        {
                            //System.out.println(e.getClass() + ": " + e.getMessage());
                        }

                        Vector<MonotonicLog> monotonicColumns = analyzer.getMonoticColumns();


                        for(MonotonicLog l: monotonicColumns)
                        {
                            if(l == null)
                                continue;
                            //System.out.println("mono col = " + l.columnName + "   ,  dir = " + l.dir);
                            //monotonicColWriter.write(l.columnName + "\t" + l.dir );
                            //monotonicColWriter.write("\n");
                            orderFinderSB.append(l.getColumnName()).append("\t")
                                            .append(l.getDir()).append("\n");
                            orderedColCount++;
                        }

                    } else
                    {
                        //System.out.println("rows null");
                    }
                } catch (ColRowSpanException crse)
                {
                    //System.out.println(crse.getMessage());
                    //System.out.println("Ignore and move on");
                } catch (Exception e)
                {
                    //System.err.println("other exception: " + e.getMessage());
                    //e.printStackTrace();
                }
            }
            if(orderedColCount > 0)
            {
                monotonicColWriter.write(orderFinderInitString);
                monotonicColWriter.write("Count:" + orderedColCount + "\n");
                monotonicColWriter.write(orderFinderSB.toString());
                monotonicColWriter.write("-----------\n");
            }

            //Now work with lists, instead of tables
            try
            {
            //System.out.println("here 1");
                //Vector<String> titleTags = StanfordManager.getPOSTags(pgTitle);
                /*int flag = 0;   //if flag is 1 or 0, add noun else not
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
                */
                Vector<WikiList> lists = ListParser.getLists(pgTitle, wikiText);
                for (WikiList list : lists)
                {
                    for (String item : list.getList())
                    {
                        //w.printList();
                        String listEntity = item.replaceAll("\n", "");
                        //String type = StanfordManager.getLemma(type);
                        //System.out.println("entity = " + entity + "  ,  type = " + StanfordManager.getLemma(noun));
                        /*
                        bigWriter.write(entity.replaceAll("\n", ""));// + "  ,  type = " + StanfordManager.getLemma(props.getEntityType()));
                        bigWriter.write(Indexer.DELIM);
                        bigWriter.write(type.replaceAll("\n", ""));
                        bigWriter.write(Indexer.DELIM);
                        bigWriter.write(" ");
                        bigWriter.write(Indexer.DELIM);
                        bigWriter.write(" ");
                        bigWriter.write(Indexer.DELIM);
                        bigWriter.write(pgTitle.replaceAll("\n", ""));
                        bigWriter.write("\n");
                        */
                        String str = joinWithDelim(listEntity, type, "", "", pgTitle, constraint);
                        bigWriter.write(str);
                        bigWriter.write("\n");
                        
                        possibleEntities.add(listEntity);
                        
                        //Now reverse relationship
                        if(constraint != null && !constraint.trim().equals(""))
                        {
                            str = joinWithDelim(constraint, "", type, listEntity, pgTitle, "");
                            bigWriter.write(str);
                            bigWriter.write("\n");
                            
                            possibleEntities.add(constraint);
                        }
                                
                    }
                }
            } catch (Exception e)
            {
                //System.out.println("error in lists: " + e.getMessage());
               // e.printStackTrace();
            }

        } catch (Exception e)
        {
            //e.printStackTrace();
            //System.err.println("Some error: " + e.getMessage());
        }
        
        return possibleEntities;
    }
    
    
    public static void processArticlePage(String pgTitle, BufferedWriter bigWriter)
    {
        try
        {
            //bigWriter = new BufferedWriter(new PrintWriter( System.err));
            //bigWriter = new BufferedWriter(new FileWriter(new File("testtt")));
            
            //System.err.println("Title :: " + pgTitle);

            Vector<String> titleTags = StanfordManager.getPOSTags(pgTitle);
            String taggedTitle = "";
            for(String token: titleTags) taggedTitle += token + " ";
            taggedTitle = taggedTitle.trim();

            //Java 8 stuff-> //String taggedTitle = String.join(" ", titleTags);
            //parse title to see what this is about:
            
            String wikiText = WikiJsonReader.getWikiText(pgTitle);
            
            
            //Get infobox
            //System.out.println("Infobox:::");
            String type = null;
            try
            {
                InfoboxExtractor2 infoboxExtractor2 = new InfoboxExtractor2();
                type = infoboxExtractor2.startAFile(bigWriter, pgTitle, wikiText);
                if(type == null || type.trim().equals(""))
                    return ;
                
                if(type.contains("<!--"))
                {
                    type = type.replaceAll("\\<\\!--.*--\\>", "");
                    type = type.replaceAll("\\|", "");
                }
            }
            catch(Exception e)
            {
                return ;
                //e.printStackTrace();
            }
            
            String mainType = type;   //comes from infobox // f.getType();
            //String constraint = pgTitle;
            String mainEntity = pgTitle;
            
            
            
            
            //bigWriter.flush();
            //System.out.println("");
            //System.out.println("");
            //System.out.println("");
            
            //Now work with lists, instead of tables
            try
            {
                Vector<WikiList> lists = ListParser.getLists(pgTitle, wikiText);
                for (WikiList list : lists)
                {
                    for (String item : list.getList())
                    {
                        //w.printList();
                        String listItemEntity = item.replaceAll("\n", "");
                        String listItemType = list.getSectionTitle();
                        String str = joinWithDelim(listItemEntity, listItemType, "", mainEntity, pgTitle, mainEntity);
                        bigWriter.write(str);
                        bigWriter.write("\n");
                        
                        //Now reverse relationship
                        
                        if(mainEntity != null && !mainEntity.trim().equals(""))
                        {
                            str = joinWithDelim(mainEntity, mainType, listItemType, listItemEntity, pgTitle, "");
                            bigWriter.write(str);
                            bigWriter.write("\n");
                        }
                                
                    }
                }
            } catch (Exception e)
            {
                //System.out.println("error in lists: " + e.getMessage());
               // e.printStackTrace();
            }

        } 
        catch (Exception e)
        {
            //e.printStackTrace();
            //System.err.println("Some error: " + e.getMessage());
        }
    }
    
    public static String joinWithDelim(String... tokens)
    {
        if(tokens == null)
            return "";
        
        StringBuilder results = new StringBuilder();
        int i;
        for(i=0;i<tokens.length-1;i++)
        {
            results.append(tokens[i]);
            results.append(Indexer.DELIM);
        }
        results.append(tokens[i]);
        return results.toString();
    }

}
