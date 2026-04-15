/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author chloe
 */
public class Ouverture {
    private int idOuverture ; 
    private String typeOuverture ;

    public int getIdOuverture() {
        return idOuverture;
    }

    public void setIdOuverture(int idOuverture) {
        this.idOuverture = idOuverture;
    }

    public String getTypeOuverture() {
        return typeOuverture;
    }

    public void setTypeOuverture(String typeOuverture) {
        this.typeOuverture = typeOuverture;
    }

public Ouverture ( int idOuverture, String typeOuverture){
this.idOuverture = idOuverture;
this.typeOuverture = typeOuverture; 
}
    
    
}
