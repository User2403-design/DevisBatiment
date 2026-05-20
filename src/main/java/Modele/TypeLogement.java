/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

public enum TypeLogement {
    MAISON("Maison"),
    APPARTEMENT("Appartement");

    private final String label;

    TypeLogement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
