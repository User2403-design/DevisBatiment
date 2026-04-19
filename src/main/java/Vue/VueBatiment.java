/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

/**
 *
 * @author chloe
 */

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class VueBatiment {

    // conteneur principal (toute l'interface)
    private VBox root;

    // éléments affichés
    private Label titre;
    private Label question;

    private Button maison;
    private Button immeuble;

    private Label labelLongueur;
    private Label labelLargeur;
    private TextField champLongueur;
    private TextField champLargeur;
    private Label labelCalcSuperficie;
    private Button valider;

    public VueBatiment() {

        // VBox = éléments empilés verticalement avec 20px d'espace
        root = new VBox(20);

        // centre tout le contenu au milieu de la fenêtre
        root.setAlignment(Pos.CENTER);

        // permet au VBox de prendre toute la place disponible
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //  TITRE 
        titre = new Label("Bienvenue chez Bâtir & Co !");
        titre.setFont(new Font(28)); // taille plus visible

        //  QUESTION
        question = new Label(
                "Chez nous créez votre nouveau bien selon vos envies.\n\n" +
                "Que souhaitez-vous construire ?"
        );

        question.setFont(new Font(18));
        question.setWrapText(true); // retour à la ligne automatique

        // BOUTONS 
        maison = new Button("Maison");
        immeuble = new Button("Immeuble");

        // taille des boutons
        maison.setPrefSize(200, 50);
        immeuble.setPrefSize(200, 50);
        
        //Longueur
        labelLongueur = new Label("Longueur du bâtiment (m) :");
        champLongueur = new TextField();
        champLongueur.setMaxWidth(200);
        
        //Largeur
        labelLargeur = new Label("Largeur du bâtiment (m) :");
        champLargeur = new TextField();
        champLargeur.setMaxWidth(200);

        // SUPERFICIE : devient le resultat du calcul avec les dimensions données, affiché uniquement
        labelCalcSuperficie = new Label("Superficie (m²) :");
        labelCalcSuperficie.setFont(new Font("Arial", 16));
        labelCalcSuperficie.setStyle("-fx-text-fill: green");
        
        valider = new Button("Valider et Créer");

        // caché au démarrage
        
        labelLongueur.setVisible(false);
        champLongueur.setVisible(false);
        labelLargeur.setVisible(false);
        champLargeur.setVisible(false);
        labelCalcSuperficie.setVisible(false);
        valider.setVisible(false);
       
        
        // AJOUT DES ÉLÉMENTS 
        root.getChildren().addAll(
                titre,
                question,
                maison,
                immeuble,
                labelLongueur,
                champLongueur,
                labelLargeur,
                champLargeur,
                labelCalcSuperficie,
                valider
        );
    }

    // GETTERS 

    // permet au contrôleur de récupérer la vue
    public VBox getRoot() {
        return root;
    }

    public Button getMaison() {
        return maison;
    }

    public Button getImmeuble() {
        return immeuble;
    }
    
    public Label getTitre() {
        return titre;
    }

    public Label getQuestion() {
        return question;
    }

    public Label getLabelLongueur() {
        return labelLongueur;
    }

    public Label getLabelLargeur() {
        return labelLargeur;
    }

    public TextField getChampLongueur() {
        return champLongueur;
    }

    public TextField getChampLargeur() {
        return champLargeur;
    }

    public Label getLabelCalcSuperficie() {
        return labelCalcSuperficie;
    }
  
    public Button getValider() {
        return valider;
    }
}