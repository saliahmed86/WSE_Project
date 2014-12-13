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
public class MonotonicLog
    {
        String columnName;
        int dir;    //=1 increasing,   -1 decreasing

        public MonotonicLog(String columnName, int dir)
        {
            this.columnName = columnName;
            this.dir = dir;
        }

    public String getColumnName()
    {
        return columnName;
    }

    public int getDir()
    {
        return dir;
    }
        
        

    }