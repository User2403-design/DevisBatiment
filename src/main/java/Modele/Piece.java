/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author seb12
 */

public class Piece {

    private int idPiece;
    private String usage;

    // Les 2 points cliqués par l'utilisateur
    private Point coin1;
    private Point coin2;

    // Surface de la pièce
    private float superficie;

    public Piece() {
    }

    public Piece(Point coin1, Point coin2, String usage) {
        this.coin1 = coin1;
        this.coin2 = coin2;
        this.usage = usage;
        this.superficie = calculerSuperficie();
    }

    public int getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(int idPiece) {
        this.idPiece = idPiece;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public Point getCoin1() {
        return coin1;
    }

    public void setCoin1(Point coin1) {
        this.coin1 = coin1;
    }

    public Point getCoin2() {
        return coin2;
    }

    public void setCoin2(Point coin2) {
        this.coin2 = coin2;
    }

    public float getSuperficie() {
        return superficie;
    }

    public void setSuperficie(float superficie) {
        this.superficie = superficie;
    }

    // Coordonnée minimale en x
    public float getXMin() {
        return Math.min(coin1.getX(), coin2.getX());
    }

    // Coordonnée maximale en x
    public float getXMax() {
        return Math.max(coin1.getX(), coin2.getX());
    }

    // Coordonnée minimale en y
    public float getYMin() {
        return Math.min(coin1.getY(), coin2.getY());
    }

    // Coordonnée maximale en y
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
}
    

