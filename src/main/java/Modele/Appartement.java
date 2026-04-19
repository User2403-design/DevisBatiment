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
//supprimer le extends car relations d'heritage incoherente
public class Appartement {
    
    //ArrayList pour stocker les pièces de l'appartement
    private ArrayList<Piece> pieces;
    private int idAppart;
    private int nbrePieces;

    public int getIdAppart() {
        return idAppart;
    }

    public void setIdAppart(int idAppart) {
        this.idAppart = idAppart;
    }

    public int getNbrePieces() {
        return nbrePieces;
    }

    public void setNbrePieces(int nbrePieces) {
        this.nbrePieces = nbrePieces;
    }
    
    
    
    
    
}
