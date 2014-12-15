/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.query;

import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class QueryResObj
{
    String key;
    Vector<String> vals;
    int important;
    
    
    
    /*
    Vector<String> keys;
    Vector<Vector<String>> vals;

    public QueryResObj(Vector<String> keys, Vector<Vector<String>> vals)
    {
        this.keys = keys;
        this.vals = vals;
    }
    */

    public QueryResObj(String key, Vector<String> vals, int important)
    {
        this.key = key;
        this.vals = vals;
        this.important = important;
    }
    
    
    
}
