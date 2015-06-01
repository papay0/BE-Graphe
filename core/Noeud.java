package core;

import java.lang.System;
import java.util.ArrayList;

public class Noeud {
	
	private float m_longitude;
	private float m_latitude;
	private int m_nb_successeurs;
    private int m_numero_noeud;
	private ArrayList<Route> m_routes;
    private ArrayList<Route> m_routes_reverse;

    /** Constructeur **/
	public Noeud(int numero_noeud, float longitude, float latitude, int nombre_successeurs)
	{
        this.m_numero_noeud = numero_noeud;
		this.m_longitude = longitude;
		this.m_latitude = latitude;
		this.m_nb_successeurs = nombre_successeurs;
		this.m_routes = new ArrayList<Route>();
        this.m_routes_reverse = new ArrayList<Route>();
	}

    public Noeud() {}

    public int get_true_nb_successeurs_array_size() {return this.m_routes.size();} // getter size array route
    public int get_num_noeud(Noeud noeud) {return this.m_numero_noeud;} // getter numero d'un noeud
    public int get_num_noeud()
    {
        return this.m_numero_noeud;
    } // test
    public ArrayList<Route> get_tab_route() {return this.m_routes;}  // getter array route
    public ArrayList<Route> get_tab_route(boolean isReversed) // getter array route reversed for the destination's dijkstra
    {
        if (!isReversed)
            return this.m_routes;
        else
            return this.m_routes_reverse;
    }
    public int get_nb_successeurs()
    {
        return this.m_nb_successeurs;
    }
    public void set_nb_successeurs(int nb_successeurs)
    {
        this.m_nb_successeurs = nb_successeurs;
    }
    public void addRoute(Route route){this.m_routes.add(route);}
    public void addRoute_reverse(Route route){this.m_routes_reverse.add(route);}
    public float getLongitude()
    {
        return this.m_longitude;
    }
    public float getLatitude()
    {
        return this.m_latitude;
    }
}
