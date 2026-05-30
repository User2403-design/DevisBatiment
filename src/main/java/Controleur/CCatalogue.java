/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;
import Modele.GestionCatalogue;
import Modele.Revetement;
import Vue.VueCatalogue;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import java.util.List;

public class CCatalogue {
    private VueCatalogue vue;
    private GestionCatalogue catalogue;

    public CCatalogue(Stage fenetre) {
        this.vue = new VueCatalogue();
        this.catalogue = new GestionCatalogue();
        peuplerCatalogue();
        Scene scene = new Scene(vue.getRoot(), 400, 600);
        fenetre.setScene(scene);
        fenetre.setTitle("Catalogue Bâtir & Co");
        fenetre.show();
    }

    
    private void peuplerCatalogue() {
        List<String> rubriques = catalogue.getNomsRubriques();

        for (String rubrique : rubriques) {
            ListView<String> listeProduits = new ListView<>();
            List<Revetement> produits = catalogue.getProduits(rubrique);

            // remplissage de la liste avec les produits et leurs prix
            for (Revetement r : produits) {
                listeProduits.getItems().add(r.getNomRevt() + " - " + r.getPrixRevt() + " €/m²");
            }

            // création de l'onglet déroulant pour cette rubrique
            TitledPane panneauRubrique = new TitledPane(rubrique, listeProduits);
            vue.getAccordionCategories().getPanes().add(panneauRubrique);
        }

        // déployer le premier volet de l'accordéon après verificatoin qu'il en existe au moins 1
        if (!vue.getAccordionCategories().getPanes().isEmpty()) {
            vue.getAccordionCategories().setExpandedPane(vue.getAccordionCategories().getPanes().get(0));
        }
    }
    
}
