/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.util.ArrayList;


/**
 *
 * @author seb12
 */


public class Appartement {

    // Nom affiché dans la liste
    private String nom;

    // Les 2 coins du rectangle de l'appartement dans l'étage
    private Point coin1;
    private Point coin2;

    // Liste des pièces de l'appartement
    private ArrayList<Piece> pieces;
    
    private Revetement revetementSolDefaut;
    private Revetement revetementPlafondDefaut;
    private Revetement revetementMurDefaut;

    public Appartement() {
        this.pieces = new ArrayList<>();
    }

    public Appartement(String nom, Point coin1, Point coin2) {
        this.nom = nom;
        this.coin1 = coin1;
        this.coin2 = coin2;
        this.pieces = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public Revetement getRevetementSolDefaut() {
        return revetementSolDefaut;
    }

    public void setRevetementSolDefaut(Revetement revetementSolDefaut) {
        this.revetementSolDefaut = revetementSolDefaut;
    }

    public Revetement getRevetementPlafondDefaut() {
        return revetementPlafondDefaut;
    }

    public void setRevetementPlafondDefaut(Revetement revetementPlafondDefaut) {
        this.revetementPlafondDefaut = revetementPlafondDefaut;
    }

    public Revetement getRevetementMurDefaut() {
        return revetementMurDefaut;
    }

    public void setRevetementMurDefaut(Revetement revetementMurDefaut) {
        this.revetementMurDefaut = revetementMurDefaut;
    }

    
    // ---------- Méthodes utiles pour le rectangle ----------

    public float getXMin() {
        return Math.min(coin1.getX(), coin2.getX());
    }

    public float getXMax() {
        return Math.max(coin1.getX(), coin2.getX());
    }

    public float getYMin() {
        return Math.min(coin1.getY(), coin2.getY());
    }

    public float getYMax() {
        return Math.max(coin1.getY(), coin2.getY());
    }

    public float getLargeur() {
        return getXMax() - getXMin();
    }

    public float getHauteur() {
        return getYMax() - getYMin();
    }

    public float getSuperficie() {
        return getLargeur() * getHauteur();
    }

    // ---------- Gestion des pièces ----------

    public void ajouterPiece(Piece piece) {
        pieces.add(piece);
    }

    public void supprimerDernierePiece() {
        if (!pieces.isEmpty()) {
            pieces.remove(pieces.size() - 1);
        }
    }

    @Override
    public String toString() {
        return nom + " - " + String.format("%.2f", getSuperficie()) + " m²";
    }
}