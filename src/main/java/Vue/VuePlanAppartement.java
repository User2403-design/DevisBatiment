/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Vue;

import Modele.Piece;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;


/**
 *
 * @author chloe
 */


public class VuePlanAppartement {

    private BorderPane root;

    private Pane panePlan;

    private Label titre;
    private Label consigne;
    private Label infoMessage;

    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;

    // 👉 Nouvelle partie : liste des pièces
    private ListView<Piece> listePieces;
    private Label labelNbPieces;

    public VuePlanAppartement() {

        root = new BorderPane();

        // ----------- HAUT -----------
        titre = new Label("Plan de l'appartement");
        titre.setFont(new Font(24));

        consigne = new Label("Cliquez sur 2 points pour créer une pièce.");
        consigne.setFont(new Font(16));

        infoMessage = new Label("Aucune pièce créée pour le moment.");
        infoMessage.setFont(new Font(14));

        VBox blocHaut = new VBox(10, titre, consigne, infoMessage);
        blocHaut.setAlignment(Pos.CENTER);
        blocHaut.setPadding(new Insets(20));

        // ----------- CENTRE -----------
        panePlan = new Pane();
        panePlan.setPrefSize(800, 600);
        panePlan.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2px;"
        );

        // ----------- DROITE (SIDEBAR) -----------

        Label titreListe = new Label("Pièces de l'appartement");
        titreListe.setFont(new Font(16));

        listePieces = new ListView<>();
        listePieces.setPrefHeight(200);

        labelNbPieces = new Label("Nombre de pièces : 0");

        VBox blocDroite = new VBox(10,
                titreListe,
                listePieces,
                labelNbPieces
        );

        blocDroite.setPadding(new Insets(20));
        blocDroite.setPrefWidth(250);
        blocDroite.setStyle("-fx-background-color: #f0f0f0;");

        // ----------- BAS -----------
        boutonEffacerSelection = new Button("Effacer la sélection");
        boutonSupprimerDernierePiece = new Button("Supprimer dernière pièce");
        boutonFermer = new Button("Fermer");

        HBox blocBas = new HBox(20,
                boutonEffacerSelection,
                boutonSupprimerDernierePiece,
                boutonFermer
        );

        blocBas.setAlignment(Pos.CENTER);
        blocBas.setPadding(new Insets(20));

        // ----------- ASSEMBLAGE -----------
        root.setTop(blocHaut);
        root.setCenter(panePlan);
        root.setRight(blocDroite); // 👈 LA SIDEBAR
        root.setBottom(blocBas);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Pane getPanePlan() {
        return panePlan;
    }

    public Label getInfoMessage() {
        return infoMessage;
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

    public ListView<Piece> getListePieces() {
        return listePieces;
    }

    public Label getLabelNbPieces() {
        return labelNbPieces;
    }
}