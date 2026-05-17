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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

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
        vue.getPanePlan().widthProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });
        vue.getPanePlan().heightProperty().addListener((o, oldV, newV) -> { calculerEchelle(); rafraichirPlan(); });
        vue.getPanePlan().setOnMouseClicked(e -> gererClicSouris(e.getX(), e.getY()));
        vue.getListePieces().getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> selectionnerPiece(n));

        vue.getBoutonEffacerSelection().setOnAction(e -> annulerSelection());
        vue.getBoutonSupprimerDernierePiece().setOnAction(e -> supprimerDernierePiece());
        vue.getBoutonFermer().setOnAction(e -> fenetre.close());

        vue.getBoutonOuvrirRevetements().setOnAction(e -> ouvrirFenetreRevetements());
        vue.getBoutonOuvrirOuvertures().setOnAction(e -> ouvrirFenetreOuvertures());
        
        vue.getBoutonCatalogue().setOnAction(e -> {
            Stage fenetreCatalogue = new Stage();
            new CCatalogue(fenetreCatalogue);
        });
    }

    private void ouvrirFenetreRevetements() {
        if (pieceSelectionnee == null) {
            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
            return;
        }

        Stage popup = new Stage();
        popup.setTitle("Gestion des surfaces - " + pieceSelectionnee.getUsage());

        Label lblMat = new Label("Appliquer un matériau à une surface :");
        lblMat.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> comboSurfMat = new ComboBox<>();
        comboSurfMat.getItems().addAll("Sol", "Plafond");
        pieceSelectionnee.getMurs().forEach(m -> comboSurfMat.getItems().add(m.getIdMur()));
        comboSurfMat.setPromptText("Surface à traiter");
        comboSurfMat.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Revetement> comboRev = new ComboBox<>();
        comboRev.setPromptText("Choisir le matériau");
        comboRev.setMaxWidth(Double.MAX_VALUE);

        comboSurfMat.setOnAction(e -> {
            String selection = comboSurfMat.getValue();
            comboRev.getItems().clear();
            if (selection == null) return;
            if (selection.equals("Sol")) {
                comboRev.getItems().addAll(catalogue.getProduits("Sol"));
            } else {
                comboRev.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
            }
        });

        Button btnAppliquerMat = new Button("Appliquer matériau");
        btnAppliquerMat.setMaxWidth(Double.MAX_VALUE);
        btnAppliquerMat.setOnAction(e -> {
            String surf = comboSurfMat.getValue();
            Revetement r = comboRev.getValue();
            if (surf != null && r != null) {
                appliquerRevetement(surf, r, false);
                mettreAJourDetailsPiece();
                rafraichirPlan();
            }
        });

        Label lblIso = new Label("Appliquer un isolant à une surface :");
        lblIso.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> comboSurfIso = new ComboBox<>();
        comboSurfIso.getItems().addAll("Sol", "Plafond");
        pieceSelectionnee.getMurs().forEach(m -> comboSurfIso.getItems().add(m.getIdMur()));
        comboSurfIso.setPromptText("Surface à traiter");
        comboSurfIso.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Revetement> comboIso = new ComboBox<>();
        comboIso.setPromptText("Choisir l'isolant");
        comboIso.setMaxWidth(Double.MAX_VALUE);
        comboIso.getItems().addAll(catalogue.getProduits("Isolant"));

        Button btnAppliquerIso = new Button("Appliquer isolant");
        btnAppliquerIso.setMaxWidth(Double.MAX_VALUE);
        btnAppliquerIso.setOnAction(e -> {
            String surf = comboSurfIso.getValue();
            Revetement i = comboIso.getValue();
            if (surf != null && i != null) {
                appliquerRevetement(surf, i, true);
                mettreAJourDetailsPiece();
                rafraichirPlan();
            }
        });

        VBox layout = new VBox(12, 
            lblMat, comboSurfMat, comboRev, btnAppliquerMat,
            new Separator(),
            lblIso, comboSurfIso, comboIso, btnAppliquerIso
        );
        layout.setPadding(new Insets(20));
        popup.setScene(new Scene(layout, 400, 450));
        popup.show();
    }

    private void appliquerRevetement(String surface, Revetement r, boolean estIsolant) {
        if (surface.equals("Sol")) {
            if (estIsolant) pieceSelectionnee.setIsolantSol(r);
            else pieceSelectionnee.setRevetementSol(r);
        } else if (surface.equals("Plafond")) {
            if (estIsolant) pieceSelectionnee.setIsolantPlafond(r);
            else pieceSelectionnee.setRevetementPlafond(r);
        } else {
            pieceSelectionnee.getMurs().stream()
                .filter(m -> m.getIdMur().equals(surface))
                .findFirst()
                .ifPresent(m -> {
                    if (estIsolant) m.setIsolant(r);
                    else m.setRevetement(r);
                });
        }
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
        comboType.setPromptText("Choisir le type");
        comboType.setDisable(true); // Désactivé tant que le mur n'est pas choisi

        // Logique de filtrage selon la nature du mur (Façade ou non)
        comboMur.setOnAction(e -> {
            String murId = comboMur.getValue();
            Mur selectedMur = pieceSelectionnee.getMurs().stream()
                .filter(m -> m.getIdMur().equals(murId))
                .findFirst().orElse(null);

            comboType.getItems().clear();
            comboType.setDisable(false);
            
            comboType.getItems().add("Fenêtre");
            
            // On n'ajoute la porte QUE si ce n'est pas un mur de façade
            if (selectedMur != null && !selectedMur.estExterieur()) {
                comboType.getItems().add("Porte");
            } else {
                comboType.setTooltip(new Tooltip("Les portes sont interdites sur les murs de façade."));
            }
        });

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

        VBox layout = new VBox(15, new Label("Ajouter une ouverture :"), comboMur, comboType, txtL, txtH, btnAjouter);
        layout.setPadding(new Insets(20));
        popup.setScene(new Scene(layout, 350, 350));
        popup.show();
    }

    private void calculerEchelle() {
        double lp = vue.getPanePlan().getWidth();
        double hp = vue.getPanePlan().getHeight();
        if (lp <= 0) lp = 900; if (hp <= 0) hp = 650;
        double ld = lp - (margeSecurite * 2);
        double hd = hp - (margeSecurite * 2);
        echelle = Math.min(ld / appartement.getLargeur(), hd / appartement.getHauteur());
    }

    private Point appliquerMagnetisme(float xReel, float yReel) {
        float xSnap = xReel; float ySnap = yReel;
        float seuilMetres = (float) (SEUIL_MAGNETISME_PIXELS / echelle);
        float distMinX = seuilMetres; float distMinY = seuilMetres;
        float[] lignesX = {0, appartement.getLargeur()};
        float[] lignesY = {0, appartement.getHauteur()};
        for (float lx : lignesX) {
            if (Math.abs(xReel - lx) < distMinX) { distMinX = Math.abs(xReel - lx); xSnap = lx; }
        }
        for (float ly : lignesY) {
            if (Math.abs(yReel - ly) < distMinY) { distMinY = Math.abs(yReel - ly); ySnap = ly; }
        }
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
        Piece p = trouverPiece(xr, yr);
        if (p != null && premierPoint == null) {
            vue.getListePieces().getSelectionModel().select(p);
            return;
        }
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
            
            // --- DETECTION DES MURS DE FAÇADE ---
            // Un mur est en façade s'il est confondu avec les bords de l'appartement (0 ou largeur/hauteur max)
            for (Mur m : np.getMurs()) {
                boolean surBordHorizontal = (m.getY1() == 0 || m.getY1() == appartement.getHauteur()) && (m.getY2() == m.getY1());
                boolean surBordVertical = (m.getX1() == 0 || m.getX1() == appartement.getLargeur()) && (m.getX2() == m.getX1());
                
                if (surBordHorizontal || surBordVertical) {
                    m.setEstExterieur(true);
                }
            }

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

        // 1. Dessiner le contour de l'appartement
        Rectangle contour = new Rectangle(ox, oy, appartement.getLargeur() * echelle, appartement.getHauteur() * echelle);
        contour.setFill(Color.TRANSPARENT); contour.setStroke(Color.BLACK); contour.setStrokeWidth(3);
        p.getChildren().add(contour);

        // 2. Dessiner les pièces et leurs composants
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

            // Nom de la pièce au centre
            Text tUsage = new Text(pc.getUsage());
            tUsage.setFont(Font.font("System", FontWeight.BOLD, 12));
            tUsage.setX(pcX + pcW/2 - tUsage.getLayoutBounds().getWidth()/2);
            tUsage.setY(pcY + pcH/2);
            p.getChildren().add(tUsage);

            // 3. Dessiner les murs de façade (en rouge)
            for (Mur m : pc.getMurs()) {
                if (m.estExterieur()) {
                    Line facadeLine = new Line(ox + m.getX1()*echelle, oy + m.getY1()*echelle, ox + m.getX2()*echelle, oy + m.getY2()*echelle);
                    facadeLine.setStroke(Color.RED);
                    facadeLine.setStrokeWidth(3);
                    p.getChildren().add(facadeLine);
                }
            }

            // 4. Dessiner les labels des murs (Nord, Sud, Est, Ouest)
            for (Mur m : pc.getMurs()) {
                Text labelMur = new Text(m.getIdMur());
                labelMur.setFont(Font.font("System", 10));
                labelMur.setFill(Color.BLACK); // Noir pour plus de contraste
                
                // Calcul du placement
                double textW = labelMur.getLayoutBounds().getWidth();
                String id = m.getIdMur();
                
                if (id.contains("Nord")) { 
                    labelMur.setX(pcX + pcW/2 - textW/2); 
                    labelMur.setY(pcY + 14); 
                } else if (id.contains("Sud")) { 
                    labelMur.setX(pcX + pcW/2 - textW/2); 
                    labelMur.setY(pcY + pcH - 6); 
                } else if (id.contains("Est")) { 
                    labelMur.setX(pcX + pcW - textW - 6); 
                    labelMur.setY(pcY + pcH/2); 
                } else if (id.contains("Ouest")) { 
                    labelMur.setX(pcX + 6); 
                    labelMur.setY(pcY + pcH/2); 
                }
                p.getChildren().add(labelMur);
            }
        }
        
        // Indicateur du premier point lors de la création
        if (premierPoint != null) {
            p.getChildren().add(new Circle(ox + premierPoint.getX() * echelle, oy + premierPoint.getY() * echelle, 4, Color.RED));
        }
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
        String details = String.format("Nom : %s\nSurface : %.2f m²\nSol : %s (Iso: %s)\nPlafond : %s (Iso: %s)", 
            pieceSelectionnee.getUsage(), pieceSelectionnee.getSuperficie(),
            pieceSelectionnee.getRevetementSol() != null ? pieceSelectionnee.getRevetementSol().getNomRevt() : "aucun",
            pieceSelectionnee.getIsolantSol() != null ? pieceSelectionnee.getIsolantSol().getNomRevt() : "aucun",
            pieceSelectionnee.getRevetementPlafond() != null ? pieceSelectionnee.getRevetementPlafond().getNomRevt() : "aucun",
            pieceSelectionnee.getIsolantPlafond() != null ? pieceSelectionnee.getIsolantPlafond().getNomRevt() : "aucun");
        vue.getLabelDetailsPiece().setText(details);
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
