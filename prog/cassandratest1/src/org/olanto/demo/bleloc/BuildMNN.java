package org.olanto.demo.bleloc;

import org.olanto.util.Timer;
import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.NNBuildTree;
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import static org.olanto.cat.GetProp.*;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test de la construction de réseaux de neurone
 */
public class BuildMNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        String signature = getSignature(SomeConstant.ROOTDIR+"/SIMPLE_CLASS/config/Identification.properties");

        id=new IdxStructure("QUERY",new ConfigurationForCat());
        id.Statistic.global();
        
        
        String fntrain=SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/bleloc/classdata.txt.cat";
        String fntest =SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/bleloc/EMPTY.cat";
        
       NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,false,false);
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile=SomeConstant.ROOTDIR+"SIMPLE_CLASS/mnn/scanble.mnn";
        
        NNBuildTree.init(
        signature,
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_7,      // stratégie de construction
        1,                      // nbr de réseaux
        100,                     // somme de toutes les catégories de tous les réseaux
        1.06f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        2,40000000,                 // min et max occurence
        3,                         // nbre de choix retenu
        5,                         // nbre de cycle d'apprentissage
        nnfile                     // fichier pour mémoriser l'arbre.
        );
         
        t1.stop();
    }
    
    
    
}