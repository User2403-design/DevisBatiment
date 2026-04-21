/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;
import Modele.Appartement;
import Modele.Immeuble;
import Modele.Niveau;
import Modele.Point;
import Vue.VuePlanImmeuble;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Optional;

/**
 *
 * @author chloe
 */




public class CPlanImmeuble {

    private VuePlanImmeuble vue;
    private Stage fenetre;
    private Immeuble immeuble;

    // Indice de l'étage affiché
    private int indexNiveauCourant;

    // Les 2 points pour créer un appartement
    private Point premierPoint;
    private Point deuxiemePoint;

    private double echelle;
    private final double margeSecurite = 40.0;

    public CPlanImmeuble(Stage fenetre, Immeuble immeuble) {
        this.fenetre = fenetre;
        this.immeuble = immeuble;
        this.indexNiveauCourant = 0;

        vue = new VuePlanImmeuble();

        Scene scene = new Scene(vue.getRoot(), 1300, 800);
        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur d'immeuble");
        fenetre.setMaximized(true);
        fenetre.show();

        calculerEchelle();
        mettreAJourAffichage();

        // Redessiner si la zone change de taille
        vue.getPanePlan().widthProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        vue.getPanePlan().heightProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        // Clic dans le plan pour créer un appartement
        vue.getPanePlan().setOnMouseClicked(event -> {
            gererClicSouris(event.getX(), event.getY());
        });

        // Ouvrir un appartement sélectionné
        vue.getBoutonOuvrirAppartement().setOnAction(e -> {
            Appartement appartementSelectionne = vue.getListeAppartements().getSelectionModel().getSelectedItem();

            if (appartementSelectionne == null) {
                afficherErreur("Veuillez sélectionner un appartement dans la liste.");
                return;
            }

            Stage nouvelleFenetre = new Stage();
            new CPlanAppartement(nouvelleFenetre, appartementSelectionne);
        });

        // Supprimer le dernier appartement de l'étage courant
        vue.getBoutonSupprimerDernierAppartement().setOnAction(e -> {
            getNiveauCourant().supprimerDernierAppartement();
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Dernier appartement supprimé.");
            mettreAJourAffichage();
        });

        // Navigation entre les étages
        vue.getBoutonEtagePrecedent().setOnAction(e -> {
            if (indexNiveauCourant > 0) {
                indexNiveauCourant--;
                premierPoint = null;
                deuxiemePoint = null;
                mettreAJourAffichage();
            }
        });

        vue.getBoutonEtageSuivant().setOnAction(e -> {
            if (indexNiveauCourant < immeuble.getNiveaux().size() - 1) {
                indexNiveauCourant++;
                premierPoint = null;
                deuxiemePoint = null;
                mettreAJourAffichage();
            }
        });

        // Ajouter un étage
        vue.getBoutonAjouterEtage().setOnAction(e -> {
            immeuble.ajouterNiveau();
            indexNiveauCourant = immeuble.getNiveaux().size() - 1;
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Nouvel étage créé.");
            mettreAJourAffichage();
        });

        // Supprimer le dernier étage
        vue.getBoutonSupprimerEtage().setOnAction(e -> {
            if (immeuble.getNiveaux().size() > 1) {
                immeuble.supprimerDernierNiveau();

                if (indexNiveauCourant >= immeuble.getNiveaux().size()) {
                    indexNiveauCourant = immeuble.getNiveaux().size() - 1;
                }

                premierPoint = null;
                deuxiemePoint = null;
                vue.getInfoMessage().setText("Dernier étage supprimé.");
                mettreAJourAffichage();
            } else {
                afficherErreur("Il faut garder au moins un étage.");
            }
        });

        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    private Niveau getNiveauCourant() {
        return immeuble.getNiveaux().get(indexNiveauCourant);
    }

    private void calculerEchelle() {
        double largeurDisponible = vue.getPanePlan().getWidth() - (margeSecurite * 2);
        double hauteurDisponible = vue.getPanePlan().getHeight() - (margeSecurite * 2);

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) {
            return;
        }

        double echelleX = largeurDisponible / immeuble.getXmax();
        double echelleY = hauteurDisponible / immeuble.getYmax();

        echelle = Math.min(echelleX, echelleY);
    }

    private void gererClicSouris(double xPixel, double yPixel) {
        double offsetX = (vue.getPanePlan().getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (immeuble.getYmax() * echelle)) / 2;

        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        // On refuse les clics hors du bâtiment
        if (xReel < 0 || xReel > immeuble.getXmax() || yReel < 0 || yReel > immeuble.getYmax()) {
            vue.getInfoMessage().setText("Clic hors de l'étage.");
            return;
        }

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);
            vue.getInfoMessage().setText("Point 1 enregistré. Cliquez sur le coin opposé.");
            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);
            finaliserAppartement();
        }
    }

    private void finaliserAppartement() {
        // Si on a un rectangle plat, on refuse
        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoMessage().setText("Erreur : rectangle impossible.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        String nomAppartement = demanderNomAppartement();
        Appartement nouvelAppartement = new Appartement(nomAppartement, premierPoint, deuxiemePoint);

        // Vérification de chevauchement avec les autres appartements de l'étage
        if (chevaucheUnAppartement(nouvelAppartement)) {
            vue.getInfoMessage().setText("Erreur : cet appartement chevauche un autre appartement.");
        } else {
            getNiveauCourant().ajouterAppartement(nouvelAppartement);
            vue.getInfoMessage().setText(
                    nomAppartement + " créé (" + String.format("%.2f", nouvelAppartement.getSuperficie()) + " m²)"
            );
        }

        premierPoint = null;
        deuxiemePoint = null;

        mettreAJourAffichage();
    }

    private boolean chevaucheUnAppartement(Appartement nouvelAppartement) {
        for (Appartement appartementExistant : getNiveauCourant().getAppartements()) {
            boolean pasDeChevauchement =
                    nouvelAppartement.getXMax() <= appartementExistant.getXMin()
                    || nouvelAppartement.getXMin() >= appartementExistant.getXMax()
                    || nouvelAppartement.getYMax() <= appartementExistant.getYMin()
                    || nouvelAppartement.getYMin() >= appartementExistant.getYMax();

            if (!pasDeChevauchement) {
                return true;
            }
        }

        return false;
    }

    private void mettreAJourAffichage() {
        vue.getInfoEtage().setText(
                "Étage " + getNiveauCourant().getNumeroNiveau()
                        + " / Nombre total d'étages : " + immeuble.getNbreNiveaux()
        );

        vue.getListeAppartements().getItems().clear();
        vue.getListeAppartements().getItems().addAll(getNiveauCourant().getAppartements());

        rafraichirPlan();
    }

    private void rafraichirPlan() {
        Pane pane = vue.getPanePlan();
        pane.getChildren().clear();

        double offsetX = (pane.getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (pane.getHeight() - (immeuble.getYmax() * echelle)) / 2;

        // Contour extérieur de l'étage
        Rectangle contour = new Rectangle(
                offsetX,
                offsetY,
                immeuble.getXmax() * echelle,
                immeuble.getYmax() * echelle
        );
        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(3);

        pane.getChildren().add(contour);

        // Dessin des appartements déjà créés
        for (Appartement appartement : getNiveauCourant().getAppartements()) {
            Rectangle rectangleAppartement = new Rectangle(
                    offsetX + appartement.getXMin() * echelle,
                    offsetY + appartement.getYMin() * echelle,
                    appartement.getLargeur() * echelle,
                    appartement.getHauteur() * echelle
            );

            rectangleAppartement.setFill(Color.LIGHTGREEN);
            rectangleAppartement.setOpacity(0.4);
            rectangleAppartement.setStroke(Color.DARKGREEN);

            pane.getChildren().add(rectangleAppartement);
        }

        // Premier point en rouge
        if (premierPoint != null) {
            Circle point1 = new Circle(
                    offsetX + premierPoint.getX() * echelle,
                    offsetY + premierPoint.getY() * echelle,
                    4,
                    Color.RED
            );
            pane.getChildren().add(point1);
        }
    }

    private String demanderNomAppartement() {
        TextInputDialog dialog = new TextInputDialog("Appartement");
        dialog.setTitle("Nouvel appartement");
        dialog.setHeaderText("Créer un appartement");
        dialog.setContentText("Nom de l'appartement :");

        Optional<String> resultat = dialog.showAndWait();

        if (resultat.isPresent() && !resultat.get().trim().isEmpty()) {
            return resultat.get().trim();
        }

        return "Appartement " + (getNiveauCourant().getNombreAppartements() + 1);
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}