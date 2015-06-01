package core;

import base.Descripteur;

import java.lang.System;

public class Route {

    private int m_zone_destination;
    private int m_numero_noeud_source;
    private int m_numero_noeud_destination;
    private int m_description_arete;
    private int m_longueur_arete;
    private int m_nombre_segments;
    private Descripteur m_descripteur;

    // constructeur
    public Route(int numero_noeud_source, int zone_destination, int numero_noeud_destination, int description_arete, int longueur_arete, int nombre_segments, Descripteur descripteur)
    {
        this.m_zone_destination = zone_destination;
        this.m_numero_noeud_source = numero_noeud_source;
        this.m_numero_noeud_destination = numero_noeud_destination;
        this.m_description_arete = description_arete;
        this.m_longueur_arete = longueur_arete;
        this.m_nombre_segments = nombre_segments;
        this.m_descripteur = descripteur;
    }

    public Route() {}

    public String toString() {return "Source : " + this.get_numero_noeud_source() + " Destination : " + this.get_numero_noeud_destination() + " Distance " + this.get_longueur_arete();} // toString, très utile pour le débug! Affichage source et destination
    public int get_numero_noeud_source() {return this.m_numero_noeud_source;} // getter numero noeud de la source
    public int get_numero_noeud_destination() {return this.m_numero_noeud_destination;} // getter numero noeud de la destination
    public int get_longueur_arete()
    {
        return this.m_longueur_arete;
    } // getter longueur de l'arrete
    public Descripteur get_descripteur() {return this.m_descripteur;} // getter du descripteur


	
	
}
