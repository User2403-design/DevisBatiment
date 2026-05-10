/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import Modele.Appartement;
import Modele.Mur;
import Modele.Piece;
import Modele.Point;
import Vue.VuePlanAppartement;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Optional;

public class CPlanAppartement {

    private VuePlanAppartement vue;
    private Stage fenetre;
    private Appartement appartement;

    private Point premierPoint;
    private Point deuxiemePoint;

    private Piece pieceSelectionnee;

    private double echelle = 1;
    private final double margeSecurite = 40.0;

    public CPlanAppartement(Stage fenetre, Appartement appartement) {
        this.fenetre = fenetre;
        this.appartement = appartement;

        vue = new VuePlanAppartement();

        Scene scene = new Scene(vue.getRoot(), 1150, 750);
        fenetre.setScene(scene);
        fenetre.setTitle("Aménagement de " + appartement.getNom());
        fenetre.show();

        calculerEchelle();
        rafraichirPlan();
        mettreAJourSidebar();

        vue.getPanePlan().widthProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        vue.getPanePlan().heightProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        vue.getPanePlan().setOnMouseClicked(event -> {
            gererClicSouris(event.getX(), event.getY());
        });

        vue.getListePieces().getSelectionModel().selectedItemProperty().addListener((obs, anciennePiece, nouvellePiece) -> {
            selectionnerPiece(nouvellePiece);
        });

        vue.getBoutonEffacerSelection().setOnAction(e -> {
            premierPoint = null;
            deuxiemePoint = null;
            pieceSelectionnee = null;

            vue.getListePieces().getSelectionModel().clearSelection();
            vue.getInfoMessage().setText("Sélection annulée.");

            mettreAJourDetailsPiece();
            rafraichirPlan();
        });

        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> {
            appartement.supprimerDernierePiece();

            premierPoint = null;
            deuxiemePoint = null;
            pieceSelectionnee = null;

            vue.getInfoMessage().setText("Dernière pièce supprimée.");

            mettreAJourSidebar();
            mettreAJourDetailsPiece();
            rafraichirPlan();
        });

        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    private void calculerEchelle() {
        double largeurPane = vue.getPanePlan().getWidth();
        double hauteurPane = vue.getPanePlan().getHeight();

        if (largeurPane <= 0) {
            largeurPane = vue.getPanePlan().getPrefWidth();
        }

        if (hauteurPane <= 0) {
            hauteurPane = vue.getPanePlan().getPrefHeight();
        }

        double largeurDisponible = largeurPane - (margeSecurite * 2);
        double hauteurDisponible = hauteurPane - (margeSecurite * 2);

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) {
            echelle = 1;
            return;
        }

        double echelleX = largeurDisponible / appartement.getLargeur();
        double echelleY = hauteurDisponible / appartement.getHauteur();

        echelle = Math.min(echelleX, echelleY);
    }

    private void gererClicSouris(double xPixel, double yPixel) {
        calculerEchelle();

        Pane pane = vue.getPanePlan();

        double largeurPane = pane.getWidth();
        double hauteurPane = pane.getHeight();

        if (largeurPane <= 0) {
            largeurPane = pane.getPrefWidth();
        }

        if (hauteurPane <= 0) {
            hauteurPane = pane.getPrefHeight();
        }

        double offsetX = (largeurPane - (appartement.getLargeur() * echelle)) / 2;
        double offsetY = (hauteurPane - (appartement.getHauteur() * echelle)) / 2;

        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        if (xReel < 0 || xReel > appartement.getLargeur()
                || yReel < 0 || yReel > appartement.getHauteur()) {
            vue.getInfoMessage().setText("Clic hors de l'appartement.");
            return;
        }

        Piece pieceCliquee = trouverPieceCliquee(xReel, yReel);

        if (pieceCliquee != null && premierPoint == null) {
            selectionnerPiece(pieceCliquee);
            vue.getListePieces().getSelectionModel().select(pieceCliquee);
            return;
        }

        if (premierPoint == null) {
            pieceSelectionnee = null;
            vue.getListePieces().getSelectionModel().clearSelection();
            mettreAJourDetailsPiece();

            premierPoint = new Point(xReel, yReel);
            vue.getInfoMessage().setText("Point 1 enregistré.");
            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);
            finaliserPiece();
        }
    }

    private Piece trouverPieceCliquee(float x, float y) {
        for (Piece piece : appartement.getPieces()) {
            boolean dedans =
                    x >= piece.getXMin()
                    && x <= piece.getXMax()
                    && y >= piece.getYMin()
                    && y <= piece.getYMax();

            if (dedans) {
                return piece;
            }
        }

        return null;
    }

    private void selectionnerPiece(Piece piece) {
        pieceSelectionnee = piece;

        if (piece != null) {
            vue.getInfoMessage().setText("Pièce sélectionnée : " + piece.getUsage());
        }

        mettreAJourDetailsPiece();
        rafraichirPlan();
    }

    private void finaliserPiece() {
        if (premierPoint.getX() == deuxiemePoint.getX()
                || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoMessage().setText("Erreur : rectangle impossible.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        String usage = demanderUsagePiece();

        Piece nouvellePiece = new Piece(premierPoint, deuxiemePoint, usage);

        if (chevaucheUnePiece(nouvellePiece)) {
            vue.getInfoMessage().setText("Erreur : chevauchement.");
        } else {
            appartement.ajouterPiece(nouvellePiece);
            pieceSelectionnee = nouvellePiece;

            vue.getInfoMessage().setText(
                    usage + " créée (" + String.format("%.2f", nouvellePiece.getSuperficie()) + " m²)"
            );

            mettreAJourSidebar();
            vue.getListePieces().getSelectionModel().select(nouvellePiece);
            mettreAJourDetailsPiece();
        }

        premierPoint = null;
        deuxiemePoint = null;

        rafraichirPlan();
    }

    private boolean chevaucheUnePiece(Piece nouvellePiece) {
        for (Piece p : appartement.getPieces()) {
            boolean pasDeChevauchement =
                    nouvellePiece.getXMax() <= p.getXMin()
                    || nouvellePiece.getXMin() >= p.getXMax()
                    || nouvellePiece.getYMax() <= p.getYMin()
                    || nouvellePiece.getYMin() >= p.getYMax();

            if (!pasDeChevauchement) {
                return true;
            }
        }

        return false;
    }

    private void rafraichirPlan() {
        calculerEchelle();

        Pane pane = vue.getPanePlan();
        pane.getChildren().clear();

        double largeurPane = pane.getWidth();
        double hauteurPane = pane.getHeight();

        if (largeurPane <= 0) {
            largeurPane = pane.getPrefWidth();
        }

        if (hauteurPane <= 0) {
            hauteurPane = pane.getPrefHeight();
        }

        double offsetX = (largeurPane - (appartement.getLargeur() * echelle)) / 2;
        double offsetY = (hauteurPane - (appartement.getHauteur() * echelle)) / 2;

        Rectangle contourAppartement = new Rectangle(
                offsetX,
                offsetY,
                appartement.getLargeur() * echelle,
                appartement.getHauteur() * echelle
        );

        contourAppartement.setFill(Color.TRANSPARENT);
        contourAppartement.setStroke(Color.BLACK);
        contourAppartement.setStrokeWidth(3);

        pane.getChildren().add(contourAppartement);

        for (Piece piece : appartement.getPieces()) {
            double xMin = offsetX + piece.getXMin() * echelle;
            double yMin = offsetY + piece.getYMin() * echelle;
            double largeur = piece.getLargeur() * echelle;
            double hauteur = piece.getHauteur() * echelle;

            Rectangle rectanglePiece = new Rectangle(
                    xMin,
                    yMin,
                    largeur,
                    hauteur
            );

            if (piece == pieceSelectionnee) {
                rectanglePiece.setFill(Color.LIGHTGREEN);
                rectanglePiece.setOpacity(0.55);
                rectanglePiece.setStroke(Color.GREEN);
                rectanglePiece.setStrokeWidth(3);
            } else {
                rectanglePiece.setFill(Color.LIGHTBLUE);
                rectanglePiece.setOpacity(0.4);
                rectanglePiece.setStroke(Color.BLUE);
                rectanglePiece.setStrokeWidth(1.5);
            }

            pane.getChildren().add(rectanglePiece);

            if (piece == pieceSelectionnee) {
                afficherNomsMurs(xMin, yMin, largeur, hauteur);
            }
        }

        if (premierPoint != null) {
            Circle pointRouge = new Circle(
                    offsetX + premierPoint.getX() * echelle,
                    offsetY + premierPoint.getY() * echelle,
                    4,
                    Color.RED
            );

            pane.getChildren().add(pointRouge);
        }
    }

    private void afficherNomsMurs(double xMin, double yMin, double largeur, double hauteur) {
        Pane pane = vue.getPanePlan();

        Text murNord = new Text("Mur Nord");
        murNord.setX(xMin + largeur / 2 - 30);
        murNord.setY(yMin + 15);

        Text murSud = new Text("Mur Sud");
        murSud.setX(xMin + largeur / 2 - 25);
        murSud.setY(yMin + hauteur - 8);

        Text murEst = new Text("Mur Est");
        murEst.setX(xMin + largeur - 50);
        murEst.setY(yMin + hauteur / 2);

        Text murOuest = new Text("Mur Ouest");
        murOuest.setX(xMin + 8);
        murOuest.setY(yMin + hauteur / 2);

        murNord.setTextAlignment(TextAlignment.CENTER);
        murSud.setTextAlignment(TextAlignment.CENTER);
        murEst.setTextAlignment(TextAlignment.CENTER);
        murOuest.setTextAlignment(TextAlignment.CENTER);

        pane.getChildren().addAll(
                murNord,
                murSud,
                murEst,
                murOuest
        );
    }

    private String demanderUsagePiece() {
        TextInputDialog dialog = new TextInputDialog("Pièce");
        dialog.setTitle("Nouvelle pièce");
        dialog.setHeaderText("Créer une pièce");
        dialog.setContentText("Nom :");

        Optional<String> res = dialog.showAndWait();

        if (res.isPresent() && !res.get().trim().isEmpty()) {
            return res.get().trim();
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

    private void mettreAJourDetailsPiece() {
        vue.getListeMurs().getItems().clear();

        if (pieceSelectionnee == null) {
            vue.getLabelDetailsPiece().setText("Aucune pièce sélectionnée.");
            return;
        }

        String details =
                "Nom : " + pieceSelectionnee.getUsage()
                + "\nLargeur : " + String.format("%.2f", pieceSelectionnee.getLargeur()) + " m"
                + "\nHauteur : " + String.format("%.2f", pieceSelectionnee.getHauteur()) + " m"
                + "\nSurface : " + String.format("%.2f", pieceSelectionnee.getSuperficie()) + " m²";

        vue.getLabelDetailsPiece().setText(details);

        for (Mur mur : pieceSelectionnee.getMurs()) {
            vue.getListeMurs().getItems().add(mur);
        }
    }
}