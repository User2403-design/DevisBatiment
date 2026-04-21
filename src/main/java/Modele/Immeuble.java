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

    // Liste des niveaux de l'immeuble
    private ArrayList<Niveau> niveaux;

    public Immeuble(String typeBatiment, float longueur, float largeur) {
        super(typeBatiment, longueur, largeur);
        this.niveaux = new ArrayList<>();

        // On crée directement un premier étage
        this.niveaux.add(new Niveau(1));
        this.setNbreNiveaux(1);
    }

    public ArrayList<Niveau> getNiveaux() {
        return niveaux;
    }

    public void setNiveaux(ArrayList<Niveau> niveaux) {
        this.niveaux = niveaux;
        this.setNbreNiveaux(niveaux.size());
    }

    public void ajouterNiveau() {
        int nouveauNumero = niveaux.size() + 1;
        niveaux.add(new Niveau(nouveauNumero));
        this.setNbreNiveaux(niveaux.size());
    }

    public void supprimerDernierNiveau() {
        // On garde toujours au moins 1 étage
        if (niveaux.size() > 1) {
            niveaux.remove(niveaux.size() - 1);
            this.setNbreNiveaux(niveaux.size());
        }
    }
}