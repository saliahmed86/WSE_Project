/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser.objects;

import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class WikiTable
{
    private String pageTitle;
    private String caption;
    private Vector<Vector<String>> table;
    private Vector<Vector<String>> transpose;

    public WikiTable(Vector<Vector<String>> table)
    {
        this.table = table;
        transpose = createTransposeTable(table);
    }

    
    public WikiTable(String pageTitle, String caption, Vector<Vector<String>> table)
    {
        this.caption = caption;
        this.pageTitle = pageTitle;
        this.table = table;
        transpose = createTransposeTable(table);
    }
    
    public static Vector<Vector<String>> createTransposeTable(Vector<Vector<String>> table)
    {
        try
        {
            Vector<Vector<String>> newTable = new Vector<Vector<String>>();
            Vector<String> header = table.get(0);
            for(int i=0;i<header.size();i++)
                newTable.add(new Vector<String>());

            for(int i=0;i<table.size();i++)
            {
                for(int j=0;j<header.size();j++)
                {
                    newTable.get(j).add(table.get(i).get(j));
                }
            }
            return newTable;
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            System.out.println("Error:" + e.getMessage());
            return null;
        }
        
    }

    public Vector<Vector<String>> getTable()
    {
        return table;
    }

    public Vector<Vector<String>> getTranspose()
    {
        return transpose;
    }

    public Vector<String> getColumn(int i)
    {
        return transpose.get(i);
    }
    
    public Vector<String> getRow(int i)
    {
        return table.get(i);
    }
    
    public int numRows()
    {
        return table.size();
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }
    
    
    
    
}
