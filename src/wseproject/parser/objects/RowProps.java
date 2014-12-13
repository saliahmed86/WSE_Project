/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser.objects;

import java.util.HashMap;

/**
 *
 * @author Ali Local
 */
public class RowProps
    {
        private String entity;
        private HashMap<String, String> props;  //property name, property value
        private String entityType;

        public RowProps(String entity)
        {
            this.entity = entity;
            this.props = new HashMap<String, String>();
        }

        public RowProps(String entity, String entityType)
        {
            this.entity = entity;
            this.entityType = entityType;
            this.props = new HashMap<String, String>();
        }

        public String getEntityType()
        {
            return entityType;
        }

        public void setEntityType(String entityType)
        {
            this.entityType = entityType;
        }
        
        

        public void addProperty(String property, String value)
        {
            if(!props.containsKey(property))
                props.put(property, value);
            else
            {
                String currVal = props.get(property);
                props.put(property, currVal + " ! " + value);
            }   
        }
        
        public String getEntity()
        {
            return entity;
        }

        public void setEntity(String entity)
        {
            this.entity = entity;
        }

        public HashMap<String, String> getProps()
        {
            return props;
        }

        public void setProps(HashMap<String, String> props)
        {
            this.props = props;
        }
        
        
    }