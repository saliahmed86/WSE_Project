/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import wseproject.indexer.Indexer;
import wseproject.nlp.StanfordManager;

/**
 *
 * @author Ali Local
 */
public class KGServer
{

    private static void startServing() throws IOException, ClassNotFoundException
    {
        // Create the handler and its associated indexer.
        Indexer indexer = Indexer.getIndexer();
        //Start Stanford
        StanfordManager.getPipeline();
        
        //indexer.loadIndex();
        QueryHandler handler = new QueryHandler(indexer);

        // Establish the serving environment
        InetSocketAddress addr = new InetSocketAddress(25816);
        HttpServer server = HttpServer.create(addr, -1);
        server.createContext("/", handler);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println(
                "Listening on port: " + Integer.toString(25816));
    }

    public static void main(String[] args)
    {
        try
        {
            KGServer.startServing();
        }
        catch(Exception e)
        {
            System.out.println("Error starting server");
            e.printStackTrace();
        }
                
    }

}
