/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.parser;

import wseproject.parser.objects.ListTitleFields;
import java.util.Vector;
import java.util.regex.Matcher;
import wseproject.nlp.Regexer;
import wseproject.nlp.StanfordManager;

/**
 *
 * @author Ali Local
 */
public class ListTitleParser
{

    String title;

    private String stripPOSTag(String tokens)
    {
        if(tokens == null)
            return null;
        //System.out.println("got token = " + tokens);
        String ss[] = tokens.split("\\s+");
        String newStr = "";
        for(String token : ss)
            newStr += (token.contains("/") ? token.substring(0,token.lastIndexOf("/")) : token)  + " ";
        return newStr.trim();
                    
    }
    
    private String stripPOSTagAndLemmatize(String tokens)
    {
        if(tokens == null)
            return null;
        //System.out.println("got token = " + tokens);
        String ss[] = tokens.split("\\s+");
        String newStr = "";
        for(String token : ss)
        {
            token = token.contains("/") ? token.substring(0,token.lastIndexOf("/")) : token;
            token = StanfordManager.getLemma(token);
            newStr += token + " ";
        }
        return newStr.trim();
                    
    }
    
    public void setTaggedTitle(String pgTitle)
    {
        Vector<String> titleTags = StanfordManager.getPOSTags(pgTitle);
        String taggedTitle = "";
        for(String token: titleTags) taggedTitle += token + " ";
        taggedTitle = taggedTitle.trim();
        
        this.title = taggedTitle;

    }
    
    public ListTitleParser(String title)
    {
        this.title = title;
    }
    
    public ListTitleFields parseTitle() //throws NotAListArticleException
    {
        Matcher basicMatcher = Regexer.matchBasicTitle(title);
        
        if(!basicMatcher.find())
            //throw new NotAListArticleException(title);       //means there is no "list of";
            return null;
        
        String type = null;
        String constraint = null;
        String order = null;
        
        
        Matcher mByNNP = Regexer.matchTitle_ByNNP(title);
        if(mByNNP.find())
        {
            /*
            for(int i=1;i<= mByNNP.groupCount();i++)
                System.out.println("x0 = " +  mByNNP.group(i) + "!");
            */
            //type = stripPOSTag(mByNNP.group(1));
            type = mByNNP.group(1);
            constraint = stripPOSTag(mByNNP.group(3));
            return new ListTitleFields(type, constraint, order, null);
        }
        
        Matcher mBy = Regexer.matchTitle_By(title);
        Matcher mInOf = Regexer.matchTitle_OfIn(title);
        
        //NEED TO STORE IN ADVANCE INSTEAD WRITING find() in if/else if, because it iterates
        boolean mByFind = mBy.find();
        boolean mInOfFind = mInOf.find();
        if(mByFind && mInOfFind)
        {
            //for(int i=1;i<=mInOf.groupCount();i++)
            //    System.out.println("x0 = " + mInOf.group(i) + "!");
            //System.out.println("part1 = " + mInOf.group(1));
            //System.out.println("part1 = " + mInOf.group(2));
            //System.out.println("part1 = " + mInOf.group(3));
            
            String pp = mInOf.group(3);
            Matcher mPartBy = Regexer.matchPartTitle_By(pp);
            //need to call find() to increment iterator
            if(!mPartBy.find())  //shouldn't have come this far if there was no "by"
                //throw new NotAListArticleException("Some thing weird!!");
                return null;
            
            
            //for(int i=1;i<=mPartBy.groupCount();i++)
            //    System.out.println("part2 = " + mPartBy.group(i));
            
            //type = stripPOSTag(mInOf.group(1));
            type = mInOf.group(1);
            constraint = stripPOSTag(mPartBy.group(1));
            order = stripPOSTag(mPartBy.group(3));
            
            
        }
        else if(mByFind)
        {
            //for(int i=1;i<=mBy.groupCount();i++)
            //    System.out.println("x2 = " + mBy.group(i));
            
            //type = stripPOSTag(mBy.group(1));
            type = mBy.group(1);
            order = stripPOSTag(mBy.group(3));
            
        }
        else if(mInOfFind)
        {
            //for(int i=1;i<=mInOf.groupCount();i++)
            //    System.out.println("x3 = " + mInOf.group(i));
            
            //type = stripPOSTag(mInOf.group(1));
            type = mInOf.group(1);
            constraint = stripPOSTag(mInOf.group(3));
        }
        else
        {
            //type = stripPOSTag(basicMatcher.group(1));
            type = basicMatcher.group(1);
        }
        
        //clean up "type", into nouns and adjectives
        String typeArr[] = type.split("\\s+");
        String jjs = null;
        String typeNouns = "";
        for(String typeToken : typeArr)
        {
            if(typeToken.contains("/JJS"))
                jjs = typeToken;
            else if(typeToken.contains("/NN"))
                typeNouns += typeToken + " ";
        }
        typeNouns = stripPOSTagAndLemmatize(typeNouns.trim());
        jjs = stripPOSTag(jjs);
        
        return new ListTitleFields(typeNouns, constraint, order, jjs);
        
    }

            
}
