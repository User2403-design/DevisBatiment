/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import javafx.scene.Scene;
import javafx.stage.Stage;
import Vue.VueBatiment;
import Modele.Batiment;
import Modele.Stockage;

public class CBatiment {
    private VueBatiment vue;
    private Stage fenetre;

    // variable qui mémorise le type choisi par l'utilisateur
    private String typeBatiment;

    // constructeur du contrôleur
    public CBatiment(Stage fenetre) {
        this.fenetre = fenetre;

        // on crée la vue
        vue = new VueBatiment();

        // CRÉATION DE LA SCÈNE 

        Scene scene = new Scene(vue.getRoot());
        fenetre.setScene(scene);
        fenetre.setTitle("Bâtir & Co");

        // la fenêtre prend toute la page
        // elle reste en mode fenêtre classique, avec la barre du haut
        fenetre.setMaximized(true);
        fenetre.show();

        //  GESTION DES BOUTONS 

        // quand on clique sur "Maison"
        vue.getMaison().setOnAction(e -> {
            // on mémorise le type choisi
            typeBatiment = "Maison";
            afficherSuperficie();
        });

        // quand on clique sur "Immeuble"
        vue.getImmeuble().setOnAction(e -> {
            // on mémorise le type choisi
            typeBatiment = "Immeuble";
            afficherSuperficie();
        });

        vue.getValider().setOnAction(e -> {

            try {
                // on récupère le texte tapé dans le champ
                String texteSuperficie = vue.getChampSuperficie().getText();

                // on transforme le texte en nombre
                float superficie = Float.parseFloat(texteSuperficie);

                // on crée un nouvel objet Batiment
                Batiment batiment = new Batiment(typeBatiment, superficie);

                // on ajoute ce bâtiment dans la liste de stockage
                Stockage.batiments.add(batiment);

                // affichage dans la console pour vérifier
                System.out.println("Bâtiment créé !");
                System.out.println("Type : " + batiment.getTypeBatiment());
                System.out.println("Superficie : " + batiment.getSuperficie());

                // on peut vider le champ après validation
                vue.getChampSuperficie().clear();

            } catch (NumberFormatException ex) {
                // erreur si l'utilisateur écrit autre chose qu'un nombre
                System.out.println("Erreur : veuillez entrer un nombre valide pour la superficie.");
            }
        });
    }

    // méthode qui affiche les éléments liés à la superficie
    private void afficherSuperficie() {

        // on rend visibles le label, le champ de texte et le bouton valider
        vue.getLabelSuperficie().setVisible(true);
        vue.getChampSuperficie().setVisible(true);
        vue.getValider().setVisible(true);
    }
}