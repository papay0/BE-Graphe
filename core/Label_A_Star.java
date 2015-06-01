

package core;

        import java.lang.Double;
        import java.lang.Float;
        import java.lang.System;

public class Label_A_Star extends Label
{
    private double estimation; // estimation de notre A*
    public Label_A_Star(Noeud current_node)
    {
        super(current_node);
    } // constructor
    public int compareTo(Label other) {return ((this.get_cout()+this.estimation)-(((Label_A_Star)other).get_cout()+((Label_A_Star)other).getEstimation()))<0?-1:1;}
    public double getEstimation()
    {
        return this.estimation;
    } // getter de l'estimation
    public void setEstimation(double estimation) {this.estimation=estimation;} // setter de l'estimation
}