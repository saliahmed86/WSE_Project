/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import wseproject.parser.objects.ColRowSpanException;
import wseproject.parser.objects.RowSpanLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wseproject.parser.objects.MonotonicLog;
import wseproject.nlp.Regexer;
import wseproject.parser.objects.RowProps;
import wseproject.parser.objects.WikiTable;

/**
 *
 * @author Wikero
 */
public class TableParser 
{
    /*
    Assume lines are separated by \n
    */
    public static TableParser instance = null;
    
    private TableParser()
    {
    }
    
    /*
    Not too dangerous without synchronization, having another instance won't kill us
    */
    public static TableParser getInstance()
    {
        if(instance == null)
        {
            instance = new TableParser();
        }
        return instance;
    }
         
    
    public Vector<String> findTable(String content)
    {
        Matcher tableMatcher = Regexer.matchTable(content);
        
        Vector<String> ll = new Vector<String>();
        while(tableMatcher.find())
        {
            String x = tableMatcher.group();
            //System.out.println("TTABLE::");
            //System.out.println(x);
            
            ll.add(x);
        }
        return ll;
    }
    
    //public Vector<Vector<String>> parseTable2(String content) throws ColRowSpanException
    public WikiTable parseTable2(String content) throws ColRowSpanException
    {
        //System.out.println("");
        //System.out.println("");
        //System.out.println("");
        try
        {
            content = content.trim();
            if(content.length() < 10) return null;

            //collapse all templates into single line
            Matcher regexMatcher = Regexer.matchMultilineTemplate(content);//multilelineTemplatePattern.matcher(content);

            while (regexMatcher.find()) 
            {
                //System.out.println("found = " + regexMatcher.group(1));
                //content = regexMatcher.replaceAll(
                //    "{{" + regexMatcher.group(1).replaceAll("\n", "") + "}}"   );
            }

            
            //is it safe to replace all "||" with "\n|"???
            //content = content.replaceAll("(?:.+)\\|\\|", "\n|");
            content = content.replaceAll("!!", "\n!");
            
            //System.out.println(content);
            StringBuilder currentRead = new StringBuilder();
            Scanner s0 = new Scanner(content);
            Vector<String> rowsX = new Vector<String>();
            
            //simply get stuff between |- and |-  (these start on new lines)
            
            while(s0.hasNextLine())
            {
                String line = s0.nextLine();
                
                if(line.length() >= 2 && line.substring(0,2).equals("|+"))
                    continue;
                if(line.length() >= 2 && line.substring(0,2).equals("{|"))
                    continue;
                
                if(line.length() >= 2 && line.substring(0,2).equals("|-"))
                {
                    //ensures that we do not store crap until headers are read
                    if(currentRead.length() == 0)
                        continue;
                    
                    String prevRow = currentRead.toString();
                    rowsX.add(prevRow);
                    
                    //System.out.println("prev row = " + prevRow);
                    
                    currentRead.setLength(0);
                    
                    if(!line.trim().equals("|-"))
                    {
                        //check if this line has actual content or just row style
                        //if just style, remove
                        //if((line.contains("style")||line.contains("bgcolor")) && !line.substring(2).contains("|"))
                        if(line.contains("style") && !line.substring(2).contains("|"))
                            //line has only style, so do not append
                            continue;
                        
                        //currentRead.append(line);
                        //currentRead.append("\n");
                    }
                }
                else
                {
                    currentRead.append(line);
                    currentRead.append("\n");
                }
            }
            
            rowsX.add(currentRead.toString());
            
            /*
            for(String row: rowsX)
            {
                String cols[] = row.split("(?s)\\|\\||\\n\\||\\n\\!");
                for(String col: cols)
                {
                    System.out.println("col = " + col);
                }
            }
            */
            
            //System.out.println("rowsX = ");
            //System.out.println(rowsX);
            
            Vector<Vector<String>> newRows = new Vector<Vector<String>>();
            
            
            //Assume first row is header
            Vector<String> headers = new Vector<String>();
            String headerStr = rowsX.get(0);
            
            headerStr = "\n" + headerStr;
            //System.out.println("head str = " + headerStr);
            //System.out.println("next str = " + rowsX.get(1));
                
            String headerCols[] = headerStr.split("(?s)\\|\\||\\n\\||\\n\\!");
            for(int i=1;i<headerCols.length;i++)
            {
                String header = headerCols[i];
                headers.add(cleanupCell(header));
                
                if(header.contains("colspan"))
                {
                    String colspanRegex = "colspan=(?:\\\")*(\\d+)(?:\\\")*";
                    Pattern pcs = Pattern.compile(colspanRegex);
                    Matcher msc = pcs.matcher(header);
                    if(msc.find())
                    {
                        int val = Integer.parseInt(msc.group(1));
                        for(int ii=1;ii<val;ii++)
                        {
                            header = cleanupCell(header);
                            headers.add(header);
                            //System.out.println("header = " + header);
                        }
                    }
                }
                
                
            }

            //System.out.println("headers = " + headers);
            
            newRows.add(headers);
            
            Vector<RowSpanLog> rowspanList = new Vector<RowSpanLog>();
            
            /*
            System.out.println("rowsX.size() = " + rowsX.size());
            for(String row: rowsX)
                System.out.println("row = <<<<<<<" + row + ">>>>>>>>");
            */
            //rowsX.get(rowsX.size()-1).re
            //for(String row: rowsX)
            for(int i=1;i<rowsX.size();i++)   
            {
                Vector<String> rowCols = new Vector<String>();
                
                String row = rowsX.get(i);
                //System.out.println("row0 = " + row);
                
                
                row = "\n" + row;
                if(i == rowsX.size()-1)
                    row = row.replaceAll("\\|\\}", "");
                
                //System.out.println("row = " + row);
                String cols[] = row.split("(?s)\\|\\||\\n\\||\\n\\!");
                
                
                //for now ignore rows if num cols != num headers
                if(cols.length-1 > headers.size())
                    continue;
                
                
                for(int j=1;j<cols.length;j++)
                {
                    int colNum = j - 1;
                    int colCount = 1;
                    String col = cols[j];
                    //System.out.println("col = <<" + col + ">>");
                    rowCols.add(cleanupCell(col));
                    //rowCols.add((col));
                    
                    if(col.contains("colspan"))
                    {
                        //System.out.println("colspan found in col " + colNum);
                        //System.out.println("this col = " + col);
                        //System.out.println("rowX till now = " + rowCols);
                        //System.out.println("col == " + col);
                        String colspanRegex = "colspan=(?:\\\")*(\\d+)(?:\\\")*";
                        Pattern pcs = Pattern.compile(colspanRegex);
                        Matcher msc = pcs.matcher(col);
                        if(msc.find())
                        {
                            colCount = Integer.parseInt(msc.group(1));
                            for(int ii=1;ii<colCount;ii++)
                            {
                                rowCols.add(cleanupCell(col));
                            }
                        }
                        //System.out.println("rowY till now = " + rowCols);
                        
                        
                    }
                    
                    
                    
                    if(col.contains("rowspan"))
                    {
                        /*
                        System.out.println("rowspan found in col " + colNum);
                        System.out.println("headers = " + headers);
                        System.out.println("this content = " + col);
                        System.out.println("row till now = " + rowCols);
                        System.out.println("END");
                        */
                        int rowCount = 0;
                        String colspanRegex = "rowspan=(?:\\\")*(\\d+)(?:\\\")*";
                        Pattern pcs = Pattern.compile(colspanRegex);
                        Matcher msc = pcs.matcher(col);
                        if(msc.find())
                        {
                            rowCount = Integer.parseInt(msc.group(1));
                        }
                        //System.out.println("row count = " + rowCount);
                        int start = i;
                        int end = i + rowCount;
                        
                        for(int q=1;q<rowCount;q++)
                        {
                            //System.out.println("adding = " + (i+q) + " , " + colCount + " , " + (colNum-1) + " , " + col);
                            rowspanList.add(new RowSpanLog(i+q, colCount, colNum-1, col));
                        }
                    }
                    //throw new ColRowSpanException("rowspan");
                    
                    
                    //check if col j-1 is in list, since we need to add cols afte this if there was a rowspan
                    int foundAt = -1;
                    for(int p=0;p<rowspanList.size();p++)
                    {
                        RowSpanLog rsl = rowspanList.get(p);
                        if(j-1 == rsl.getAfterWhich() && i == rsl.getRow())
                        {
                            //System.out.println("After = " + j);
                            //System.out.println("to add = " + rsl.contents);
                            //System.out.println("cur cell num = " + colNum);
                            //System.out.println("cur cell = " + col);
                            foundAt = p;
                            break;
                        }
                        if(i == rsl.getRow() && j == 1 && rsl.getAfterWhich() == -1 )
                        {
                            foundAt = 0;
                            break;
                        }
                    }
                    if(foundAt != -1)
                    {
                        //rsl.afterWhich;
                        //rsl.row;
                        RowSpanLog rsl = rowspanList.get(foundAt);
                        
                        for(int ii=0;ii<rsl.getHowMany();ii++)
                        {
                            if(rsl.getAfterWhich() == -1)
                            {
                                //rowCols.add();
                                rowCols.insertElementAt(cleanupCell(rsl.getContents()), rowCols.size()-1);
                            }
                            else
                                rowCols.add(cleanupCell(rsl.getContents()));
                        }
                        rowspanList.remove(foundAt);
                                
                    }
                    
                }
                
                
                
                
                
                
                
                newRows.add(rowCols);
                //System.out.println("col len = " + cols.length);
                //System.out.println("-----------------");
            }
            
            int finalNumCols = newRows.get(0).size();
            Vector<Vector<String>> finalRows = new Vector<Vector<String>>();
            for(Vector<String> v: newRows)
            {
                if(v.size() == finalNumCols)
                    finalRows.add(v);
                else
                {
                    //System.out.println("v = " + v + " has incorrect length");
                }
            }
            
            /*
            //System.out.println(newRows);
            for(Vector<String> v: finalRows)
            {
                for(String s: v)
                    //System.out.print("<" + s + ">\t");
                    System.out.print(s + "\t");
                System.out.println(v.size());
            }
            */
            
            return new WikiTable(finalRows);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public String cleanupCell(String cell)
    {
        //System.out.println("Received cell: " + cell);
        try
        {
            if(cell.startsWith("-"))
                cell = cell.substring(1);
                        
            //learn how to parse templates
            //Till then, just delete everything them
            //may cause problems because text could be nested in them, and we have nested templates too
            //cell = cell.replaceAll("\\{\\{.*?\\}\\}", "TEMPLATE");
            //Remove greedy regexer
            //cell = cell.replaceAll("\\{\\{.*\\}\\}", "");
            //System.out.println("cell = " + cell);
            //cell = LinkParser.parseTemplate(cell);
            
            cell = cell.trim();
            if(cell.startsWith("|") && !cell.startsWith("||"))
                cell = cell.substring(1);

            //Remove ref tags
            cell = cell.replaceAll("<ref[^>]*?>.*?</ref>", "");
            cell = cell.replaceAll("<ref[^>]*?/>", "");
            
            
            //Remove | that denotes style, but do not remove if it is nested in something???
            if(cell.indexOf("|") >= 1)
                if(!(cell.indexOf("[[") < cell.indexOf("|") && cell.indexOf("[[") != -1))
                    cell = cell.substring(cell.indexOf("|")+1).trim();

            
            //remove quotes
            cell = cell.replaceAll("\\\"", "");
            cell = cell.replaceAll("\\\'", "");
            
            return cell;
        }
        catch(Exception e)
        {
            System.out.println("Exception at cell: " + cell);
            throw e;
        }
    }
    
    
    
    
    /*
    public static List<String> parseLinkBoxAll(String text)
    {
        String regex = "(?:\\[\\[)(.*?)(?:\\]\\])";
        String str = "[[cc|aa|xa]]   [[xt|pt]]";
        //String str = text;
                
         
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        
              
        List<String> ll = new Vector<String>();
        while(m.find())
        {
            System.out.println(m.group(1));
            String ss[] = m.group(1).split("\\|");
            if(ss.length == 2)
                ll.add(ss[0]);
            else if(ss.length == 1)
                ll.add(ss[0]);
            //what to return for 3??
        }
        System.out.println(ll);
        return ll;
    }
    */
    

    
    
    
    
    

}
