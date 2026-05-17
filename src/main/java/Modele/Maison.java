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
                TypeLogement.MAISON  // Spécifier le type MAISON
        );
    }

    public Appartement getPlanMaison() {
        return planMaison;
    }

    public void setPlanMaison(Appartement planMaison) {
        this.planMaison = planMaison;
    }

    /**
     * Valide que le plan de la maison respecte les règles spécifiques
     */
    public boolean validerPlan() {
        return planMaison != null && planMaison.validerPlan();
    }

}