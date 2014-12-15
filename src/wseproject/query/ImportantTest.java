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
        
        Set<String> country = new HashSet<String>();
        //important.put(entity, country);
        //country.add("name");
        country.add("population");
        country.add("demonym");
        country.add("independence");
        country.add("president");
        country.add("prime minister");
        country.add("capital");
        country.add("currency");
        
        
        if(types.contains("country"))
        {
            important = country;
        }
        else if(types.contains("person") || 
                types.contains("people") || 
                types.contains("officeholder") || 
                types.contains("writer") || 
                types.contains("scientist")     )
        {
            
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
