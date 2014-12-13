/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ali Local
 */
public class Regexer
{
    private static final String tableRegex = "(?s)\\{\\|(.*?)\\n\\|\\}\\n";
    private static final String linkBoxRegex = "(?:\\[\\[)(.*?)(?:\\]\\])";
    private static final String templateRegex = "(?:\\{\\{)(.*?)(?:\\}\\})";
    private static final String multilineTemplateRegex = "(?s)\\{\\{(.*?)\\}\\}";
    //private static final String title_OfIn_Regex = "^List of ([A-z0-9\\s]+?) (?:(\\bin\\b|\\bof\\b?) ([A-z0-9\\s]+)?){0,1}$";
    private static final String title_OfIn_Regex = "^List/NN of/IN (.+?) (?:(\\bin/IN\\b|\\bof/IN\\b|\\babout/IN\\b?) (.+)?){0,1}$";
    //private static final String title_By_Regex = "^List of ([A-z0-9\\s]+?) (?:(\\bby\\b?) ([A-z0-9\\s]+)){0,1}$";
    private static final String title_By_Regex = "^List/NN of/IN (.+?) (?:(\\bby/IN\\b?) (.+)/NN){0,1}$";
    private static final String title_ByNNP_Regex = "^List/NN of/IN (.+?) (?:(\\bby/IN\\b?) (.+)/NNP){0,1}$";
    //private static final String partTitle_By_Regex = "^([A-z0-9\\s]+?) (?:(\\bby\\b?) ([A-z0-9\\s]+))$";
    private static final String partTitle_By_Regex = "^(.+) (\\bby/IN\\b?) (.+)$";
    private static final String basicTitleRegex = "^List/NN of/IN (.+?)$";
    
    private static final String integerRegex = "";
        

    private static Pattern tablePattern;
    private static Pattern linkBoxPattern;
    private static Pattern templatePattern;
    private static Pattern multilineTemplatePattern;
    private static Pattern title_OfIn_Pattern;
    private static Pattern title_By_Pattern;
    private static Pattern title_ByNNP_Pattern;
    private static Pattern partTitle_By_Pattern;
    private static Pattern basicTitlePattern;

    private Regexer()
    {
        
    }
    
    public static Matcher matchTable(String text)
    {
        if(tablePattern == null)
            tablePattern = Pattern.compile(tableRegex);
        
        return tablePattern.matcher(text);
    }
    
    public static Matcher matchLinkBox(String text)
    {
        if(linkBoxPattern == null)
            linkBoxPattern = Pattern.compile(linkBoxRegex);
        
        return linkBoxPattern.matcher(text);
    }

    public static Matcher matchTemplate(String text)
    {
        if(templatePattern == null)
            templatePattern = Pattern.compile(templateRegex);
        
        return templatePattern.matcher(text);
    }
    
    public static Matcher matchMultilineTemplate(String text)
    {
        if(multilineTemplatePattern == null)
            multilineTemplatePattern = Pattern.compile(multilineTemplateRegex);
        
        return multilineTemplatePattern.matcher(text);
    }
    
    public static Matcher matchTitle_OfIn(String text)
    {
        if(title_OfIn_Pattern == null)
            title_OfIn_Pattern = Pattern.compile(title_OfIn_Regex);
        
        return title_OfIn_Pattern.matcher(text);
    }
    
    public static Matcher matchTitle_By(String text)
    {
        if(title_By_Pattern == null)
            title_By_Pattern = Pattern.compile(title_By_Regex);
        
        return title_By_Pattern.matcher(text);
    }

    public static Matcher matchTitle_ByNNP(String text)
    {
        if(title_ByNNP_Pattern == null)
            title_ByNNP_Pattern = Pattern.compile(title_ByNNP_Regex);
        
        return title_ByNNP_Pattern.matcher(text);
    }
    
    public static Matcher matchPartTitle_By(String text)
    {
        if(partTitle_By_Pattern == null)
            partTitle_By_Pattern = Pattern.compile(partTitle_By_Regex);
        
        return partTitle_By_Pattern.matcher(text);
    }
    
    public static Matcher matchBasicTitle(String text)
    {
        if(basicTitlePattern == null)
            basicTitlePattern = Pattern.compile(basicTitleRegex);
        
        return basicTitlePattern.matcher(text);
    }
    

    public static void main(String[] args)
    {
        
        
        //Matcher mByNNP = Regexer.matchTitle_ByNNP("List/NN of/IN countries/NNS by/IN population/NN");
        Matcher mByNNP = Regexer.matchTitle_ByNNP("List/NN of/IN works/NNS by/IN Pierre/NNP Schaeffer/NNP");
        if(mByNNP.find())
        {
            for(int i=1;i<= mByNNP.groupCount();i++)
                System.out.println("x0 = " +  mByNNP.group(i) + "!");
        }
        
        
        
        if(true) return;
        //List of (<NP>) (of|in <NP>) (by <NP>)
        String regex2 = "^([A-z0-9\\s]+?) (?:(\\bby\\b?) ([A-z0-9\\s]+))$";
        String s1 = "Pakistan by poplation";
        
        Pattern p = Pattern.compile(regex2);
        Matcher m = p.matcher(s1);
        if(m.find())
            for(int i=1;i<=m.groupCount();i++)
                System.out.println("x1 = " + m.group(i));
        
        /*
        //List of (<NP>) (of|in <NP>) (by <NP>)
        String regex1 = "^List of ([A-z0-9\\s]+?) (?:(\\bin\\b|\\bof\\b?) ([A-z0-9\\s]+)?){0,1}$";
        String regex2 = "^List of ([A-z0-9\\s]+?) (?:(\\bby\\b?) ([A-z0-9\\s]+)){0,1}$";
        String s1 = "List of cities in Pakistan by poplation";
        String s2 = "List of cities by poplation";
        
        String s = s2;
        
        Pattern p = Pattern.compile(regex1);
        Matcher m = p.matcher(s);
        if(m.find())
            for(int i=1;i<=m.groupCount();i++)
                System.out.println("x1 = " + m.group(i));
        
        System.out.println("");
        Pattern p2 = Pattern.compile(regex2);
        Matcher m2 = p2.matcher(s);
        if(m2.find())
            for(int i=1;i<=m2.groupCount();i++)
                System.out.println("x2 = " + m2.group(i));

        */
        
        
    }
}
