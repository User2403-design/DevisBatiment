/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

public class Piece {

    private int idPiece;

    private String usage;

    private Point coin1;
    private Point coin2;

    private float superficie;

    private Revetement revetementSol;
    private Revetement revetementPlafond;

    private ArrayList<Mur> murs;

    public Piece() {

        this.murs = new ArrayList<>();
    }

    public Piece(Point coin1, Point coin2, String usage) {

        this.coin1 = coin1;
        this.coin2 = coin2;

        this.usage = usage;

        this.superficie = calculerSuperficie();

        this.murs = new ArrayList<>();

        genererMurs();
    }

    private void genererMurs() {

        Point hautGauche =
                new Point(getXMin(), getYMin());

        Point hautDroit =
                new Point(getXMax(), getYMin());

        Point basDroit =
                new Point(getXMax(), getYMax());

        Point basGauche =
                new Point(getXMin(), getYMax());

        murs.add(
                new Mur(
                        "Mur Nord",
                        hautGauche,
                        hautDroit,
                        false
                )
        );

        murs.add(
                new Mur(
                        "Mur Est",
                        hautDroit,
                        basDroit,
                        false
                )
        );

        murs.add(
                new Mur(
                        "Mur Sud",
                        basDroit,
                        basGauche,
                        false
                )
        );

        murs.add(
                new Mur(
                        "Mur Ouest",
                        basGauche,
                        hautGauche,
                        false
                )
        );
    }
    
    
public Revetement getRevetementSol() {
    return revetementSol;
}

public void setRevetementSol(Revetement revetementSol) {
    this.revetementSol = revetementSol;
}

public Revetement getRevetementPlafond() {
    return revetementPlafond;
}

public void setRevetementPlafond(Revetement revetementPlafond) {
    this.revetementPlafond = revetementPlafond;
}

    public double calculerPrixTotal(double hauteurPlafond) {

        double total = 0;

        if (revetementSol != null) {

            total +=
                    getSuperficie()
                    *
                    revetementSol.getPrixRevt();
        }

        if (revetementPlafond != null) {

            total +=
                    getSuperficie()
                    *
                    revetementPlafond.getPrixRevt();
        }

        for (Mur m : murs) {

            total +=
                    m.calculerPrix(hauteurPlafond);
        }

        return total;
    }

    @Override
    public String toString() {

        return usage
                + " - "
                + String.format("%.2f", superficie)
                + " m²";
    }

    public ArrayList<Mur> getMurs() {
        return murs;
    }

    public float getXMin() {
        return Math.min(coin1.getX(), coin2.getX());
    }

    public float getXMax() {
        return Math.max(coin1.getX(), coin2.getX());
    }

    public float getYMin() {
        return Math.min(coin1.getY(), coin2.getY());
    }

    public float getYMax() {
        return Math.max(coin1.getY(), coin2.getY());
    }

    public float getLargeur() {
        return getXMax() - getXMin();
    }

    public float getHauteur() {
        return getYMax() - getYMin();
    }

    public float calculerSuperficie() {

        return getLargeur() * getHauteur();
    }

    public float getSuperficie() {
        return superficie;
    }

    public String getUsage() {
        return usage;
    }
}