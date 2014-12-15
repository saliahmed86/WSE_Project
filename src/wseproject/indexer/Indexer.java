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
            
    //final static String relationsSrcFolder = "data/relations/";
    //final static String relationsNamesFile = "data/relations.txt";
    final static String relationsNamesFile = "data/relations/data-srclist_entity_prop_out.txt";
    //final static String relationsNamesFile = "data/relations/test-srclist_entity_prop_out.txt";
    //final static String allRelationsFile = "data/all_relations";
    
    private HashMap<String, Set<String>> entityToType = new HashMap<String, Set<String>>();
    private HashMap<String, Set<String>> typeToentity = new HashMap<String, Set<String>>();
    
    private HashMap<String, Integer> entityToId = new HashMap<String, Integer> ();
    private HashMap<String, Integer> typeToId = new HashMap<String, Integer> ();
    private Vector<String> idToEntity = new Vector<String> ();
    private Vector<String> idToType = new Vector<String> ();
    
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
        /*
        System.out.println("Reading file of relations files");
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(relationsNamesFile)));
            String line;
            while((line = br.readLine()) != null)
            {
                relationsFiles.add(line);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        */
        
        int i = 0;
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
                    i++;
                    
                    //if(i%10000 == 0)
                    //    System.out.println("read " + i + " lines");
                }
                
                System.out.println("Total entities: " + idToEntity.size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            
            //print all posting lists:
            Set<String> keySet = index.keySet();
            Vector<String> keys = new Vector<String>(keySet);
            Collections.sort(keys);
            /*
            for(String key: keys)
            {
                System.out.print(key);
                System.out.print("\t");
                System.out.println(index.get(key).size());
            }
            */
            


            //Main one:::
            
            /*
            iterate through list of entities
            For each entity, get properties. If any pair of properties is the same,
            note that information for the TYPE of that entity.
            */
            
            //if(true) return;
            /*
            //maps type to hashmap (which maps property to other properties)
            //HashMap<String, HashMap<String, Set<String>>> bigTypePropMap = new HashMap<String, HashMap<String, Set<String>>>();
            HashMap<String, Set<Pair>> typeSameProperties = new HashMap<String, Set<Pair>> ();
            //types:
            for(String type: idToType)
            {
                Set<Pair> set = new HashSet<Pair>();
                typeSameProperties.put(type, set);
            }
                    
            int totalKeys = keys.size();
            System.out.println("totalKeys = " + totalKeys);
            int donek = 0;
            //for each entity in the index, look at posting list and see which properties are shared
            for(String entity: keys)
            {
                //Maps one property to set of similar ones
                HashMap<String, Set<String>> map11 = new HashMap<String, Set<String>>();
                Vector<ER> ll = index.get(entity);
                
                if(ll != null)
                    PropertyCombiner.combine(entity, ll, map11, idToEntity);
                
                //look at props:
                for(Map.Entry e: map11.entrySet())
                {
                    System.out.println("prop = " + e.getKey());
                    System.out.println("similar props = " + e.getValue());
                }
                
                donek++;
                if(donek % 1000 == 0)
                    System.out.println(donek + " done");
            }
            */
            
            
            
            
            //System.out.println("here 1");
            /*
            int totalTypes = typeToentity.keySet().size();
            int typesDone = 0;
            System.out.println("totalTypes to process = " + totalTypes);
            
            for(String type : typeToentity.keySet())
            {
                if(type.trim().equals(""))
                    continue;
                            
                //System.out.println("here 2");
                
                
                Set<String> typeEntities = typeToentity.get(type);
                //System.out.println("type = " + type + "   , has " + typeEntities.size() + " entries");
                //System.out.println(type);
                HashMap<String, Set<String>> map11 = new HashMap<String, Set<String>>();
                
                for(String typeEntity: typeEntities)
                {
                    //System.out.println(mountain + " ::");
                    //System.out.println("type entity = " + typeEntity);
                    Vector<ER> ll = index.get(typeEntity);

                    if(ll != null)
                        PropertyCombiner.combine(typeEntity, ll, map11, idToEntity);
                }

                bigTypePropMap.put(type, map11);
                System.out.println("type = " + type);        
                //for(Map.Entry e : map11.entrySet())
                {
                    //System.out.println("prop = " + e.getKey());
                    //System.out.println("matching props = " + e.getValue());
                }
                
                typesDone++;
                
                if(typesDone % 10 == 0)
                    System.out.println("Types done = " + typesDone);
            }
            */
            
            
            //System.out.println("");
            //System.out.println("");
            
            //Set<String> val = typeToentity.get("river");
            //for(String s: val)
            //    System.out.println("val = " + s);
            
            
            
            /*
            for(Map.Entry e : typeToentity.entrySet() )
            {
                String key = (String) e.getKey();
                Set<String> val = (Set<String>) e.getValue();
                //if (((Set<String>)e.getValue()).size() > 1)
                //    System.out.println(e.getKey() + " => " + e.getValue());
            }
            */
            
            //if(((String)e.getKey()).toLowerCase().equals("pakistan"))
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void handleRelation(String line)
    {
        //System.out.println(line);
        
        if(line.toLowerCase().contains("china") && line.toLowerCase().contains("gdp"))
            System.out.println(line);
        try
        {
            String tokens[] = line.split(ESCAPED_DELIM);
//System.out.println(line);
            //System.out.println("tokens len: " + tokens.length);
            
            //if(line.toLowerCase().contains("the blood of gods"))
            //    System.out.println(line);
            
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
                //System.out.println("img entity = " + entity);
                //get image via hash
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
            
            
            //if(type == null || type.trim().equals("") || type.length() >= 50 || type.length() < 3)
            //    return;
            
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

            //entity = entity.replaceAll("^[A-z0-9]", " ");
            entity = entity.replaceAll("\\}\\}", " ").trim();
            entity = LinkParser.parseLinkBox(entity);
//System.out.println("ok");            
            //if(entity.toLowerCase().contains("pakistan"))// && property.toLowerCase().equals("river"))
            //    System.out.println("-> "+ line);
//System.out.println("ok2");
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
                //System.out.println("-Added '" + entity + "' to list with id = " + entityId);
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
                //System.out.println("+Added '" + nextEntity + "' to list with id = " + nextEntityId);
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
            //System.out.println("index does NOT contain " + entity);
            return new Vector<String>();
        }
        //else
        //    System.out.println("index contains " + entity);
        Vector<ER> postingList = index.get(entity);
        
        Vector<String> results = new Vector<String>();
        for(ER er: postingList)
        {
            
            if(er.property.contains(property))
            {
                //System.out.println("property: " + er.property + ",  entity: " + idToEntity.get(er.nextEntity));
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
        //else
        //    System.out.println("index contains " + entity);
        Vector<ER> postingList = index.get(entity);
        Set<String> resultsSet = new HashSet<String>();
        //Vector<String> results = new Vector<String>();
        for(ER er: postingList)
        {
            String res = er.property + " :|: ";
            res += idToEntity.get(er.nextEntity);
            //System.out.println("property: " + er.property + ",  entity: " + idToEntity.get(er.nextEntity));
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
        
        Vector<String> x = index.searchEntityProperty("pakistan", "river");
        System.out.println("x = " + x);
        //index.preprocess("F:\\wikidumps\\clean\\infoboxes_cities\\", "cities");
        //index.preprocess("F:\\wikidumps\\clean\\infoboxes_countries\\", "countries");
        
        //String key = "pakistan".toLowerCase();
        
        //for(int i=0;i<index.idToEntity.size();i++)
        //    System.out.println("i = " + i + ",  entity = " + index.idToEntity.get(i));
        
        /*
        String key = "china".toLowerCase();
        String property = "mountain";
        System.out.println("key = " + key);
        if(index.index.containsKey(key))
        {
            Vector<ER> ll = index.index.get(key);
            for(ER er: ll)
            {
                if(er.property.toLowerCase().contains(property))
                {
                    System.out.print(key + " -> ");
                    System.out.println(er.property + " => " + index.idToEntity.get(er.nextEntity));
                    //System.out.println(er.property + " => " + er.nextEntity);
                    
                    if(index.index.containsKey(index.idToEntity.get(er.nextEntity)))
                    {
                        Vector<ER> ll2 = index.index.get(index.idToEntity.get(er.nextEntity));
                        for(ER er2: ll2)
                        {
                            System.out.println("\t\t" + er2.property + " => " + index.idToEntity.get(er2.nextEntity));
                        }
            
                    }
                    
                }
                
            }
        }
        else
        {
            System.out.println(key + " not in map");
        }
        */
        
        /*
        Vector<ER> ll = index.index.get("chenab river");
        for(ER er: ll)
        {
            System.out.println(er.property + " => " + index.idToEntity.get(er.nextEntity));
        }
        */
        /*
        key = "united states".toLowerCase();
        System.out.println("\nkey = " + key);
        if(index.index.containsKey(key))
        {
            Vector<ER> ll = index.index.get(key);
            for(ER er: ll)
            {
                System.out.println(er.relationship + " => " + er.entity + "   (by type " + er.nextEntityType + ", in article = " + er.article + ")");
            }
        }
        else
        {
            System.out.println(key + " not in map");
        }
        */
        /*
        System.out.println("");
        System.out.println("");
        
        String key2 = "city".toLowerCase();
        if(index.typeToentity.containsKey(key2))
        {
            Set<String> ll = index.typeToentity.get(key2);
            for(String ss: ll)
            {
                System.out.println("entity = " + ss);
            }
        }
        else
        {
            System.out.println(key2 + " not in map");
        }
        */
        
        /*
        index.processQuery("karachi");
        index.processQuery("lahore");
        index.processQuery("java");
        index.processQuery("hyderabad");
        index.processQuery("london");
        index.processQuery("new york");
        */
    }
    
    
    class ER
    {
        int nextEntity;
        String property;
        String prettyNextEntity;
        //String nextEntityType;
        //String article;

        //public ER(String property, String entity, String nextEntityType, String article)
        //public ER(String property, int nextEntity, String article)
        public ER(String property, int nextEntity)
        {
            this.nextEntity = nextEntity;
            this.property = property;
            //this.nextEntityType = nextEntityType;
            //this.article = article;
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
