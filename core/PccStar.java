package core ;




import java.io.* ;
import java.lang.Exception;
import java.lang.Float;
import java.lang.System;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;
import java.util.Collections;

import base.* ;

import base.Readarg ;

public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg)
    {
        super(gr, sortie, readarg);
    }

    public void init()  // init de l'A*, on crée des labels A* à la place de label Dijkstra
    {
        for (Noeud node:this.graphe.getNoeuds())
        {
            Label_A_Star label = new Label_A_Star(node);
            map_label.put(node, label);
            if (node.get_num_noeud() == origine) {
                this.tas.insert(label);
                label.setCout(0);
            }
            if (node.get_num_noeud() == destination)
                this.dest = label;
        }
    }

    public void run() { // Main du A*

        System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
        super.run(); // On appelle la méthode Main (Run) parente (Dijkstra)

    }

}
