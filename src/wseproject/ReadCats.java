package wseproject;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ali Local
 */
public class ReadCats
{

    private HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
    public ReadCats()
    {
    }

    public void readCats()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\bigCat.dat\\bigCat.dat")));
            String line = null;
            int i = 0;
            int min = 100;
            while((line = br.readLine())!= null)
            {
                i++;
                
                if(i%100000 == 0)
                    System.out.println(i + " done");
                
                String tokens[] = line.split("\t");
                String title = URLDecoder.decode(tokens[0], "utf-8") ;
                
                if(title.contains("Category:"))
                {
                    Vector<String> cats = new Vector<String>();
                    
                    for(int j=1;j<tokens.length;j++)
                    {
                        //System.out.print(tokens[j] + "\t");
                        String cat = URLDecoder.decode(tokens[j], "utf-8") ;
                        cats.add(cat);
                    }
                    map.put(title, cats);
                    
                    
                    /*
                    System.out.print(tokens[0] + "  ::  ");
                    System.out.println(title);
                    */
                }
                
            }
            System.out.println("i = " + i);
            System.out.println("min = " + min);
            
            CatTree tree = new CatTree(map);
            System.out.println("Computed. Now saving file.");
            try 
            {
                ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("tree_adjList"));
                writer.writeObject(map);
                System.out.println("Saved file.");
            }  
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        catch(Exception e)
        {
            
        }

    }
    
    
    public void testCats()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File("F:\\wikidumps\\bigCat.dat\\bigCat.dat")));
            String line = null;
            int i = 0;
            int min = 100;
            while((line = br.readLine())!= null)
            {
                String tokens[] = line.split("\t");
                String title = URLDecoder.decode(tokens[0], "utf-8") ;
                
                if( title.equals("Category:Society-related lists") || 
                    title.equals("Category:Lists of people") ||
                    title.equals("Category:Society") ||
                    title.equals("Category:Lists")
                    )
                {
                    int c = 0;
                    for(int j=1;j<tokens.length;j++)
                    {
                        //if(tokens[j].contains("Category%3A"))
                        //    c++;
                    }
                    if(c == 0)
                        System.out.println(line);
                }
                
            }
            System.out.println("i = " + i);
            System.out.println("min = " + min);
        }
        catch(Exception e)
        {
            
        }

    }
    
    
    public static void main(String[] args)
    {
        ReadCats rc = new ReadCats();
        rc.readCats();
        //rc.testCats();
                 
    }
    
    
    class CatTree implements Serializable
    {
        public HashMap<String, Vector<String>> map;

        public CatTree(HashMap<String, Vector<String>> map)
        {
            this.map = map;
        }
        
        
    }
}
