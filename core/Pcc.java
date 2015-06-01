package core ;



import java.io.* ;
import java.lang.Exception;
import java.lang.Float;
import java.lang.System;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;
import java.util.Collections;
import java.util.Scanner;

import base.Readarg ;
import base.* ;


public class Pcc extends Algo {

    /*Attributs*/
    protected int zoneOrigine ;
    protected int origine ;
    protected int zoneDestination ;
    protected int destination ;
    protected BinaryHeap<Label> tas;
    protected HashMap< Noeud,Label> map_label;
    protected Label dest;




    /*Constructeurs*/
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
        super(gr, sortie, readarg) ;
        this.zoneOrigine = gr.getZone () ;
        this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;
        this.zoneDestination = gr.getZone () ;
        this.destination = readarg.lireInt ("Numero du sommet destination ? ");
        this.tas = new BinaryHeap<Label>();
        this.map_label = new HashMap<Noeud, Label>();
    }

    /*Methodes*/
    public void init() // initialisation : met l'origine dans le tas et set la destination
    {
        for (Noeud node:this.graphe.getNoeuds())
        {
            Label label = new Label(node);
            this.map_label.put(node, label);

            if (node.get_num_noeud() == this.origine) { // sommet d'origine dans le tas
                this.tas.insert(label);
                label.setCout(0);
            }
            if (node.get_num_noeud() == this.destination) { //On enregistre le label de destination
                this.dest = label;
            }
        }
    }

    public void dijkstraAStar(int choix_distance_temps) // dijkstra principal
    {
        double new_cost=0;
        double new_cost2=0;
        double long1 = 0;
        double lat1 = 0;
        double long2 = 0;
        double lat2 = 0;

        int nbElemExplore = 0;
        int nbMax = 0;
        Noeud next_node=null;
        Noeud destination_node = null;
        Label current=null;
        Label label_noeud_destination=null;

        while (! this.tas.isEmpty())
        {
            if (this.tas.size() >= nbMax){
                nbMax = this.tas.size();
            }
            current = this.tas.deleteMin();
            current.setMarquage(true);

            if (current == this.dest) {break;} // Si on est arrivé, on arrête de chercher
            nbElemExplore++;

            for (Route next_route:this.graphe.getNoeuds().get(current.getNumeroSommetCourant()).get_tab_route())
            {
                next_node = this.graphe.getNoeuds().get(next_route.get_numero_noeud_destination());
                destination_node =  this.graphe.getNoeuds().get(destination);
                label_noeud_destination = map_label.get(next_node);
                long1 = next_node.getLongitude();
                lat1 = next_node.getLatitude();
                long2 = destination_node.getLongitude();
                lat2 = destination_node.getLatitude();

                if (choix_distance_temps==0) // ici en distance
                    label_noeud_destination.setEstimation(Graphe.distance(long1, lat1, long2, lat2));  // setter de l'estimation, mais si c'est un Dijkstra et non un A*, ça appelle le setEstimation du labelDijkstra, qui lui ne fait rien ...!
                else // ici en temps
                    label_noeud_destination.setEstimation(Graphe.distance(long1, lat1, long2, lat2)/(130*1000/60));


                if (! label_noeud_destination.is_marque()) {
                    if (choix_distance_temps == 0) //calcul du coup en distance ou en temps
                        new_cost = next_route.get_longueur_arete() + current.get_cout();
                    else {
                        new_cost = (float) (next_route.get_longueur_arete() * 60) / (1000 * next_route.get_descripteur().vitesseMax()) + current.get_cout();
                        new_cost2 = next_route.get_longueur_arete() + current.get_cout2();
                    }

                    if ( new_cost < label_noeud_destination.get_cout()) { // on compare les couts
                        label_noeud_destination.setCout(new_cost);
                        label_noeud_destination.setCout2(new_cost2);
                        label_noeud_destination.setPere(current.getNumeroSommetCourant());
                        if (!this.tas.labelExist(label_noeud_destination)) {
                            this.tas.insert(label_noeud_destination);
                            graphe.getDessin().setColor(Color.magenta);
                            this.graphe.getDessin().drawPoint(next_node.getLongitude(), next_node.getLatitude(), 3);
                        }
                        else
                            this.tas.update(label_noeud_destination);
                    }
                }
            }
        }
        afficherChemin(choix_distance_temps);
    }

    public void run(){ // Main du PCC dijkstra

        int choix_distance_temps=-1; // 0 = distance, 1 = temps
        boolean valid_input = false;
        long temps_execution;

        Scanner user_input = new Scanner(System.in);
        System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination);

        System.out.println("Distance = 0, Temps = 1 ?"); // + check input user (is number...)
        while (!valid_input) {
            if(user_input.hasNextInt()) {
                choix_distance_temps = user_input.nextInt();
                if (choix_distance_temps == 0 || choix_distance_temps == 1)
                    valid_input=true;
            }
            if (!valid_input) {
                System.out.println("Bad input !");
                user_input.next();
            }
        }

        temps_execution = System.currentTimeMillis();
        try{ // Exception si l'origine ou la destination n'existe pas
            this.graphe.getNoeuds().get(destination);
            this.graphe.getNoeuds().get(origine);
        }catch (Exception e){
            System.out.println("L'origine ou la destination ne sont pas dans le graphe!");
            System.out.println("C'est la fin du programme.");
            System.exit(1);
        }

        init();
        dijkstraAStar(choix_distance_temps);

        temps_execution = System.currentTimeMillis() - temps_execution;
        System.out.println("Temps d'execution de l'algo : " + temps_execution + " ms");
    }

    public void afficherChemin(int choix_distance_temps){ // affichage du chemin noeud par noeud avec le getter du pere
        int nbElemMarque = 0;
        Chemin chemin=new Chemin(this.origine, this.destination);
        chemin.addNoeud(this.graphe.getNoeuds().get(destination));
        Label current_label= this.dest;
        System.out.println(choix_distance_temps == 0 ? "Distance jusqu'à destination : "+ (double)Math.round(dest.get_cout()*1000)/1000 + " m":"Temps jusqu'à destination : "+  (double)Math.round(dest.get_cout()*1000)/1000+ " min\nDistance jusqu'à destination : "+(double)Math.round(dest.get_cout2()*1000)/1000+" m");
        while(current_label.getPere()!= -1)
        {
            Noeud node=this.graphe.getNoeuds().get((current_label).getPere());
            chemin.addNoeud(node);
            current_label=map_label.get(node);
            nbElemMarque++;
        }
        System.out.println();
        Collections.reverse(chemin.get_tab_noeud());
        chemin.dessiner_chemin(this.graphe.getDessin(), Color.green);
    }
}

