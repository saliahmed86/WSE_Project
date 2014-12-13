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
public class NotAListArticleException extends Exception
    {
        public NotAListArticleException(String message)
        {
            super("Not a list article: " + message);
        }
    }