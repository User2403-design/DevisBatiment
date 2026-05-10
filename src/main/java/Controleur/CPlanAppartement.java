/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controleur;

import Modele.Appartement;
import Modele.GestionCatalogue;
import Modele.Mur;
import Modele.Ouverture;
import Modele.Piece;
import Modele.Point;
import Modele.Revetement;
import Vue.VuePlanAppartement;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
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

    private GestionCatalogue catalogue;
    private int compteurOuverture = 1;

    public CPlanAppartement(Stage fenetre, Appartement appartement) {
        this.fenetre = fenetre;
        this.appartement = appartement;
        this.catalogue = new GestionCatalogue();

        vue = new VuePlanAppartement();

        Scene scene = new Scene(vue.getRoot());
        fenetre.setScene(scene);
        fenetre.setTitle("Plan du logement");
        fenetre.setMaximized(true);
        fenetre.show();

        chargerRevetements();

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

        vue.getBoutonAppliquerRevetement().setOnAction(e -> appliquerRevetement());

        vue.getBoutonAjouterOuverture().setOnAction(e -> ajouterOuverture());
    }

    private void chargerRevetements() {
        vue.getComboRevetement().getItems().clear();

        for (Object nomRubriqueObj : catalogue.getNomsRubriques()) {
            String nomRubrique = nomRubriqueObj.toString();

            List produits = catalogue.getProduits(nomRubrique);

            for (Object obj : produits) {
                if (obj instanceof Revetement) {
                    vue.getComboRevetement().getItems().add((Revetement) obj);
                }
            }
        }
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
            vue.getInfoMessage().setText("Clic hors du logement.");
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
        mettreAJourSurfacesDisponibles();
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
            mettreAJourSurfacesDisponibles();
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

    private void appliquerRevetement() {
        if (pieceSelectionnee == null) {
            afficherErreur("Veuillez d'abord sélectionner une pièce.");
            return;
        }

        String surface = vue.getComboSurface().getValue();
        Revetement revetement = vue.getComboRevetement().getValue();

        if (surface == null || revetement == null) {
            afficherErreur("Veuillez choisir une surface et un revêtement.");
            return;
        }

        if (surface.equals("Sol")) {
            pieceSelectionnee.setRevetementSol(revetement);
        } else if (surface.equals("Plafond")) {
            pieceSelectionnee.setRevetementPlafond(revetement);
        } else {
            Mur mur = trouverMurParNom(surface);

            if (mur != null) {
                mur.setRevetement(revetement);
            }
        }

        vue.getInfoMessage().setText("Revêtement appliqué sur : " + surface);

        mettreAJourDetailsPiece();
        rafraichirPlan();
    }

    private void ajouterOuverture() {
        if (pieceSelectionnee == null) {
            afficherErreur("Veuillez d'abord sélectionner une pièce.");
            return;
        }

        String surface = vue.getComboSurface().getValue();

        if (surface == null || surface.equals("Sol") || surface.equals("Plafond")) {
            afficherErreur("Une ouverture ne peut être ajoutée que sur un mur.");
            return;
        }

        Mur mur = trouverMurParNom(surface);

        if (mur == null) {
            afficherErreur("Mur introuvable.");
            return;
        }

        String typeOuverture = vue.getComboTypeOuverture().getValue();

        if (typeOuverture == null) {
            afficherErreur("Veuillez choisir Fenêtre ou Porte.");
            return;
        }

        try {
            float largeur = Float.parseFloat(vue.getChampLargeurOuverture().getText());
            float hauteur = Float.parseFloat(vue.getChampHauteurOuverture().getText());

            if (largeur <= 0 || hauteur <= 0) {
                afficherErreur("Les dimensions de l'ouverture doivent être positives.");
                return;
            }

            Ouverture ouverture = new Ouverture(compteurOuverture, typeOuverture, largeur, hauteur);
            compteurOuverture++;

            mur.ajouterOuverture(ouverture);

            vue.getInfoMessage().setText(typeOuverture + " ajoutée sur : " + surface);

            vue.getChampLargeurOuverture().clear();
            vue.getChampHauteurOuverture().clear();

            mettreAJourDetailsPiece();
            rafraichirPlan();

        } catch (NumberFormatException e) {
            afficherErreur("Largeur et hauteur doivent être des nombres valides.");
        }
    }

    private Mur trouverMurParNom(String nomMur) {
        if (pieceSelectionnee == null) {
            return null;
        }

        for (Mur mur : pieceSelectionnee.getMurs()) {
            if (mur.getIdMur().equals(nomMur)) {
                return mur;
            }
        }

        return null;
    }

    private void mettreAJourSurfacesDisponibles() {
        vue.getComboSurface().getItems().clear();

        if (pieceSelectionnee == null) {
            return;
        }

        vue.getComboSurface().getItems().add("Sol");
        vue.getComboSurface().getItems().add("Plafond");

        for (Mur mur : pieceSelectionnee.getMurs()) {
            vue.getComboSurface().getItems().add(mur.getIdMur());
        }
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

        Rectangle contourLogement = new Rectangle(
                offsetX,
                offsetY,
                appartement.getLargeur() * echelle,
                appartement.getHauteur() * echelle
        );

        contourLogement.setFill(Color.TRANSPARENT);
        contourLogement.setStroke(Color.BLACK);
        contourLogement.setStrokeWidth(3);

        pane.getChildren().add(contourLogement);

        for (Piece piece : appartement.getPieces()) {
            double xMin = offsetX + piece.getXMin() * echelle;
            double yMin = offsetY + piece.getYMin() * echelle;
            double largeur = piece.getLargeur() * echelle;
            double hauteur = piece.getHauteur() * echelle;

            Rectangle rectanglePiece = new Rectangle(xMin, yMin, largeur, hauteur);

            if (piece == pieceSelectionnee) {
                rectanglePiece.setFill(Color.LIGHTGREEN);
                rectanglePiece.setOpacity(0.55);
                rectanglePiece.setStroke(Color.GREEN);
                rectanglePiece.setStrokeWidth(3);
            } else {
                rectanglePiece.setFill(Color.LIGHTBLUE);
                rectanglePiece.setOpacity(0.35);
                rectanglePiece.setStroke(Color.BLUE);
                rectanglePiece.setStrokeWidth(1.5);
            }

            pane.getChildren().add(rectanglePiece);

            afficherNomPiece(piece, xMin, yMin, largeur, hauteur);

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

    private void afficherNomPiece(Piece piece, double xMin, double yMin, double largeur, double hauteur) {
        Text nomPiece = new Text(piece.getUsage());

        nomPiece.setX(xMin + largeur / 2 - 25);
        nomPiece.setY(yMin + hauteur / 2);

        vue.getPanePlan().getChildren().add(nomPiece);
    }

    private void afficherNomsMurs(double xMin, double yMin, double largeur, double hauteur) {
        Label murNord = creerLabelMur("Mur Nord");
        murNord.setLayoutX(xMin + largeur / 2 - 35);
        murNord.setLayoutY(yMin - 10);

        Label murSud = creerLabelMur("Mur Sud");
        murSud.setLayoutX(xMin + largeur / 2 - 30);
        murSud.setLayoutY(yMin + hauteur - 12);

        Label murEst = creerLabelMur("Mur Est");
        murEst.setLayoutX(xMin + largeur - 55);
        murEst.setLayoutY(yMin + hauteur / 2 - 12);

        Label murOuest = creerLabelMur("Mur Ouest");
        murOuest.setLayoutX(xMin + 5);
        murOuest.setLayoutY(yMin + hauteur / 2 - 12);

        vue.getPanePlan().getChildren().addAll(
                murNord,
                murSud,
                murEst,
                murOuest
        );
    }

    private Label creerLabelMur(String texte) {
        Label label = new Label(texte);

        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 1px;" +
                "-fx-padding: 2px 6px;" +
                "-fx-font-size: 11px;"
        );

        return label;
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
            vue.getComboSurface().getItems().clear();
            return;
        }

        String revSol = pieceSelectionnee.getRevetementSol() == null
                ? "aucun"
                : pieceSelectionnee.getRevetementSol().getNomRevt();

        String revPlafond = pieceSelectionnee.getRevetementPlafond() == null
                ? "aucun"
                : pieceSelectionnee.getRevetementPlafond().getNomRevt();

        String details =
                "Nom : " + pieceSelectionnee.getUsage()
                + "\nLargeur : " + String.format("%.2f", pieceSelectionnee.getLargeur()) + " m"
                + "\nHauteur : " + String.format("%.2f", pieceSelectionnee.getHauteur()) + " m"
                + "\nSurface : " + String.format("%.2f", pieceSelectionnee.getSuperficie()) + " m²"
                + "\nRevêtement sol : " + revSol
                + "\nRevêtement plafond : " + revPlafond;

        vue.getLabelDetailsPiece().setText(details);

        for (Mur mur : pieceSelectionnee.getMurs()) {
            vue.getListeMurs().getItems().add(mur);
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}