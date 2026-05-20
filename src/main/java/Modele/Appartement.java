/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.util.ArrayList;

public class Appartement {
    

    private String nom;

    // Les 2 coins du rectangle de l'appartement dans l'étage
    private Point coin1;
    private Point coin2;

    
    private TypeLogement typeLogement;

    
    private ArrayList<Piece> pieces;
    
    private Revetement revetementSolDefaut;
    private Revetement revetementPlafondDefaut;
    private Revetement revetementMurDefaut;

    
    private Ouverture porteEntree;

     // Pour la zone vide restante pas comprise dans pièce
    private Revetement revetementSolZoneRestante;
    private Revetement revetementMurZoneRestante;
    private Revetement revetementPlafondZoneRestante;
   
    public Appartement() {
        this.pieces = new ArrayList<>();
        this.typeLogement = TypeLogement.APPARTEMENT;
    }

    public Appartement(String nom, Point coin1, Point coin2) {
        this.nom = nom;
        this.coin1 = coin1;
        this.coin2 = coin2;
        this.pieces = new ArrayList<>();
        this.typeLogement = TypeLogement.APPARTEMENT; // constante de l'enum defini dans la classe TypeLogement
    }

    public Appartement(String nom, Point coin1, Point coin2, TypeLogement typeLogement) {
        this.nom = nom;
        this.coin1 = coin1;
        this.coin2 = coin2;
        this.pieces = new ArrayList<>();
        this.typeLogement = typeLogement;
    }

    public Revetement getRevetementSolZoneRestante() {
        return revetementSolZoneRestante;
    }

    public void setRevetementSolZoneRestante(Revetement revetementSolZoneRestante) {
        this.revetementSolZoneRestante = revetementSolZoneRestante;
    }

    public Revetement getRevetementMurZoneRestante() {
        return revetementMurZoneRestante;
    }

    public void setRevetementMurZoneRestante(Revetement revetementMurZoneRestante) {
        this.revetementMurZoneRestante = revetementMurZoneRestante;
    }

    public Revetement getRevetementPlafondZoneRestante() {
        return revetementPlafondZoneRestante;
    }

    public void setRevetementPlafondZoneRestante(Revetement revetementPlafondZoneRestante) {
        this.revetementPlafondZoneRestante = revetementPlafondZoneRestante;
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

    public TypeLogement getTypeLogement() {
        return typeLogement;
    }

    public void setTypeLogement(TypeLogement typeLogement) {
        this.typeLogement = typeLogement;
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

    public Ouverture getPorteEntree() {
        return porteEntree;
    }

    public void setPorteEntree(Ouverture porteEntree) {
        this.porteEntree = porteEntree;
    }

    /**
     * Vérifie si le plan respecte les règles du type de logement
     */
    public boolean validerPlan() {
        if (typeLogement == TypeLogement.MAISON) {
            // Pour une maison : vérifier qu'il existe une porte d'entrée
            return porteEntree != null;
        }
        // Pour un appartement : pas de validation spécifique pour l'instant
        return true;
    }

    
    // Méthodes utiles pour le rectangle

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

    // Gestion des pièces 

    public void ajouterPiece(Piece piece) {
        pieces.add(piece);
    }

    public void supprimerDernierePiece() {
        if (!pieces.isEmpty())  // permet de verifier que le liste n'est pas vide 
            pieces.remove(pieces.size() - 1); // donne l'indice de l'élement dans la pièce (chambre, salon,...) et le supprime
        }
    
// methode qui retourne une chaine de caractère représentant l'appartement, affiche le nom et la superficie 
    @Override
    public String toString() {
        return nom + " - " + String.format("%.2f", getSuperficie()) + " m²"; // %.2f permet d'afficher 2 chiffres après la virgule
    }
}