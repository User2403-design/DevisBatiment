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
 * Contrôleur gérant l'édition dynamique du plan.
 * Adapte l'affichage en temps réel à la taille de l'écran.
 */
public class CPlanBatiment {

    private VuePlanBatiment vue;
    private Stage fenetre;
    private Batiment batiment;

    // État de la sélection
    private Point premierPoint;
    private Point deuxiemePoint;

    // Paramètres d'affichage dynamiques
    private double echelle;
    private final double margeSecurite = 40.0; // Espace minimal aux bords du Pane

    public CPlanBatiment(Stage fenetre, Batiment batiment) {
        this.fenetre = fenetre;
        this.batiment = batiment;
        this.vue = new VuePlanBatiment();

        Scene scene = new Scene(vue.getRoot());
        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur de Plan Interactif - " + batiment.getTypeBatiment());
        
        // 1. Passage en plein écran
        fenetre.setMaximized(true);
        fenetre.show();

        // 2. Initialisation de l'affichage (après show() pour avoir les dimensions)
        calculerEchelle();
        rafraichirPlan();

        // --- Écouteurs d'événements (Listeners) ---

        // Redessiner si la zone de dessin change de taille (redimensionnement fenêtre)
        vue.getPanePlan().widthProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });
        vue.getPanePlan().heightProperty().addListener((obs, oldVal, newVal) -> {
            calculerEchelle();
            rafraichirPlan();
        });

        // Gestion du clic pour le dessin
        vue.getPanePlan().setOnMouseClicked(event -> {
            gererClicSouris(event.getX(), event.getY());
        });

        // Liaison des boutons de la sidebar
        vue.getBoutonEffacerSelection().setOnAction(e -> {
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoSurface().setText("Sélection annulée.");
            rafraichirPlan();
        });

        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> {
            if (!batiment.getPieces().isEmpty()) {
                batiment.supprimerDernierePiece();
                vue.getInfoSurface().setText("Dernière pièce supprimée.");
                rafraichirPlan();
            }
        });

        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    /**
     * Calcule l'échelle optimale pour que le bâtiment occupe le maximum d'espace
     * sans jamais dépasser du Pane, tout en gardant ses proportions.
     */
    private void calculerEchelle() {
        double largeurDisponible = vue.getPanePlan().getWidth() - (margeSecurite * 2);
        double hauteurDisponible = vue.getPanePlan().getHeight() - (margeSecurite * 2);

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) return;

        // Ratio Pixels / Mètres sur chaque axe
        double echelleX = largeurDisponible / batiment.getXmax();
        double echelleY = hauteurDisponible / batiment.getYmax();

        // On garde la plus petite des deux pour que tout le bâtiment soit visible
        echelle = Math.min(echelleX, echelleY);
    }

    /**
     * Convertit un clic écran en coordonnées réelles dans le bâtiment,
     * en tenant compte du centrage dynamique.
     */
    private void gererClicSouris(double xPixel, double yPixel) {
        // Calcul des offsets (décalages) de centrage utilisés par rafraichirPlan
        double offsetX = (vue.getPanePlan().getWidth() - (batiment.getXmax() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (batiment.getYmax() * echelle)) / 2;

        // Conversion vers les mètres
        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        // Sécurité : clic hors du bâtiment
        if (xReel < 0 || xReel > batiment.getXmax() || yReel < 0 || yReel > batiment.getYmax()) {
            vue.getInfoSurface().setText("Clic hors des murs extérieurs !");
            return;
        }

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);
            vue.getInfoSurface().setText("Point 1 posé. Cliquez sur le coin opposé.");
            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);
            finaliserPiece();
        }
    }

    private void finaliserPiece() {
        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoSurface().setText("Erreur : Points alignés, rectangle impossible.");
            deuxiemePoint = null;
            return;
        }

        String usage = demanderUsage();
        Piece p = new Piece(premierPoint, deuxiemePoint, usage);

        if (chevaucheUnePiece(p)) {
            vue.getInfoSurface().setText("Erreur : Chevauchement avec une pièce existante.");
        } else {
            batiment.ajouterPiece(p);
            vue.getInfoSurface().setText(usage + " créé (" + String.format("%.2f", p.getSuperficie()) + " m²)");
        }

        premierPoint = null;
        deuxiemePoint = null;
        rafraichirPlan();
    }

    private boolean chevaucheUnePiece(Piece n) {
        for (Piece p : batiment.getPieces()) {
            if (!(n.getXMax() <= p.getXMin() || n.getXMin() >= p.getXMax() ||
                  n.getYMax() <= p.getYMin() || n.getYMin() >= p.getYMax())) return true;
        }
        return false;
    }

    /**
     * Dessine le bâtiment et ses composants en les centrant dans le Pane.
     */
    private void rafraichirPlan() {
        Pane p = vue.getPanePlan();
        p.getChildren().clear();

        // Calcul dynamique du centrage (Offset)
        double offsetX = (p.getWidth() - (batiment.getXmax() * echelle)) / 2;
        double offsetY = (p.getHeight() - (batiment.getYmax() * echelle)) / 2;

        // 1. Contour extérieur
        Rectangle contour = new Rectangle(offsetX, offsetY, batiment.getXmax() * echelle, batiment.getYmax() * echelle);
        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(2.5);
        p.getChildren().add(contour);

        // 2. Pièces validées
        for (Piece piece : batiment.getPieces()) {
            Rectangle r = new Rectangle(
                offsetX + piece.getXMin() * echelle,
                offsetY + piece.getYMin() * echelle,
                piece.getLargeur() * echelle,
                piece.getHauteur() * echelle
            );
            r.setFill(Color.LIGHTBLUE);
            r.setOpacity(0.4);
            r.setStroke(Color.BLUE);
            p.getChildren().add(r);
        }

        // 3. Aide visuelle pour la sélection
        if (premierPoint != null) {
            Circle c = new Circle(offsetX + premierPoint.getX() * echelle, offsetY + premierPoint.getY() * echelle, 4, Color.RED);
            p.getChildren().add(c);
        }
    }

    private String demanderUsage() {
        TextInputDialog d = new TextInputDialog("Pièce");
        d.setHeaderText("Type de la pièce");
        d.setContentText("Usage :");
        Optional<String> r = d.showAndWait();
        return r.orElse("Pièce");
    }
}


//package Controleur;
//
//import Modele.Batiment;
//import Modele.Piece;
//import Modele.Point;
//import Vue.VuePlanBatiment;
//import javafx.scene.Scene;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Rectangle;
//import javafx.stage.Stage;
//import java.util.Optional;
//
///**
// *
// * @author chloe
// */
//
//public class CPlanBatiment {
//
//    private VuePlanBatiment vue;
//    private Stage fenetre;
//    private Batiment batiment;
//
//    // Les 2 points choisis par l'utilisateur
//    private Point premierPoint;
//    private Point deuxiemePoint;
//
//    // Marges pour le dessin
//    private final double margeGauche = 40;
//    private final double margeHaut = 40;
//
//    // Taille maximale du dessin du bâtiment dans le Pane
//    private final double largeurDessin = 700;
//    private final double hauteurDessin = 450;
//
//    // Facteur d'échelle pour passer des mètres aux pixels
//    private double echelle;
//
//    public CPlanBatiment(Stage fenetre, Batiment batiment) {
//        this.fenetre = fenetre;
//        this.batiment = batiment;
//
//        vue = new VuePlanBatiment();
//
//        Scene scene = new Scene(vue.getRoot(), 1000, 750);
//        fenetre.setScene(scene);
//        fenetre.setTitle("Plan du bâtiment");
//        fenetre.show();
//
//        // Calcule l'échelle puis dessine le bâtiment vide
//        calculerEchelle();
//        rafraichirPlan();
//
//        // Gestion des clics dans la zone de dessin
//        vue.getPanePlan().setOnMouseClicked(event -> {
//            gererClicSouris(event.getX(), event.getY());
//        });
//
//        // Bouton pour effacer une sélection en cours
//        vue.getBoutonEffacerSelection().setOnAction(e -> {
//            premierPoint = null;
//            deuxiemePoint = null;
//            vue.getInfoSurface().setText("Sélection effacée. Recommencez.");
//            rafraichirPlan();
//        });
//
//        // Bouton pour supprimer la dernière pièce créée
//        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> {
//            if (!batiment.getPieces().isEmpty()) {
//                batiment.supprimerDernierePiece();
//                premierPoint = null;
//                deuxiemePoint = null;
//                vue.getInfoSurface().setText("La dernière pièce a été supprimée.");
//                rafraichirPlan();
//            } else {
//                vue.getInfoSurface().setText("Il n'y a aucune pièce à supprimer.");
//            }
//        });
//
//        // Bouton pour fermer la fenêtre
//        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
//    }
//
//    // Calcule l'échelle d'affichage pour que le bâtiment rentre dans le Pane
//    private void calculerEchelle() {
//        double echelleX = largeurDessin / batiment.getXmax();
//        double echelleY = hauteurDessin / batiment.getYmax();
//
//        // On prend la plus petite pour que tout le bâtiment soit visible
//        echelle = Math.min(echelleX, echelleY);
//    }
//
//    // Gère un clic souris dans la zone du plan
//    private void gererClicSouris(double xPixel, double yPixel) {
//
//        // On vérifie que le clic est bien à l'intérieur du bâtiment
//        if (!clicDansBatiment(xPixel, yPixel)) {
//            vue.getInfoSurface().setText("Le clic doit être à l'intérieur du bâtiment.");
//            return;
//        }
//
//        // Conversion des coordonnées pixels vers coordonnées réelles
//        float xReel = (float) ((xPixel - margeGauche) / echelle);
//        float yReel = (float) ((yPixel - margeHaut) / echelle);
//
//        Point pointClique = new Point(xReel, yReel);
//
//        // Premier clic : on enregistre juste le premier point
//        if (premierPoint == null) {
//            premierPoint = pointClique;
//            vue.getInfoSurface().setText(
//                    "Premier point enregistré : (" + arrondir(xReel) + " ; " + arrondir(yReel) + ")."
//            );
//            rafraichirPlan();
//            return;
//        }
//
//        // Deuxième clic : on enregistre le deuxième point
//        deuxiemePoint = pointClique;
//
//        // On vérifie qu'on a bien un vrai rectangle
//        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
//            vue.getInfoSurface().setText("Les 2 points doivent former un rectangle.");
//            deuxiemePoint = null;
//            rafraichirPlan();
//            return;
//        }
//
//        // On demande le nom ou l'usage de la pièce
//        String usage = demanderUsagePiece();
//
//        if (usage == null || usage.trim().isEmpty()) {
//            usage = "Pièce";
//        }
//
//        // Création de la pièce
//        Piece nouvellePiece = new Piece(premierPoint, deuxiemePoint, usage);
//
//        // Vérifie que la pièce reste bien dans le bâtiment
//        if (!pieceDansBatiment(nouvellePiece)) {
//            vue.getInfoSurface().setText("Cette pièce dépasse du bâtiment.");
//            deuxiemePoint = null;
//            rafraichirPlan();
//            return;
//        }
//
//        // Vérifie qu'elle ne chevauche pas une autre pièce
//        if (chevaucheUnePieceExistante(nouvellePiece)) {
//            vue.getInfoSurface().setText("Cette pièce chevauche une pièce déjà créée.");
//            deuxiemePoint = null;
//            rafraichirPlan();
//            return;
//        }
//
//        // On ajoute la pièce au bâtiment
//        batiment.ajouterPiece(nouvellePiece);
//
//        vue.getInfoSurface().setText(
//                usage + " créée : " + arrondir(nouvellePiece.getSuperficie()) + " m²"
//        );
//
//        // On remet les points à zéro pour créer la prochaine pièce
//        premierPoint = null;
//        deuxiemePoint = null;
//
//        rafraichirPlan();
//    }
//
//    // Vérifie si le clic est dans le rectangle du bâtiment
//    private boolean clicDansBatiment(double xPixel, double yPixel) {
//        double largeurBatimentPx = batiment.getXmax() * echelle;
//        double hauteurBatimentPx = batiment.getYmax() * echelle;
//
//        return xPixel >= margeGauche
//                && xPixel <= margeGauche + largeurBatimentPx
//                && yPixel >= margeHaut
//                && yPixel <= margeHaut + hauteurBatimentPx;
//    }
//
//    // Vérifie que la pièce reste dans le bâtiment
//    private boolean pieceDansBatiment(Piece piece) {
//        return piece.getXMin() >= 0
//                && piece.getYMin() >= 0
//                && piece.getXMax() <= batiment.getXmax()
//                && piece.getYMax() <= batiment.getYmax();
//    }
//
//    // Vérifie si une nouvelle pièce chevauche une pièce existante
//    private boolean chevaucheUnePieceExistante(Piece nouvellePiece) {
//        for (Piece pieceExistante : batiment.getPieces()) {
//
//            boolean pasDeChevauchement =
//                    nouvellePiece.getXMax() <= pieceExistante.getXMin()
//                    || nouvellePiece.getXMin() >= pieceExistante.getXMax()
//                    || nouvellePiece.getYMax() <= pieceExistante.getYMin()
//                    || nouvellePiece.getYMin() >= pieceExistante.getYMax();
//
//            // Si on n'est pas dans le cas "pas de chevauchement",alors il y a chevauchement
//           
//            if (!pasDeChevauchement) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    // Redessine entièrement le plan
//    private void rafraichirPlan() {
//        Pane pane = vue.getPanePlan();
//        pane.getChildren().clear();
//
//        // 1. Dessin du contour du bâtiment
//        Rectangle rectangleBatiment = new Rectangle(
//                margeGauche,
//                margeHaut,
//                batiment.getXmax() * echelle,
//                batiment.getYmax() * echelle
//        );
//        rectangleBatiment.setFill(Color.TRANSPARENT);
//        rectangleBatiment.setStroke(Color.BLACK);
//        rectangleBatiment.setStrokeWidth(3);
//
//        pane.getChildren().add(rectangleBatiment);
//
//        // 2. Dessin de toutes les pièces créées
//        for (Piece piece : batiment.getPieces()) {
//            Rectangle rectanglePiece = new Rectangle(
//                    margeGauche + piece.getXMin() * echelle,
//                    margeHaut + piece.getYMin() * echelle,
//                    piece.getLargeur() * echelle,
//                    piece.getHauteur() * echelle
//            );
//
//            rectanglePiece.setFill(Color.rgb(173, 216, 230, 0.35));
//            rectanglePiece.setStroke(Color.DODGERBLUE);
//            rectanglePiece.setStrokeWidth(2);
//
//            pane.getChildren().add(rectanglePiece);
//        }
//
//        // 3. Dessin du premier point s'il existe
//        if (premierPoint != null) {
//            Circle point1 = new Circle(
//                    margeGauche + premierPoint.getX() * echelle,
//                    margeHaut + premierPoint.getY() * echelle,
//                    5
//            );
//            point1.setFill(Color.RED);
//            pane.getChildren().add(point1);
//        }
//
//        // 4. Dessin du deuxième point s'il existe
//        if (deuxiemePoint != null) {
//            Circle point2 = new Circle(
//                    margeGauche + deuxiemePoint.getX() * echelle,
//                    margeHaut + deuxiemePoint.getY() * echelle,
//                    5
//            );
//            point2.setFill(Color.GREEN);
//            pane.getChildren().add(point2);
//        }
//    }
//
//    // Demande à l'utilisateur le type de pièce
//    private String demanderUsagePiece() {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Nouvelle pièce");
//        dialog.setHeaderText("Créer une nouvelle pièce");
//        dialog.setContentText("Usage de la pièce (ex : cuisine, chambre, salon) :");
//
//        Optional<String> resultat = dialog.showAndWait();
//        return resultat.orElse("Pièce");
//    }
//
//    // Méthode simple pour arrondir l'affichage
//    private String arrondir(float valeur) {
//        return String.format("%.2f", valeur);
//    }
//}