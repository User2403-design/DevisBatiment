/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author seb12
 */

public class Piece {

    private int idPiece;
    private String usage;

    // Les 2 points cliqués par l'utilisateur
    private Point coin1;
    private Point coin2;

    // Surface de la pièce
    private float superficie;
    
    private Revetement revetementSol;
    private Revetement revetementPlafond;
    private ArrayList<Mur> murs;    

    public Piece() {
    }

    public Piece(Point coin1, Point coin2, String usage) {
        this.coin1 = coin1;
        this.coin2 = coin2;
        this.usage = usage;
        this.superficie = calculerSuperficie();
        this.murs = new ArrayList<>();
        //Génération automatique des 4 murs du rectangle
        genererMurs();
    }
    

    public int getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(int idPiece) {
        this.idPiece = idPiece;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public Point getCoin1() {
        return coin1;
    }

    public void setCoin1(Point coin1) {
        this.coin1 = coin1;
    }

    public Point getCoin2() {
        return coin2;
    }

    public void setCoin2(Point coin2) {
        this.coin2 = coin2;
    }

    public float getSuperficie() {
        return superficie;
    }

    public void setSuperficie(float superficie) {
        this.superficie = superficie;
    }

    public void setRevetementSol(Revetement revetementSol) {
        this.revetementSol = revetementSol;
    }

    public void setRevetementPlafond(Revetement revetementPlafond) {
        this.revetementPlafond = revetementPlafond;
    }

    public ArrayList<Mur> getMurs() {
        return murs;
    }
    
    

    // Coordonnée minimale en x
    public float getXMin() {
        return Math.min(coin1.getX(), coin2.getX());
    }

    // Coordonnée maximale en x
    public float getXMax() {
        return Math.max(coin1.getX(), coin2.getX());
    }

    // Coordonnée minimale en y
    public float getYMin() {
        return Math.min(coin1.getY(), coin2.getY());
    }

    // Coordonnée maximale en y
    public float getYMax() {
        return Math.max(coin1.getY(), coin2.getY());
    }

    public float getLargeur() {
        return getXMax() - getXMin();
    }

    public float getHauteur() {
        return getYMax() - getYMin();
    }

    public float calculerSuperficie() {
        return getLargeur() * getHauteur();
    }
    
      @Override
    public String toString() {
        return usage + " - " + String.format("%.2f", superficie) + " m²";
    }
   /**
     * Crée les 4 objets Mur délimitant la pièce.
     */
    private void genererMurs() {
        Point hautGauche = new Point(coin1.getX(), coin1.getY());
        Point hautDroit  = new Point(coin2.getX(), coin1.getY());
        Point basDroit   = new Point(coin2.getX(), coin2.getY());
        Point basGauche  = new Point(coin1.getX(), coin2.getY());

        // On crée les 4 segments. Par défaut, on les considère intérieurs (false).
        // Le contrôleur pourra modifier 'estExterieur' si le mur touche le bord du bâtiment.
        murs.add(new Mur("Nord", hautGauche, hautDroit, false)); // Mur Nord
        murs.add(new Mur("Est", hautDroit, basDroit, false));   // Mur Est
        murs.add(new Mur("Sud", basDroit, basGauche, false));   // Mur Sud
        murs.add(new Mur("Ouest", basGauche, hautGauche, false)); // Mur Ouest
    }

    /**
     * Synthèse du coût total de la pièce.
     */
    public double calculerPrixTotal(double hauteurPlafond) {
        double total = 0;
        
        // Coût Sol
        if (revetementSol != null) {
            total += getSuperficie() * revetementSol.getPrixRevt();
        }
        
        // Coût Plafond
        if (revetementPlafond != null) {
            total += getSuperficie() * revetementPlafond.getPrixRevt();
        }
        
        // Coût des Murs
        for (Mur m : murs) {
            total += m.calculerPrix(hauteurPlafond);
        }
        
        return total;
    }
}
    