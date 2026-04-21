/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import Modele.Appartement;
import Modele.Piece;
import Modele.Point;
import Vue.VuePlanAppartement;
import javafx.scene.Scene;
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

public class CPlanAppartement {

    private VuePlanAppartement vue;
    private Stage fenetre;
    private Appartement appartement;

    // Points pour créer une pièce
    private Point premierPoint;
    private Point deuxiemePoint;

    // Gestion de l'affichage
    private double echelle;
    private final double margeSecurite = 40.0;

    public CPlanAppartement(Stage fenetre, Appartement appartement) {
        this.fenetre = fenetre;
        this.appartement = appartement;

        vue = new VuePlanAppartement();

        Scene scene = new Scene(vue.getRoot(), 1100, 750);
        fenetre.setScene(scene);
        fenetre.setTitle("Aménagement de " + appartement.getNom());
        fenetre.show();

        // Calcul initial
        calculerEchelle();
        rafraichirPlan();
        mettreAJourSidebar(); // 👉 IMPORTANT

        // Si la fenêtre change de taille
        vue.getPanePlan().widthProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        vue.getPanePlan().heightProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        // Clic souris
        vue.getPanePlan().setOnMouseClicked(event -> {
            gererClicSouris(event.getX(), event.getY());
        });

        // Effacer sélection
        vue.getBoutonEffacerSelection().setOnAction(e -> {
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Sélection annulée.");
            rafraichirPlan();
        });

        // Supprimer dernière pièce
        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> {
            appartement.supprimerDernierePiece();
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Dernière pièce supprimée.");
            
            mettreAJourSidebar(); // 👉 MAJ
            rafraichirPlan();
        });

        // Fermer
        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    // Calcul de l'échelle
    private void calculerEchelle() {
        double largeurDisponible = vue.getPanePlan().getWidth() - (margeSecurite * 2);
        double hauteurDisponible = vue.getPanePlan().getHeight() - (margeSecurite * 2);

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) return;

        double echelleX = largeurDisponible / appartement.getLargeur();
        double echelleY = hauteurDisponible / appartement.getHauteur();

        echelle = Math.min(echelleX, echelleY);
    }

    // Gestion du clic
    private void gererClicSouris(double xPixel, double yPixel) {

        double offsetX = (vue.getPanePlan().getWidth() - (appartement.getLargeur() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (appartement.getHauteur() * echelle)) / 2;

        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        // Vérifie si on clique dans l'appartement
        if (xReel < 0 || xReel > appartement.getLargeur()
         || yReel < 0 || yReel > appartement.getHauteur()) {
            vue.getInfoMessage().setText("Clic hors de l'appartement.");
            return;
        }

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);
            vue.getInfoMessage().setText("Point 1 enregistré.");
            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);
            finaliserPiece();
        }
    }

    // Création d'une pièce
    private void finaliserPiece() {

        // Vérification rectangle valide
        if (premierPoint.getX() == deuxiemePoint.getX()
         || premierPoint.getY() == deuxiemePoint.getY()) {

            vue.getInfoMessage().setText("Erreur : rectangle impossible.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        String usage = demanderUsagePiece();
        Piece nouvellePiece = new Piece(premierPoint, deuxiemePoint, usage);

        // Vérifie chevauchement
        if (chevaucheUnePiece(nouvellePiece)) {
            vue.getInfoMessage().setText("Erreur : chevauchement.");
        } else {
            appartement.ajouterPiece(nouvellePiece);

            vue.getInfoMessage().setText(
                    usage + " créée (" + String.format("%.2f", nouvellePiece.getSuperficie()) + " m²)"
            );

            mettreAJourSidebar(); // 👉 MAJ ici
        }

        premierPoint = null;
        deuxiemePoint = null;

        rafraichirPlan();
    }

    // Vérifie si une pièce chevauche une autre
    private boolean chevaucheUnePiece(Piece nouvellePiece) {

        for (Piece p : appartement.getPieces()) {

            boolean pasDeChevauchement =
                    nouvellePiece.getXMax() <= p.getXMin()
                 || nouvellePiece.getXMin() >= p.getXMax()
                 || nouvellePiece.getYMax() <= p.getYMin()
                 || nouvellePiece.getYMin() >= p.getYMax();

            if (!pasDeChevauchement) return true;
        }

        return false;
    }

    // Redessine tout
    private void rafraichirPlan() {

        Pane pane = vue.getPanePlan();
        pane.getChildren().clear();

        double offsetX = (pane.getWidth() - (appartement.getLargeur() * echelle)) / 2;
        double offsetY = (pane.getHeight() - (appartement.getHauteur() * echelle)) / 2;

        // Contour appartement
        Rectangle contour = new Rectangle(
                offsetX,
                offsetY,
                appartement.getLargeur() * echelle,
                appartement.getHauteur() * echelle
        );
        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(3);

        pane.getChildren().add(contour);

        // Pièces
        for (Piece piece : appartement.getPieces()) {

            Rectangle r = new Rectangle(
                    offsetX + piece.getXMin() * echelle,
                    offsetY + piece.getYMin() * echelle,
                    piece.getLargeur() * echelle,
                    piece.getHauteur() * echelle
            );

            r.setFill(Color.LIGHTBLUE);
            r.setOpacity(0.4);
            r.setStroke(Color.BLUE);

            pane.getChildren().add(r);
        }

        // Point sélection
        if (premierPoint != null) {
            Circle c = new Circle(
                    offsetX + premierPoint.getX() * echelle,
                    offsetY + premierPoint.getY() * echelle,
                    4,
                    Color.RED
            );
            pane.getChildren().add(c);
        }
    }

    // Demande nom pièce
    private String demanderUsagePiece() {

        TextInputDialog dialog = new TextInputDialog("Pièce");
        dialog.setTitle("Nouvelle pièce");
        dialog.setHeaderText("Créer une pièce");
        dialog.setContentText("Nom :");

        Optional<String> res = dialog.showAndWait();

        if (res.isPresent() && !res.get().trim().isEmpty()) {
            return res.get();
        }

        return "Pièce";
    }

 
    private void mettreAJourSidebar() {

        vue.getListePieces().getItems().clear();
        vue.getListePieces().getItems().addAll(appartement.getPieces());

        vue.getLabelNbPieces().setText(
                "Nombre de pièces : " + appartement.getPieces().size()
        );
    }
}