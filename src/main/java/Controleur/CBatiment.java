/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import javafx.scene.Scene;
import javafx.stage.Stage;
import Vue.VueBatiment;
import Modele.Batiment;
import Modele.Stockage;

public class CBatiment {

    private VueBatiment vue;
    private Stage fenetre;

    private String typeBatiment;

    public CBatiment(Stage fenetre) {

        this.fenetre = fenetre;

        // création de la vue
        vue = new VueBatiment();

        // création de la scène + affichage
        Scene scene = new Scene(vue.getRoot(), 600, 400);
        fenetre.setScene(scene);
        fenetre.setTitle("Bâtir & Co");
        fenetre.show();

        // clic Maison
        vue.getMaison().setOnAction(e -> {
            typeBatiment = "Maison";
            afficherSuperficie();
        });

        // clic Immeuble
        vue.getImmeuble().setOnAction(e -> {
            typeBatiment = "Immeuble";
            afficherSuperficie();
        });

        // bouton valider
        vue.getValider().setOnAction(e -> {

            try {
                // sécurité : vérifier le type
                if (typeBatiment == null) {
                    System.out.println("Erreur : choisissez Maison ou Immeuble !");
                    return;
                }

                // récupérer texte
                String texteSuperficie = vue.getChampSuperficie().getText();

                // sécurité conversion
                float superficie = Float.parseFloat(texteSuperficie);

                // création objet
                Batiment batiment = new Batiment(typeBatiment, superficie);

                // stockage global
                Stockage.batiments.add(batiment);

                // debug
                System.out.println("Bâtiment créé !");
                System.out.println("Type : " + batiment.getTypeBatiment());
                System.out.println("Superficie : " + batiment.getSuperficie());

            } catch (NumberFormatException ex) {
                System.out.println("Erreur : superficie invalide !");
            }
        });
    }

    private void afficherSuperficie() {
        vue.getLabelSuperficie().setVisible(true);
        vue.getChampSuperficie().setVisible(true);
        vue.getValider().setVisible(true);
    }
}