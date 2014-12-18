/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.query;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
        
        this.entity = this.entity.toLowerCase();
        
        //if(this.entity.matches("\\bmovies\\b"))
        if(this.props.contains("movies"))
        {
            System.out.println("yes movies");
            this.props = this.props.replaceAll("movies", "films");
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
                
                //PropertyCombiner.combine(typeEntity, ll, map11, idToEntity);
            }
            
            Vector<QueryResEntity> qreList_img = new Vector<QueryResEntity>();
            Vector<QueryResEntity> qreList_noimg = new Vector<QueryResEntity>();
            for(String res: resultSet)
            {
                String imgPath = indexer.getImagePath(res);
                if(imgPath.equals(""))
                    qreList_noimg.add( new QueryResEntity(res,imgPath , ""));
                else
                    qreList_img.add( new QueryResEntity(res,imgPath , ""));
            }
            Vector<QueryResEntity> qreList = new Vector<QueryResEntity>(qreList_img);
            qreList.addAll(qreList_noimg);
            
            
            QueryReturnObj o = new QueryReturnObj(1, qreList);
            Gson gson = new Gson();
            
            String json = gson.toJson(o);
            return json;

        }
        else if (entity.trim().isEmpty() && !props.trim().isEmpty())
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
            StringBuilder sb = new StringBuilder();
            
            //System.out.println("here 1");
            resultSet = indexer.searchEntitiy(entity);
            //System.out.println("here 2");
            HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>> ();
            Vector<String> keyVec = new Vector<String>();
            //System.out.println("here 3");
            
            
            String imgPath = indexer.getImagePath(entity.toLowerCase());
            
            for(String s: resultSet)
            {
                //System.out.println(s);
                String ss[] = s.split("\\:\\|\\:");
                if(ss.length != 2)
                    continue;
                ss[0] = ss[0].replaceAll("\\|", "").replaceAll("_", " ").trim();
                ss[1] = ss[1].trim();
                if(!map.containsKey(ss[0]))
                {
                    map.put(ss[0], new Vector<String>());
                    keyVec.add(ss[0]);
                }
                //System.out.println("here 5");
                map.get(ss[0]).add(ss[1]);
            }
            
            Collections.sort(keyVec, new Comparator()
            {
                @Override
                public int compare(Object o1, Object o2)
                {
                    //return map.get((String) o1).size() - map.get((String) o2).size();
                    return ((String) o1).compareTo((String) o2);
                    
                }
            
            });
            
            //Vector<Vector<String>> vals = new Vector<Vector<String>> ();
            Vector<QueryResObj> queryRes = new Vector<QueryResObj>();
            ImportantTest impTest = new ImportantTest(entity, indexer);
            
            for(String s: keyVec)
            {
                Vector<String> vals__ = new Vector<String>();
                
                //System.out.println(s + " : ");
                sb.append(s).append(" : ").append("\n");
                for(String val: map.get(s))
                {
                    //System.out.println("\t" + val);
                    sb.append("\t").append(" : ").append(val).append("\n");
                    vals__.add(val);
                }
                //vals.add(vals__);
                
                QueryResObj resObj = new QueryResObj(s, vals__, (impTest.isImportant(s))?1:0 );
                queryRes.add(resObj);
            }
            
            QueryReturnObj o = new QueryReturnObj(2, queryRes);
            if(!imgPath.equals(""))
            {
                o.imagePath = imgPath;
                //System.out.println("pakistan has image");
            }
            else
            {
                System.out.println("pakistan has no image");
            }

            Gson gson = new Gson();
            String json = gson.toJson(o);
            //Now show important ones. which are those? maybe thos with less number of entities
            
            //return sb.toString();
            return json;
        }

        //if there is no contraint, then get all entities of type "Type"
        //Indexer.getEntitesForType(type)
        return "";
    }


}
