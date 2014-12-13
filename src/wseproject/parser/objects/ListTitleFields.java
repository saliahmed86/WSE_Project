/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser.objects;

/**
 *
 * @author Ali Local
 */
public class ListTitleFields
    {
        private String type;
        private String constraint;
        private String order;
        private String jjs;

        public ListTitleFields(String type, String constraint, String order, String jjs)
        {
            this.type = type;
            this.constraint = constraint;
            this.order = order;
            this.jjs = jjs;
        }
        
        
        public String getType()
        {
            if(type == null)
                return "";
            return type;
        }

        public String getConstraint()
        {
            if(constraint == null)
                return "";
            return constraint;
        }

        public String getOrder()
        {
            if(order == null)
                return "";
            return order;
        }

        public String getJjs()
        {
            if(jjs == null)
                return "";
            return jjs;
        }
        
        
        
        
    }