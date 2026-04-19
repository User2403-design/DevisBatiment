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

public class Immeuble extends Batiment {
    //ArrayList pour stocker les niveaux du batiment
    private ArrayList<Niveau> niveaux;
    
    public Immeuble (String typeBatiment, float longueur, float largeur){
        super (typeBatiment, longueur, largeur);
        this.niveaux = new ArrayList<>();
    }

}
