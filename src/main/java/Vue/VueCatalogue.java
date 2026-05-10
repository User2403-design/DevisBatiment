/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

/**
 *
 * @author seb12
 */
public class VueCatalogue {
    private BorderPane root;
    private Accordion accordionCategories;

    public VueCatalogue() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f7f7f7;");

        // Titre de la fenêtre
        Label titre = new Label("Catalogue des Matériaux");
        titre.setFont(new Font("Arial Bold", 22));
        BorderPane.setAlignment(titre, Pos.CENTER);
        BorderPane.setMargin(titre, new Insets(0, 0, 20, 0));
        root.setTop(titre);

        // Composant Accordéon pour afficher les catégories déroulantes
        accordionCategories = new Accordion();
        root.setCenter(accordionCategories);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Accordion getAccordionCategories() {
        return accordionCategories;
    }
    
}
