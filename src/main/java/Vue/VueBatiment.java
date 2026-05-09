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

/**
 * Vue initiale pour la configuration du bâtiment (Maison ou Immeuble).
 * Permet de saisir les dimensions et de choisir les matériaux extérieurs par défaut.
 */
public class VueBatiment {

    private VBox root;
    private Label titre, question;
    private Button maison, immeuble;

    private Label labelLongueur, labelLargeur;
    private TextField champLongueur, champLargeur;
    
    // Sélecteurs pour les matériaux extérieurs (Isolant et Façade)
    private Label labelIsolant, labelFacade;
    private ComboBox<Revetement> comboIsolant, comboFacade;
    
    private Label labelCalcSuperficie;
    private Button valider;

    public VueBatiment() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        titre = new Label("Bienvenue chez Bâtir & Co !");
        titre.setFont(new Font(28));

        question = new Label("Que souhaitez-vous construire ?");
        question.setFont(new Font(18));

        maison = new Button("Maison");
        immeuble = new Button("Immeuble");
        maison.setPrefSize(200, 40);
        immeuble.setPrefSize(200, 40);
        
        labelLongueur = new Label("Longueur du bâtiment (m) :");
        champLongueur = new TextField();
        champLongueur.setMaxWidth(200);
        
        labelLargeur = new Label("Largeur du bâtiment (m) :");
        champLargeur = new TextField();
        champLargeur.setMaxWidth(200);

        // Section Matériaux Extérieurs
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

        // Masquage initial des champs de configuration
        setChampsVisibles(false);
       
        root.getChildren().addAll(
                titre, question, maison, immeuble,
                labelLongueur, champLongueur,
                labelLargeur, champLargeur,
                labelIsolant, comboIsolant,
                labelFacade, comboFacade,
                labelCalcSuperficie, valider
        );
    }

    /**
     * Affiche ou masque les champs de saisie une fois le type de bâtiment choisi.
     */
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

    // Getters pour le contrôleur CBatiment
    public VBox getRoot() { return root; }
    public Button getMaison() { return maison; }
    public Button getImmeuble() { return immeuble; }
    public TextField getChampLongueur() { return champLongueur; }
    public TextField getChampLargeur() { return champLargeur; }
    public ComboBox<Revetement> getComboIsolant() { return comboIsolant; }
    public ComboBox<Revetement> getComboFacade() { return comboFacade; }
    public Label getLabelCalcSuperficie() { return labelCalcSuperficie; }
    public Button getValider() { return valider; }
    public Label getLabelLongueur() { return labelLongueur; }
    public Label getLabelLargeur() { return labelLargeur; }
}
