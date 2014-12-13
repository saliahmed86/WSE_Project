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
public class WikiList
{
    private String article;
    private String sectionTitle;
    private Vector<String> list;

    public WikiList(String article, String sectionTitle)
    {
        this.article = article;
        this.sectionTitle = sectionTitle;
        this.list = new Vector<String>();
    }

    public void addElemToList(String elem)
    {
        list.add(elem);
    }

    public void printList()
    {
        System.out.println(sectionTitle);
        System.out.println("----------");
        for(String s: list)
        {
            System.out.println("- " + s);
        }
        System.out.println("");
        //System.out.println("");
    }

    public Vector<String> getList()
    {
        return list;
    }

    public String getArticle()
    {
        return article;
    }

    public String getSectionTitle()
    {
        return sectionTitle;
    }
    
    
    
    
        
}
