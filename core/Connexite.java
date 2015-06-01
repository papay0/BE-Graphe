package core ;

import java.io.* ;
import java.lang.Double;
import java.lang.System;
import java.util.HashMap;
import java.awt.Color;
import java.util.Collections;

import base.* ;

import base.Readarg ;

public class Connexite extends Algo {

    private HashMap< Noeud,Label> labelsPieton;
    private HashMap< Noeud,Label> labelsVoiture;
    private HashMap< Noeud,Label> labelsDestination;
    private HashMap<Noeud, Label> mapLabel;
    private double timeMaxToWalk;
    private boolean found = false;
    private static boolean pietonByBus = Math.random() > 0.5 ? true : false;

    private int pourcentageVoiture;
    private double timeMaxToDrive;
    protected long temps_execution;
    private Noeud nodeRencontre;
    private double normalTimeDrivingCar;
    private int vitessePieton = 10;

    // constructor
    public Connexite(Graphe gr, PrintStream sortie, Readarg readarg) {
        super(gr, sortie, readarg);
        labelsPieton = new HashMap<Noeud, Label>();
        labelsVoiture = new HashMap<Noeud, Label>();
        labelsDestination = new HashMap<Noeud, Label>();
        timeMaxToWalk = 10;
        pourcentageVoiture = 100;
    }

    public void run() { // main of Connexite

        temps_execution = System.currentTimeMillis();
	// A vous d'implementer la recherche de connexite.

        int origineNumberPieton = 108104; // point de départ du pieton
        int origineNumberVoiture = 108791; // point de départ de la voiture
        int origineNumberDestination = 118975; // point de destination

        // calcul des couts
        labelsPieton = computeDijkstra(origineNumberPieton, true, false);
        labelsVoiture = computeDijkstra(origineNumberVoiture, false, false);
        labelsDestination = computeDijkstra(origineNumberDestination, false, true);

        double coutPieton = 0;
        double coutVoiture = 0;
        double coutDestination = 0;
        int numberNodePointRencontre = 0;
        Label destinationLabel = null;
        Label origineLabel = null;
        nodeRencontre = null;
        double min = Double.POSITIVE_INFINITY;
        double currentCoutSomme = 0;

        normalTimeDrivingCar = labelsVoiture.get(this.graphe.getNoeuds().get(origineNumberDestination)).get_cout();
        timeMaxToDrive = normalTimeDrivingCar+normalTimeDrivingCar*pourcentageVoiture/100;

        for (Noeud node:this.graphe.getNoeuds()) // calcul du minimum (= point d'intersection)
        {
            coutPieton = labelsPieton.get(node).get_cout();
            coutVoiture = labelsVoiture.get(node).get_cout();
            coutDestination = labelsDestination.get(node).get_cout();
            currentCoutSomme = coutDestination + coutPieton + coutVoiture;
            if ( (currentCoutSomme < min) && (coutDestination + coutVoiture < timeMaxToDrive))
            {
                min = currentCoutSomme;
                numberNodePointRencontre = node.get_num_noeud();
                destinationLabel = labelsPieton.get(node);
                nodeRencontre = node;
                found = true;
            }
        }

        if (pietonByBus ==  true) {
            System.out.println("Pieton prend le bus");
        } else
        {
            System.out.println("Pieton marche");
        }

        if (found == true) {
            printChemin(origineNumberPieton, numberNodePointRencontre, labelsPieton, Color.green);
            printChemin(origineNumberVoiture, numberNodePointRencontre, labelsVoiture, Color.red);
            printChemin(origineNumberDestination, numberNodePointRencontre, labelsDestination, Color.blue);
            System.out.println("Durée chemin pieton : "+ labelsPieton.get(nodeRencontre).get_cout()+" min");
            System.out.println("Durée total pieton : " + (labelsPieton.get(nodeRencontre).get_cout() + labelsDestination.get(nodeRencontre).get_cout()) + " min");
            System.out.println("Durée voiture sans covoit : "+ normalTimeDrivingCar +" min");
            System.out.println("Durée total voiture (avec covoit) : "+ (labelsVoiture.get(nodeRencontre).get_cout() + labelsDestination.get(nodeRencontre).get_cout()) +" min");
        } else
        {
            System.out.println("Aucun point de rassemblement trouvé!");
        }
        temps_execution = System.currentTimeMillis() - temps_execution;
        System.out.println("Temps d'execution de l'algo : "+ temps_execution+ " ms");
    }

    private HashMap< Noeud,Label>computeDijkstra(int origine, boolean isPieton, boolean isReversed) // principal dijkstra
    {
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
        mapLabel = new HashMap<Noeud, Label>();
        Label current;
        Noeud nextNode;
        Label labelNoeudDestination;
        int choix_distance_temps = 1;
        double newCost = 0.0;
        int vitesseMax = 0;
        for (Noeud node:this.graphe.getNoeuds())
        {
            Label label = new Label(node);
            mapLabel.put(node, label);
            if (node.get_num_noeud() == origine) {
                tas.insert(label);
                label.setCout(0);
            }
        }
        while (! tas.isEmpty())
        {
            current = tas.deleteMin();
            current.setMarquage(true);
            for (Route nextRoute:this.graphe.getNoeuds().get(   current.getNumeroSommetCourant()  ).get_tab_route(isReversed))
            {
                nextNode = this.graphe.getNoeuds().get(nextRoute.get_numero_noeud_destination());
                labelNoeudDestination = mapLabel.get(nextNode);
                if (! labelNoeudDestination.is_marque()) {
                    if (isPieton == true && !pietonByBus) {
                        vitesseMax = this.vitessePieton;
                    } else {
                        vitesseMax = nextRoute.get_descripteur().vitesseMax();
                    }
                    newCost = (float)(nextRoute.get_longueur_arete()*60)/(1000*vitesseMax) + current.get_cout();
                    if ( newCost < labelNoeudDestination.get_cout() && (newCost <= timeMaxToWalk || !isPieton || pietonByBus)) {
                        labelNoeudDestination.setCout(newCost);
                        labelNoeudDestination.setPere(current.getNumeroSommetCourant());
                        if (!tas.labelExist(labelNoeudDestination)) {
                            tas.insert(labelNoeudDestination);
                            graphe.getDessin().setColor(Color.magenta);
                            this.graphe.getDessin().drawPoint(nextNode.getLongitude(), nextNode.getLatitude(), 3);
                        }
                        else
                        {
                            tas.update(labelNoeudDestination);
                        }
                    }
                }
            }
        }
        return  mapLabel;
    }

    public void printChemin(int origine, int destination, HashMap<Noeud, Label> labels,  Color color) // dessine le chemin trouvé sur la carte
    {
        Chemin chemin=new Chemin(origine, destination);
        int currentLabel = destination;
        while (currentLabel != origine)
        {
            Noeud node=this.graphe.getNoeuds().get(currentLabel);
            chemin.addNoeud(node);
            currentLabel=labels.get(node).getPere();
        }
        chemin.dessiner_chemin(this.graphe.getDessin(), color);
    }


}
