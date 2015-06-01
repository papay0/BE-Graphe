package core;

import java.lang.Double;
import java.lang.Float;

public class Label implements Comparable<Label>
{
    private boolean m_marquage;
    private double m_cout;
    private int m_numero_sommet_courant;
    private int m_pere;
    private double m_cout2;

    // constructor
    public Label(Noeud noeud)
    {
        this.m_marquage = false;
        this.m_cout = Double.POSITIVE_INFINITY;
        this.m_cout2 = 0;
        this.m_numero_sommet_courant = noeud.get_num_noeud();
        this.m_pere = -1;
    }

    public void setPere(int pere)
    {
        this.m_pere = pere;
    } // setter pere
    public int getPere()
    {
        return this.m_pere;
    } // getter pere
    public int getNumeroSommetCourant() {return this.m_numero_sommet_courant; } // getter numero sommet courant
    public void setMarquage(boolean marquage)
    {
        this.m_marquage = marquage;
    } //setter marquage
    public boolean is_marque() {
        return this.m_marquage;
    }
    public void setCout(double cout)
    {
        this.m_cout = cout;
    }
    public double get_cout()
    {
        return this.m_cout;
    }
    public void setCout2(double cout) {this.m_cout2 = cout;} // utile pour savoir le cout de la distance quand on calcule dijkstra en temps
    public double get_cout2()
    {
        return this.m_cout2;
    }

    public int compareTo(Label other) {
        Double cout_this = new Double(this.get_cout());
        Double cout_other  = new Double (other.get_cout());
        return cout_this.compareTo(cout_other);
    }
    public void setEstimation(double estimation) {}

}