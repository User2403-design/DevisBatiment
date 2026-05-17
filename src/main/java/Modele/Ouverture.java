
package Modele;

public class Ouverture {
    private int id;
    private String type;
    private float largeur;
    private float hauteur;
    private Revetement article; // Nouvel attribut pour associer l'article du catalogue

    public Ouverture(int id, String type, float largeur, float hauteur) {
        this.id = id;
        this.type = type;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.article = null;
    }

    public Ouverture(int id, String type, float largeur, float hauteur, Revetement article) {
        this.id = id;
        this.type = type;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.article = article;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getLargeur() {
        return largeur;
    }

    public void setLargeur(float largeur) {
        this.largeur = largeur;
    }

    public float getHauteur() {
        return hauteur;
    }

    public void setHauteur(float hauteur) {
        this.hauteur = hauteur;
    }

    public Revetement getArticle() {
        return article;
    }

    public void setArticle(Revetement article) {
        this.article = article;
    }

    @Override
    public String toString() {
        String description = type + " (L: " + largeur + "m x H: " + hauteur + "m)";
        if (article != null) {
            description += " - " + article.getNomRevt() + " (" + article.getPrixRevt() + " €/u)";
        }
        return description;
    }
}

//////*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package Modele;
//
///**
// *
// * @author chloe
// */
//public class Ouverture {
//    private int idOuverture;
//    private float largeur;
//    private float hauteur;
//    private String type; // ex: "Porte", "Fenêtre"
//
//    public Ouverture(int id, String type, float largeur, float hauteur) {
//        this.idOuverture = id;
//        this.type = type;
//        this.largeur = largeur;
//        this.hauteur = hauteur;
//    }
//
//    // --- Getters et Setters ---
//
//    public int getIdOuverture() {
//        return idOuverture;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * Retourne la largeur de l'ouverture en mètres.
//     */
//    public float getLargeur() {
//        return largeur;
//    }
//
//    public void setLargeur(float largeur) {
//        this.largeur = largeur;
//    }
//
//    /**
//     * Retourne la hauteur de l'ouverture en mètres.
//     */
//    public float getHauteur() {
//        return hauteur;
//    }
//
//    public void setHauteur(float hauteur) {
//        this.hauteur = hauteur;
//    }
//
//    @Override
//    public String toString() {
//        return type + " [" + idOuverture + "] (" + largeur + "x" + hauteur + "m)";
//    }
//    
//    
//}
