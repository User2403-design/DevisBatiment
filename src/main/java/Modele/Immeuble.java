/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

public class Immeuble extends Batiment {

    private ArrayList<Niveau> niveaux;

    private Point pointEscalier1;
    private Point pointEscalier2;

    public Immeuble(String typeBatiment, float longueur, float largeur) {
        super(typeBatiment, longueur, largeur);

        this.niveaux = new ArrayList<>();

        Niveau premierNiveau = new Niveau(1);
        premierNiveau.setPointEscalier1(pointEscalier1);
        premierNiveau.setPointEscalier2(pointEscalier2);

        this.niveaux.add(premierNiveau);

        this.setNbreNiveaux(1);
    }

    public ArrayList<Niveau> getNiveaux() {
        return niveaux;
    }

    public void setNiveaux(ArrayList<Niveau> niveaux) {
        this.niveaux = niveaux;

        for (Niveau niveau : this.niveaux) {
            niveau.setPointEscalier1(pointEscalier1);
            niveau.setPointEscalier2(pointEscalier2);
        }

        this.setNbreNiveaux(niveaux.size());
    }

    public Point getPointEscalier1() {
        return pointEscalier1;
    }

    public Point getPointEscalier2() {
        return pointEscalier2;
    }

    public void definirEscalierCommun(Point point1, Point point2) {
        this.pointEscalier1 = point1;
        this.pointEscalier2 = point2;

        for (Niveau niveau : niveaux) {
            niveau.setPointEscalier1(point1);
            niveau.setPointEscalier2(point2);
        }
    }

    public void ajouterNiveau() {
        int nouveauNumero = niveaux.size() + 1;

        Niveau nouveauNiveau = new Niveau(nouveauNumero);
        nouveauNiveau.setPointEscalier1(pointEscalier1);
        nouveauNiveau.setPointEscalier2(pointEscalier2);

        niveaux.add(nouveauNiveau);

        this.setNbreNiveaux(niveaux.size());
    }

    public void supprimerDernierNiveau() {
        if (niveaux.size() > 1) {
            niveaux.remove(niveaux.size() - 1);
            this.setNbreNiveaux(niveaux.size());
        }
    }
}