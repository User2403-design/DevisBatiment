/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import Modele.Appartement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class VuePlanImmeuble {

    private BorderPane root;

    private Pane panePlan;

    private Label titre;
    private Label infoEtage;
    private Label infoMessage;

    private ListView<Appartement> listeAppartements;

    private Button boutonEtagePrecedent;
    private Button boutonEtageSuivant;
    private Button boutonAjouterEtage;
    private Button boutonSupprimerEtage;

    private Button boutonSupprimerDernierAppartement;
    private Button boutonOuvrirAppartement;
    private Button boutonFermer;

    private Button boutonCatalogue;

    private Button boutonDefinirEscalier;

    public VuePlanImmeuble() {
        root = new BorderPane();

        titre = new Label("Plan de l'immeuble");
        titre.setFont(new Font(24));

        infoEtage = new Label("Étage 1");
        infoEtage.setFont(new Font(18));

        infoMessage = new Label("Cliquez sur 2 points pour créer un appartement.");
        infoMessage.setFont(new Font(14));

        VBox blocCentreHaut = new VBox(8, titre, infoEtage, infoMessage);
        blocCentreHaut.setAlignment(Pos.CENTER);

        boutonCatalogue = new Button(" Consulter le Catalogue");
        boutonCatalogue.setStyle("-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-color: #2b579a; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

        HBox blocDroiteHaut = new HBox(boutonCatalogue);
        blocDroiteHaut.setAlignment(Pos.CENTER_RIGHT);

        BorderPane blocHaut = new BorderPane();
        blocHaut.setCenter(blocCentreHaut);
        blocHaut.setRight(blocDroiteHaut);
        blocHaut.setPadding(new Insets(20));
        BorderPane.setMargin(blocDroiteHaut, new Insets(0, 30, 0, 0));

        panePlan = new Pane();
        panePlan.setPrefSize(900, 600);
        panePlan.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 2px;"
        );

        VBox blocCentre = new VBox(panePlan);
        blocCentre.setPadding(new Insets(20));
        blocCentre.setAlignment(Pos.CENTER);

        Label labelListe = new Label("Appartements de l'étage");
        labelListe.setFont(new Font(16));

        listeAppartements = new ListView<>();
        listeAppartements.setPrefSize(260, 250);

        boutonOuvrirAppartement = new Button("Ouvrir l'appartement");
        boutonOuvrirAppartement.setMaxWidth(Double.MAX_VALUE);

        boutonSupprimerDernierAppartement = new Button("Supprimer dernier appartement");
        boutonSupprimerDernierAppartement.setMaxWidth(Double.MAX_VALUE);

        boutonDefinirEscalier = new Button("Définir escalier");
        boutonDefinirEscalier.setMaxWidth(Double.MAX_VALUE);

        Label labelEtages = new Label("Gestion des étages");
        labelEtages.setFont(new Font(16));

        boutonEtagePrecedent = new Button("Étage précédent");
        boutonEtageSuivant = new Button("Étage suivant");
        boutonAjouterEtage = new Button("Ajouter un étage");
        boutonSupprimerEtage = new Button("Supprimer dernier étage");

        boutonEtagePrecedent.setMaxWidth(Double.MAX_VALUE);
        boutonEtageSuivant.setMaxWidth(Double.MAX_VALUE);
        boutonAjouterEtage.setMaxWidth(Double.MAX_VALUE);
        boutonSupprimerEtage.setMaxWidth(Double.MAX_VALUE);

        boutonFermer = new Button("Fermer");
        boutonFermer.setMaxWidth(Double.MAX_VALUE);

        VBox blocDroite = new VBox(
                12,
                labelListe,
                listeAppartements,
                boutonOuvrirAppartement,
                boutonSupprimerDernierAppartement,
                boutonDefinirEscalier,
                labelEtages,
                boutonEtagePrecedent,
                boutonEtageSuivant,
                boutonAjouterEtage,
                boutonSupprimerEtage,
                boutonFermer
        );

        blocDroite.setPadding(new Insets(20));
        blocDroite.setPrefWidth(300);
        blocDroite.setAlignment(Pos.TOP_CENTER);
        blocDroite.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #cccccc;");

        root.setTop(blocHaut);
        root.setCenter(blocCentre);
        root.setRight(blocDroite);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Pane getPanePlan() {
        return panePlan;
    }

    public Label getInfoEtage() {
        return infoEtage;
    }

    public Label getInfoMessage() {
        return infoMessage;
    }

    public ListView<Appartement> getListeAppartements() {
        return listeAppartements;
    }

    public Button getBoutonEtagePrecedent() {
        return boutonEtagePrecedent;
    }

    public Button getBoutonEtageSuivant() {
        return boutonEtageSuivant;
    }

    public Button getBoutonAjouterEtage() {
        return boutonAjouterEtage;
    }

    public Button getBoutonSupprimerEtage() {
        return boutonSupprimerEtage;
    }

    public Button getBoutonSupprimerDernierAppartement() {
        return boutonSupprimerDernierAppartement;
    }

    public Button getBoutonOuvrirAppartement() {
        return boutonOuvrirAppartement;
    }

    public Button getBoutonFermer() {
        return boutonFermer;
    }

    public Button getBoutonCatalogue() {
        return boutonCatalogue;
    }

    public Button getBoutonDefinirEscalier() {
        return boutonDefinirEscalier;
    }
}