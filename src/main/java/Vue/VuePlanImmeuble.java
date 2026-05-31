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
    private Button boutonGenererDevis;
    
    private Label labelDetailsCouloir;

    public VuePlanImmeuble() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #FDF7F2;");

        titre = new Label("Plan de l'immeuble");
        titre.setFont(new Font("Arial", 26));
        titre.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        infoEtage = new Label("Étage 1");
        infoEtage.setFont(new Font("Arial", 18));
        infoEtage.setStyle("-fx-text-fill: #52796F; -fx-font-weight: bold;");

        infoMessage = new Label("Définissez d'abord l'escalier.");
        infoMessage.setFont(new Font("Arial", 14));
        infoMessage.setStyle("-fx-text-fill: #555555;");

        VBox blocCentreHaut = new VBox(8, titre, infoEtage, infoMessage);
        blocCentreHaut.setAlignment(Pos.CENTER);

        boutonCatalogue = new Button("Consulter le catalogue");
        appliquerStyleBouton(boutonCatalogue, "#A2D2FF");

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
                "-fx-background-color: #FFFDF9;"
                + "-fx-border-color: #C9ADA7;"
                + "-fx-border-width: 2px;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
        );

        VBox blocCentre = new VBox(panePlan);
        blocCentre.setPadding(new Insets(20));
        blocCentre.setAlignment(Pos.CENTER);

        Label labelListe = new Label("Appartements de l'étage");
        labelListe.setFont(new Font("Arial", 16));
        labelListe.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        listeAppartements = new ListView<>();
        listeAppartements.setPrefSize(260, 250);

        boutonOuvrirAppartement = new Button("Ouvrir l'appartement");
        boutonSupprimerDernierAppartement = new Button("Supprimer dernier appartement");

        appliquerStyleBouton(boutonOuvrirAppartement, "#B8E0D2");
        appliquerStyleBouton(boutonSupprimerDernierAppartement, "#FFC8DD");

        Label labelEtages = new Label("Gestion des étages");
        labelEtages.setFont(new Font("Arial", 16));
        labelEtages.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");
        Label labelTitreCouloir = new Label("Matériaux du couloir");
        labelTitreCouloir.setFont(new Font("Arial", 16));
        labelTitreCouloir.setStyle("-fx-text-fill: #2F3E46; -fx-font-weight: bold;");

        labelDetailsCouloir = new Label("Aucun couloir défini pour le moment.");
        labelDetailsCouloir.setWrapText(true);
        labelDetailsCouloir.setMinHeight(70);
        labelDetailsCouloir.setStyle("-fx-text-fill: #555555; -fx-font-family: 'Arial'; -fx-font-size: 13px;");

        boutonEtagePrecedent = new Button("Étage précédent");
        boutonEtageSuivant = new Button("Étage suivant");
        boutonAjouterEtage = new Button("Ajouter un étage");
        boutonSupprimerEtage = new Button("Supprimer dernier étage");
        boutonFermer = new Button("Fermer");
        boutonGenererDevis = new Button("Générer devis");
        appliquerStyleBouton(boutonGenererDevis, "#A2D2FF");
        appliquerStyleBouton(boutonEtagePrecedent, "#D8D8D8");
        appliquerStyleBouton(boutonEtageSuivant, "#D8D8D8");
        appliquerStyleBouton(boutonAjouterEtage, "#B8E0D2");
        appliquerStyleBouton(boutonSupprimerEtage, "#FFC8DD");
        appliquerStyleBouton(boutonFermer, "#D8D8D8");

        VBox blocDroite = new VBox(
                12,
                labelListe,
                listeAppartements,
                boutonOuvrirAppartement,
                boutonSupprimerDernierAppartement,
      
                new javafx.scene.control.Separator(), 
                labelTitreCouloir,
                labelDetailsCouloir,
                new javafx.scene.control.Separator(), 
                labelEtages,
                boutonEtagePrecedent,
                boutonEtageSuivant,
                boutonAjouterEtage,
                boutonSupprimerEtage,
                boutonGenererDevis,
                boutonFermer
        );

        blocDroite.setPadding(new Insets(20));
        blocDroite.setPrefWidth(310);
        blocDroite.setAlignment(Pos.TOP_CENTER);
        blocDroite.setStyle(
                "-fx-background-color: #F8EDEB;"
                + "-fx-border-color: #E8C2CA;"
                + "-fx-border-width: 1px;"
        );

        root.setTop(blocHaut);
        root.setCenter(blocCentre);
        root.setRight(blocDroite);
    }

    
    private void appliquerStyleBouton(Button bouton, String couleur) {
        bouton.setMaxWidth(Double.MAX_VALUE);
        bouton.setStyle(
                "-fx-background-color: " + couleur + ";"
                + "-fx-text-fill: #2F3E46;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 5 15 5 15;"
                + "-fx-cursor: hand;"
        );
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
    
    public Label getLabelDetailsCouloir() {
        return labelDetailsCouloir;
    }
    public Button getBoutonGenererDevis() {
    return boutonGenererDevis;
    }
  
}
