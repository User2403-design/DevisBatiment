/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author chloe
 */
public class Revetement {
 
private int idRevt;
private String nomRevt;
private boolean estIsolt;
private float prixRevt; 

    public int getIdRevt() {
        return idRevt;
    }

    public void setIdRevt(int idRevt) {
        this.idRevt = idRevt;
    }

    public String getNomRevt() {
        return nomRevt;
    }

    public void setNomRevt(String nomRevt) {
        this.nomRevt = nomRevt;
    }

    public boolean getEstIsolt() {
        return estIsolt;
    }

    public void setEstIsolt(boolean estIsolt) {
        this.estIsolt = estIsolt;
    }

    public float getPrixRevt() {
        return prixRevt;
    }

    public void setPrixRevt(float prixRevt) {
        this.prixRevt = prixRevt;
    }

    public Revetement ( int idRevt, String nomRevt, boolean estIsolt, float prixRevt){ //refaire le constructeur avec un booléen sur l'isolement
    this.idRevt = idRevt;
    this.prixRevt = prixRevt;
    this.nomRevt = nomRevt;
    this.estIsolt = estIsolt;
    }
@Override
    public String toString() {
        return nomRevt + " (" + prixRevt + " €/m²)";
    }

}
