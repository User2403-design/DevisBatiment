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

    private VBox root;

    private Label titre;
    private Label question;

    private Button maison;
    private Button immeuble;

    // champs étape 2
    private Label labelSuperficie;
    private TextField champSuperficie;
    private Button valider;

    public VueBatiment() {

        root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // TITRE
        titre = new Label("Bienvenue chez Bâtir & Co !");
        titre.setFont(new Font(22));

        // QUESTION
        question = new Label(
                "Chez nous créez votre nouveau bien selon vos envies.\n\n" +
                "Pour commencer, que souhaitez-vous construire ?"
        );
        question.setFont(new Font(16));
        question.setWrapText(true);

        // BOUTONS CHOIX
        maison = new Button("Maison");
        immeuble = new Button("Immeuble");

        maison.setPrefSize(160, 45);
        immeuble.setPrefSize(160, 45);

        maison.setStyle("-fx-font-size: 14px;");
        immeuble.setStyle("-fx-font-size: 14px;");

        // SUPERFICIE (CACHÉ AU DÉBUT)
        labelSuperficie = new Label("Superficie du bâtiment (m²) :");
        champSuperficie = new TextField();
        valider = new Button("Valider");

        labelSuperficie.setVisible(false);
        champSuperficie.setVisible(false);
        valider.setVisible(false);

        champSuperficie.setMaxWidth(150);

        // AJOUT À L'ÉCRAN
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