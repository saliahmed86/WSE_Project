/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Scanner;
import wseproject.indexer.Indexer;
import wseproject.query.QueryProcessor;
import wseproject.query.StructQueryProcessor;

/**
 *
 * @author Ali Local
 */
class QueryHandler implements HttpHandler
{

    public static class CgiArguments
    {

        // The raw user query

        public String _query = "";
        public String _entity = "";
        public String _property = "";
        public String _order = "";
        
        // The output format.
        public enum OutputFormat
        {

            TEXT,
            HTML,
        }
        public OutputFormat _outputFormat = OutputFormat.TEXT;

        public CgiArguments(String uriQuery)
        {
            String[] params = uriQuery.split("&");
            for (String param : params)
            {
                String[] keyval = param.split("=", 2);
                if (keyval.length < 2)
                {
                    continue;
                }
                String key = keyval[0].toLowerCase();
                String val = keyval[1];
                if (key.equals("query"))
                {
                    _query = val;
                } 
                else if(key.equals("entity"))
                {
                    _entity = val;
                }
                else if(key.equals("property"))
                {
                    _property = val;
                }
                else if(key.equals("order"))
                {
                    _order = val;
                }
                
                else if (key.equals("format"))
                {
                    try
                    {
                        _outputFormat = OutputFormat.valueOf(val.toUpperCase());
                    } catch (IllegalArgumentException e)
                    {
                        // Ignored, search engine should never fail upon invalid user input.
                    }
                }
            }  // End of iterating over params
        }
    }

    private Indexer indexer;

    public QueryHandler(Indexer indexer)
    {
        this.indexer = indexer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String requestMethod = exchange.getRequestMethod();
        if (!requestMethod.equalsIgnoreCase("GET"))
        { // GET requests only.
            return;
        }

        // Print the user request header.
        Headers requestHeaders = exchange.getRequestHeaders();
        System.out.print("Incoming request: ");
        for (String key : requestHeaders.keySet())
        {
            System.out.print(key + ":" + requestHeaders.get(key) + "; ");
        }
        System.out.println();

        // Validate the incoming request.
        String uriQuery = exchange.getRequestURI().getQuery();
        String uriPath = exchange.getRequestURI().getPath();
        if (uriPath == null || uriQuery == null)
        {
            //respondWithMsg(exchange, "Something wrong with the URI!");
        }
        //if (uriPath.equals("/search"))
        {
            //respondWithMsg(exchange, "Only /search is handled!");
        }
        //if (uriPath == null || uriPath.equals("") || uriPath.equals("/"))
        if (uriPath.equals("/knowledge"))
        {
            try
            {
                File f = new File("data/web/index.html");
                String content = new Scanner(f).useDelimiter("\\Z").next();
                respondWithMsg(exchange, content, "text/html");
            }
            catch(Exception e)
            {
                respondWithMsg(exchange, "Whoops, something went wrong!", "text/html");
            }
        }
        
        if (uriPath.equals("/jquery"))
        {
            try
            {
                File f = new File("data/web/jquery-1.7.1.min.js");
                String content = new Scanner(f).useDelimiter("\\Z").next();
                respondWithMsg(exchange, content, "text/javascript");
            }
            catch(Exception e)
            {
                respondWithMsg(exchange, "Whoops, something went wrong!", "text/html");
            }
        }
        
        System.out.println("Query: " + uriQuery);

        // Process the CGI arguments.
        CgiArguments cgiArgs = new CgiArguments(uriQuery);
        if (!cgiArgs._query.isEmpty())
        {
            //respondWithMsg(exchange, "No query is given!");
            String query = URLDecoder.decode(cgiArgs._query, "utf-8");
            System.out.println("sending \"" + query + "\" to QueryProcessor");
            QueryProcessor qp = new QueryProcessor(query);
            qp.process();
        }
        else if (!cgiArgs._entity.isEmpty() || !cgiArgs._property.isEmpty())
        {
            String entity = URLDecoder.decode(cgiArgs._entity, "utf-8");
            String property = URLDecoder.decode(cgiArgs._property, "utf-8");
            String order = URLDecoder.decode(cgiArgs._order, "utf-8");
            //System.out.println("sending \"" + query + "\" to QueryProcessor");
            StructQueryProcessor qp = new StructQueryProcessor(entity, property, order);
            String result = qp.process();
            
            respondWithMsg(exchange, result, "application/json");
        }
        else
        {
            //respondWithMsg(exchange, "No query is given!");
            //get page they are looking for
        }
        

    }

    private void respondWithMsg(HttpExchange exchange, final String message, String type)
            throws IOException
    {
        Headers responseHeaders = exchange.getResponseHeaders();
        //responseHeaders.set("Content-Type", "text/html");
        responseHeaders.set("Content-Type", type);
        exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(message.getBytes());
        responseBody.close();
    }

}
