/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wseproject.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 *
 * @author Ali Local
 */
public class StanfordManager
{
    private static StanfordCoreNLP pipeline = getPipeline();// = new StanfordCoreNLP();
    
    private StanfordManager()
    {
        //return new StanfordCoreNLP(props);
        
    }
    
    public static StanfordCoreNLP getPipeline()
    {
        
        if(pipeline == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, parse, lemma");
     
            pipeline = new StanfordCoreNLP(props);
        }
        return pipeline;
    }
    
    public static Vector<String> getPOSTags(String input)
    {
        Annotation annotation;
        annotation = new Annotation(input);
        getPipeline().annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        if(sentences != null && sentences.size() > 0)
        {
            Vector<String> tags = new Vector<String>();
            CoreMap sentence = sentences.get(0);
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                
                String result = word;
                result += "/";
                result += pos;
                tags.add(result);
            }
            return tags;

        }
        return null;
    }
    
    public static String getLemma(String plural)
    {
        Annotation annotation;
        annotation = new Annotation(plural);
        getPipeline().annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        String lemma = "";
        if(sentences != null && sentences.size() > 0)
        {
            CoreMap sentence = sentences.get(0);
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String singular = token.get(CoreAnnotations.LemmaAnnotation.class);
                lemma += singular + " ";
                
            }
            return lemma.trim();
        }
        return null;
    }
    
    //public static annotate()
    
    private static void printTree(Tree tree, int d)
    {
        for(int i=0;i<d;i++) System.out.print("--");
        System.out.println(tree.nodeString());
        Tree[] children = tree.children();
        for(Tree child: children)
        {
            //System.out.println("str = " + child.nodeString());
            printTree(child, d+1);
        }     
    }
    
}
