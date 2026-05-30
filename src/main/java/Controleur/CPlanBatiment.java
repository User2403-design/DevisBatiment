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


public class CPlanBatiment {

    private VuePlanBatiment vue;
    private Stage fenetre;
    private Batiment batiment;

    
    private Point premierPoint;
    private Point deuxiemePoint;

    
    private double echelle;
    private final double margeSecurite = 40.0; // Espace minimal aux bords du Pane
    
    // Tolérance du magnétisme fixée en PIXELS sur l'écran
    private final double SEUIL_MAGNETISME_PIXELS = 20.0;

    public CPlanBatiment(Stage fenetre, Batiment batiment) {
        this.fenetre = fenetre;
        this.batiment = batiment;
        this.vue = new VuePlanBatiment();

        Scene scene = new Scene(vue.getRoot());
        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur de Plan Interactif - " + batiment.getTypeBatiment());
        
       
        fenetre.setMaximized(true);
        fenetre.show();

        // 2. Initialisation de l'affichage (après show() pour avoir les dimensions)
        calculerEchelle();
        rafraichirPlan();


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
            gererClicSouris(event.getX(), event.getY()); // appelle la methode gererclicsouris ave cles coordonnées du clic
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

    
      //Calcule l'échelle optimale pour que le bâtiment occupe le maximum d'espace
    // sans jamais dépasser du Pane, tout en gardant ses proportions.
    
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
    
    
     // Ajuste les coordonnées du clic pour les "coller" aux murs proches.
     // Le seuil visuel en pixels est converti dynamiquement en mètres.
     
    private Point appliquerMagnetisme(float xReel, float yReel) {
        float xSnap = xReel;
        float ySnap = yReel;
        
        // Convertit les pixels de l'écran en distance réelle (mètres) selon le zoom actuel
        float seuilMetres = (float) (SEUIL_MAGNETISME_PIXELS / echelle);
        
        float distMinX = seuilMetres;
        float distMinY = seuilMetres;

        // Bords du bâtiment (murs extérieurs)
        float[] lignesX = {0, batiment.getXmax()};
        float[] lignesY = {0, batiment.getYmax()};

        for (float lx : lignesX) {
            if (Math.abs(xReel - lx) < distMinX) { distMinX = Math.abs(xReel - lx); xSnap = lx; }
        }
        for (float ly : lignesY) {
            if (Math.abs(yReel - ly) < distMinY) { distMinY = Math.abs(yReel - ly); ySnap = ly; }
        }

        // Bords des pièces existantes
        for (Piece p : batiment.getPieces()) {
            if (Math.abs(xReel - p.getXMin()) < distMinX) { distMinX = Math.abs(xReel - p.getXMin()); xSnap = p.getXMin(); }
            if (Math.abs(xReel - p.getXMax()) < distMinX) { distMinX = Math.abs(xReel - p.getXMax()); xSnap = p.getXMax(); }
            if (Math.abs(yReel - p.getYMin()) < distMinY) { distMinY = Math.abs(yReel - p.getYMin()); ySnap = p.getYMin(); }
            if (Math.abs(yReel - p.getYMax()) < distMinY) { distMinY = Math.abs(yReel - p.getYMax()); ySnap = p.getYMax(); }
        }

        return new Point(xSnap, ySnap);
    }

    
     // Convertit un clic écran en coordonnées réelles dans le bâtiment,
     // en tenant compte du centrage dynamique.
     
    private void gererClicSouris(double xPixel, double yPixel) {
        // Calcul des offsets (=décalages) de centrage utilisés par rafraichirPlan
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
        
        // Application de l'assistance (magnétisme basé sur les pixels)
        Point ptClique = appliquerMagnetisme(xReel, yReel);

        if (premierPoint == null) {
            premierPoint = ptClique;
            vue.getInfoSurface().setText("Point 1 posé. Cliquez sur le coin opposé.");
            rafraichirPlan();
        } else {
            deuxiemePoint = ptClique;
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

   
    private void rafraichirPlan() {
        Pane p = vue.getPanePlan();
        p.getChildren().clear();

        // Calcul dynamique du centrage (Offset)
        double offsetX = (p.getWidth() - (batiment.getXmax() * echelle)) / 2;
        double offsetY = (p.getHeight() - (batiment.getYmax() * echelle)) / 2;

        //  Contour extérieur
        Rectangle contour = new Rectangle(offsetX, offsetY, batiment.getXmax() * echelle, batiment.getYmax() * echelle);
        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(2.5);
        p.getChildren().add(contour);

        //  Pièces validées
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

        // Aide visuelle pour la sélection
        if (premierPoint != null) {
            Circle c = new Circle(offsetX + premierPoint.getX() * echelle, offsetY + premierPoint.getY() * echelle, 4, Color.RED);
            p.getChildren().add(c);
        }
    }

    private String demanderUsage() {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle pièce");
            dialog.setHeaderText("Créer une nouvelle pièce");
            dialog.setContentText("Usage de la pièce (ex : cuisine, chambre, salon) :");

            Optional<String> resultat = dialog.showAndWait();
            return resultat.orElse("Pièce");
          }
}
