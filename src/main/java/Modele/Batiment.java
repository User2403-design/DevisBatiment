/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author seb12
 */
public class Batiment {
    
    private int idBatiment;
    private String typeBatiment;
    private int nbreNiveaux;
    private float Xmax;
    private float Ymax;
    private float superficie;

    public int getIdBatiment() {
        return idBatiment;
    }

    public void setIdBatiment(int idBatiment) {
        this.idBatiment = idBatiment;
    }

    public String getTypeBatiment() {
        return typeBatiment;
    }

    public void setTypeBatiment(String typeBatiment) {
        this.typeBatiment = typeBatiment;
    }

    public int getNbreNiveaux() {
        return nbreNiveaux;
    }

    public void setNbreNiveaux(int nbreNiveaux) {
        this.nbreNiveaux = nbreNiveaux;
    }

    public float getXmax() {
        return Xmax;
    }

    public void setXmax(float Xmax) {
        this.Xmax = Xmax;
    }

    public float getYmax() {
        return Ymax;
    }

    public void setYmax(float Ymax) {
        this.Ymax = Ymax;
    }

    public float getSuperficie() {
        return superficie;
    }

    public void setSuperficie(float superficie) {
        this.superficie = superficie;
    }
    
   
    public Batiment(String typeBatiment, float longueur, float largeur) {
        this.typeBatiment = typeBatiment;
        this.Xmax = longueur;
        this.Ymax = largeur;
        this.superficie = longueur * largeur;
    
}
}