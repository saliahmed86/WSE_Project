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
import wseproject.indexer.MonotonicModeller;
import wseproject.nlp.StanfordManager;
import wseproject.parser.ListTitleParser;
import wseproject.parser.objects.ListTitleFields;

/**
 * Take a string and see what it wants you to get First parse using simple regex
 *
 * @author Ali Local
 */
public class StructQueryProcessor
{

    //private String query;
    private String entity;
    private String props;
    private String order;

    public StructQueryProcessor(String entity, String props, String order)
    {
        this.entity = (entity == null) ? "" : entity;
        this.props = (props == null) ? "" : props;
        this.order = (order == null) ? "" : order;

        if (!this.props.isEmpty())
        {
            this.props = StanfordManager.getLemma(this.props);
        }
    }

    public String process()
    {
        Indexer indexer = Indexer.getIndexer();

        //for each constraint (split on "and" or "or") do:
        //  list_i = Indexer.search(entity = constraint, property = type)
        //  (this should return only related entities names)
        //find intersection of lists
        //then in intersection, look at jjs or order
        //apply jjs or order and print
        Set<String> resultSet = new HashSet<String>();
        if (!entity.trim().isEmpty() && !props.trim().isEmpty())
        {
            int combineType = 0;//means or
            String constraints[] = entity.split("\\s+and\\s+|\\s+or\\s+");
            if (entity.contains(" and "))
            {
                combineType = 1;
            }

            Vector<String> result = indexer.searchEntityProperty(constraints[0], props);
            System.out.println("result for " + constraints[0] + " = " + result);
            resultSet.addAll(result);
            for (int i = 1; i < constraints.length; i++)
            {
                String constraint = constraints[i];
                result = indexer.searchEntityProperty(constraint, props);
                System.out.println("result for " + constraint + " = " + result);
                if (combineType == 0)
                {
                    resultSet.addAll(result);
                } else
                {
                    resultSet.retainAll(result);
                }
            }
            System.out.println("resutlts = ");

            for (String res : resultSet)
            {
                System.out.println(res);
            }

        } else if (entity.trim().isEmpty() && !props.trim().isEmpty())
        {
            System.out.println("searching for type = " + props);
            Vector<String> result = indexer.searchType(props);
            resultSet = new HashSet<String>(result);
            System.out.println("resutlts = ");
            for (String res : result)
            {
                System.out.println(res);
            }

        } else if (!entity.trim().isEmpty() && props.trim().isEmpty())
        {
            indexer.searchEntitiy(entity);
        }

        //if there is no contraint, then get all entities of type "Type"
        //Indexer.getEntitesForType(type)
        return "";
    }

    public static void main(String[] args)
    {
        //StructQueryProcessor qp = new StructQueryProcessor("capital of pakistan");
        //qp.process();
    
        //QueryProcessor qp = new QueryProcessor("List of rivers of pakistan and india");
        //qp.process();
        //QueryProcessor qp2 = new QueryProcessor("List of mountains of pakistan and china");
        //qp2.process();
        //QueryProcessor qp3 = new QueryProcessor("List of rivers");
        //qp3.process();
        StructQueryProcessor qp = new StructQueryProcessor("pakistan", "capital", null);
        qp.process();
    
    }

}
