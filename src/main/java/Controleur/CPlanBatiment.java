/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;

import Modele.Batiment;
import Modele.Piece;
import Modele.Point;
import Vue.VuePlanBatiment;
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

public class CPlanBatiment {

    private VuePlanBatiment vue;
    private Stage fenetre;
    private Batiment batiment;

    // Les 2 points choisis par l'utilisateur
    private Point premierPoint;
    private Point deuxiemePoint;

    // Marges pour le dessin
    private final double margeGauche = 40;
    private final double margeHaut = 40;

    // Taille maximale du dessin du bâtiment dans le Pane
    private final double largeurDessin = 700;
    private final double hauteurDessin = 450;

    // Facteur d'échelle pour passer des mètres aux pixels
    private double echelle;

    public CPlanBatiment(Stage fenetre, Batiment batiment) {
        this.fenetre = fenetre;
        this.batiment = batiment;

        vue = new VuePlanBatiment();

        Scene scene = new Scene(vue.getRoot(), 1000, 750);
        fenetre.setScene(scene);
        fenetre.setTitle("Plan du bâtiment");
        fenetre.show();

        // Calcule l'échelle puis dessine le bâtiment vide
        calculerEchelle();
        rafraichirPlan();

        // Gestion des clics dans la zone de dessin
        vue.getPanePlan().setOnMouseClicked(event -> {
            gererClicSouris(event.getX(), event.getY());
        });

        // Bouton pour effacer une sélection en cours
        vue.getBoutonEffacerSelection().setOnAction(e -> {
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoSurface().setText("Sélection effacée. Recommencez.");
            rafraichirPlan();
        });

        // Bouton pour supprimer la dernière pièce créée
        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> {
            if (!batiment.getPieces().isEmpty()) {
                batiment.supprimerDernierePiece();
                premierPoint = null;
                deuxiemePoint = null;
                vue.getInfoSurface().setText("La dernière pièce a été supprimée.");
                rafraichirPlan();
            } else {
                vue.getInfoSurface().setText("Il n'y a aucune pièce à supprimer.");
            }
        });

        // Bouton pour fermer la fenêtre
        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    // Calcule l'échelle d'affichage pour que le bâtiment rentre dans le Pane
    private void calculerEchelle() {
        double echelleX = largeurDessin / batiment.getXmax();
        double echelleY = hauteurDessin / batiment.getYmax();

        // On prend la plus petite pour que tout le bâtiment soit visible
        echelle = Math.min(echelleX, echelleY);
    }

    // Gère un clic souris dans la zone du plan
    private void gererClicSouris(double xPixel, double yPixel) {

        // On vérifie que le clic est bien à l'intérieur du bâtiment
        if (!clicDansBatiment(xPixel, yPixel)) {
            vue.getInfoSurface().setText("Le clic doit être à l'intérieur du bâtiment.");
            return;
        }

        // Conversion des coordonnées pixels vers coordonnées réelles
        float xReel = (float) ((xPixel - margeGauche) / echelle);
        float yReel = (float) ((yPixel - margeHaut) / echelle);

        Point pointClique = new Point(xReel, yReel);

        // Premier clic : on enregistre juste le premier point
        if (premierPoint == null) {
            premierPoint = pointClique;
            vue.getInfoSurface().setText(
                    "Premier point enregistré : (" + arrondir(xReel) + " ; " + arrondir(yReel) + ")."
            );
            rafraichirPlan();
            return;
        }

        // Deuxième clic : on enregistre le deuxième point
        deuxiemePoint = pointClique;

        // On vérifie qu'on a bien un vrai rectangle
        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoSurface().setText("Les 2 points doivent former un rectangle.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        // On demande le nom ou l'usage de la pièce
        String usage = demanderUsagePiece();

        if (usage == null || usage.trim().isEmpty()) {
            usage = "Pièce";
        }

        // Création de la pièce
        Piece nouvellePiece = new Piece(premierPoint, deuxiemePoint, usage);

        // Vérifie que la pièce reste bien dans le bâtiment
        if (!pieceDansBatiment(nouvellePiece)) {
            vue.getInfoSurface().setText("Cette pièce dépasse du bâtiment.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        // Vérifie qu'elle ne chevauche pas une autre pièce
        if (chevaucheUnePieceExistante(nouvellePiece)) {
            vue.getInfoSurface().setText("Cette pièce chevauche une pièce déjà créée.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        // On ajoute la pièce au bâtiment
        batiment.ajouterPiece(nouvellePiece);

        vue.getInfoSurface().setText(
                usage + " créée : " + arrondir(nouvellePiece.getSuperficie()) + " m²"
        );

        // On remet les points à zéro pour créer la prochaine pièce
        premierPoint = null;
        deuxiemePoint = null;

        rafraichirPlan();
    }

    // Vérifie si le clic est dans le rectangle du bâtiment
    private boolean clicDansBatiment(double xPixel, double yPixel) {
        double largeurBatimentPx = batiment.getXmax() * echelle;
        double hauteurBatimentPx = batiment.getYmax() * echelle;

        return xPixel >= margeGauche
                && xPixel <= margeGauche + largeurBatimentPx
                && yPixel >= margeHaut
                && yPixel <= margeHaut + hauteurBatimentPx;
    }

    // Vérifie que la pièce reste dans le bâtiment
    private boolean pieceDansBatiment(Piece piece) {
        return piece.getXMin() >= 0
                && piece.getYMin() >= 0
                && piece.getXMax() <= batiment.getXmax()
                && piece.getYMax() <= batiment.getYmax();
    }

    // Vérifie si une nouvelle pièce chevauche une pièce existante
    private boolean chevaucheUnePieceExistante(Piece nouvellePiece) {
        for (Piece pieceExistante : batiment.getPieces()) {

            boolean pasDeChevauchement =
                    nouvellePiece.getXMax() <= pieceExistante.getXMin()
                    || nouvellePiece.getXMin() >= pieceExistante.getXMax()
                    || nouvellePiece.getYMax() <= pieceExistante.getYMin()
                    || nouvellePiece.getYMin() >= pieceExistante.getYMax();

            // Si on n'est pas dans le cas "pas de chevauchement",alors il y a chevauchement
           
            if (!pasDeChevauchement) {
                return true;
            }
        }

        return false;
    }

    // Redessine entièrement le plan
    private void rafraichirPlan() {
        Pane pane = vue.getPanePlan();
        pane.getChildren().clear();

        // 1. Dessin du contour du bâtiment
        Rectangle rectangleBatiment = new Rectangle(
                margeGauche,
                margeHaut,
                batiment.getXmax() * echelle,
                batiment.getYmax() * echelle
        );
        rectangleBatiment.setFill(Color.TRANSPARENT);
        rectangleBatiment.setStroke(Color.BLACK);
        rectangleBatiment.setStrokeWidth(3);

        pane.getChildren().add(rectangleBatiment);

        // 2. Dessin de toutes les pièces créées
        for (Piece piece : batiment.getPieces()) {
            Rectangle rectanglePiece = new Rectangle(
                    margeGauche + piece.getXMin() * echelle,
                    margeHaut + piece.getYMin() * echelle,
                    piece.getLargeur() * echelle,
                    piece.getHauteur() * echelle
            );

            rectanglePiece.setFill(Color.rgb(173, 216, 230, 0.35));
            rectanglePiece.setStroke(Color.DODGERBLUE);
            rectanglePiece.setStrokeWidth(2);

            pane.getChildren().add(rectanglePiece);
        }

        // 3. Dessin du premier point s'il existe
        if (premierPoint != null) {
            Circle point1 = new Circle(
                    margeGauche + premierPoint.getX() * echelle,
                    margeHaut + premierPoint.getY() * echelle,
                    5
            );
            point1.setFill(Color.RED);
            pane.getChildren().add(point1);
        }

        // 4. Dessin du deuxième point s'il existe
        if (deuxiemePoint != null) {
            Circle point2 = new Circle(
                    margeGauche + deuxiemePoint.getX() * echelle,
                    margeHaut + deuxiemePoint.getY() * echelle,
                    5
            );
            point2.setFill(Color.GREEN);
            pane.getChildren().add(point2);
        }
    }

    // Demande à l'utilisateur le type de pièce
    private String demanderUsagePiece() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle pièce");
        dialog.setHeaderText("Créer une nouvelle pièce");
        dialog.setContentText("Usage de la pièce (ex : cuisine, chambre, salon) :");

        Optional<String> resultat = dialog.showAndWait();
        return resultat.orElse("Pièce");
    }

    // Méthode simple pour arrondir l'affichage
    private String arrondir(float valeur) {
        return String.format("%.2f", valeur);
    }
}