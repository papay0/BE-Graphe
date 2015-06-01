package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.awt.*;
import java.awt.Color;
import java.io.* ;
import java.lang.System;
import java.util.ArrayList;

import base.* ;

public class Graphe {

    // Nom de la carte utilisee pour construire ce graphe
    private final String nomCarte ;

    // Fenetre graphique
    private final Dessin dessin ;

    // Version du format MAP utilise'.
    private static final int version_map = 4 ;
    private static final int magic_number_map = 0xbacaff ;

    // Version du format PATH.
    private static final int version_path = 1 ;
    private static final int magic_number_path = 0xdecafe ;

    // Identifiant de la carte
    private int idcarte ;

    // Numero de zone de la carte
    private int numzone ;

    /*
     * Ces attributs constituent une structure ad-hoc pour stocker les informations du graphe.
     * Vous devez modifier et ameliorer ce choix de conception simpliste.
     */
    private Descripteur[] descripteurs ;
    public static ArrayList<Noeud> m_tab_noeud;
    public static ArrayList<Route> m_tab_routes;

    public ArrayList<Noeud> getNoeuds() {return this.m_tab_noeud;} // getter node


    // Deux malheureux getters.
    public Dessin getDessin() { return dessin ; }
    public int getZone() { return numzone ; }

    // Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
    public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

        this.nomCarte = nomCarte ;
        this.dessin = dessin ;
        Utils.calibrer(nomCarte, dessin) ;
        m_tab_noeud = new ArrayList<Noeud>();
        m_tab_routes = new ArrayList<Route>();

        float nb_successeurs_total = 0;

        // Lecture du fichier MAP.
        // Voir le fichier "FORMAT" pour le detail du format binaire.
        try {

            // Nombre d'aretes
            int edges = 0 ;

            // Verification du magic number et de la version du format du fichier .map
            int magic = dis.readInt () ;
            int version = dis.readInt () ;
            Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

            // Lecture de l'identifiant de carte et du numero de zone,
            this.idcarte = dis.readInt () ;
            this.numzone = dis.readInt () ;
           // System.out.println("[ID Carte] "+this.idcarte);

            // Lecture du nombre de descripteurs, nombre de noeuds.
            int nb_descripteurs = dis.readInt () ;
            int nb_nodes = dis.readInt () ;

            // Nombre de successeurs enregistrÃ©s dans le fichier.
            int[] nsuccesseurs_a_lire = new int[nb_nodes] ;

            // En fonction de vos choix de conception, vous devrez certainement adapter la suite.

            this.descripteurs = new Descripteur[nb_descripteurs] ;

            float longitude, latitude;
            int nb_successeurs_a_lire;

            for (int num_node = 0 ; num_node < nb_nodes ; num_node++) // Lecture des noeuds
            {
                longitude = ((float)dis.readInt ()) / 1E6f ;
                latitude = ((float)dis.readInt ()) / 1E6f ;
                nb_successeurs_a_lire = dis.readUnsignedByte() ;
                this.m_tab_noeud.add(new Noeud(num_node, longitude, latitude, nb_successeurs_a_lire));
                nsuccesseurs_a_lire[num_node] = nb_successeurs_a_lire ;
            }
            Utils.checkByte(255, dis) ;

            for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) { // Lecture des descripteurs
                // Lecture du descripteur numero num_descr
                descripteurs[num_descr] = new Descripteur(dis) ;
            }

            Utils.checkByte(254, dis) ;

            // Lecture des successeurs
            for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
                // Lecture de tous les successeurs du noeud num_node
                for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) {
                    // zone du successeur
                    nb_successeurs_total++;
                    int succ_zone = dis.readUnsignedByte() ;

                    // numero de noeud du successeur
                    int dest_node = Utils.read24bits(dis) ;

                    // descripteur de l'arete
                    int descr_num = Utils.read24bits(dis) ;

                    // longueur de l'arete en metres
                    int longueur  = dis.readUnsignedShort() ;

                    // Nombre de segments constituant l'arete
                    int nb_segm   = dis.readUnsignedShort() ;

                    edges++ ;
                    if (succ_zone == numzone)
                    {
                        Route route_sortante = new Route(num_node, succ_zone, dest_node, descr_num, longueur, nb_segm, descripteurs[descr_num]);
                        this.m_tab_routes.add(route_sortante);
                        this.m_tab_noeud.get(num_node).addRoute(route_sortante);

                        if(!descripteurs[descr_num].isSensUnique()){
                            this.m_tab_noeud.get(num_node).addRoute_reverse(route_sortante); // useful for the destination's dijkstra
                            Route route_dest=new Route(dest_node, succ_zone, num_node, descr_num, longueur, nb_segm, descripteurs[descr_num]);
                            this.m_tab_noeud.get(dest_node).addRoute(route_dest);
                            this.m_tab_noeud.get(dest_node).addRoute_reverse(route_dest);
                            this.m_tab_noeud.get(dest_node).set_nb_successeurs(m_tab_noeud.get(dest_node).get_nb_successeurs()+1);
                            nb_successeurs_total++;
                        } else {
                            Route route_dest=new Route(dest_node, numzone, num_node, descr_num, longueur, nb_segm, descripteurs[descr_num]);
                            m_tab_noeud.get(dest_node).addRoute_reverse(route_dest);
                        }
                    }

                    Couleur.set(dessin, descripteurs[descr_num].getType()) ;
                    if(descripteurs[descr_num].isSensUnique())
                        dessin.setColor(Color.pink);

                    float current_long = this.m_tab_noeud.get(num_node).getLongitude();
                    float current_lat = this.m_tab_noeud.get(num_node).getLatitude();

                    // Chaque segment est dessine'
                    for (int i = 0 ; i < nb_segm ; i++) {
                        float delta_lon = (dis.readShort()) / 2.0E5f ;
                        float delta_lat = (dis.readShort()) / 2.0E5f ;
                        dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
                        current_long += delta_lon ;
                        current_lat  += delta_lat ;
                    }
                    if (succ_zone == numzone)
                    {
                        dessin.drawLine(current_long, current_lat, this.m_tab_noeud.get(dest_node).getLongitude(), this.m_tab_noeud.get(dest_node).getLatitude());
                    }
                }
            }

            Utils.checkByte(253, dis) ;

            System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, "
                    + nb_descripteurs + " descripteurs.") ;

        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(1) ;
        }

    afficher_nb_successeurs_moyen();

    }

    // Rayon de la terre en metres
    private static final double rayon_terre = 6378137.0 ;

    /**
     *  Calcule de la distance orthodromique - plus court chemin entre deux points à la surface d'une sphère
     *  @param long1 longitude du premier point.
     *  @param lat1 latitude du premier point.
     *  @param long2 longitude du second point.
     *  @param lat2 latitude du second point.
     *  @return la distance entre les deux points en metres.
     *  Methode Ã©crite par Thomas Thiebaud, mai 2013
     */
    public static double distance(double long1, double lat1, double long2, double lat2) {
        double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
        double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
        double cosLong = Math.cos(Math.toRadians(long2-long1));
        return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
    }

    public void afficher_nb_successeurs_moyen()
    {
        float indexMoyenne = 0;
        for(Noeud node: this.m_tab_noeud)
        {
            indexMoyenne += node.get_true_nb_successeurs_array_size();
        }
        System.out.println("[INFO] Average = " + indexMoyenne / this.m_tab_noeud.size());
    }

    /**
     *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
     *  A n'utiliser que pour faire du debug ou des tests ponctuels.
     *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
     */
    public void situerClick() {

        System.out.println("Allez-y, cliquez donc.") ;

        if (dessin.waitClick()) {
            float lon = dessin.getClickLon() ;
            float lat = dessin.getClickLat() ;

            System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

            // On cherche le noeud le plus proche. O(n)
            float minDist = Float.MAX_VALUE ;
            int   noeud   = 0 ;

            for (int num_node = 0 ; num_node < this.m_tab_noeud.size() ; num_node++) {
                float londiff = (this.m_tab_noeud.get(num_node).getLongitude() - lon) ;
                float latdiff = (this.m_tab_noeud.get(num_node).getLatitude() - lat) ;
                float dist = londiff*londiff + latdiff*latdiff ;
                if (dist < minDist) {
                    noeud = num_node ;
                    minDist = dist ;
                }
            }

            System.out.println("Noeud le plus proche : " + noeud) ;
            System.out.println() ;
            dessin.setColor(java.awt.Color.red) ;
            dessin.drawPoint(this.m_tab_noeud.get(noeud).getLongitude(), this.m_tab_noeud.get(noeud).getLatitude(), 5) ;
        }
    }

    /**
     *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
     *  Verifie que le chemin est empruntable et calcule le temps de trajet.
     */
    public void verifierChemin(DataInputStream dis, String nom_chemin) {



        try {

            // Verification du magic number et de la version du format du fichier .path
            int magic = dis.readInt () ;
            int version = dis.readInt () ;
            Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

            // Lecture de l'identifiant de carte
            int path_carte = dis.readInt () ;

            if (path_carte != this.idcarte) {
                System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
                System.exit(1) ;
            }

            int nb_noeuds = dis.readInt () ;

            // Origine du chemin
            int first_zone = dis.readUnsignedByte() ;
            int first_node = Utils.read24bits(dis) ;

            // Destination du chemin
            int last_zone  = dis.readUnsignedByte() ;
            int last_node = Utils.read24bits(dis) ;

            System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

            int current_zone = 0 ;
            int current_node = 0 ;

            Chemin chemin = new Chemin(path_carte, nb_noeuds, first_node, last_node);

            // Tous les noeuds du chemin
            for (int i = 0 ; i < nb_noeuds ; i++) {
                current_zone = dis.readUnsignedByte() ;
                current_node = Utils.read24bits(dis) ;
                chemin.addNoeud(this.m_tab_noeud.get(current_node));
                System.out.println(" --> " + current_zone + ":" + current_node) ;
            }

            if ((current_zone != last_zone) || (current_node != last_node)) {
                System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
                System.exit(1) ;
            }

            chemin.calcul_temps();
            chemin.dessiner_chemin(dessin, Color.blue);


        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(1) ;
        }



    }

}
