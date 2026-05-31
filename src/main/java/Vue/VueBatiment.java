/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Vue;

import Modele.Revetement;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.ComboBox;
import Modele.Revetement;


//Vue initiale pour la configuration du bâtiment (Maison ou Immeuble) pour saisir les dimensions et de choisir les matériaux extérieurs par défaut.
 
public class VueBatiment {
    

    private VBox root;
    private Label titre;
    private Label question;
    private Button maison;
    private Button immeuble;

    private Label labelLongueur;
    private Label labelLargeur;
    private TextField champLongueur;
    private TextField champLargeur;
    
    
    private Label labelIsolant;
    private Label labelFacade;
    private ComboBox<Revetement> comboIsolant;
    private ComboBox<Revetement> comboFacade;
    
    private Label labelCalcSuperficie;
    private Button valider;

    public VueBatiment() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        titre = new Label("BIENVENUE CHEZ BATIR & CO");
        titre.setFont(new Font(28));

        question = new Label("Que souhaitez-vous construire ?");
        question.setFont(new Font(18));

        maison = new Button("Une maison");
        immeuble = new Button("Un immeuble");
        maison.setPrefSize(200, 40);
        immeuble.setPrefSize(200, 40);
        
        labelLongueur = new Label("Veuillez indiquer la longueur du bâtiment (m) :");
        champLongueur = new TextField();
        champLongueur.setMaxWidth(200);
        
        labelLargeur = new Label("Veuillez indiquer la largeur du bâtiment (m) :");
        champLargeur = new TextField();
        champLargeur.setMaxWidth(200);

        
        labelIsolant = new Label("Choisir l'isolant extérieur :");
        comboIsolant = new ComboBox<>();
        comboIsolant.setPrefWidth(200);

        labelFacade = new Label("Choisir le revêtement de façade :");
        comboFacade = new ComboBox<>();
        comboFacade.setPrefWidth(200);

        labelCalcSuperficie = new Label("Superficie (m²) :");
        labelCalcSuperficie.setFont(new Font("Arial", 16));
        labelCalcSuperficie.setStyle("-fx-text-fill: green");
        
        valider = new Button("Valider et Créer");
        appliquerStyleBouton(valider, "#A2D2FF");
        appliquerStyleBouton(maison, "#B8E0D2");
        appliquerStyleBouton(immeuble, "#FFC8DD");
        
        setChampsVisibles(false);
       
        root.getChildren().addAll(
                titre, 
                question, 
                maison, 
                immeuble,
                labelLongueur, 
                champLongueur,
                labelLargeur, 
                champLargeur,
                labelIsolant, 
                comboIsolant,
                labelFacade, 
                comboFacade,
                labelCalcSuperficie, 
                valider
        );
    }
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
    

    
    //Affiche ou masque les champs de saisie une fois le type de bâtiment choisi.
    
    public void setChampsVisibles(boolean visible) {
        labelLongueur.setVisible(visible);
        champLongueur.setVisible(visible);
        labelLargeur.setVisible(visible);
        champLargeur.setVisible(visible);
        labelIsolant.setVisible(visible);
        comboIsolant.setVisible(visible);
        labelFacade.setVisible(visible);
        comboFacade.setVisible(visible);
        valider.setVisible(visible);
        labelCalcSuperficie.setVisible(false); // Reste masqué jusqu'au calcul
    }

    public VBox getRoot() { 
        return root; 
    }
    
    public Button getMaison() { 
        return maison; 
    }
    
    public Button getImmeuble() {
        return immeuble; 
    }
    
    public TextField getChampLongueur() {
        return champLongueur; 
    }
    
    public TextField getChampLargeur() { 
        return champLargeur; 
    }
    
    public ComboBox<Revetement> getComboIsolant() { 
        return comboIsolant; 
    }
    
    public ComboBox<Revetement> getComboFacade() { 
        return comboFacade; 
    }
    
    public Label getLabelCalcSuperficie() { 
        return labelCalcSuperficie; 
    }
    
    public Button getValider() { 
        return valider; 
    }
    
    public Label getLabelLongueur() { 
        return labelLongueur; 
    }
    
    public Label getLabelLargeur() { 
        return labelLargeur; 
    }
}
