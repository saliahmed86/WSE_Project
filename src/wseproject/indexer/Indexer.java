/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.indexer;

import wseproject.parser.LinkParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.security.*;
import wseproject.Options;

/**
 *
 * @author Ali Local
 */
public class Indexer
{
    //private HashMap<String, Integer> wordToIndex = new HashMap<String, Integer>();
    //private HashMap<Integer, String> indexToWord = new HashMap<Integer, String>();
    
    private HashMap<String, String> imageMap = new HashMap<String, String> ();
    
    HashMap<String, Vector<ER>> index = new HashMap<String, Vector<ER>>();
            
    final static String relationsNamesFile = Options.indexFilePath;
    
    private HashMap<String, Set<String>> entityToType = new HashMap<String, Set<String>>();
    private HashMap<String, Set<String>> typeToentity = new HashMap<String, Set<String>>();
    
    private HashMap<String, Integer> entityToId = new HashMap<String, Integer> ();
    private Vector<String> idToEntity = new Vector<String> ();
    
    //private Vector<String> relationsFiles = new Vector<String>();
    public static final String DELIM = " -$|$- ";
    public static final String ESCAPED_DELIM = " -\\$\\|\\$- ";
    
    static long relCount = 0;
    
    private static Indexer indexer = null;
    
    public static Indexer getIndexer()
    {
        if(indexer == null)
            indexer = new Indexer();
        
        return indexer;
    }
    
    private Indexer()
    {
        try
        {
            System.out.println("Reading file: " + relationsNamesFile);
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(new File(relationsNamesFile)));
                String line;
                while((line = br.readLine()) != null)
                {
                    handleRelation(line);
                }
                
                System.out.println("Total entities: " + idToEntity.size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            
            Set<String> keySet = index.keySet();
            Vector<String> keys = new Vector<String>(keySet);
            Collections.sort(keys);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void handleRelation(String line)
    {
        if(line.toLowerCase().contains("china") && line.toLowerCase().contains("gdp"))
            System.out.println(line);
        try
        {
            String tokens[] = line.split(ESCAPED_DELIM);

            if(tokens.length < 5)
                return;

            String entity = tokens[0].toLowerCase();
            String type = tokens[1].toLowerCase();
            String property = tokens[2].toLowerCase();
            String value = tokens[3].toLowerCase();
            String article = tokens[4].toLowerCase();
            String association = "";

            
            
            if(property.equals("image") || property.equals("image_name") || property.equals("image_flag"))
            {
                String image = LinkParser.parseLinkBox(tokens[3]).replaceAll("\\s", "_").replaceAll("File\\:", "");
                if(image.toLowerCase().endsWith(".jpg") || image.toLowerCase().endsWith(".png") || image.toLowerCase().endsWith(".gif") || image.toLowerCase().endsWith(".svg") || image.toLowerCase().endsWith(".tiff"))
                {
                
                    byte[] bytesOfMessage = image.getBytes("UTF-8");

                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.reset();
                    md.update(image.getBytes());
                    byte[] digest = md.digest();
                    BigInteger bigInt = new BigInteger(1,digest);
                    String hashtext = bigInt.toString(16);
                    // Now we need to zero pad it if you actually want the full 32 chars.
                    while(hashtext.length() < 32 ){
                      hashtext = "0"+hashtext;
                    }
                    //System.out.println("filename = " + image + "   ,   md5 = " + hashtext);

                    String hashpath = hashtext.substring(0,1) + "/" + hashtext.substring(0,2) + "/";
                    String path = "http://upload.wikimedia.org/wikipedia/commons/thumb/" + hashpath + image + "/200px-" + image;
                    
                    if(path.toLowerCase().endsWith(".svg"))
                        path = path + ".png";
                        
                    imageMap.put(entity, path);
                }
            }
            
            
            property = LinkParser.parseLinkBox(property);
            
            
            if(tokens.length == 6)
                association = tokens[5].toLowerCase();

            
            if(entity == null || entity.trim().equals("") || entity.length() >= 50 || entity.length() < 3)
                return;
            
            
            if(property != null && property.length() >= 50)
                return;
            
            if(entity.contains(":template:") || property.contains(":template:"))
                return;
            
            if(value != null && value.length() >= 50)
                return;
            
            if(property != null && property.contains("|"))
            {
                //property = property.substring(0, property.indexOf("|")).trim();
            }
            
            String nextEntity = LinkParser.parseLinkBox(value);

            entity = entity.replaceAll("\\}\\}", " ").trim();
            entity = LinkParser.parseLinkBox(entity);

            if(!entityToType.containsKey(entity))
                entityToType.put(entity, new HashSet<String>());

            entityToType.get(entity).add(type);


            if(!typeToentity.containsKey(type))
                typeToentity.put(type, new HashSet<String>());

            typeToentity.get(type).add(entity);


            if(nextEntity == null || nextEntity.trim().isEmpty())
                return;
            
            if(property == null || property.trim().isEmpty())
                return;
            
            int nextEntityId;

            if(!entityToId.containsKey(entity))
            {
                int entityId = idToEntity.size();
                idToEntity.add(entity);
                entityToId.put(entity, entityId);
            }
            
            
            if(!index.containsKey(entity))
            {
                index.put(entity, new Vector<ER>());
            }

            if(!entityToId.containsKey(nextEntity))
            {
                nextEntityId = idToEntity.size();
                idToEntity.add(nextEntity);
                entityToId.put(nextEntity, nextEntityId);
            }
            else
            {
                nextEntityId = entityToId.get(nextEntity);
            }

            index.get(entity).add(new ER(property, nextEntityId));


            relCount++;
            if(relCount % 10000 == 0)
                System.err.println("Added " + relCount + " relations");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public Vector<String> searchEntityProperty(String entity, String property)
    {
        entity = entity.toLowerCase();
        property = property.toLowerCase();
        
        if(!index.containsKey(entity))
        {
            return new Vector<String>();
        }

        Vector<ER> postingList = index.get(entity);
        
        Vector<String> results = new Vector<String>();
        for(ER er: postingList)
        {
            
            if(er.property.contains(property))
            {
                results.add(idToEntity.get(er.nextEntity));
            }
        }
        return results;
    }
    
    
    public Set<String> searchEntitiy(String entity)
    {
        entity = entity.toLowerCase();
        
        if(!index.containsKey(entity))
        {
            System.out.println("index does NOT contain " + entity);
            return null;
        }

        Vector<ER> postingList = index.get(entity);
        Set<String> resultsSet = new HashSet<String>();
        
        for(ER er: postingList)
        {
            String res = er.property + " :|: ";
            res += idToEntity.get(er.nextEntity);
        
            resultsSet.add(res);
        }
        return resultsSet;
        
    }
    
    
    public Vector<String> searchType(String type)
    {
        type = type.toLowerCase();
        
        if(!typeToentity.containsKey(type))
            return new Vector<String>();
        
        return new Vector<String>( typeToentity.get(type) );
    }
    

    public Set<String> getTypes(String entity)
    {
        return entityToType.get(entity);
    }
    
    
    public String getImagePath(String enitity)
    {
        if(imageMap.containsKey(enitity))
        {
            return imageMap.get(enitity);
        }
        return "";
    }
    
    
    public static void main(String[] args)
    {
        Indexer index = new Indexer();
        
        Vector<String> x = index.searchEntityProperty("china", "river");
        System.out.println("x = " + x);
    }
    
    
    class ER
    {
        int nextEntity;
        String property;
        String prettyNextEntity;
        
        
        public ER(String property, int nextEntity)
        {
            this.nextEntity = nextEntity;
            this.property = property;
        }

        public ER(int nextEntity, String property, String prettyNextEntity)
        {
            this.nextEntity = nextEntity;
            this.property = property;
            this.prettyNextEntity = prettyNextEntity;
        }
        
        

        
        
        
    }
    
    class Pair
    {
        String a;
        String b;
    }
    
    class Data
    {
        String property;
        String entity;
    }
}
