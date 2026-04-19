/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * @author chloe
 */

public class VuePlanBatiment {

    // Conteneur principal
    private BorderPane root;

    // Zone de dessin
    private Pane panePlan;

    // Labels
    private Label titre;
    private Label consigne;
    private Label infoSurface;

    // Boutons
    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;

    public VuePlanBatiment() {

        
        root = new BorderPane();

      
        // Partie haute : titre + consignes
       
        titre = new Label("Plan du bâtiment");
        titre.setFont(new Font(24));

        consigne = new Label("Cliquez sur 2 points opposés pour créer une pièce rectangulaire.");
        consigne.setFont(new Font(16));

        infoSurface = new Label("Aucune pièce créée pour le moment.");
        infoSurface.setFont(new Font(14));

        VBox blocHaut = new VBox();
        blocHaut.setSpacing(10);
        blocHaut.setAlignment(Pos.CENTER);
        blocHaut.setPadding(new Insets(20));
        blocHaut.getChildren().addAll(titre, consigne, infoSurface);

        
        // Partie centrale : zone du plan

        panePlan = new Pane();
        panePlan.setPrefSize(900, 600);
        panePlan.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2px;"
        );

      
        // Partie basse : boutons
    
        boutonEffacerSelection = new Button("Effacer la sélection");
        boutonSupprimerDernierePiece = new Button("Supprimer la dernière pièce");
        boutonFermer = new Button("Fermer");

        HBox blocBas = new HBox();
        blocBas.setSpacing(20);
        blocBas.setAlignment(Pos.CENTER);
        blocBas.setPadding(new Insets(20));
        blocBas.getChildren().addAll(
                boutonEffacerSelection,
                boutonSupprimerDernierePiece,
                boutonFermer
        );

        // Placement dans le BorderPane
        root.setTop(blocHaut);
        root.setCenter(panePlan);
        root.setBottom(blocBas);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Pane getPanePlan() {
        return panePlan;
    }

    public Label getInfoSurface() {
        return infoSurface;
    }

    public Button getBoutonEffacerSelection() {
        return boutonEffacerSelection;
    }

    public Button getBoutonSupprimerDernierePiece() {
        return boutonSupprimerDernierePiece;
    }

    public Button getBoutonFermer() {
        return boutonFermer;
    }
}