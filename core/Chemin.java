package core ;

import java.awt.*;
import java.awt.Color;
import java.lang.System;
import java.util.ArrayList;
import base.* ;

public class Chemin
{
    private int m_id;
    private int m_nb_noeud;
    private int m_num_noeud_origine;
    private int m_num_noeud_dest;
    private ArrayList<Noeud> m_tab_noeud;

    // constructeur
    public Chemin(int id, int nb_noeud, int num_noeud_origine, int num_noeud_dest)
    {
        this.m_id = id;
        this.m_nb_noeud = nb_noeud;
        this.m_num_noeud_origine = num_noeud_origine;
        this.m_num_noeud_dest = num_noeud_dest;
        this.m_tab_noeud = new ArrayList<Noeud>();
    }

    // constructeur
    public Chemin(int noeud_debut,int noeud_destination) {
        this.m_num_noeud_origine=noeud_debut;
        this.m_num_noeud_dest=noeud_destination;
        this.m_tab_noeud=new ArrayList<Noeud>();
    }


    public void addNoeud(Noeud noeud)
    {
        this.m_tab_noeud.add(noeud);
    } // add node in array

    public void calcul_temps()
    {
        Route route = new Route();
        Noeud noeud_debut_temp = new Noeud();
        Noeud noeud_fin_temp = new Noeud();
        noeud_debut_temp = Graphe.m_tab_noeud.get(m_num_noeud_origine);

        float vitesse = 0;
        float longueur_arete = 0;
        float temps = 0;

        for (int i=0; i< get_tab_noeud().size()-1;i++)
        {
            noeud_debut_temp = get_tab_noeud().get(i);
            noeud_fin_temp = get_tab_noeud().get(i+1);
            route = findRoute_temps(noeud_debut_temp, noeud_fin_temp);
            vitesse = route.get_descripteur().vitesseMax(); // km/h
            longueur_arete = (float)route.get_longueur_arete()/1000; // km
            temps += 60*(longueur_arete/vitesse); // h
        }
        System.out.println("First node = "+get_tab_noeud().get(0).get_num_noeud());
        System.out.println("Last node = "+get_tab_noeud().get(get_tab_noeud().size()-1).get_num_noeud());
        System.out.println("Temps = "+temps+" minutes");
    }

    public void dessiner_chemin(Dessin dessin, Color color){ // draw the path on the map

        float delta_lon = 0;
        float delta_lat = 0;
        float current_long  = 0;
        float current_lat = 0;

        Route route = new Route();
        Noeud noeud_debut_temp = new Noeud();
        Noeud noeud_fin_temp = new Noeud();

        for(int i=0;i<this.get_tab_noeud().size()-1;i++){

            current_long = this.get_tab_noeud().get(i).getLongitude() ;
            current_lat  = this.get_tab_noeud().get(i).getLatitude();
            noeud_debut_temp = get_tab_noeud().get(i);
            noeud_fin_temp = get_tab_noeud().get(i+1);
            route = findRoute_temps(noeud_debut_temp, noeud_fin_temp);
            dessin.setColor(color);
            dessin.setWidth(3);
            dessin.drawLine(current_long, current_lat, this.get_tab_noeud().get(i+1).getLongitude(),
                    this.get_tab_noeud().get(i+1).getLatitude()) ;
        }
    }

    private Route findRoute_temps(Noeud noeud_debut, Noeud noeud_dest)
    {
        float vitesse = 0;
        float temps = 0;
        float temps_minimum = Float.POSITIVE_INFINITY;
        Route return_route = new Route();
        for(Noeud node:this.m_tab_noeud)
        {
            if (node == noeud_debut)
            {
                for (Route route: node.get_tab_route())
                {
                    if (Graphe.m_tab_noeud.get(route.get_numero_noeud_destination()) == noeud_dest)
                    {
                        vitesse = route.get_descripteur().vitesseMax();
                        temps = (float)route.get_longueur_arete()/vitesse;
                        if (temps < temps_minimum)
                        {
                            temps_minimum = temps;
                            return_route = route;
                        }
                    }
                }
            }
        }
        return  return_route;
    }

    public ArrayList<Noeud> get_tab_noeud()
    {
        return this.m_tab_noeud;
    } // getter array of node

}