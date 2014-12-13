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
public class ColRowSpanException extends Exception
    {

        public ColRowSpanException(String message)
        {
            super("Table has row or col span: " + message);
        }
        
    }
