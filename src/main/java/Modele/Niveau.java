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

    // Deux points pour représenter le rectangle de l'escalier.
    // On évite de créer une nouvelle classe pour garder le code simple.
    private Point pointEscalier1;
    private Point pointEscalier2;
    
    private Point pointCouloir1;
private Point pointCouloir2;

public Point getPointCouloir1() {
    return pointCouloir1;
}

public void setPointCouloir1(Point pointCouloir1) {
    this.pointCouloir1 = pointCouloir1;
}

public Point getPointCouloir2() {
    return pointCouloir2;
}

public void setPointCouloir2(Point pointCouloir2) {
    this.pointCouloir2 = pointCouloir2;
}

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
