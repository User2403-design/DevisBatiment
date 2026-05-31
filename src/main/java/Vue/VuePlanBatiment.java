/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;


//interface organisée en zones : Top : Titre, consignes et boutons de navigation rapide; Center : Zone d'édition graphique du plan; Right : Panneau d'information détaillé et boutons de gestion de session.
 
public class VuePlanBatiment {

    private BorderPane root;

    private Pane panePlan;

    private Label titre;
    private Label consigne;
    private Label infoSurface;

    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;
    
    private HBox conteneurBoutonsTop;

    public VuePlanBatiment() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

       //bloc top
        titre = new Label("Plan du bâtiment");
        titre.setFont(new Font(28));
        titre.setStyle("-fx-font-weight: bold;");

        consigne = new Label("Cliquez sur 2 points opposés pour créer une pièce rectangulaire.");
        consigne.setFont(new Font(13));

        VBox blocInfoText = new VBox();
        blocInfoText.setSpacing(8);
        blocInfoText.setAlignment(Pos.CENTER_LEFT);
        blocInfoText.getChildren().addAll(titre, consigne);

        conteneurBoutonsTop = new HBox();
        conteneurBoutonsTop.setSpacing(15);
        conteneurBoutonsTop.setAlignment(Pos.CENTER_RIGHT);
        conteneurBoutonsTop.setPadding(new Insets(0, 20, 0, 0));

        BorderPane topContent = new BorderPane();
        topContent.setLeft(blocInfoText);
        topContent.setRight(conteneurBoutonsTop);
        topContent.setPadding(new Insets(20));
        topContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");

       //bloc central : affiche le plan
        VBox conteneurPlan = new VBox();
        conteneurPlan.setPadding(new Insets(20));
        conteneurPlan.setAlignment(Pos.CENTER); 

        panePlan = new Pane();
        panePlan.setPrefSize(800, 600);
        panePlan.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        conteneurPlan.getChildren().add(panePlan);

        //bloc droit : sidebar
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(320);
        sidebar.setSpacing(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 0 2;");

        Label titreSidebar = new Label("Détails de la sélection");
        titreSidebar.setFont(new Font("Arial Bold", 16));

        infoSurface = new Label("Aucune pièce créée pour le moment.");
        infoSurface.setFont(new Font(12));
        infoSurface.setWrapText(true);
        infoSurface.setStyle("-fx-text-alignment: justify;");

        VBox blocInfoSurface = new VBox();
        blocInfoSurface.setSpacing(10);
        blocInfoSurface.setStyle("-fx-border-color: #eee; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #fafafa;");
        blocInfoSurface.getChildren().addAll(titreSidebar, new Separator(), infoSurface);

       
        boutonEffacerSelection = new Button("Effacer la sélection");
        boutonEffacerSelection.setPrefWidth(Double.MAX_VALUE);
        boutonEffacerSelection.setStyle("-fx-padding: 10;");

        boutonSupprimerDernierePiece = new Button("Supprimer dernière pièce");
        boutonSupprimerDernierePiece.setPrefWidth(Double.MAX_VALUE);
        boutonSupprimerDernierePiece.setStyle("-fx-padding: 10;");

        boutonFermer = new Button("Quitter l'éditeur");
        boutonFermer.setPrefWidth(Double.MAX_VALUE);
        boutonFermer.setStyle("-fx-padding: 10; -fx-text-fill: white; -fx-background-color: #d32f2f; -fx-font-weight: bold;");

        VBox blocBoutonsAction = new VBox();
        blocBoutonsAction.setSpacing(10);
        blocBoutonsAction.getChildren().addAll(
                boutonEffacerSelection,
                boutonSupprimerDernierePiece,
                new Separator(),
                boutonFermer
        );

        
        sidebar.getChildren().addAll(blocInfoSurface, blocBoutonsAction);
        VBox.setVgrow(blocInfoSurface, Priority.ALWAYS);

        
        root.setTop(topContent);
        root.setCenter(conteneurPlan);
        root.setRight(sidebar);
    }

    //getters pour CPlanBatiment
    
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
    
    public HBox getConteneurBoutonsTop() {
        return conteneurBoutonsTop;
    }
}



