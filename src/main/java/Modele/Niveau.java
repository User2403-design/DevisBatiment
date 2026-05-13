/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

public class Niveau {

    private int numeroNiveau;

    private ArrayList<Appartement> appartements;

    private final double hauteurPlafond = 3;

    private Point pointEscalier1;
    private Point pointEscalier2;

    public Niveau() {
        this.appartements = new ArrayList<>();
    }

    public Niveau(int numeroNiveau) {
        this.numeroNiveau = numeroNiveau;
        this.appartements = new ArrayList<>();
    }

    public double getHauteurPlafond() {
        return hauteurPlafond;
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

    public Point getPointEscalier1() {
        return pointEscalier1;
    }

    public void setPointEscalier1(Point pointEscalier1) {
        this.pointEscalier1 = pointEscalier1;
    }

    public Point getPointEscalier2() {
        return pointEscalier2;
    }

    public void setPointEscalier2(Point pointEscalier2) {
        this.pointEscalier2 = pointEscalier2;
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