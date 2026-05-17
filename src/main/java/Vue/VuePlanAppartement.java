package Vue;

import Modele.Mur;
import Modele.Piece;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class VuePlanAppartement {

    private BorderPane root;
    private Pane panePlan;

    private Label titre;
    private Label infoMessage;

    private Button boutonOuvrirRevetements;
    private Button boutonOuvrirOuvertures;
    private Button boutonCatalogue;

    private ListView<Piece> listePieces;
    private Label labelNbPieces;
    private Label labelDetailsPiece;
    private ListView<Mur> listeMurs;

    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;

    public VuePlanAppartement() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #FDF7F2;");

        titre = new Label("Plan du logement");
        titre.setFont(new Font("Arial", 26));
        titre.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        infoMessage = new Label("Cliquez sur 2 points pour créer une pièce.");
        infoMessage.setFont(new Font("Arial", 14));
        infoMessage.setStyle("-fx-text-fill: #555555;");

        VBox blocTexteHaut = new VBox(5, titre, infoMessage);
        blocTexteHaut.setAlignment(Pos.CENTER_LEFT);

        boutonCatalogue = new Button("Catalogue");
        boutonOuvrirRevetements = new Button("Revêtements");
        boutonOuvrirOuvertures = new Button("Ouvertures");

        appliquerStyleBouton(boutonCatalogue, "#A2D2FF");
        appliquerStyleBouton(boutonOuvrirRevetements, "#B8E0D2");
        appliquerStyleBouton(boutonOuvrirOuvertures, "#CDB4DB");

        HBox blocBoutonsMenu = new HBox(
                15,
                boutonCatalogue,
                boutonOuvrirRevetements,
                boutonOuvrirOuvertures
        );

        blocBoutonsMenu.setAlignment(Pos.CENTER_RIGHT);

        BorderPane topContent = new BorderPane();
        topContent.setLeft(blocTexteHaut);
        topContent.setRight(blocBoutonsMenu);
        topContent.setPadding(new Insets(15, 25, 15, 25));
        topContent.setStyle(
                "-fx-background-color: #F8EDEB;"
                + "-fx-border-color: #E8C2CA;"
                + "-fx-border-width: 0 0 2 0;"
        );

        panePlan = new Pane();
        panePlan.setPrefSize(900, 650);
        panePlan.setStyle(
                "-fx-background-color: #FFFDF9;"
                + "-fx-border-color: #C9ADA7;"
                + "-fx-border-width: 2px;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
        );

        VBox conteneurPlan = new VBox(panePlan);
        conteneurPlan.setPadding(new Insets(20));
        conteneurPlan.setAlignment(Pos.CENTER);

        Label titreListe = new Label("Pièces du logement");
        titreListe.setFont(new Font("Arial", 16));
        titreListe.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        listePieces = new ListView<>();
        VBox.setVgrow(listePieces, Priority.ALWAYS);

        labelNbPieces = new Label("Nombre de pièces : 0");
        labelNbPieces.setStyle("-fx-text-fill: #555555;");

        Label titreDetails = new Label("Détails de la sélection");
        titreDetails.setFont(new Font("Arial", 16));
        titreDetails.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        labelDetailsPiece = new Label("Sélectionnez une pièce sur le plan.");
        labelDetailsPiece.setWrapText(true);
        labelDetailsPiece.setMinHeight(80);
        labelDetailsPiece.setStyle("-fx-text-fill: #555555;");

        Label titreMurs = new Label("Liste des murs");
        titreMurs.setFont(new Font("Arial", 15));
        titreMurs.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        listeMurs = new ListView<>();
        VBox.setVgrow(listeMurs, Priority.ALWAYS);

        VBox blocDroite = new VBox(
                12,
                titreListe,
                listePieces,
                labelNbPieces,
                new Separator(),
                titreDetails,
                labelDetailsPiece,
                titreMurs,
                listeMurs
        );

        blocDroite.setPadding(new Insets(20));
        blocDroite.setPrefWidth(350);
        blocDroite.setStyle(
                "-fx-background-color: #F8EDEB;"
                + "-fx-border-color: #E8C2CA;"
                + "-fx-border-width: 0 0 0 1;"
        );

        boutonEffacerSelection = new Button("Annuler sélection");
        boutonSupprimerDernierePiece = new Button("Supprimer dernière pièce");
        boutonFermer = new Button("Enregistrer et quitter");

        appliquerStyleBouton(boutonEffacerSelection, "#D8D8D8");
        appliquerStyleBouton(boutonSupprimerDernierePiece, "#FFC8DD");
        appliquerStyleBouton(boutonFermer, "#B8E0D2");

        HBox blocBas = new HBox(
                25,
                boutonEffacerSelection,
                boutonSupprimerDernierePiece,
                boutonFermer
        );

        blocBas.setAlignment(Pos.CENTER);
        blocBas.setPadding(new Insets(20));
        blocBas.setStyle("-fx-background-color: #F8EDEB;");

        root.setTop(topContent);
        root.setCenter(conteneurPlan);
        root.setRight(blocDroite);
        root.setBottom(blocBas);
    }

    // Méthode simple pour donner le même style aux boutons.
    private void appliquerStyleBouton(Button bouton, String couleur) {

        bouton.setStyle(
                "-fx-background-color: " + couleur + ";"
                + "-fx-text-fill: #2F3E46;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 9 15 9 15;"
                + "-fx-cursor: hand;"
        );
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

    public Button getBoutonOuvrirRevetements() {
        return boutonOuvrirRevetements;
    }

    public Button getBoutonOuvrirOuvertures() {
        return boutonOuvrirOuvertures;
    }

    public Button getBoutonCatalogue() {
        return boutonCatalogue;
    }

    public ListView<Piece> getListePieces() {
        return listePieces;
    }

    public Label getLabelNbPieces() {
        return labelNbPieces;
    }

    public Label getLabelDetailsPiece() {
        return labelDetailsPiece;
    }

    public ListView<Mur> getListeMurs() {
        return listeMurs;
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