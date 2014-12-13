/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.query;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import wseproject.indexer.Indexer;
import wseproject.parser.ListTitleParser;
import wseproject.parser.objects.ListTitleFields;

/**
 * Take a string and see what it wants you to get
 * First parse using simple regex
 *
 * @author Ali Local
 */
public class QueryProcessor
{
    private String query;

    public QueryProcessor(String query)
    {
        this.query = query;
    }
    
    public void process()
    {
        Indexer indexer = Indexer.getIndexer();
        
        
        ListTitleParser ltp  = new ListTitleParser(null);
        ltp.setTaggedTitle(query);
        ListTitleFields fields = ltp.parseTitle();
        
        System.out.println("type = " + fields.getType());
        System.out.println("constraint = " + fields.getConstraint());
        System.out.println("jjs = " + fields.getJjs());
        System.out.println("order = " + fields.getOrder());
        
        
        //for each constraint (split on "and" or "or") do:
        //  list_i = Indexer.search(entity = constraint, property = type)
        //  (this should return only related entities names)
        //find intersection of lists
        //then in intersection, look at jjs or order
        //apply jjs or order and print
        Set<String> resultSet = new HashSet<String>();
        if(!fields.getConstraint().trim().isEmpty() && !fields.getType().trim().isEmpty() )
        {
            String constraints[] = fields.getConstraint().split(",|\\sand\\s|\\sor\\s");
            for(String constraint : constraints)
            {
                Vector<String> result = indexer.searchEntityProperty(constraint, fields.getType());
                System.out.println("result for " + constraint  + " = " + result);
                if(resultSet.isEmpty())
                    resultSet.addAll(result);
                else
                    resultSet.retainAll(result);
            }
            System.out.println("resutlts = ");
            for(String res: resultSet)
            {
                System.out.println(res);
            }
        }
        
        if(fields.getConstraint().trim().isEmpty() && !fields.getType().trim().isEmpty())
        {
            System.out.println("searching for type = " + fields.getType());
            Vector<String> result = indexer.searchType(fields.getType());
            System.out.println("resutlts = ");
            for(String res: result)
            {
                System.out.println(res);
            }
        }
        
        
        //if there is no contraint, then get all entities of type "Type"
        //Indexer.getEntitesForType(type)
    }
    
    public static void main(String[] args)
    {
        QueryProcessor qp = new QueryProcessor("List of rivers of pakistan and india");
        qp.process();
        
        QueryProcessor qp2 = new QueryProcessor("List of mountains of pakistan and china");
        qp2.process();

        //QueryProcessor qp3 = new QueryProcessor("List of rivers");
        //qp3.process();
    }
        
}
