package Controleur;
import Modele.Appartement;
import Modele.GestionCatalogue;
import Modele.Mur;
import Modele.Stockage;
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
    
    
private Revetement revetementSolZoneRestante;
private Revetement revetementMurZoneRestante;
private Revetement revetementPlafondZoneRestante;


private final double HAUTEUR_MURS = 2.5;
    private final double SEUIL_MAGNETISME_PIXELS = 20.0;
    
    // Ajout d'une propriété pour retenir l'emplacement obligatoire du couloir
    private String coteCouloir;
    
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
        // Contrainte immédiate avant le dessin des pièces
        if (appartement.getPieces().isEmpty() && coteCouloir == null) {
            demanderCoteCouloir();
        }
    }
    /**
     * Ouvre une boîte de choix pour définir l'orientation du couloir d'accès.
     */
    private void demanderCoteCouloir() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Nord", "Nord", "Sud", "Est", "Ouest");
        dialog.setTitle("Configuration initiale de l'appartement");
        dialog.setHeaderText("Emplacement du couloir commun");
        dialog.setContentText("De quel côté de l'appartement se trouve le couloir d'accès (requis pour la porte d'entrée) ?");
        
        dialog.showAndWait().ifPresentOrElse(
            cote -> {
                this.coteCouloir = cote;
                vue.getInfoMessage().setText("Couloir configuré au " + cote + ". Créez vos pièces.");
            },
            () -> {
                // Par défaut si fermeture brutale de la boîte
                this.coteCouloir = "Nord";
                vue.getInfoMessage().setText("Couloir configuré par défaut au Nord. Créez vos pièces.");
            }
        );
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
        
        vue.getBoutonDetailsMur().setOnAction(e -> afficherDetailsMurSelectionne());
        
        vue.getBoutonCatalogue().setOnAction(e -> {
            Stage fenetreCatalogue = new Stage();
            new CCatalogue(fenetreCatalogue);
        });
        
        vue.getBoutonGenererDevis().setOnAction(e -> {
    if (calculerSurfaceZoneRestante() > 0
            && (revetementSolZoneRestante == null
            || revetementMurZoneRestante == null
            || revetementPlafondZoneRestante == null)) {

        ouvrirFenetreZoneRestante();
    } else {
        afficherDevisAvecZoneRestante();
    }
});
        
        
        
    }
    private void afficherDetailsMurSelectionne() {
        Mur murSelectionne = vue.getListeMurs().getSelectionModel().getSelectedItem();
        if (murSelectionne == null) {
            afficherErreur("Sélectionnez d'abord un mur dans la liste (cliquez sur une pièce puis sur un mur).");
            return;
        }
        ouvrirFenetreDetailsMur(murSelectionne);
    }
    private void ouvrirFenetreDetailsMur(Mur mur) {
        Stage popup = new Stage();
        popup.setTitle("Détails du mur - " + mur.getIdMur());
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FDF7F2;");
        Label labelTitre = new Label("Détails de la paroi : " + mur.getIdMur());
        labelTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        labelTitre.setStyle("-fx-text-fill: #2F3E46;");
        double hauteur = 2.5; 
        double longueur = mur.calculerLongueur();
        double surfaceBrute = longueur * hauteur;
        double surfaceNette = mur.calculerSurfaceNette(hauteur);
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(8);
        grid.setPadding(new Insets(10, 0, 10, 0));
        grid.add(new Label("Type de mur :"), 0, 0);
        Label lblType = new Label(mur.estExterieur() ? "Façade (Extérieur)" : "Intérieur");
        lblType.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (mur.estExterieur() ? "#B00020;" : "#2F3E46;"));
        grid.add(lblType, 1, 0);
        grid.add(new Label("Longueur :"), 0, 1);
        grid.add(new Label(String.format("%.2f m", longueur)), 1, 1);
        grid.add(new Label("Hauteur :"), 0, 2);
        grid.add(new Label(String.format("%.2f m (par défaut)", hauteur)), 1, 2);
        grid.add(new Label("Surface brute :"), 0, 3);
        grid.add(new Label(String.format("%.2f m²", surfaceBrute)), 1, 3);
        grid.add(new Label("Surface nette :"), 0, 4);
        grid.add(new Label(String.format("%.2f m²", surfaceNette)), 1, 4);
        Label lblMat = new Label("Matériaux appliqués");
        lblMat.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblMat.setStyle("-fx-text-fill: #2F3E46;");
        GridPane gridMat = new GridPane();
        gridMat.setHgap(15); gridMat.setVgap(8);
        gridMat.add(new Label("Isolant :"), 0, 0);
        String nomIsolant = mur.getIsolant() != null ? mur.getIsolant().getNomRevt() + " (" + mur.getIsolant().getPrixRevt() + " €/m²)" : "aucun";
        Label lblIsoVal = new Label(nomIsolant); lblIsoVal.setStyle("-fx-font-weight: bold;");
        gridMat.add(lblIsoVal, 1, 0);
        gridMat.add(new Label("Revêtement :"), 0, 1);
        String nomRev = mur.getRevetement() != null ? mur.getRevetement().getNomRevt() + " (" + mur.getRevetement().getPrixRevt() + " €/m²)" : "aucun";
        Label lblRevVal = new Label(nomRev); lblRevVal.setStyle("-fx-font-weight: bold;");
        gridMat.add(lblRevVal, 1, 1);
        Label lblOuv = new Label("Ouvertures intégrées");
        lblOuv.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblOuv.setStyle("-fx-text-fill: #2F3E46;");
        ListView<String> listeOuv = new ListView<>();
        listeOuv.setPrefHeight(120);
        if (mur.getOuvertures().isEmpty()) {
            listeOuv.getItems().add("Aucune ouverture");
        } else {
            for (Ouverture o : mur.getOuvertures()) {
                listeOuv.getItems().add(o.toString());
            }
        }
        Button btnPopupFermer = new Button("Fermer");
        btnPopupFermer.setMaxWidth(Double.MAX_VALUE);
        btnPopupFermer.setOnAction(ev -> popup.close());
        layout.getChildren().addAll(labelTitre, new Separator(), grid, new Separator(), lblMat, gridMat, new Separator(), lblOuv, listeOuv, new Separator(), btnPopupFermer);
        popup.setScene(new Scene(layout, 420, 560));
        popup.show();
    }
    private void ouvrirFenetreRevetements() {
        if (pieceSelectionnee == null) {
            afficherErreur("Sélectionnez d'abord une pièce sur le plan.");
            return;
        }
        Stage popup = new Stage();
        popup.setTitle("Gestion des surfaces - " + pieceSelectionnee.getUsage());
        Label lblMat = new Label("Appliquer un matériau à une surface :");
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
        VBox layout = new VBox(12, lblMat, comboSurfMat, comboRev, btnAppliquerMat, new Separator(), lblIso, comboSurfIso, comboIso, btnAppliquerIso);
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
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FDF7F2;");
        Label labelExplicatif = new Label("Ajouter une ouverture :");
        labelExplicatif.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ComboBox<String> comboMur = new ComboBox<>();
        comboMur.setPromptText("1. Choisir un mur");
        comboMur.setMaxWidth(Double.MAX_VALUE);
        pieceSelectionnee.getMurs().forEach(m -> comboMur.getItems().add(m.getIdMur()));
        ComboBox<String> comboType = new ComboBox<>();
        comboType.setPromptText("2. Choisir le type");
        comboType.setMaxWidth(Double.MAX_VALUE);
        comboType.setDisable(true);
        ComboBox<Revetement> comboArticle = new ComboBox<>();
        comboArticle.setPromptText("3. Sélectionner un produit");
        comboArticle.setMaxWidth(Double.MAX_VALUE);
        comboArticle.setDisable(true);
        
        TextField txtL = new TextField(); txtL.setEditable(false);
        txtL.setStyle("-fx-background-color: #EAEAEA; -fx-text-fill: #555555; -fx-font-weight: bold;");
        TextField txtH = new TextField(); txtH.setEditable(false);
        txtH.setStyle("-fx-background-color: #EAEAEA; -fx-text-fill: #555555; -fx-font-weight: bold;");
        // Modification fondamentale de la logique des portes pour l'Appartement
        comboMur.setOnAction(e -> {
            String murId = comboMur.getValue();
            Mur selectedMur = pieceSelectionnee.getMurs().stream()
                .filter(m -> m.getIdMur().equals(murId))
                .findFirst().orElse(null);
            comboType.getItems().clear();
            comboArticle.getItems().clear();
            comboArticle.setDisable(true);
            comboType.setDisable(false);
            
            comboType.getItems().add("Fenêtre");
            
            if (selectedMur != null) {
                if (!selectedMur.estExterieur()) {
                    // Mur intérieur : portes intérieures toujours autorisées
                    comboType.getItems().add("Porte");
                } else if (coteCouloir != null && selectedMur.getIdMur().contains(coteCouloir)) {
                    // Mur extérieur faisant face au couloir désigné : porte autorisée (Porte d'entrée)
                    comboType.getItems().add("Porte");
                } else {
                    // Autre mur extérieur (balcons, fenêtres de chambres) : Porte de communication refusée
                    comboType.setTooltip(new Tooltip("Les portes d'accès sont interdites sur les façades extérieures hors couloir."));
                }
            }
        });
        comboType.setOnAction(e -> {
            String type = comboType.getValue();
            comboArticle.getItems().clear();
            if (type == null) {
                comboArticle.setDisable(true);
                return;
            }
            comboArticle.setDisable(false);
            
            String categorieCatalogue = type.equals("Fenêtre") ? "Fenetre" : "Porte";
            List<Revetement> articles = catalogue.getProduits(categorieCatalogue);
            if (articles.isEmpty() && type.equals("Fenêtre")) {
                articles = catalogue.getProduits("Fenêtre");
            }
            comboArticle.getItems().addAll(articles);
            if (type.equals("Fenêtre")) { txtL.setText("1.2"); txtH.setText("1.2"); } 
            else { txtL.setText("0.9"); txtH.setText("2.1"); }
        });
        Button btnAjouter = new Button("Ajouter l'ouverture");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setOnAction(e -> {
            try {
                String murId = comboMur.getValue();
                String type = comboType.getValue();
                Revetement articleChoisi = comboArticle.getValue();
                
                if (murId == null || type == null || articleChoisi == null) {
                    afficherErreur("Veuillez sélectionner un mur, un type et un produit.");
                    return;
                }
                float l = Float.parseFloat(txtL.getText());
                float h = Float.parseFloat(txtH.getText());
                pieceSelectionnee.getMurs().stream()
                    .filter(m -> m.getIdMur().equals(murId))
                    .findFirst()
                    .ifPresent(m -> {
                        Ouverture o = new Ouverture(compteurOuverture++, type, l, h, articleChoisi);
                        
                        // Si l'utilisateur pose la porte sur la façade autorisée du couloir
                        if (type.equals("Porte") && m.estExterieur()) {
                            appartement.setPorteEntree(o);
                        }
                        
                        m.ajouterOuverture(o);
                    });
                
                mettreAJourDetailsPiece();
                popup.close();
            } catch (NumberFormatException ex) { 
                afficherErreur("Valeurs de dimensions incorrectes."); 
            }
        });
        layout.getChildren().addAll(labelExplicatif, comboMur, comboType, new Separator(), new Label("Choisir l'article :"), comboArticle, new Separator(), new Label("Dimensions requis (Lecture seule) :"), txtL, txtH, new Separator(), btnAjouter);
        popup.setScene(new Scene(layout, 380, 460));
        popup.show();
    }
    private void calculerEchelle() {
        double lp = vue.getPanePlan().getWidth(); double hp = vue.getPanePlan().getHeight();
        if (lp <= 0) lp = 900; if (hp <= 0) hp = 650;
        double ld = lp - (margeSecurite * 2); double hd = hp - (margeSecurite * 2);
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
            for (Mur m : np.getMurs()) {
                boolean surBordHorizontal = (m.getY1() == 0 || m.getY1() == appartement.getHauteur()) && (m.getY2() == m.getY1());
                boolean surBordVertical = (m.getX1() == 0 || m.getX1() == appartement.getLargeur()) && (m.getX2() == m.getX1());
                if (surBordHorizontal || surBordVertical) { m.setEstExterieur(true); }
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
        Rectangle contour = new Rectangle(ox, oy, appartement.getLargeur() * echelle, appartement.getHauteur() * echelle);
        contour.setFill(Color.TRANSPARENT); contour.setStroke(Color.BLACK); contour.setStrokeWidth(3);
        p.getChildren().add(contour);
        for (Piece pc : appartement.getPieces()) {
            double pcX = ox + pc.getXMin() * echelle; double pcY = oy + pc.getYMin() * echelle;
            double pcW = pc.getLargeur() * echelle; double pcH = pc.getHauteur() * echelle;
            Rectangle r = new Rectangle(pcX, pcY, pcW, pcH);
            r.setFill(pc == pieceSelectionnee ? Color.LIGHTGREEN : Color.LIGHTBLUE);
            r.setOpacity(0.3); r.setStroke(pc == pieceSelectionnee ? Color.GREEN : Color.BLUE); r.setStrokeWidth(2);
            p.getChildren().add(r);
            Text tUsage = new Text(pc.getUsage());
            tUsage.setFont(Font.font("System", FontWeight.BOLD, 12));
            tUsage.setX(pcX + pcW/2 - tUsage.getLayoutBounds().getWidth()/2); tUsage.setY(pcY + pcH/2);
            p.getChildren().add(tUsage);
            for (Mur m : pc.getMurs()) {
                if (m.estExterieur()) {
                    Line facadeLine = new Line(ox + m.getX1()*echelle, oy + m.getY1()*echelle, ox + m.getX2()*echelle, oy + m.getY2()*echelle);
                    facadeLine.setStroke(Color.RED); facadeLine.setStrokeWidth(3);
                    p.getChildren().add(facadeLine);
                }
            }
            for (Mur m : pc.getMurs()) {
                Text labelMur = new Text(m.getIdMur());
                labelMur.setFont(Font.font("System", 10)); labelMur.setFill(Color.BLACK);
                double textW = labelMur.getLayoutBounds().getWidth();
                String id = m.getIdMur();
                if (id.contains("Nord")) { labelMur.setX(pcX + pcW/2 - textW/2); labelMur.setY(pcY + 14); } 
                else if (id.contains("Sud")) { labelMur.setX(pcX + pcW/2 - textW/2); labelMur.setY(pcY + pcH - 6); } 
                else if (id.contains("Est")) { labelMur.setX(pcX + pcW - textW - 6); labelMur.setY(pcY + pcH/2); } 
                else if (id.contains("Ouest")) { labelMur.setX(pcX + 6); labelMur.setY(pcY + pcH/2); }
                p.getChildren().add(labelMur);
            }
        }
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
    
    
private double calculerSurfaceZoneRestante() {
    double surfacePieces = 0;

    for (Piece piece : appartement.getPieces()) {
        surfacePieces += piece.getSuperficie();
    }

    double surfaceRestante = appartement.getSuperficie() - surfacePieces;

    if (surfaceRestante < 0) {
        return 0;
    }

    return surfaceRestante;
}

// AJOUT : calcule le périmètre de la zone restante
private double calculerPerimetreZoneRestante() {
    double perimetreAppartement = 2 * (appartement.getLargeur() + appartement.getHauteur());
    double perimetrePieces = 0;

    for (Piece piece : appartement.getPieces()) {
        perimetrePieces += 2 * (piece.getLargeur() + piece.getHauteur());
    }

    return perimetreAppartement + perimetrePieces;
}

// AJOUT : calcule la surface murale de la zone restante
private double calculerSurfaceMursZoneRestante() {
    return calculerPerimetreZoneRestante() * HAUTEUR_MURS;
}

// AJOUT : fenêtre de choix des revêtements de la zone restante
private void ouvrirFenetreZoneRestante() {
    Stage popup = new Stage();
    popup.setTitle("Revêtements de la zone restante");

    Label titre = new Label("Zone restante non attribuée aux pièces");
    titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

    Label infos = new Label(
        String.format(
            "Surface sol/plafond : %.2f m²\nSurface murs estimée : %.2f m²",
            calculerSurfaceZoneRestante(),
            calculerSurfaceMursZoneRestante()
        )
    );

    ComboBox<Revetement> comboSol = new ComboBox<>();
    comboSol.setPromptText("Revêtement du sol");
    comboSol.setMaxWidth(Double.MAX_VALUE);
    comboSol.getItems().addAll(catalogue.getProduits("Sol"));

    ComboBox<Revetement> comboMur = new ComboBox<>();
    comboMur.setPromptText("Revêtement des murs");
    comboMur.setMaxWidth(Double.MAX_VALUE);
    comboMur.getItems().addAll(catalogue.getProduits("CouleurPeinture"));

    ComboBox<Revetement> comboPlafond = new ComboBox<>();
    comboPlafond.setPromptText("Revêtement du plafond");
    comboPlafond.setMaxWidth(Double.MAX_VALUE);
    comboPlafond.getItems().addAll(catalogue.getProduits("CouleurPeinture"));

    Button btnValider = new Button("Valider et générer le devis");
    btnValider.setMaxWidth(Double.MAX_VALUE);

    btnValider.setOnAction(e -> {
        if (comboSol.getValue() == null || comboMur.getValue() == null || comboPlafond.getValue() == null) {
            afficherErreur("Veuillez choisir un revêtement pour le sol, les murs et le plafond.");
            return;
        }

        appartement.setRevetementSolZoneRestante(comboSol.getValue());
        appartement.setRevetementMurZoneRestante(comboMur.getValue());
        appartement.setRevetementPlafondZoneRestante(comboPlafond.getValue());
        popup.close();
        afficherDevisAvecZoneRestante();
    });

    VBox layout = new VBox(12, titre, infos, new Separator(), comboSol, comboMur, comboPlafond, btnValider);
    layout.setPadding(new Insets(20));
    layout.setStyle("-fx-background-color: #FDF7F2;");

    popup.setScene(new Scene(layout, 430, 330));
    popup.show();
}

// AJOUT : affiche le devis normal + la zone restante
private void afficherDevisAvecZoneRestante() {
    String texteDevis = Stockage.genererTexteDevis();

    if (calculerSurfaceZoneRestante() > 0) {
        texteDevis += "\n\n===== Zone restante =====\n";
        texteDevis += String.format("Surface sol : %.2f m²\n", calculerSurfaceZoneRestante());
        texteDevis += String.format("Surface plafond : %.2f m²\n", calculerSurfaceZoneRestante());
        texteDevis += String.format("Surface murs : %.2f m²\n", calculerSurfaceMursZoneRestante());

        texteDevis += "\nRevêtement sol : "
                + revetementSolZoneRestante.getNomRevt()
                + " - "
                + String.format("%.2f €", revetementSolZoneRestante.getPrixRevt() * calculerSurfaceZoneRestante());

        texteDevis += "\nRevêtement murs : "
                + revetementMurZoneRestante.getNomRevt()
                + " - "
                + String.format("%.2f €", revetementMurZoneRestante.getPrixRevt() * calculerSurfaceMursZoneRestante());

        texteDevis += "\nRevêtement plafond : "
                + revetementPlafondZoneRestante.getNomRevt()
                + " - "
                + String.format("%.2f €", revetementPlafondZoneRestante.getPrixRevt() * calculerSurfaceZoneRestante());
    }

    TextArea zoneTexte = new TextArea(texteDevis);
    zoneTexte.setEditable(false);
    zoneTexte.setWrapText(false);

    Stage fenetreDevis = new Stage();
    fenetreDevis.setTitle("Devis du bâtiment");

    Scene scene = new Scene(zoneTexte, 900, 650);
    fenetreDevis.setScene(scene);
    fenetreDevis.show();
}
    
    private void afficherErreur(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle("Attention"); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}