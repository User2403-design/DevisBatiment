package Controleur;

import Modele.Appartement;
import Modele.Immeuble;
import Modele.Niveau;
import Modele.Point;
import Modele.Stockage;
import Vue.VuePlanImmeuble;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import Modele.GestionCatalogue;
import Modele.Revetement;
import javafx.scene.control.TextArea;

/**
 * Contrôleur pour la gestion du plan de l'immeuble.
 * Gère l'ancrage magnétique dynamique et l'affichage des zones avec libellés et points de saisie.
 */
public class CPlanImmeuble {

    private VuePlanImmeuble vue;
    private Stage fenetre;
    private Immeuble immeuble;
    private int indexNiveauCourant;
    private Point premierPoint;
    private Point deuxiemePoint;
    private double echelle;

    private final double margeSecurite = 30.0; 
    private final double SEUIL_MAGNETISME_PIXELS = 20.0; 

    private String modeCreation = "ESCALIER";

    public CPlanImmeuble(Stage fenetre, Immeuble immeuble) {
        this.fenetre = fenetre;
        this.immeuble = immeuble;
        this.indexNiveauCourant = 0;
        this.vue = new VuePlanImmeuble();

        Scene scene = new Scene(vue.getRoot(), 1300, 800);
        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur d'immeuble - Conception");
        fenetre.show();
        fenetre.setMaximized(true);

        calculerEchelle();
        vue.getInfoMessage().setText("Étape 1 : tracez l'escalier.");
        mettreAJourAffichage();

        vue.getPanePlan().widthProperty().addListener((obs, old, newVal) -> { calculerEchelle(); rafraichirPlan(); });
        vue.getPanePlan().heightProperty().addListener((obs, old, newVal) -> { calculerEchelle(); rafraichirPlan(); });

        vue.getPanePlan().setOnMouseClicked(event -> gererClicSouris(event.getX(), event.getY()));
        
        configurerBoutons();
    }

    private void configurerBoutons() {
        vue.getBoutonCatalogue().setOnAction(e -> new CCatalogue(new Stage()));
        vue.getBoutonOuvrirAppartement().setOnAction(e -> ouvrirAppartementSelectionne());
        vue.getBoutonSupprimerDernierAppartement().setOnAction(e -> supprimerDernierAppartement());
        vue.getBoutonEtagePrecedent().setOnAction(e -> changerEtage(-1));
        vue.getBoutonEtageSuivant().setOnAction(e -> changerEtage(1));
        vue.getBoutonAjouterEtage().setOnAction(e -> ajouterEtage());
        vue.getBoutonSupprimerEtage().setOnAction(e -> supprimerEtage());
        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
        vue.getBoutonGenererDevis().setOnAction(e -> {

    String texteDevis = Stockage.genererTexteDevis();

    TextArea zoneTexte = new TextArea(texteDevis);
    zoneTexte.setEditable(false);
    zoneTexte.setWrapText(false);

    Stage fenetreDevis = new Stage();
    fenetreDevis.setTitle("Devis du bâtiment");

    Scene scene = new Scene(zoneTexte, 900, 650);
    fenetreDevis.setScene(scene);
    fenetreDevis.show();
});
    
    }

    private void calculerEchelle() {
        double largeurDisponible = vue.getPanePlan().getWidth() - (margeSecurite * 2);
        double hauteurDisponible = vue.getPanePlan().getHeight() - (margeSecurite * 2);
        if (largeurDisponible <= 0 || hauteurDisponible <= 0) return;
        echelle = Math.min(largeurDisponible / immeuble.getXmax(), hauteurDisponible / immeuble.getYmax());
    }

    private void gererClicSouris(double xPixel, double yPixel) {
        double offsetX = (vue.getPanePlan().getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (immeuble.getYmax() * echelle)) / 2;

        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        if (xReel < -0.5 || xReel > immeuble.getXmax() + 0.5 || yReel < -0.5 || yReel > immeuble.getYmax() + 0.5) {
            vue.getInfoMessage().setText("Clic hors de l'étage.");
            return;
        }
        
        double seuilDynamique = SEUIL_MAGNETISME_PIXELS / echelle;

        List<Float> ancragesX = new ArrayList<>();
        List<Float> ancragesY = new ArrayList<>();

        if (!modeCreation.equals("ESCALIER")) {
            Point p1Esc = immeuble.getPointEscalier1();
            Point p2Esc = immeuble.getPointEscalier2();
            if (p1Esc != null && p2Esc != null) {
                ancragesX.add(p1Esc.getX()); ancragesX.add(p2Esc.getX());
                ancragesY.add(p1Esc.getY()); ancragesY.add(p2Esc.getY());
            }
        }

        if (modeCreation.equals("APPARTEMENT")) {
            Point p1Cou = immeuble.getPointCouloir1();
            Point p2Cou = immeuble.getPointCouloir2();
            if (p1Cou != null && p2Cou != null) {
                ancragesX.add(p1Cou.getX()); ancragesX.add(p2Cou.getX());
                ancragesY.add(p1Cou.getY()); ancragesY.add(p2Cou.getY());
            }
        }

        xReel = appliquerMagnetisme(xReel, (float) immeuble.getXmax(), seuilDynamique, ancragesX);
        yReel = appliquerMagnetisme(yReel, (float) immeuble.getYmax(), seuilDynamique, ancragesY);

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);
            vue.getInfoMessage().setText("Premier point posé. Cliquez sur le coin opposé.");
            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);
            traiterFinAction();
        }
    }

    private float appliquerMagnetisme(float valeur, float maximum, double seuil, List<Float> ancragesSupplementaires) {
        if (Math.abs(valeur - 0) <= seuil) return 0;
        if (Math.abs(valeur - maximum) <= seuil) return maximum;
        for (float ancrage : ancragesSupplementaires) {
            if (Math.abs(valeur - ancrage) <= seuil) return ancrage;
        }
        return valeur;
    }
            
    private void traiterFinAction() {
        if (zoneInvisible(premierPoint, deuxiemePoint)) {
            afficherErreur("Zone invalide (épaisseur nulle).");
            resetPoints();
            return;
        }

        switch (modeCreation) {
            case "ESCALIER":
                validerEscalier();
                break;
            case "COULOIR":
                validerCouloir();
                break;
            case "APPARTEMENT":
                finaliserAppartement();
                break;
        }
    }

    private void validerEscalier() {
        rafraichirPlan();
        Platform.runLater(() -> {
            if (demanderConfirmation("Valider l'escalier ?", "Vérifiez l'aperçu. L'escalier sera identique à tous les étages.")) {
                immeuble.definirEscalierCommun(premierPoint, deuxiemePoint);
                modeCreation = "COULOIR";
                vue.getInfoMessage().setText("Escalier validé. Tracez maintenant le couloir.");
                resetPoints();
                mettreAJourAffichage();
            } else {
                resetPoints();
            }
        });
    }

private void validerCouloir() {
    if (!zonesSeTouchent(premierPoint, deuxiemePoint, immeuble.getPointEscalier1(), immeuble.getPointEscalier2())) {
        afficherErreur("Le couloir doit toucher l'escalier.");
        resetPoints();
        return;
    }
    
    rafraichirPlan();
    Platform.runLater(() -> {
        if (demanderConfirmation("Valider le couloir ?", "Vérifiez l'aperçu. Le couloir sera identique à tous les étages.")) {
            immeuble.definirCouloirCommun(premierPoint, deuxiemePoint);
            
            // OUVERTURE DE LA BOÎTE DE DIALOGUE POUR LES REVÊTEMENTS
            ouvrirDialogRevetementsCouloir();

            modeCreation = "APPARTEMENT";
            vue.getInfoMessage().setText("Couloir validé. Créez vos appartements.");
            resetPoints();
            mettreAJourAffichage();
        } else {
            resetPoints();
        }
    });
}

private void ouvrirDialogRevetementsCouloir() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Configuration du couloir");
    dialog.setHeaderText("Sélectionnez les revêtements pour le couloir commun :");

    ButtonType btnValider = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 20, 20, 20));

    // Récupération des produits du catalogue
    GestionCatalogue catalogue = new GestionCatalogue();

    ComboBox<Revetement> comboSol = new ComboBox<>();
    comboSol.getItems().addAll(catalogue.getProduits("Sol"));
    comboSol.setPromptText("Sélectionner un sol");
    comboSol.setMaxWidth(Double.MAX_VALUE);

    ComboBox<Revetement> comboPlafond = new ComboBox<>();
    comboPlafond.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
    comboPlafond.setPromptText("Sélectionner une peinture");
    comboPlafond.setMaxWidth(Double.MAX_VALUE);

    ComboBox<Revetement> comboMurNord = new ComboBox<>();
    comboMurNord.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
    comboMurNord.setPromptText("Peinture Mur Nord");
    comboMurNord.setMaxWidth(Double.MAX_VALUE);

    ComboBox<Revetement> comboMurEst = new ComboBox<>();
    comboMurEst.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
    comboMurEst.setPromptText("Peinture Mur Est");
    comboMurEst.setMaxWidth(Double.MAX_VALUE);

    ComboBox<Revetement> comboMurSud = new ComboBox<>();
    comboMurSud.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
    comboMurSud.setPromptText("Peinture Mur Sud");
    comboMurSud.setMaxWidth(Double.MAX_VALUE);

    ComboBox<Revetement> comboMurOuest = new ComboBox<>();
    comboMurOuest.getItems().addAll(catalogue.getProduits("CouleurPeinture"));
    comboMurOuest.setPromptText("Peinture Mur Ouest");
    comboMurOuest.setMaxWidth(Double.MAX_VALUE);

    // Placement des éléments dans la grille
    grid.add(new Label("Revêtement de Sol :"), 0, 0);
    grid.add(comboSol, 1, 0);
    
    grid.add(new Label("Revêtement du Plafond :"), 0, 1);
    grid.add(comboPlafond, 1, 1);
    
    grid.add(new Label("Paroi Mur Nord :"), 0, 2);
    grid.add(comboMurNord, 1, 2);
    
    grid.add(new Label("Paroi Mur Est :"), 0, 3);
    grid.add(comboMurEst, 1, 3);
    
    grid.add(new Label("Paroi Mur Sud :"), 0, 4);
    grid.add(comboMurSud, 1, 4);
    
    grid.add(new Label("Paroi Mur Ouest :"), 0, 5);
    grid.add(comboMurOuest, 1, 5);

    dialog.getDialogPane().setContent(grid);

    // Attente de la validation par l'utilisateur
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == btnValider) {
        // Enregistrement des choix dans l'immeuble
        immeuble.setRevetementSolCouloir(comboSol.getValue());
        immeuble.setRevetementPlafondCouloir(comboPlafond.getValue());
        immeuble.setRevetementMurNordCouloir(comboMurNord.getValue());
        immeuble.setRevetementMurEstCouloir(comboMurEst.getValue());
        immeuble.setRevetementMurSudCouloir(comboMurSud.getValue());
        immeuble.setRevetementMurOuestCouloir(comboMurOuest.getValue());
    }
}

    private void resetPoints() { 
        premierPoint = null; 
        deuxiemePoint = null; 
        rafraichirPlan(); 
    }

    private Niveau getNiveauCourant() { return immeuble.getNiveaux().get(indexNiveauCourant); }

    private boolean zoneInvisible(Point p1, Point p2) { return p1.getX() == p2.getX() || p1.getY() == p2.getY(); }

    private void changerEtage(int delta) {
        int nouvelIndex = indexNiveauCourant + delta;
        if (nouvelIndex >= 0 && nouvelIndex < immeuble.getNiveaux().size()) {
            indexNiveauCourant = nouvelIndex;
            resetPoints();
            mettreAJourAffichage();
        }
    }

    private void ajouterEtage() {
        immeuble.ajouterNiveau();
        indexNiveauCourant = immeuble.getNiveaux().size() - 1;
        resetPoints();
        mettreAJourAffichage();
    }

    private void supprimerEtage() {
        if (immeuble.getNiveaux().size() > 1) {
            immeuble.supprimerDernierNiveau();
            if (indexNiveauCourant >= immeuble.getNiveaux().size()) indexNiveauCourant--;
            resetPoints();
            mettreAJourAffichage();
        }
    }

    private void finaliserAppartement() {
        // Avant de demander le nom, on rafraichit pour voir les deux points et l'aperçu
        rafraichirPlan();
        
        String nom = demanderNomAppartement();
        Appartement appartement = creerAppartementAjusteAuCouloir(nom, premierPoint, deuxiemePoint);
        
        if (chevaucheEscalier(appartement) || chevaucheCouloir(appartement) || chevaucheUnAppartement(appartement)) {
            afficherErreur("Collision détectée avec un élément existant.");
        } else if (!toucheCouloir(appartement)) {
            afficherErreur("Erreur de conception : L'appartement doit obligatoirement être en contact direct avec le couloir commun.");
        } else {
            getNiveauCourant().ajouterAppartement(appartement);
        }
        resetPoints();
        mettreAJourAffichage();
    }
    
private void mettreAJourAffichage() {
    vue.getInfoEtage().setText("Étage " + getNiveauCourant().getNumeroNiveau() + " / " + immeuble.getNbreNiveaux());
    vue.getListeAppartements().getItems().setAll(getNiveauCourant().getAppartements());
    
    // MISE À JOUR ERGONOMIQUE DES MATÉRIAUX DU COULOIR
    if (immeuble.getPointCouloir1() == null) {
        vue.getLabelDetailsCouloir().setText("Aucun couloir défini pour le moment.");
    } else {
        String details = String.format(
            "• Sol : %s\n" +
            "• Plafond : %s\n" +
            "• Murs N/E : %s | %s\n" +
            "• Murs S/O : %s | %s",
            immeuble.getRevetementSolCouloir() != null ? immeuble.getRevetementSolCouloir().getNomRevt() : "Aucun",
            immeuble.getRevetementPlafondCouloir() != null ? immeuble.getRevetementPlafondCouloir().getNomRevt() : "Aucun",
            immeuble.getRevetementMurNordCouloir() != null ? immeuble.getRevetementMurNordCouloir().getNomRevt() : "Aucun",
            immeuble.getRevetementMurEstCouloir() != null ? immeuble.getRevetementMurEstCouloir().getNomRevt() : "Aucun",
            immeuble.getRevetementMurSudCouloir() != null ? immeuble.getRevetementMurSudCouloir().getNomRevt() : "Aucun",
            immeuble.getRevetementMurOuestCouloir() != null ? immeuble.getRevetementMurOuestCouloir().getNomRevt() : "Aucun"
        );
        vue.getLabelDetailsCouloir().setText(details);
    }

    rafraichirPlan();
}

    private void rafraichirPlan() {
        Pane pane = vue.getPanePlan();
        pane.getChildren().clear();
        double offsetX = (pane.getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (pane.getHeight() - (immeuble.getYmax() * echelle)) / 2;

        Rectangle contour = new Rectangle(offsetX, offsetY, immeuble.getXmax() * echelle, immeuble.getYmax() * echelle);
        contour.setFill(Color.web("#FFFDF9"));
        contour.setStroke(Color.web("#4A4A4A"));
        contour.setStrokeWidth(2);
        pane.getChildren().add(contour);

        dessinerGrille(pane, offsetX, offsetY);
        dessinerEscalier(pane, offsetX, offsetY);
        dessinerCouloir(pane, offsetX, offsetY);
        dessinerAppartements(pane, offsetX, offsetY);
        dessinerApercu(pane, offsetX, offsetY); 
        dessinerPointEnCours(pane, offsetX, offsetY);
    }

    private void dessinerGrille(Pane pane, double offsetX, double offsetY) {
        for (int x = 0; x <= immeuble.getXmax(); x++) {
            Line l = new Line(offsetX + x * echelle, offsetY, offsetX + x * echelle, offsetY + immeuble.getYmax() * echelle);
            l.setStroke(Color.web("#E8E8E8")); pane.getChildren().add(l);
        }
        for (int y = 0; y <= immeuble.getYmax(); y++) {
            Line l = new Line(offsetX, offsetY + y * echelle, offsetX + immeuble.getXmax() * echelle, offsetY + y * echelle);
            l.setStroke(Color.web("#E8E8E8")); pane.getChildren().add(l);
        }
    }

    private void dessinerEscalier(Pane pane, double offsetX, double offsetY) {
        dessinerZone(pane, offsetX, offsetY, immeuble.getPointEscalier1(), immeuble.getPointEscalier2(), "#FF8A8A", "#B00020", "ESCALIER");
    }

    private void dessinerCouloir(Pane pane, double offsetX, double offsetY) {
        dessinerZone(pane, offsetX, offsetY, immeuble.getPointCouloir1(), immeuble.getPointCouloir2(), "#CDB4DB", "#6A4C93", "COULOIR");
    }

    private void dessinerAppartements(Pane pane, double offsetX, double offsetY) {
        for (Appartement a : getNiveauCourant().getAppartements()) {
            Point p1 = new Point(a.getXMin(), a.getYMin());
            Point p2 = new Point(a.getXMax(), a.getYMax());
            dessinerZone(pane, offsetX, offsetY, p1, p2, "#B8E0D2", "#52796F", a.getNom());
        }
    }

    private void dessinerApercu(Pane pane, double offsetX, double offsetY) {
        if (premierPoint == null || deuxiemePoint == null) return;
        
        String fond = "#E0E0E0";
        String bord = "#999999";
        String texte = "APERÇU";
        
        if (modeCreation.equals("ESCALIER")) {
            fond = "#FFB3B3"; bord = "#B00020";
        } else if (modeCreation.equals("COULOIR")) {
            fond = "#D8B4E2"; bord = "#6A4C93";
        } else if (modeCreation.equals("APPARTEMENT")) {
            fond = "#B8E0D2"; bord = "#52796F";
        } else {
            return; 
        }
        
        dessinerZone(pane, offsetX, offsetY, premierPoint, deuxiemePoint, fond, bord, texte);
    }

    private void dessinerZone(Pane pane, double offX, double offY, Point p1, Point p2, String fond, String bord, String txt) {
        if (p1 == null || p2 == null) return;
        
        float xMin = Math.min(p1.getX(), p2.getX()); float xMax = Math.max(p1.getX(), p2.getX());
        float yMin = Math.min(p1.getY(), p2.getY()); float yMax = Math.max(p1.getY(), p2.getY());
        
        double largeur = (xMax - xMin) * echelle;
        double hauteur = (yMax - yMin) * echelle;
        double xBase = offX + xMin * echelle;
        double yBase = offY + yMin * echelle;

        Rectangle r = new Rectangle(xBase, yBase, largeur, hauteur);
        r.setFill(Color.web(fond)); 
        r.setOpacity(0.5); 
        r.setStroke(Color.web(bord));
        pane.getChildren().add(r);

        if (txt != null && !txt.isEmpty()) {
            Text label = new Text(txt);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            label.setFill(Color.web(bord));
            label.setBoundsType(TextBoundsType.VISUAL);
            
            double textWidth = label.getLayoutBounds().getWidth();
            double textHeight = label.getLayoutBounds().getHeight();
            
            if (largeur > textWidth && hauteur > textHeight) {
                label.setX(xBase + (largeur - textWidth) / 2);
                label.setY(yBase + (hauteur + textHeight) / 2);
                pane.getChildren().add(label);
            }
        }
    }

    /**
     * Dessine les points de saisie actuels sur le plan.
     */
    private void dessinerPointEnCours(Pane pane, double offX, double offY) {
        // Premier point en bleu
        if (premierPoint != null) {
            Circle c1 = new Circle(offX + premierPoint.getX() * echelle, offY + premierPoint.getY() * echelle, 5);
            c1.setFill(Color.BLUE);
            c1.setStroke(Color.WHITE);
            c1.setStrokeWidth(1);
            pane.getChildren().add(c1);
        }
        
        // Second point en rouge
        if (deuxiemePoint != null) {
            Circle c2 = new Circle(offX + deuxiemePoint.getX() * echelle, offY + deuxiemePoint.getY() * echelle, 5);
            c2.setFill(Color.RED);
            c2.setStroke(Color.WHITE);
            c2.setStrokeWidth(1);
            pane.getChildren().add(c2);
        }
    }

    private void ouvrirAppartementSelectionne() {
        Appartement a = vue.getListeAppartements().getSelectionModel().getSelectedItem();
        if (a != null) new CPlanAppartement(new Stage(), a);
    }

    private void supprimerDernierAppartement() {
        getNiveauCourant().supprimerDernierAppartement();
        resetPoints();
        mettreAJourAffichage();
    }

    private boolean demanderConfirmation(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(msg);
        return a.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    private void afficherErreur(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    private String demanderNomAppartement() {
        TextInputDialog d = new TextInputDialog("Appartement " + (getNiveauCourant().getNombreAppartements() + 1));
        d.setTitle("Nom"); d.setHeaderText(null); d.setContentText("Nom de l'appartement :");
        return d.showAndWait().orElse("Appartement");
    }

    private Appartement creerAppartementAjusteAuCouloir(String n, Point p1, Point p2) {
        float x1 = Math.min(p1.getX(), p2.getX()); float x2 = Math.max(p1.getX(), p2.getX());
        float y1 = Math.min(p1.getY(), p2.getY()); float y2 = Math.max(p1.getY(), p2.getY());
        return new Appartement(n, new Point(x1, y1), new Point(x2, y2));
    }

    private boolean chevaucheUnAppartement(Appartement a) {
        for (Appartement ex : getNiveauCourant().getAppartements()) {
            if (!(a.getXMax() <= ex.getXMin() || a.getXMin() >= ex.getXMax() || a.getYMax() <= ex.getYMin() || a.getYMin() >= ex.getYMax())) return true;
        }
        return false;
    }
    
    private boolean chevaucheEscalier(Appartement a) { return chevaucheZone(a, immeuble.getPointEscalier1(), immeuble.getPointEscalier2()); }
    private boolean chevaucheCouloir(Appartement a) { return chevaucheZone(a, immeuble.getPointCouloir1(), immeuble.getPointCouloir2()); }
    
    private boolean chevaucheZone(Appartement a, Point p1, Point p2) {
        if (p1 == null || p2 == null) return false;
        float xm = Math.min(p1.getX(), p2.getX()); float xM = Math.max(p1.getX(), p2.getX());
        float ym = Math.min(p1.getY(), p2.getY()); float yM = Math.max(p1.getY(), p2.getY());
        return !(a.getXMax() <= xm || a.getXMin() >= xM || a.getYMax() <= ym || a.getYMin() >= yM);
    }

    private boolean zonesSeTouchent(Point a1, Point a2, Point b1, Point b2) {
        if (a1 == null || b1 == null) return false;
        float axm = Math.min(a1.getX(), a2.getX()); float axM = Math.max(a1.getX(), a2.getX());
        float aym = Math.min(a1.getY(), a2.getY()); float ayM = Math.max(a1.getY(), a2.getY());
        float bxm = Math.min(b1.getX(), b2.getX()); float bxM = Math.max(b1.getX(), b2.getX());
        float bym = Math.min(b1.getY(), b2.getY()); float byM = Math.max(b1.getY(), b2.getY());
        return !(axM < bxm || axm > bxM || ayM < bym || aym > byM);
    }

    /**
     * Vérifie si l'appartement est en contact physique avec le couloir commun.
     */
    private boolean toucheCouloir(Appartement a) {
        if (immeuble.getPointCouloir1() == null || immeuble.getPointCouloir2() == null) {
            return false;
        }
        return zonesSeTouchent(
            new Point(a.getXMin(), a.getYMin()),
            new Point(a.getXMax(), a.getYMax()),
            immeuble.getPointCouloir1(),
            immeuble.getPointCouloir2()
        );
    }
}

