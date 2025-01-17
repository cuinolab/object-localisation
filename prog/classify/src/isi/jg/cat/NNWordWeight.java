package isi.jg.cat;



import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;

/** classe pour stocker la pond�ration du r�seau pour une classe et pour un document
  * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI
 * toute autre utilisation est sujette � autorisation
 *
*/



class NNWordWeight{
    
    int[] wordOfDoc;
    float[] weightOfWord;
    int lastused,doc,group;
    
    NNWordWeight(int maxlength,int _doc, int _group){
        wordOfDoc= new int[maxlength];
        weightOfWord=new float[maxlength];
        lastused=0;
        doc=_doc;
        group=_group;
    }
    
    void add(int word, float weight){
        wordOfDoc[lastused]=word;
        weightOfWord[lastused]=weight;
        lastused++;
    }
    
    void displayXML(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.println("<MR:keywords>"+glue.getStringforW(wordOfDoc[j])+"</MR:keywords>");
            //System.out.println(doc+","+group+","+glue.getTermOfW(wordOfDoc[j])+","+((int)weightOfWord[j]));
        }
    }
    
    void displayTXT(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.print(","+glue.getStringforW(wordOfDoc[j]));
        }
    }
    
    void displayTXTDetail(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.println(glue.getStringforW(wordOfDoc[j])+
                    ", "+glue.getOccCorpusOfW(wordOfDoc[j])+
                    ", "+weightOfWord[j]
                    );
        }
    }
    
    void display(IdxStructure glue){
        for(int i=0;i<lastused;i++){
            System.out.println(doc+","+group+","+glue.getStringforW(wordOfDoc[i])+","+((int)weightOfWord[i]));
        }
    }
    
} // end class


