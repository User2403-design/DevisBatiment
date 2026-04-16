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

    private Label labelSuperficie;
    private TextField champSuperficie;
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

        // SUPERFICIE
        labelSuperficie = new Label("Superficie (m²) :");

        champSuperficie = new TextField();
        champSuperficie.setMaxWidth(200); // évite qu'il soit trop large

        valider = new Button("Valider");

        // caché au démarrage
        labelSuperficie.setVisible(false);
        champSuperficie.setVisible(false);
        valider.setVisible(false);

        // AJOUT DES ÉLÉMENTS 
        root.getChildren().addAll(
                titre,
                question,
                maison,
                immeuble,
                labelSuperficie,
                champSuperficie,
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

    public Label getLabelSuperficie() {
        return labelSuperficie;
    }

    public TextField getChampSuperficie() {
        return champSuperficie;
    }

    public Button getValider() {
        return valider;
    }
}