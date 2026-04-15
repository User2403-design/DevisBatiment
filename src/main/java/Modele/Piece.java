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
    //mur, sol, plafond, revetement : boolean pour verif l'appartenance

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
    
    
    
}
