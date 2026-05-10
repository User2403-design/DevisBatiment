/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import Modele.Mur;
import Modele.Piece;
import Modele.Revetement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class VuePlanAppartement {

    private BorderPane root;
    private Pane panePlan;

    private Label titre;
    private Label consigne;
    private Label infoMessage;

    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;

    private ListView<Piece> listePieces;
    private Label labelNbPieces;

    private Label labelDetailsPiece;
    private ListView<Mur> listeMurs;

    private ComboBox<String> comboSurface;
    private ComboBox<Revetement> comboRevetement;
    private Button boutonAppliquerRevetement;

    private ComboBox<String> comboTypeOuverture;
    private TextField champLargeurOuverture;
    private TextField champHauteurOuverture;
    private Button boutonAjouterOuverture;

    public VuePlanAppartement() {
        root = new BorderPane();

        titre = new Label("Plan du logement");
        titre.setFont(new Font(24));

        consigne = new Label("Cliquez sur 2 points pour créer une pièce. Cliquez sur une pièce pour la sélectionner.");
        consigne.setFont(new Font(16));

        infoMessage = new Label("Aucune pièce créée pour le moment.");
        infoMessage.setFont(new Font(14));

        VBox blocHaut = new VBox(10, titre, consigne, infoMessage);
        blocHaut.setAlignment(Pos.CENTER);
        blocHaut.setPadding(new Insets(20));

        panePlan = new Pane();
        panePlan.setPrefSize(900, 650);
        panePlan.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2px;"
        );

        Label titreListe = new Label("Pièces du logement");
        titreListe.setFont(new Font(16));

        listePieces = new ListView<>();
        listePieces.setPrefHeight(120);

        labelNbPieces = new Label("Nombre de pièces : 0");

        Label titreDetails = new Label("Détails de la pièce sélectionnée");
        titreDetails.setFont(new Font(16));

        labelDetailsPiece = new Label("Aucune pièce sélectionnée.");
        labelDetailsPiece.setWrapText(true);

        Label titreMurs = new Label("Murs de la pièce");
        titreMurs.setFont(new Font(15));

        listeMurs = new ListView<>();
        listeMurs.setPrefHeight(120);

        Label titreRevetement = new Label("Revêtement");
        titreRevetement.setFont(new Font(15));

        comboSurface = new ComboBox<>();
        comboSurface.setPromptText("Surface à modifier");
        comboSurface.setMaxWidth(Double.MAX_VALUE);

        comboRevetement = new ComboBox<>();
        comboRevetement.setPromptText("Choisir un revêtement");
        comboRevetement.setMaxWidth(Double.MAX_VALUE);

        boutonAppliquerRevetement = new Button("Appliquer le revêtement");
        boutonAppliquerRevetement.setMaxWidth(Double.MAX_VALUE);

        Label titreOuverture = new Label("Ouverture");
        titreOuverture.setFont(new Font(15));

        comboTypeOuverture = new ComboBox<>();
        comboTypeOuverture.getItems().addAll("Fenêtre", "Porte");
        comboTypeOuverture.setPromptText("Type d'ouverture");
        comboTypeOuverture.setMaxWidth(Double.MAX_VALUE);

        champLargeurOuverture = new TextField();
        champLargeurOuverture.setPromptText("Largeur en m");

        champHauteurOuverture = new TextField();
        champHauteurOuverture.setPromptText("Hauteur en m");

        boutonAjouterOuverture = new Button("Ajouter ouverture au mur");
        boutonAjouterOuverture.setMaxWidth(Double.MAX_VALUE);

        VBox blocDroite = new VBox(10,
                titreListe,
                listePieces,
                labelNbPieces,
                new Separator(),
                titreDetails,
                labelDetailsPiece,
                titreMurs,
                listeMurs,
                new Separator(),
                titreRevetement,
                comboSurface,
                comboRevetement,
                boutonAppliquerRevetement,
                new Separator(),
                titreOuverture,
                comboTypeOuverture,
                champLargeurOuverture,
                champHauteurOuverture,
                boutonAjouterOuverture
        );

        blocDroite.setPadding(new Insets(20));
        blocDroite.setPrefWidth(340);
        blocDroite.setStyle("-fx-background-color: #f0f0f0;");

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

        root.setTop(blocHaut);
        root.setCenter(panePlan);
        root.setRight(blocDroite);
        root.setBottom(blocBas);
    }

    public BorderPane getRoot() { return root; }

    public Pane getPanePlan() { return panePlan; }

    public Label getInfoMessage() { return infoMessage; }

    public Button getBoutonEffacerSelection() { return boutonEffacerSelection; }

    public Button getBoutonSupprimerDernierePiece() { return boutonSupprimerDernierePiece; }

    public Button getBoutonFermer() { return boutonFermer; }

    public ListView<Piece> getListePieces() { return listePieces; }

    public Label getLabelNbPieces() { return labelNbPieces; }

    public Label getLabelDetailsPiece() { return labelDetailsPiece; }

    public ListView<Mur> getListeMurs() { return listeMurs; }

    public ComboBox<String> getComboSurface() { return comboSurface; }

    public ComboBox<Revetement> getComboRevetement() { return comboRevetement; }

    public Button getBoutonAppliquerRevetement() { return boutonAppliquerRevetement; }

    public ComboBox<String> getComboTypeOuverture() { return comboTypeOuverture; }

    public TextField getChampLargeurOuverture() { return champLargeurOuverture; }

    public TextField getChampHauteurOuverture() { return champHauteurOuverture; }

    public Button getBoutonAjouterOuverture() { return boutonAjouterOuverture; }
}