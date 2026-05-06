/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chloe
 */
public class Mur {
private String  idMur ; 
private Point p1;
private Point p2;

//inutile? (+get/set)
//private Point xmax;
//private Point ymax;
//private Point xmin;
//private Point ymin;

private Revetement revetement;
private ArrayList<Ouverture> ouvertures;
private boolean estExterieur; // Pour distinguer facade et cloison intérieure

    public Mur(String id, Point p1, Point p2, boolean estExterieur) {
        this.idMur = id;
        this.p1 = p1;
        this.p2 = p2;
        this.estExterieur = estExterieur;
        this.ouvertures = new ArrayList<>();
    }
    
    
    //METHODES
    
    /**
     * Calcule la longueur horizontale du mur en mètres.
     * Utilise les getters de la classe Point pour la cohérence syntaxique.
     */
    public double calculerLongueur() {
        return Math.sqrt(Math.pow(getX2() - getX1(), 2) + Math.pow(getY2() - getY1(), 2));
    }

    /**
     * Calcule la surface nette (Surface brute - Surface des ouvertures).
     * @param hauteurPlafond Hauteur du mur (définie par le Niveau)
     */
    public double calculerSurfaceNette(double hauteurPlafond) {
        double surfaceBrute = calculerLongueur() * hauteurPlafond;
        double surfaceOuvertures = 0;
        for (Ouverture o : ouvertures) {
            surfaceOuvertures += (o.getLargeur() * o.getHauteur());
        }
        return Math.max(0, surfaceBrute - surfaceOuvertures);
    }

    /**
     * Calcule le coût du revêtement pour ce mur.
     */
    public double calculerPrix(double hauteurPlafond) {
        if (revetement == null) return 0.0;
        return calculerSurfaceNette(hauteurPlafond) * revetement.getPrixRevt();
    }

    // --- Accesseurs directs aux coordonnées (Lien avec la classe Point) ---

    public float getX1() {
        return p1.getX();
    }

    public float getY1() {
        return p1.getY();
    }

    public float getX2() {
        return p2.getX();
    }

    public float getY2() {
        return p2.getY();
    }

    // --- Getters et Setters standards ---

    public String getIdMur() {
        return idMur;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Revetement getRevetement() {
        return revetement;
    }

    public void setRevetement(Revetement r) {
        this.revetement = r;
    }

    public boolean estExterieur() {
        return estExterieur;
    }

    public void setEstExterieur(boolean b) {
        this.estExterieur = b;
    }

    public void ajouterOuverture(Ouverture o) {
        this.ouvertures.add(o);
    }

    public List<Ouverture> getOuvertures() {
        return ouvertures;
    }
}
    
