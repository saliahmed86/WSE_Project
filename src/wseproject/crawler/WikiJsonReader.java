/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.crawler;

import java.net.*;
import java.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import wseproject.Options;
import wseproject.parser.LinkParser;

public class WikiJsonReader
{

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        InputStream is = new URL(url).openStream();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String input;
            StringBuilder sb = new StringBuilder();
            while((input = br.readLine()) != null)
                sb.append(input);
            
            JSONObject json = new JSONObject(sb.toString());
            return json;
        } 
        catch(Exception e)
        {
            System.out.println("Failed to download json for: " + url);
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        finally
        {
            is.close();
        }
    }

    //List_of_tallest_buildings_and_structures_in_London
    public static String getWikiText(String article) 
    {
        return getWikiText(article, true);
    }
    
    public static String getWikiText(String article, boolean useCached) 
    {
        article = article.replace(" ", "_");
        try
        {
            article = URLEncoder.encode(article.trim(), "utf-8");
            //System.out.println("article = " + article);
        }catch(Exception e)
        {
            return null;
        }
        if(useCached)
        {
            try
            {
                String cachePath = Options.cachePath;
                String fileName = article;//URLEncoder.encode(article.trim(), "utf-8");
                String filepath = Paths.get(cachePath, fileName).toString();
                File f = new File(filepath);
                if(f.exists())
                {
                    String content = new Scanner(f).useDelimiter("\\Z").next();
                    //System.out.println("found in cache");
                    return content;
                }
            }
            catch(Exception e)
            {
                
            }
        }
        
        //System.out.println("Read and loaded JSON for: " + article);
        String srcURL = "http://en.wikipedia.org/w/api.php?action=query&prop=revisions&"
                + "rvprop=content&format=json&titles=" + article;
        
        //System.out.println(srcURL);
        try
        {
            JSONObject json = readJsonFromUrl(srcURL);
            //System.out.println(json.toString());
            JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
            Iterator i = pages.keys();
            while (i.hasNext())
            {
                String key = (String) i.next();
                JSONObject page = pages.getJSONObject(key);
                JSONObject rev0 = page.getJSONArray("revisions").getJSONObject(0);
                String text = rev0.getString("*");
                //System.out.println("Read and loaded JSON for: " + article);
                String t = text.toString();
                //System.out.println(t);
                
                if(t.trim().toLowerCase().startsWith("#redirect"))
                {
                    return getWikiText(LinkParser.parseLinkBox(t));
                }
                else
                {
                    saveFile(article, text);
                    //System.out.println("downloaded " + article);
                    return t;
                }
            }
            
        }
        catch(Exception e)
        {
            System.out.println("Failed to load JSON for: " + article);
            //e.printStackTrace();
        }
        return null;
    }
    
    public static void saveFile(String article, String contents)
    {
        
        try
        {
            String cachePath = Options.cachePath;
            //String fileName = URLEncoder.encode(article.trim(), "utf-8");
            String fileName = article; // this should already be encoded;
            String filepath = Paths.get(cachePath, fileName).toString();
            BufferedWriter br = null;
            br = new BufferedWriter(new FileWriter(new File(filepath)));
            
            br.write(contents);
            br.close();
            
        }
        catch(Exception e)
        {
            
        }
    }
    
    public static Vector<String> getWikiCategoryText(String article) 
    {
        //System.out.println("Read and loaded JSON for: " + article);
        article = article.replace(" ", "_");
        String srcURL = "http://en.wikipedia.org/w/api.php?action=query&format=json&list=categorymembers&cmlimit=500&cmtitle="
                            + article;
        
        Vector<String> vec = new Vector<String> ();
        
        System.out.println(srcURL);
        try
        {
            JSONObject json = readJsonFromUrl(srcURL);
            //System.out.println(json.toString());
            JSONArray pages = json.getJSONObject("query").getJSONArray("categorymembers");
            
            for(int i=0;i<pages.length();i++)
            {
                JSONObject elem = (JSONObject)pages.get(i);
                //JSONObject page = pages.getJSONObject(key);
                //JSONObject rev0 = page.getJSONArray("revisions").getJSONObject(0);
                //String text = rev0.getString("*");
                //System.out.println("Read and loaded JSON for: " + article);
                //String t = text.toString();
                //System.out.println("elem = " + elem.toString());
                //System.out.println("key = " + elem.getString("title"));
                vec.add(elem.getString("title"));
                //System.out.println(elem.getString("title"));
                //System.out.println(t);
                /*
                if(t.trim().toLowerCase().startsWith("#redirect"))
                {
                    return getWikiText(LinkParser.parseLinkBox(t));
                }
                else
                    return t;
                */
                
            }
            return vec;
            
        }
        catch(Exception e)
        {
            System.out.println("Failed to load JSON for: " + article);
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static Vector<String> getWikiTextMultiple(Vector<String> articles) 
    {
        //System.out.println("Read and loaded JSON for: " + article);
        String srcURL = "http://en.wikipedia.org/w/api.php?action=query&prop=revisions&"
                + "rvprop=content&format=json&titles=" ;
        int ai;
        for(ai=0; ai<articles.size()-1;ai++)
            srcURL += articles.get(ai).replace(" ", "_") + "|";
        srcURL += articles.get(ai);
        
        Vector<String> results = new Vector<String>();
        try
        {
            JSONObject json = readJsonFromUrl(srcURL);
            JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
            Iterator i = pages.keys();
            while (i.hasNext())
            {
                String key = (String) i.next();
                JSONObject page = pages.getJSONObject(key);
                JSONObject rev0 = page.getJSONArray("revisions").getJSONObject(0);
                String text = rev0.getString("*");
                //System.out.println("Read and loaded JSON for: " + article);
                String t = text.toString();
                System.out.println(t);
                results.add(t);
            }
            return results;
            
        }
        catch(Exception e)
        {
            System.out.println("Failed to load JSON for: " + articles);
        }
        return null;
    }

    
    
}
