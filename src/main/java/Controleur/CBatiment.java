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

/**
 *
 * @author chloe
 */

public class CBatiment {

    private VueBatiment vue;
    private Stage fenetre;

    // Type choisi par l'utilisateur
    private String typeBatiment;

    public CBatiment(Stage fenetre) {
        this.fenetre = fenetre;

        // Création de la vue
        vue = new VueBatiment();

        // Création de la scène
        Scene scene = new Scene(vue.getRoot());

        fenetre.setScene(scene);
        fenetre.setTitle("Bâtir & Co");
        fenetre.setMaximized(true);
        fenetre.show();

        // Bouton Maison
        vue.getMaison().setOnAction(e -> {
            typeBatiment = "Maison";
            afficherChampsSaisie();
        });

        // Bouton Immeuble
        vue.getImmeuble().setOnAction(e -> {
            typeBatiment = "Immeuble";
            afficherChampsSaisie();
        });

        // Bouton Valider
        vue.getValider().setOnAction(e -> {
            try {
                // Vérifie qu'un type a bien été choisi
                if (typeBatiment == null) {
                    afficherErreur("Veuillez choisir un type de bâtiment.");
                    return;
                }

                // Lecture des dimensions
                float longueur = Float.parseFloat(vue.getChampLongueur().getText());
                float largeur = Float.parseFloat(vue.getChampLargeur().getText());

                // Vérifie que les dimensions sont positives
                if (longueur <= 0 || largeur <= 0) {
                    afficherErreur("La longueur et la largeur doivent être positives.");
                    return;
                }

                // Création du bâtiment
                Batiment batiment;

                if (typeBatiment.equals("Maison")) {
                    batiment = new Maison(typeBatiment, longueur, largeur);
                } else {
                    batiment = new Immeuble(typeBatiment, longueur, largeur);
                }

                // Ajout au stockage
                Stockage.batiments.add(batiment);

                // Affichage de la surface
                vue.getLabelCalcSuperficie().setText(
                        "Superficie du bâtiment : " + batiment.getSuperficie() + " m²"
                );

                // Ouvre la fenêtre du plan
                Stage nouvelleFenetre = new Stage();
                new CPlanBatiment(nouvelleFenetre, batiment);

            } catch (NumberFormatException ex) {
                afficherErreur("Veuillez entrer des nombres valides pour la longueur et la largeur.");
            }
        });
    }

    // Affiche les champs quand l'utilisateur choisit un type
    private void afficherChampsSaisie() {
        vue.getLabelLongueur().setVisible(true);
        vue.getChampLongueur().setVisible(true);

        vue.getLabelLargeur().setVisible(true);
        vue.getChampLargeur().setVisible(true);

        vue.getValider().setVisible(true);
    }

    // Petite méthode pratique pour les messages d'erreur
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}