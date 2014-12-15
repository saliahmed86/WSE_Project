/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.query;

/**
 *
 * @author Ali Local
 */
public class QueryReturnObj
{
    int type;
    Object obj;
    String imagePath;
    
    public QueryReturnObj(int type, Object obj)
    {
        this.type = type;
        this.obj = obj;
    }
    
    
}
