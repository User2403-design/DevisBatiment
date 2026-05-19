////*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */

package Controleur;

import Modele.Appartement;
import Modele.Batiment;
import Modele.GestionCatalogue;
import Modele.Immeuble;
import Modele.Maison;
import Modele.Revetement;
import Modele.Stockage;
import Vue.VueBatiment;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class CBatiment {

    private VueBatiment vue;
    private Stage fenetre;

    private String typeBatiment;

    private GestionCatalogue catalogue;

    public CBatiment(Stage fenetre) {

        this.fenetre = fenetre;

        this.vue = new VueBatiment();

        this.catalogue = new GestionCatalogue();

        Scene scene = new Scene(vue.getRoot());

        fenetre.setScene(scene);

        fenetre.setTitle("Bâtir & Co - Configuration");

        fenetre.setMaximized(true);

        fenetre.show();

        initCatalogueCombos();

        vue.getMaison().setOnAction(e -> {

            typeBatiment = "Maison";

            vue.setChampsVisibles(true);
        });

        vue.getImmeuble().setOnAction(e -> {

            typeBatiment = "Immeuble";

            vue.setChampsVisibles(true);
        });

        vue.getValider().setOnAction(e -> {

            validerCreation();
        });
    }

    private void initCatalogueCombos() {

        List isolants = catalogue.getProduits("Isolant");

        vue.getComboIsolant().getItems().addAll(isolants);

        List facades = catalogue.getProduits("Facade");

        vue.getComboFacade().getItems().addAll(facades);

        StringConverter<Revetement> converter = new StringConverter<>() {

            @Override
            public String toString(Revetement r) {

                if (r == null) {
                    return "";
                }

                return r.getNomRevt()
                        + " ("
                        + r.getPrixRevt()
                        + "€/m²)";
            }

            @Override
            public Revetement fromString(String string) {

                return null;
            }
        };

        vue.getComboIsolant().setConverter(converter);

        vue.getComboFacade().setConverter(converter);
    }

    private void validerCreation() {

        try {

            if (typeBatiment == null) {

                afficherErreur(
                        "Veuillez d'abord choisir entre Maison et Immeuble."
                );

                return;
            }

            float longueur =
                    Float.parseFloat(
                            vue.getChampLongueur().getText()
                    );

            float largeur =
                    Float.parseFloat(
                            vue.getChampLargeur().getText()
                    );

            Revetement isolant =
                    vue.getComboIsolant().getValue();

            Revetement facade =
                    vue.getComboFacade().getValue();

            if (
                    longueur <= 0
                    ||
                    largeur <= 0
                    ||
                    isolant == null
                    ||
                    facade == null
            ) {

                afficherErreur(
                        "Veuillez remplir les dimensions et choisir les matériaux extérieurs."
                );

                return;
            }

            Batiment batiment;

            if (typeBatiment.equals("Maison")) {

                batiment =
                        new Maison(
                                typeBatiment,
                                longueur,
                                largeur
                        );

            } else {

                batiment =
                        new Immeuble(
                                typeBatiment,
                                longueur,
                                largeur
                        );
            }

            batiment.setIsolantExt(isolant);

            batiment.setRevtFacade(facade);

            Stockage.batiments.add(batiment);

            vue.getLabelCalcSuperficie().setVisible(true);

            vue.getLabelCalcSuperficie().setText(
                    "Superficie : "
                            + batiment.getSuperficie()
                            + " m²"
            );

            if (batiment instanceof Maison) {

                Appartement planMaison =
                        ((Maison) batiment).getPlanMaison();

                new CPlanMaison(
                        fenetre,
                        planMaison
                );

            } else {

                new CPlanImmeuble(
                        fenetre,
                        (Immeuble) batiment
                );
            }

        } catch (NumberFormatException ex) {

            afficherErreur(
                    "Les dimensions doivent être des nombres valides."
            );
        }
    }

    private void afficherErreur(String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText("Erreur de saisie");

        alert.setContentText(message);

        alert.showAndWait();
    }
}


//package Controleur;
//
//import Modele.Appartement;
//import Modele.Batiment;
//import Modele.Immeuble;
//import Modele.Maison;
//import Vue.VueBatiment;
//import javafx.stage.Stage;
//
//public class CBatiment {
//
//    private VueBatiment vue;
//    private Stage fenetre;
//    private Batiment batiment;
//
//    public CBatiment(Stage fenetre, Batiment batiment) {
//        this.fenetre = fenetre;
//        this.batiment = batiment;
//        this.vue = new VueBatiment();
//
//        // On s'assure que la scène est bien configurée
//        if (fenetre.getScene() != null) {
//            fenetre.getScene().setRoot(vue.getRoot());
//        }
//        
//        fenetre.setTitle("Gestion du Bâtiment : " + batiment.getNom());
//
//        initEvents();
//        rafraichirInfos();
//    }
//
//    private void initEvents() {
//        // Bouton pour voir le plan de masse (vue de dessus extérieure)
//        vue.getBoutonEditerPlan().setOnAction(e -> {
//            Stage fenetrePlan = new Stage();
//            new CPlanBatiment(fenetrePlan, batiment);
//        });
//
//        // Bouton pour l'aménagement intérieur
//        vue.getBoutonEditerAppartement().setOnAction(e -> {
//            if (batiment instanceof Maison) {
//                // Si c'est une maison, on édite directement son appartement unique
//                editerAppartement(((Maison) batiment).getAppartement());
//            } else if (batiment instanceof Immeuble) {
//                // Si c'est un immeuble, on passe par la vue de gestion des appartements
//                Stage fenetreImmeuble = new Stage();
//                new CPlanImmeuble(fenetreImmeuble, (Immeuble) batiment);
//            }
//        });
//
//        vue.getBoutonRetour().setOnAction(e -> fenetre.close());
//    }
//
//    private void editerAppartement(Appartement a) {
//        if (a != null) {
//            // true si c'est une maison, false sinon (appartement d'immeuble)
//            boolean estMaison = (batiment instanceof Maison);
//            new CPlanAppartement(new Stage(), a, estMaison);
//        }
//    }
//
//    private void rafraichirInfos() {
//        String typeLogement = (batiment instanceof Maison) ? "Maison Individuelle" : "Immeuble Collectif";
//        String infos = "Nom : " + batiment.getNom() + "\n"
//                     + "Adresse : " + batiment.getAdresse() + "\n"
//                     + "Type : " + typeLogement + "\n"
//                     + "Nombre de niveaux : " + batiment.getNiveaux().size();
//        vue.getLabelInfos().setText(infos);
//    }
//}