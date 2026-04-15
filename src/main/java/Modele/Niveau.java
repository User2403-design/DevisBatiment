/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author seb12
 */
public class Niveau extends Immeuble {
    
    private int idNiveau;
    private int nbreAppartements;
    private float hauteurPlafond;

    public int getIdNiveau() {
        return idNiveau;
    }

    public void setIdNiveau(int idNiveau) {
        this.idNiveau = idNiveau;
    }

    public int getNbreAppartements() {
        return nbreAppartements;
    }

    public void setNbreAppartements(int nbreAppartements) {
        this.nbreAppartements = nbreAppartements;
    }

    public float getHauteurPlafond() {
        return hauteurPlafond;
    }

    public void setHauteurPlafond(float hauteurPlafond) {
        this.hauteurPlafond = hauteurPlafond;
    }
    
    
}
