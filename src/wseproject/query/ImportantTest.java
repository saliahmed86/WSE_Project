/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import wseproject.indexer.Indexer;

/**
 *
 * @author Ali Local
 */
public class ImportantTest
{
    private String entity;
    private Indexer indexer;
    private Set<String> types;
    //private HashMap<String, Set<String>> important = new HashMap<String, Set<String>>();
    private Set<String> important = new HashSet<String>();
    
    public ImportantTest(String entity, Indexer indexer)
    {
        this.entity = entity;
        this.indexer = indexer;
        types = indexer.getTypes(entity);
        
        //important = new HashMap<String, Set<String>>();
        
        
        if(types.contains("country")){
            //important.add("name");
            important.add("population");
            important.add("demonym");
            important.add("independence");
            important.add("president");
            important.add("prime minister");
            important.add("capital");
            important.add("currency");
        }else if(types.contains("university")){
            important.add("motto");
            important.add("president");
            important.add("student");
            important.add("established");
            important.add("location");
        }else if(types.contains("person")){
            important.add("birthth");
            important.add("death");
            important.add("known_for");
            important.add("children");
        }else if(types.contains("mountain")){
            important.add("elavation");
            important.add("location");
        }else if(types.contains("river")){
            important.add("length");
            important.add("watershed");
            important.add("origin");
            important.add("discharge");
        }else if(types.contains("film")){
            important.add("producer");
            important.add("director");
            important.add("released");
            important.add("budget");
            important.add("gross");
            important.add("released");
            important.add("language");
        }else if(types.contains("book")){
            important.add("author");
            important.add("genre");
            important.add("publisher");
            important.add("released");
            important.add("isbn");
        }
        
    }
    
    public boolean isImportant(String property)
    {
        for(String s: important)
        {
            //return important.contains(property);
            if(property.contains(s))
                return true;
        }
        return false;
    }
    
    
    
}
