
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//

package Controleur;

import Modele.Appartement;
import Modele.GestionCatalogue;
import Modele.Mur;
import Modele.Ouverture;
import Modele.Piece;
import Modele.Point;
import Modele.Revetement;
import Vue.VuePlanAppartement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur gérant la logique de l'appartement.
 * Ajout de l'ouverture du catalogue depuis le bandeau supérieur.
 */
public class CPlanAppartement {

    private VuePlanAppartement vue;
    private Stage fenetre;
    private Appartement appartement;
    private GestionCatalogue catalogue;

    private Point premierPoint;
    private Piece pieceSelectionnee;
    private double echelle = 1;
    private final double margeSecurite = 40.0;
    private int compteurOuverture = 1;
    
    // Tolérance du magnétisme fixée en PIXELS sur l'écran
    private final double SEUIL_MAGNETISME_PIXELS = 20.0;

    public CPlanAppartement(Stage fenetre, Appartement appartement) {
        this.fenetre = fenetre;
        this.appartement = appartement;
        this.catalogue = new GestionCatalogue();
        this.vue = new VuePlanAppartement();

        Scene scene = new Scene(vue.getRoot(), 1300, 850);
        fenetre.setScene(scene);
        fenetre.setTitle("Édition du logement : " + appartement.getNom());
        fenetre.setMaximized(true);
        fenetre.show();

        initEvents();
        calculerEchelle();
        rafraichirPlan();
        mettreAJourSidebar();
    }

    private void initEvents() {
        // Redimensionnement
        vue.getPanePlan().widthProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });
        vue.getPanePlan().heightProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });

        // Clic sur le plan
        vue.getPanePlan().setOnMouseClicked(e -> gererClicSouris(e.getX(), e.getY()));

        // Sélection dans la liste
        vue.getListePieces().getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> selectionnerPiece(n));

        // Boutons du bas
        vue.getBoutonEffacerSelection().setOnAction(e -> annulerSelection());
        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> supprimerDernierePiece());
        vue.getBoutonFermer().setOnAction(e -> fenetre.close());

        // Boutons du haut
        vue.getBoutonOuvrirRevetements().setOnAction(e -> ouvrirFenetreRevetements());
        vue.getBoutonOuvrirOuvertures().setOnAction(e -> ouvrirFenetreOuvertures());
        
        // Nouveau : Événement pour le bouton Catalogue
        vue.getBoutonCatalogue().setOnAction(e -> {
            Stage fenetreCatalogue = new Stage();
            new CCatalogue(fenetreCatalogue);
        });
    }

    // --- LOGIQUE DES FENÊTRES SECONDAIRES ---

    private void ouvrirFenetreRevetements() {
        if (pieceSelectionnee == null) {
            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
            return;
        }

        Stage popup = new Stage();
        popup.setTitle("Revêtements - " + pieceSelectionnee.getUsage());

        ComboBox<String> comboSurface = new ComboBox<>();
        comboSurface.getItems().addAll("Sol", "Plafond");
        pieceSelectionnee.getMurs().forEach(m -> comboSurface.getItems().add(m.getIdMur()));
        comboSurface.setPromptText("Surface à traiter");

        ComboBox<Revetement> comboRev = new ComboBox<>();
        catalogue.getNomsRubriques().forEach(rub -> comboRev.getItems().addAll(catalogue.getProduits(rub)));
        comboRev.setPromptText("Matériau");

        Button btnValider = new Button("Appliquer");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setOnAction(e -> {
            String surf = comboSurface.getValue();
            Revetement r = comboRev.getValue();
            if (surf != null && r != null) {
                if (surf.equals("Sol")) pieceSelectionnee.setRevetementSol(r);
                else if (surf.equals("Plafond")) pieceSelectionnee.setRevetementPlafond(r);
                else pieceSelectionnee.getMurs().stream().filter(m -> m.getIdMur().equals(surf)).findFirst().ifPresent(m -> m.setRevetement(r));
                mettreAJourDetailsPiece();
                rafraichirPlan();
                popup.close();
            }
        });

        VBox layout = new VBox(15, new Label("Appliquer un matériau sur une surface :"), comboSurface, comboRev, btnValider);
        layout.setPadding(new Insets(20));
        popup.setScene(new Scene(layout, 350, 250));
        popup.show();
    }

    private void ouvrirFenetreOuvertures() {
        if (pieceSelectionnee == null) {
            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
            return;
        }

        Stage popup = new Stage();
        popup.setTitle("Ouvertures - " + pieceSelectionnee.getUsage());

        ComboBox<String> comboMur = new ComboBox<>();
        pieceSelectionnee.getMurs().forEach(m -> comboMur.getItems().add(m.getIdMur()));
        comboMur.setPromptText("Choisir un mur");

        ComboBox<String> comboType = new ComboBox<>();
        comboType.getItems().addAll("Fenêtre", "Porte");
        
        TextField txtL = new TextField(); txtL.setPromptText("Largeur (m)");
        TextField txtH = new TextField(); txtH.setPromptText("Hauteur (m)");

        Button btnAjouter = new Button("Ajouter l'ouverture");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setOnAction(e -> {
            try {
                String murId = comboMur.getValue();
                String type = comboType.getValue();
                float l = Float.parseFloat(txtL.getText());
                float h = Float.parseFloat(txtH.getText());

                if (murId != null && type != null) {
                    pieceSelectionnee.getMurs().stream().filter(m -> m.getIdMur().equals(murId)).findFirst()
                        .ifPresent(m -> m.ajouterOuverture(new Ouverture(compteurOuverture++, type, l, h)));
                    mettreAJourDetailsPiece();
                    popup.close();
                }
            } catch (Exception ex) { afficherErreur("Valeurs numériques invalides."); }
        });

        VBox layout = new VBox(15, new Label("Ajouter une ouverture sur un mur :"), comboMur, comboType, txtL, txtH, btnAjouter);
        layout.setPadding(new Insets(20));
        popup.setScene(new Scene(layout, 350, 350));
        popup.show();
    }

    // --- DESSIN ET SIDEBAR ---

    private void calculerEchelle() {
        double lp = vue.getPanePlan().getWidth();
        double hp = vue.getPanePlan().getHeight();
        if (lp <= 0) lp = 900; if (hp <= 0) hp = 650;

        double ld = lp - (margeSecurite * 2);
        double hd = hp - (margeSecurite * 2);

        echelle = Math.min(ld / appartement.getLargeur(), hd / appartement.getHauteur());
    }
    
    /**
     * Ajuste les coordonnées du clic pour les "coller" aux murs proches.
     * Conversion dynamique pixels -> mètres.
     */
    private Point appliquerMagnetisme(float xReel, float yReel) {
        float xSnap = xReel;
        float ySnap = yReel;
        
        float seuilMetres = (float) (SEUIL_MAGNETISME_PIXELS / echelle);
        float distMinX = seuilMetres;
        float distMinY = seuilMetres;

        // Limites de l'appartement
        float[] lignesX = {0, appartement.getLargeur()};
        float[] lignesY = {0, appartement.getHauteur()};

        for (float lx : lignesX) {
            if (Math.abs(xReel - lx) < distMinX) { distMinX = Math.abs(xReel - lx); xSnap = lx; }
        }
        for (float ly : lignesY) {
            if (Math.abs(yReel - ly) < distMinY) { distMinY = Math.abs(yReel - ly); ySnap = ly; }
        }

        // Bords des autres pièces
        for (Piece p : appartement.getPieces()) {
            if (Math.abs(xReel - p.getXMin()) < distMinX) { distMinX = Math.abs(xReel - p.getXMin()); xSnap = p.getXMin(); }
            if (Math.abs(xReel - p.getXMax()) < distMinX) { distMinX = Math.abs(xReel - p.getXMax()); xSnap = p.getXMax(); }
            if (Math.abs(yReel - p.getYMin()) < distMinY) { distMinY = Math.abs(yReel - p.getYMin()); ySnap = p.getYMin(); }
            if (Math.abs(yReel - p.getYMax()) < distMinY) { distMinY = Math.abs(yReel - p.getYMax()); ySnap = p.getYMax(); }
        }

        return new Point(xSnap, ySnap);
    }

    private void gererClicSouris(double xPx, double yPx) {
        double offsetX = (vue.getPanePlan().getWidth() - (appartement.getLargeur() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (appartement.getHauteur() * echelle)) / 2;

        float xr = (float) ((xPx - offsetX) / echelle);
        float yr = (float) ((yPx - offsetY) / echelle);

        if (xr < 0 || xr > appartement.getLargeur() || yr < 0 || yr > appartement.getHauteur()) return;

        // L'utilisateur a cliqué sur une pièce existante ? (Sélection avec clic direct)
        Piece p = trouverPiece(xr, yr);
        if (p != null && premierPoint == null) {
            vue.getListePieces().getSelectionModel().select(p);
            return;
        }

        // Sinon, c'est un clic pour dessiner une nouvelle pièce (Application du magnétisme basé sur les pixels)
        Point ptClique = appliquerMagnetisme(xr, yr);

        if (premierPoint == null) {
            premierPoint = ptClique;
            vue.getInfoMessage().setText("Point 1 posé. Cliquez sur le coin opposé.");
            rafraichirPlan();
        } else {
            finaliserPiece(ptClique);
        }
    }

    private void finaliserPiece(Point p2) {
        if (premierPoint.getX() == p2.getX() || premierPoint.getY() == p2.getY()) {
            afficherErreur("Rectangle invalide.");
        } else {
            String usage = demanderNom();
            Piece np = new Piece(premierPoint, p2, usage);
            appartement.ajouterPiece(np);
            selectionnerPiece(np);
            mettreAJourSidebar();
        }
        premierPoint = null;
        rafraichirPlan();
    }

    private Piece trouverPiece(float x, float y) {
        return appartement.getPieces().stream().filter(p -> x >= p.getXMin() && x <= p.getXMax() && y >= p.getYMin() && y <= p.getYMax()).findFirst().orElse(null);
    }

    private void selectionnerPiece(Piece p) {
        pieceSelectionnee = p;
        if (p != null) vue.getInfoMessage().setText("Sélection : " + p.getUsage());
        mettreAJourDetailsPiece();
        rafraichirPlan();
    }

    private void rafraichirPlan() {
        Pane p = vue.getPanePlan(); p.getChildren().clear();
        double ox = (p.getWidth() - (appartement.getLargeur() * echelle)) / 2;
        double oy = (p.getHeight() - (appartement.getHauteur() * echelle)) / 2;

        // Contour de l'appartement
        Rectangle contour = new Rectangle(ox, oy, appartement.getLargeur() * echelle, appartement.getHauteur() * echelle);
        contour.setFill(Color.TRANSPARENT); contour.setStroke(Color.BLACK); contour.setStrokeWidth(3);
        p.getChildren().add(contour);

        for (Piece pc : appartement.getPieces()) {
            double pcX = ox + pc.getXMin() * echelle;
            double pcY = oy + pc.getYMin() * echelle;
            double pcW = pc.getLargeur() * echelle;
            double pcH = pc.getHauteur() * echelle;

            // Rectangle de la pièce
            Rectangle r = new Rectangle(pcX, pcY, pcW, pcH);
            r.setFill(pc == pieceSelectionnee ? Color.LIGHTGREEN : Color.LIGHTBLUE);
            r.setOpacity(0.3); r.setStroke(pc == pieceSelectionnee ? Color.GREEN : Color.BLUE);
            r.setStrokeWidth(2);
            p.getChildren().add(r);

            // Nom de la pièce (au centre)
            Text tUsage = new Text(pc.getUsage());
            tUsage.setFont(Font.font("System", FontWeight.BOLD, 12));
            tUsage.setX(pcX + pcW/2 - tUsage.getLayoutBounds().getWidth()/2);
            tUsage.setY(pcY + pcH/2);
            p.getChildren().add(tUsage);

            // --- Ajout des labels des murs ---
            for (Mur m : pc.getMurs()) {
                Text labelMur = new Text(m.getIdMur());
                labelMur.setFont(Font.font("System", 9));
                labelMur.setFill(Color.DARKSLATEGRAY);
                
                // Calcul des dimensions du texte pour l'alignement
                double textW = labelMur.getLayoutBounds().getWidth();
                double textH = labelMur.getLayoutBounds().getHeight();

                String id = m.getIdMur();
                // Utilisation de .contains() car le modèle renvoie "Mur Nord", "Mur Sud", etc.
                if (id.contains("Nord")) {
                    labelMur.setX(pcX + pcW/2 - textW/2);
                    labelMur.setY(pcY + 12);
                } else if (id.contains("Sud")) {
                    labelMur.setX(pcX + pcW/2 - textW/2);
                    labelMur.setY(pcY + pcH - 5);
                } else if (id.contains("Est")) {
                    labelMur.setX(pcX + pcW - textW - 5);
                    labelMur.setY(pcY + pcH/2 + textH/4);
                } else if (id.contains("Ouest")) {
                    labelMur.setX(pcX + 5);
                    labelMur.setY(pcY + pcH/2 + textH/4);
                }
                
                p.getChildren().add(labelMur);
            }
        }

        if (premierPoint != null) p.getChildren().add(new Circle(ox + premierPoint.getX() * echelle, oy + premierPoint.getY() * echelle, 4, Color.RED));
    }

    private void mettreAJourSidebar() {
        vue.getListePieces().getItems().setAll(appartement.getPieces());
        vue.getLabelNbPieces().setText("Nombre de pièces : " + appartement.getPieces().size());
    }

    private void mettreAJourDetailsPiece() {
        if (pieceSelectionnee == null) {
            vue.getLabelDetailsPiece().setText("Aucune pièce sélectionnée.");
            vue.getListeMurs().getItems().clear();
            return;
        }
        String s = String.format("Nom : %s\nSurface : %.2f m²\nSol : %s\nPlafond : %s", 
            pieceSelectionnee.getUsage(), pieceSelectionnee.getSuperficie(),
            pieceSelectionnee.getRevetementSol() != null ? pieceSelectionnee.getRevetementSol().getNomRevt() : "aucun",
            pieceSelectionnee.getRevetementPlafond() != null ? pieceSelectionnee.getRevetementPlafond().getNomRevt() : "aucun");
        vue.getLabelDetailsPiece().setText(s);
        vue.getListeMurs().getItems().setAll(pieceSelectionnee.getMurs());
    }

    private void annulerSelection() { premierPoint = null; pieceSelectionnee = null; rafraichirPlan(); mettreAJourDetailsPiece(); }
    
    private void supprimerDernierePiece() { appartement.supprimerDernierePiece(); annulerSelection(); mettreAJourSidebar(); }

    private String demanderNom() {
        TextInputDialog d = new TextInputDialog("Pièce");
        d.setTitle("Nouvelle pièce"); d.setHeaderText(null); d.setContentText("Nom de la pièce :");
        return d.showAndWait().orElse("Pièce");
    }

    private void afficherErreur(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle("Attention"); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}

///
////////*
//// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//// */
////
//
//package Controleur;
//
//import Modele.Appartement;
//import Modele.GestionCatalogue;
//import Modele.Mur;
//import Modele.Ouverture;
//import Modele.Piece;
//import Modele.Point;
//import Modele.Revetement;
//import Vue.VuePlanAppartement;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Contrôleur gérant la logique de l'appartement.
// * Ajout de l'ouverture du catalogue depuis le bandeau supérieur.
// */
//public class CPlanAppartement {
//
//    private VuePlanAppartement vue;
//    private Stage fenetre;
//    private Appartement appartement;
//    private GestionCatalogue catalogue;
//
//    private Point premierPoint;
//    private Piece pieceSelectionnee;
//    private double echelle = 1;
//    private final double margeSecurite = 40.0;
//    private int compteurOuverture = 1;
//    
//    // Tolérance du magnétisme (ex: 0.5m)
//    private final float SEUIL_MAGNETISME = 1.0f;
//
//    public CPlanAppartement(Stage fenetre, Appartement appartement) {
//        this.fenetre = fenetre;
//        this.appartement = appartement;
//        this.catalogue = new GestionCatalogue();
//        this.vue = new VuePlanAppartement();
//
//        Scene scene = new Scene(vue.getRoot(), 1300, 850);
//        fenetre.setScene(scene);
//        fenetre.setTitle("Édition du logement : " + appartement.getNom());
//        fenetre.setMaximized(true);
//        fenetre.show();
//
//        initEvents();
//        calculerEchelle();
//        rafraichirPlan();
//        mettreAJourSidebar();
//    }
//
//    private void initEvents() {
//        // Redimensionnement
//        vue.getPanePlan().widthProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });
//        vue.getPanePlan().heightProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });
//
//        // Clic sur le plan
//        vue.getPanePlan().setOnMouseClicked(e -> gererClicSouris(e.getX(), e.getY()));
//
//        // Sélection dans la liste
//        vue.getListePieces().getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> selectionnerPiece(n));
//
//        // Boutons du bas
//        vue.getBoutonEffacerSelection().setOnAction(e -> annulerSelection());
//        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> supprimerDernierePiece());
//        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
//
//        // Boutons du haut
//        vue.getBoutonOuvrirRevetements().setOnAction(e -> ouvrirFenetreRevetements());
//        vue.getBoutonOuvrirOuvertures().setOnAction(e -> ouvrirFenetreOuvertures());
//        
//        // Nouveau : Événement pour le bouton Catalogue
//        vue.getBoutonCatalogue().setOnAction(e -> {
//            Stage fenetreCatalogue = new Stage();
//            new CCatalogue(fenetreCatalogue);
//        });
//    }
//
//    // --- LOGIQUE DES FENÊTRES SECONDAIRES ---
//
//    private void ouvrirFenetreRevetements() {
//        if (pieceSelectionnee == null) {
//            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
//            return;
//        }
//
//        Stage popup = new Stage();
//        popup.setTitle("Revêtements - " + pieceSelectionnee.getUsage());
//
//        ComboBox<String> comboSurface = new ComboBox<>();
//        comboSurface.getItems().addAll("Sol", "Plafond");
//        pieceSelectionnee.getMurs().forEach(m -> comboSurface.getItems().add(m.getIdMur()));
//        comboSurface.setPromptText("Surface à traiter");
//
//        ComboBox<Revetement> comboRev = new ComboBox<>();
//        catalogue.getNomsRubriques().forEach(rub -> comboRev.getItems().addAll(catalogue.getProduits(rub)));
//        comboRev.setPromptText("Matériau");
//
//        Button btnValider = new Button("Appliquer");
//        btnValider.setMaxWidth(Double.MAX_VALUE);
//        btnValider.setOnAction(e -> {
//            String surf = comboSurface.getValue();
//            Revetement r = comboRev.getValue();
//            if (surf != null && r != null) {
//                if (surf.equals("Sol")) pieceSelectionnee.setRevetementSol(r);
//                else if (surf.equals("Plafond")) pieceSelectionnee.setRevetementPlafond(r);
//                else pieceSelectionnee.getMurs().stream().filter(m -> m.getIdMur().equals(surf)).findFirst().ifPresent(m -> m.setRevetement(r));
//                mettreAJourDetailsPiece();
//                rafraichirPlan();
//                popup.close();
//            }
//        });
//
//        VBox layout = new VBox(15, new Label("Appliquer un matériau sur une surface :"), comboSurface, comboRev, btnValider);
//        layout.setPadding(new Insets(20));
//        popup.setScene(new Scene(layout, 350, 250));
//        popup.show();
//    }
//
//    private void ouvrirFenetreOuvertures() {
//        if (pieceSelectionnee == null) {
//            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
//            return;
//        }
//
//        Stage popup = new Stage();
//        popup.setTitle("Ouvertures - " + pieceSelectionnee.getUsage());
//
//        ComboBox<String> comboMur = new ComboBox<>();
//        pieceSelectionnee.getMurs().forEach(m -> comboMur.getItems().add(m.getIdMur()));
//        comboMur.setPromptText("Choisir un mur");
//
//        ComboBox<String> comboType = new ComboBox<>();
//        comboType.getItems().addAll("Fenêtre", "Porte");
//        
//        TextField txtL = new TextField(); txtL.setPromptText("Largeur (m)");
//        TextField txtH = new TextField(); txtH.setPromptText("Hauteur (m)");
//
//        Button btnAjouter = new Button("Ajouter l'ouverture");
//        btnAjouter.setMaxWidth(Double.MAX_VALUE);
//        btnAjouter.setOnAction(e -> {
//            try {
//                String murId = comboMur.getValue();
//                String type = comboType.getValue();
//                float l = Float.parseFloat(txtL.getText());
//                float h = Float.parseFloat(txtH.getText());
//
//                if (murId != null && type != null) {
//                    pieceSelectionnee.getMurs().stream().filter(m -> m.getIdMur().equals(murId)).findFirst()
//                        .ifPresent(m -> m.ajouterOuverture(new Ouverture(compteurOuverture++, type, l, h)));
//                    mettreAJourDetailsPiece();
//                    popup.close();
//                }
//            } catch (Exception ex) { afficherErreur("Valeurs numériques invalides."); }
//        });
//
//        VBox layout = new VBox(15, new Label("Ajouter une ouverture sur un mur :"), comboMur, comboType, txtL, txtH, btnAjouter);
//        layout.setPadding(new Insets(20));
//        popup.setScene(new Scene(layout, 350, 350));
//        popup.show();
//    }
//
//    // --- DESSIN ET SIDEBAR ---
//
//    private void calculerEchelle() {
//        double lp = vue.getPanePlan().getWidth();
//        double hp = vue.getPanePlan().getHeight();
//        if (lp <= 0) lp = 900; if (hp <= 0) hp = 650;
//
//        double ld = lp - (margeSecurite * 2);
//        double hd = hp - (margeSecurite * 2);
//
//        echelle = Math.min(ld / appartement.getLargeur(), hd / appartement.getHauteur());
//    }
//    
//    /**
//     * Ajuste les coordonnées du clic pour les "coller" aux murs proches.
//     */
//    private Point appliquerMagnetisme(float xReel, float yReel) {
//        float xSnap = xReel;
//        float ySnap = yReel;
//        float distMinX = SEUIL_MAGNETISME;
//        float distMinY = SEUIL_MAGNETISME;
//
//        // Limites de l'appartement
//        float[] lignesX = {0, appartement.getLargeur()};
//        float[] lignesY = {0, appartement.getHauteur()};
//
//        for (float lx : lignesX) {
//            if (Math.abs(xReel - lx) < distMinX) { distMinX = Math.abs(xReel - lx); xSnap = lx; }
//        }
//        for (float ly : lignesY) {
//            if (Math.abs(yReel - ly) < distMinY) { distMinY = Math.abs(yReel - ly); ySnap = ly; }
//        }
//
//        // Bords des autres pièces
//        for (Piece p : appartement.getPieces()) {
//            if (Math.abs(xReel - p.getXMin()) < distMinX) { distMinX = Math.abs(xReel - p.getXMin()); xSnap = p.getXMin(); }
//            if (Math.abs(xReel - p.getXMax()) < distMinX) { distMinX = Math.abs(xReel - p.getXMax()); xSnap = p.getXMax(); }
//            if (Math.abs(yReel - p.getYMin()) < distMinY) { distMinY = Math.abs(yReel - p.getYMin()); ySnap = p.getYMin(); }
//            if (Math.abs(yReel - p.getYMax()) < distMinY) { distMinY = Math.abs(yReel - p.getYMax()); ySnap = p.getYMax(); }
//        }
//
//        return new Point(xSnap, ySnap);
//    }
//
//    private void gererClicSouris(double xPx, double yPx) {
//        double offsetX = (vue.getPanePlan().getWidth() - (appartement.getLargeur() * echelle)) / 2;
//        double offsetY = (vue.getPanePlan().getHeight() - (appartement.getHauteur() * echelle)) / 2;
//
//        float xr = (float) ((xPx - offsetX) / echelle);
//        float yr = (float) ((yPx - offsetY) / echelle);
//
//        if (xr < 0 || xr > appartement.getLargeur() || yr < 0 || yr > appartement.getHauteur()) return;
//
//        // L'utilisateur a cliqué sur une pièce existante ? (Sélection avec clic direct)
//        Piece p = trouverPiece(xr, yr);
//        if (p != null && premierPoint == null) {
//            vue.getListePieces().getSelectionModel().select(p);
//            return;
//        }
//
//        // Sinon, c'est un clic pour dessiner une nouvelle pièce (Application du magnétisme)
//        Point ptClique = appliquerMagnetisme(xr, yr);
//
//        if (premierPoint == null) {
//            premierPoint = ptClique;
//            vue.getInfoMessage().setText("Point 1 posé. Cliquez sur le coin opposé.");
//            rafraichirPlan();
//        } else {
//            finaliserPiece(ptClique);
//        }
//    }
//
//    private void finaliserPiece(Point p2) {
//        if (premierPoint.getX() == p2.getX() || premierPoint.getY() == p2.getY()) {
//            afficherErreur("Rectangle invalide.");
//        } else {
//            String usage = demanderNom();
//            Piece np = new Piece(premierPoint, p2, usage);
//            appartement.ajouterPiece(np);
//            selectionnerPiece(np);
//            mettreAJourSidebar();
//        }
//        premierPoint = null;
//        rafraichirPlan();
//    }
//
//    private Piece trouverPiece(float x, float y) {
//        return appartement.getPieces().stream().filter(p -> x >= p.getXMin() && x <= p.getXMax() && y >= p.getYMin() && y <= p.getYMax()).findFirst().orElse(null);
//    }
//
//    private void selectionnerPiece(Piece p) {
//        pieceSelectionnee = p;
//        if (p != null) vue.getInfoMessage().setText("Sélection : " + p.getUsage());
//        mettreAJourDetailsPiece();
//        rafraichirPlan();
//    }
//
//    private void rafraichirPlan() {
//        Pane p = vue.getPanePlan(); p.getChildren().clear();
//        double ox = (p.getWidth() - (appartement.getLargeur() * echelle)) / 2;
//        double oy = (p.getHeight() - (appartement.getHauteur() * echelle)) / 2;
//
//        // Contour de l'appartement
//        Rectangle contour = new Rectangle(ox, oy, appartement.getLargeur() * echelle, appartement.getHauteur() * echelle);
//        contour.setFill(Color.TRANSPARENT); contour.setStroke(Color.BLACK); contour.setStrokeWidth(3);
//        p.getChildren().add(contour);
//
//        for (Piece pc : appartement.getPieces()) {
//            double pcX = ox + pc.getXMin() * echelle;
//            double pcY = oy + pc.getYMin() * echelle;
//            double pcW = pc.getLargeur() * echelle;
//            double pcH = pc.getHauteur() * echelle;
//
//            // Rectangle de la pièce
//            Rectangle r = new Rectangle(pcX, pcY, pcW, pcH);
//            r.setFill(pc == pieceSelectionnee ? Color.LIGHTGREEN : Color.LIGHTBLUE);
//            r.setOpacity(0.3); r.setStroke(pc == pieceSelectionnee ? Color.GREEN : Color.BLUE);
//            r.setStrokeWidth(2);
//            p.getChildren().add(r);
//
//            // Nom de la pièce (au centre)
//            Text tUsage = new Text(pc.getUsage());
//            tUsage.setFont(Font.font("System", FontWeight.BOLD, 12));
//            tUsage.setX(pcX + pcW/2 - tUsage.getLayoutBounds().getWidth()/2);
//            tUsage.setY(pcY + pcH/2);
//            p.getChildren().add(tUsage);
//
//            // --- Ajout des labels des murs ---
//            for (Mur m : pc.getMurs()) {
//                Text labelMur = new Text(m.getIdMur());
//                labelMur.setFont(Font.font("System", 9));
//                labelMur.setFill(Color.DARKSLATEGRAY);
//                
//                // Calcul des dimensions du texte pour l'alignement
//                double textW = labelMur.getLayoutBounds().getWidth();
//                double textH = labelMur.getLayoutBounds().getHeight();
//
//                String id = m.getIdMur();
//                // Utilisation de .contains() car le modèle renvoie "Mur Nord", "Mur Sud", etc.
//                if (id.contains("Nord")) {
//                    labelMur.setX(pcX + pcW/2 - textW/2);
//                    labelMur.setY(pcY + 12);
//                } else if (id.contains("Sud")) {
//                    labelMur.setX(pcX + pcW/2 - textW/2);
//                    labelMur.setY(pcY + pcH - 5);
//                } else if (id.contains("Est")) {
//                    labelMur.setX(pcX + pcW - textW - 5);
//                    labelMur.setY(pcY + pcH/2 + textH/4);
//                } else if (id.contains("Ouest")) {
//                    labelMur.setX(pcX + 5);
//                    labelMur.setY(pcY + pcH/2 + textH/4);
//                }
//                
//                p.getChildren().add(labelMur);
//            }
//        }
//
//        if (premierPoint != null) p.getChildren().add(new Circle(ox + premierPoint.getX() * echelle, oy + premierPoint.getY() * echelle, 4, Color.RED));
//    }
//
//    private void mettreAJourSidebar() {
//        vue.getListePieces().getItems().setAll(appartement.getPieces());
//        vue.getLabelNbPieces().setText("Nombre de pièces : " + appartement.getPieces().size());
//    }
//
//    private void mettreAJourDetailsPiece() {
//        if (pieceSelectionnee == null) {
//            vue.getLabelDetailsPiece().setText("Aucune pièce sélectionnée.");
//            vue.getListeMurs().getItems().clear();
//            return;
//        }
//        String s = String.format("Nom : %s\nSurface : %.2f m²\nSol : %s\nPlafond : %s", 
//            pieceSelectionnee.getUsage(), pieceSelectionnee.getSuperficie(),
//            pieceSelectionnee.getRevetementSol() != null ? pieceSelectionnee.getRevetementSol().getNomRevt() : "aucun",
//            pieceSelectionnee.getRevetementPlafond() != null ? pieceSelectionnee.getRevetementPlafond().getNomRevt() : "aucun");
//        vue.getLabelDetailsPiece().setText(s);
//        vue.getListeMurs().getItems().setAll(pieceSelectionnee.getMurs());
//    }
//
//    private void annulerSelection() { premierPoint = null; pieceSelectionnee = null; rafraichirPlan(); mettreAJourDetailsPiece(); }
//    
//    private void supprimerDernierePiece() { appartement.supprimerDernierePiece(); annulerSelection(); mettreAJourSidebar(); }
//
//    private String demanderNom() {
//        TextInputDialog d = new TextInputDialog("Pièce");
//        d.setTitle("Nouvelle pièce"); d.setHeaderText(null); d.setContentText("Nom de la pièce :");
//        return d.showAndWait().orElse("Pièce");
//    }
//
//    private void afficherErreur(String m) {
//        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle("Attention"); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
//    }
//}
