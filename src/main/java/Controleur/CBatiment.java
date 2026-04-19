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
import Modele.Maison;
import Modele.Immeuble;

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
            afficherChampsSaisie();
        });

        // quand on clique sur "Immeuble"
        vue.getImmeuble().setOnAction(e -> {
            // on mémorise le type choisi
            typeBatiment = "Immeuble";
            afficherChampsSaisie();
        });

        vue.getValider().setOnAction(e -> {

            try {
                
                // 1. Récupération des valeurs depuis les deux champs + conversion en texte
                float longueur = Float.parseFloat(vue.getChampLongueur().getText());
                float largeur = Float.parseFloat(vue.getChampLargeur().getText());

               // 2. Création de l'objet spécifique (Polymorphisme)
                Batiment batiment;
                if (typeBatiment.equals("Maison")) {
                    batiment = new Maison(typeBatiment, longueur, largeur);
                } else {
                    batiment = new Immeuble(typeBatiment, longueur, largeur);
                }
                
                // 3. Mise à jour de l'interface avec la superficie calculée par le Modèle
                vue.getLabelCalcSuperficie().setText("Superficie calculée : " + batiment.getSuperficie() + " m²");
                vue.getLabelCalcSuperficie().setVisible(true);

                // 4. Ajour du batiment crée dans la classe Stockage
                Stockage.batiments.add(batiment);
                
                
                // affichage dans la console pour vérifier
                System.out.println("Batiment cree avec succes !");
                System.out.println("Type : " + batiment.getTypeBatiment());
                System.out.println("Superficie : " + batiment.getSuperficie());

                // on peut vider les champs après validation
                vue.getChampLongueur().clear();
                vue.getChampLargeur().clear();

            } catch (NumberFormatException ex) {
                // Gestion de l'erreur si l'utilisateur saisit du texte au lieu de nombres
        System.out.println("Erreur : veuillez entrer des dimensions valides.");
                // erreur si l'utilisateur écrit autre chose qu'un nombre
                System.out.println("Erreur : veuillez entrer un nombre valide pour la superficie.");
            }
        });
    }

    // méthode qui affiche les éléments liés à la superficie
    private void afficherChampsSaisie() {
        
    // On affiche les deux champs de saisie et leurs labels respectifs
    vue.getLabelLongueur().setVisible(true);
    vue.getChampLongueur().setVisible(true);
    
    vue.getLabelLargeur().setVisible(true);
    vue.getChampLargeur().setVisible(true);
    
    // On affiche le bouton de validation
    vue.getValider().setVisible(true);
    
    // On s'assure que le label de résultat est caché au début
    vue.getLabelCalcSuperficie().setVisible(false);

    }
}