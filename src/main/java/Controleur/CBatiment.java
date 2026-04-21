/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;

import Modele.Batiment;
import Modele.Immeuble;
import Modele.Maison;
import Modele.Stockage;
import Vue.VueBatiment;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class CBatiment {

    private VueBatiment vue;
    private Stage fenetre;

    // Type choisi par l'utilisateur
    private String typeBatiment;

    public CBatiment(Stage fenetre) {
        this.fenetre = fenetre;

        vue = new VueBatiment();

        Scene scene = new Scene(vue.getRoot());
        fenetre.setScene(scene);
        fenetre.setTitle("Bâtir & Co");
        fenetre.setMaximized(true);
        fenetre.show();

        // Bouton maison
        vue.getMaison().setOnAction(e -> {
            typeBatiment = "Maison";
            afficherChampsSaisie();
        });

        // Bouton immeuble
        vue.getImmeuble().setOnAction(e -> {
            typeBatiment = "Immeuble";
            afficherChampsSaisie();
        });

        // Bouton valider
        vue.getValider().setOnAction(e -> {
            try {
                // Vérification du type choisi
                if (typeBatiment == null) {
                    afficherErreur("Veuillez choisir un type de bâtiment.");
                    return;
                }

                // Lecture des dimensions
                float longueur = Float.parseFloat(vue.getChampLongueur().getText()); //  récupère ce que l’utilisateur a écrit dans le champ, et le transforme en nombre pour pouvoir faire des calculs 
                float largeur = Float.parseFloat(vue.getChampLargeur().getText());

                // Vérification des valeurs
                if (longueur <= 0 || largeur <= 0) {
                    afficherErreur("La longueur et la largeur doivent être positives.");
                    return;
                }

                Batiment batiment;

                // Création du bon type de bâtiment
                if (typeBatiment.equals("Maison")) {
                    batiment = new Maison(typeBatiment, longueur, largeur);
                } else {
                    batiment = new Immeuble(typeBatiment, longueur, largeur);
                }

                // Stockage du bâtiment créé
                Stockage.batiments.add(batiment);

                // Affichage de la superficie
                vue.getLabelCalcSuperficie().setVisible(true); // setVisible(true/false) rend un element visible ou non 
                vue.getLabelCalcSuperficie().setText(
                        "Superficie du bâtiment : " + batiment.getSuperficie() + " m²"
                );

                // Nouvelle fenêtre pour la suite
                Stage nouvelleFenetre = new Stage();

                // Si c'est une maison : plan normal des pièces
                if (batiment instanceof Maison) {
                    new CPlanBatiment(nouvelleFenetre, batiment);
                }

                // Si c'est un immeuble : plan par étage / appartement
                if (batiment instanceof Immeuble) {
                    new CPlanImmeuble(nouvelleFenetre, (Immeuble) batiment);
                }

            } catch (NumberFormatException ex) {
                afficherErreur("Veuillez entrer des nombres valides pour la longueur et la largeur.");
            }
        });
    }

    // Affiche les champs de saisie
    private void afficherChampsSaisie() {
        vue.getLabelLongueur().setVisible(true);
        vue.getChampLongueur().setVisible(true);

        vue.getLabelLargeur().setVisible(true);
        vue.getChampLargeur().setVisible(true);

        vue.getValider().setVisible(true);
    }

    // Petite fenêtre d'erreur
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}