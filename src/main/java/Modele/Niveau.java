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


public class Niveau {

    // Numéro d'étage : 1, 2, 3...
    private int numeroNiveau;

    // Liste des appartements de ce niveau
    private ArrayList<Appartement> appartements;

    public Niveau() {
        this.appartements = new ArrayList<>();
    }

    public Niveau(int numeroNiveau) {
        this.numeroNiveau = numeroNiveau;
        this.appartements = new ArrayList<>();
    }

    public int getNumeroNiveau() {
        return numeroNiveau;
    }

    public void setNumeroNiveau(int numeroNiveau) {
        this.numeroNiveau = numeroNiveau;
    }

    public ArrayList<Appartement> getAppartements() {
        return appartements;
    }

    public void setAppartements(ArrayList<Appartement> appartements) {
        this.appartements = appartements;
    }

    public void ajouterAppartement(Appartement appartement) {
        appartements.add(appartement);
    }

    public void supprimerDernierAppartement() {
        if (!appartements.isEmpty()) {
            appartements.remove(appartements.size() - 1);
        }
    }

    public int getNombreAppartements() {
        return appartements.size();
    }
}