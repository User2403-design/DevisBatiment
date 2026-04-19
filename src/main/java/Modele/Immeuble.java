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

import java.util.ArrayList;

public class Immeuble extends Batiment {

    // Vous l'aviez déjà commencé, on le garde
    private ArrayList<Niveau> niveaux;

    public Immeuble(String typeBatiment, float longueur, float largeur) {
        super(typeBatiment, longueur, largeur);
        this.niveaux = new ArrayList<>();
    }

    public ArrayList<Niveau> getNiveaux() {
        return niveaux;
    }

    public void setNiveaux(ArrayList<Niveau> niveaux) {
        this.niveaux = niveaux;
    }
}
