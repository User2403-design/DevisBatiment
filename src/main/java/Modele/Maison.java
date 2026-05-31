/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

public class Maison extends Batiment {
    
    private Appartement planMaison;

    public Maison(String typeBatiment, float longueur, float largeur) {
        super(typeBatiment, longueur, largeur);

        this.planMaison = new Appartement(
                "Plan maison",
                new Point(0, 0),
                new Point(longueur, largeur),
                TypeLogement.MAISON  // Spécifier le type MAISON ( provient d'enum dans la classe typelogement) : differnencie de l'appartement pour la logique de fonctionnement
        );
    }

    public Appartement getPlanMaison() {
        return planMaison;
    }

    public void setPlanMaison(Appartement planMaison) {
        this.planMaison = planMaison;
    }

    public boolean validerPlan() {
        return planMaison != null && planMaison.validerPlan();
    }

}