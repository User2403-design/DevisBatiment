/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

public class Immeuble extends Batiment {
    
    private Point pointCouloir1;
    private Point pointCouloir2;

    private ArrayList<Niveau> niveaux;

    
    private Point pointEscalier1;
    private Point pointEscalier2;
    
   
    private Revetement revetementSolCouloir;
    private Revetement revetementPlafondCouloir;
    private Revetement revetementMurNordCouloir;
    private Revetement revetementMurEstCouloir;
    private Revetement revetementMurSudCouloir;
    private Revetement revetementMurOuestCouloir;

    
public Point getPointCouloir1() {
    return pointCouloir1;
}

public Point getPointCouloir2() {
    return pointCouloir2;
}

public void definirCouloirCommun(Point point1, Point point2) {
    this.pointCouloir1 = point1;
    this.pointCouloir2 = point2;

    for (Niveau niveau : niveaux) {
        niveau.setPointCouloir1(point1);
        niveau.setPointCouloir2(point2);
    }
}
    
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

        // On remet l'escalier sur chaque niveau.
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

    public Revetement getRevetementSolCouloir() {
        return revetementSolCouloir;
    }

    public void setRevetementSolCouloir(Revetement revetementSolCouloir) {
        this.revetementSolCouloir = revetementSolCouloir;
    }

    public Revetement getRevetementPlafondCouloir() {
        return revetementPlafondCouloir;
    }

    public void setRevetementPlafondCouloir(Revetement revetementPlafondCouloir) {
        this.revetementPlafondCouloir = revetementPlafondCouloir;
    }

    public Revetement getRevetementMurNordCouloir() {
        return revetementMurNordCouloir;
    }

    public void setRevetementMurNordCouloir(Revetement revetementMurNordCouloir) {
        this.revetementMurNordCouloir = revetementMurNordCouloir;
    }

    public Revetement getRevetementMurEstCouloir() {
        return revetementMurEstCouloir;
    }

    public void setRevetementMurEstCouloir(Revetement revetementMurEstCouloir) {
        this.revetementMurEstCouloir = revetementMurEstCouloir;
    }

    public Revetement getRevetementMurSudCouloir() {
        return revetementMurSudCouloir;
    }

    public void setRevetementMurSudCouloir(Revetement revetementMurSudCouloir) {
        this.revetementMurSudCouloir = revetementMurSudCouloir;
    }

    public Revetement getRevetementMurOuestCouloir() {
        return revetementMurOuestCouloir;
    }

    public void setRevetementMurOuestCouloir(Revetement revetementMurOuestCouloir) {
        this.revetementMurOuestCouloir = revetementMurOuestCouloir;
    }
    
    

    //  méthode qui place le même escalier sur tous les étages
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

        // Le nouvel étage récupère automatiquement l'escalier.
        nouveauNiveau.setPointEscalier1(pointEscalier1);
        nouveauNiveau.setPointEscalier2(pointEscalier2);

        niveaux.add(nouveauNiveau);
        this.setNbreNiveaux(niveaux.size());
    nouveauNiveau.setPointCouloir1(pointCouloir1);
nouveauNiveau.setPointCouloir2(pointCouloir2);
    }

    public void supprimerDernierNiveau() {
        if (niveaux.size() > 1) {
            niveaux.remove(niveaux.size() - 1);
            this.setNbreNiveaux(niveaux.size());
        }
    }
}
