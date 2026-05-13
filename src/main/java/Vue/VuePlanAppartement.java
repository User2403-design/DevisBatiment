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

/**
 * Vue optimisée de l'aménagement du logement.
 * Les outils de personnalisation sont désormais accessibles via le bandeau supérieur.
 */
public class VuePlanAppartement {

    private BorderPane root;
    private Pane panePlan;

    // Bandeau supérieur
    private Label titre;
    private Label infoMessage;
    private Button boutonOuvrirRevetements;
    private Button boutonOuvrirOuvertures;
    private Button boutonCatalogue;

    // Barre latérale (Sidebar) - Contient désormais uniquement les listes
    private ListView<Piece> listePieces;
    private Label labelNbPieces;
    private Label labelDetailsPiece;
    private ListView<Mur> listeMurs;

    // Bas de page
    private Button boutonEffacerSelection;
    private Button boutonSupprimerDernierePiece;
    private Button boutonFermer;

    public VuePlanAppartement() {
        root = new BorderPane();

        // ----------- BANDEAU SUPÉRIEUR (TOP) -----------
        titre = new Label("Plan du logement");
        titre.setFont(new Font("System Bold", 24));

        infoMessage = new Label("Prêt pour la création de pièces.");
        infoMessage.setFont(new Font(14));

        VBox blocTexteHaut = new VBox(5, titre, infoMessage);
        blocTexteHaut.setAlignment(Pos.CENTER_LEFT);

        // Nouveaux boutons de menu
        boutonOuvrirRevetements = new Button("🎨 Revêtements");
        boutonOuvrirOuvertures = new Button("🔲 Ouvertures");
        boutonCatalogue = new Button("📖 Catalogue");
        
        String styleMenu = "-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5;";
        boutonOuvrirRevetements.setStyle(styleMenu + "-fx-background-color: #e1e1e1;");
        boutonOuvrirOuvertures.setStyle(styleMenu + "-fx-background-color: #e1e1e1;");
        boutonCatalogue.setStyle(styleMenu + "-fx-background-color: #2b579a; -fx-text-fill: white;");

        HBox blocBoutonsMenu = new HBox(15, boutonCatalogue, boutonOuvrirRevetements, boutonOuvrirOuvertures);
        blocBoutonsMenu.setAlignment(Pos.CENTER_RIGHT);

        BorderPane topContent = new BorderPane();
        topContent.setLeft(blocTexteHaut);
        topContent.setRight(blocBoutonsMenu);
        topContent.setPadding(new Insets(15, 25, 15, 25));
        topContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");

        // ----------- ZONE CENTRALE (PLAN) -----------
        panePlan = new Pane();
        panePlan.setPrefSize(900, 650);
        panePlan.setStyle("-fx-background-color: white; -fx-border-color: #444; -fx-border-width: 2px;");
        
        VBox conteneurPlan = new VBox(panePlan);
        conteneurPlan.setPadding(new Insets(20));
        conteneurPlan.setAlignment(Pos.CENTER);

        // ----------- BARRE LATÉRALE (DROITE) -----------
        Label titreListe = new Label("Pièces du logement");
        titreListe.setFont(new Font("System Bold", 16));

        listePieces = new ListView<>();
        VBox.setVgrow(listePieces, Priority.ALWAYS); // Prend la place disponible

        labelNbPieces = new Label("Nombre de pièces : 0");

        Label titreDetails = new Label("Détails de la sélection");
        titreDetails.setFont(new Font("System Bold", 16));

        labelDetailsPiece = new Label("Sélectionnez une pièce sur le plan.");
        labelDetailsPiece.setWrapText(true);
        labelDetailsPiece.setMinHeight(80);

        Label titreMurs = new Label("Liste des murs");
        titreMurs.setFont(new Font("System Bold", 15));

        listeMurs = new ListView<>();
        VBox.setVgrow(listeMurs, Priority.ALWAYS);

        VBox blocDroite = new VBox(12,
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
        blocDroite.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-width: 0 0 0 1;");

        // ----------- BAS DE PAGE (BOTTOM) -----------
        boutonEffacerSelection = new Button("Annuler sélection");
        boutonSupprimerDernierePiece = new Button("Supprimer dernière pièce");
        boutonFermer = new Button("Enregistrer et Quitter");
        
        boutonFermer.setStyle("-fx-background-color: #2b579a; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox blocBas = new HBox(25, boutonEffacerSelection, boutonSupprimerDernierePiece, boutonFermer);
        blocBas.setAlignment(Pos.CENTER);
        blocBas.setPadding(new Insets(20));
        blocBas.setStyle("-fx-background-color: #eee;");

        // ----------- ASSEMBLAGE -----------
        root.setTop(topContent);
        root.setCenter(conteneurPlan);
        root.setRight(blocDroite);
        root.setBottom(blocBas);
    }

    // Getters
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
