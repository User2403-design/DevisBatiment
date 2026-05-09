/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import Modele.Batiment;
import Modele.Immeuble;
import Modele.Maison;
import Modele.Stockage;
import Modele.GestionCatalogue;
import Modele.Revetement;
import Vue.VueBatiment;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur gérant la création initiale du bâtiment.
 * Correction : Chargement correct des rubriques du catalogue.
 */
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

        // Initialisation des données du catalogue
        initCatalogueCombos();

        vue.getMaison().setOnAction(e -> { typeBatiment = "Maison"; vue.setChampsVisibles(true); });
        vue.getImmeuble().setOnAction(e -> { typeBatiment = "Immeuble"; vue.setChampsVisibles(true); });
        vue.getValider().setOnAction(e -> validerCreation());
    }

    private void initCatalogueCombos() {
        // 1. Récupérer la rubrique exacte du fichier Catalogue.txt
        // Note : Le nom doit correspondre exactement au texte après "Rubrique:"
        List<Revetement> eltsExterieurs = catalogue.getProduits("Facade");

        if (eltsExterieurs.isEmpty()) {
            System.out.println("Attention : La rubrique 'Facade' n'a renvoye aucun produit.");
        }

        // 2. Filtrer pour remplir les deux ComboBox distinctes
        // On cherche les isolants pour la première
        List<Revetement> isolants = eltsExterieurs.stream()
                .filter(r -> r.getNomRevt().toLowerCase().contains("Isolant"))
                .collect(Collectors.toList());

        // On cherche les revêtements (enduit, bardage) pour la seconde
        List<Revetement> facades = eltsExterieurs.stream()
                .filter(r -> !r.getNomRevt().toLowerCase().contains("Facade"))
                .collect(Collectors.toList());

        vue.getComboIsolant().getItems().addAll(isolants);
        vue.getComboFacade().getItems().addAll(facades);

        // 3. Formattage de l'affichage (éviter l'adresse mémoire de l'objet)
        StringConverter<Revetement> converter = new StringConverter<>() {
            @Override 
            public String toString(Revetement r) { 
                return (r == null) ? "" : r.getNomRevt() + " (" + r.getPrixRevt() + "€/m²)"; 
            }
            @Override 
            public Revetement fromString(String string) { return null; }
        };
        
        vue.getComboIsolant().setConverter(converter);
        vue.getComboFacade().setConverter(converter);
    }

    private void validerCreation() {
        try {
            if (typeBatiment == null) { 
                afficherErreur("Veuillez d'abord choisir entre Maison et Immeuble."); 
                return; 
            }

            float lon = Float.parseFloat(vue.getChampLongueur().getText());
            float lar = Float.parseFloat(vue.getChampLargeur().getText());
            
            Revetement isolant = vue.getComboIsolant().getValue();
            Revetement facade = vue.getComboFacade().getValue();

            if (lon <= 0 || lar <= 0 || isolant == null || facade == null) {
                afficherErreur("Veuillez remplir les dimensions et choisir les matériaux extérieurs.");
                return;
            }

            Batiment b = typeBatiment.equals("Maison") ? new Maison(typeBatiment, lon, lar) : new Immeuble(typeBatiment, lon, lar);
            b.setIsolantExt(isolant);
            b.setRevtFacade(facade);

            Stockage.batiments.add(b);
            vue.getLabelCalcSuperficie().setVisible(true);
            vue.getLabelCalcSuperficie().setText("Superficie : " + b.getSuperficie() + " m²");

            Stage nF = new Stage();
            if (b instanceof Maison) new CPlanBatiment(nF, b);
            else new CPlanImmeuble(nF, (Immeuble) b);

        } catch (NumberFormatException ex) {
            afficherErreur("Les dimensions doivent être des nombres valides.");
        }
    }

    private void afficherErreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Erreur de saisie");
        a.setContentText(msg);
        a.showAndWait();
    }
}
