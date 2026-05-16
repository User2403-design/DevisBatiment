
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controleur;

import Modele.Appartement;
import Modele.Immeuble;
import Modele.Niveau;
import Modele.Point;
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
import javafx.stage.Stage;
import java.util.Optional;

public class CPlanImmeuble {

    private VuePlanImmeuble vue;
    private Stage fenetre;
    private Immeuble immeuble;

    private int indexNiveauCourant;

    private Point premierPoint;
    private Point deuxiemePoint;

    private double echelle;

    private final double margeSecurite = 40.0;
    private final double seuilMagnetisme = 10.0;

    // Étapes : ESCALIER -> COULOIR -> APPARTEMENT
    private String modeCreation = "ESCALIER";

    public CPlanImmeuble(Stage fenetre, Immeuble immeuble) {
        this.fenetre = fenetre;
        this.immeuble = immeuble;
        this.indexNiveauCourant = 0;

        vue = new VuePlanImmeuble();

        Scene scene = new Scene(vue.getRoot(), 1300, 800);

        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur d'immeuble");
        fenetre.show();
        fenetre.setMaximized(true);
        fenetre.setFullScreen(false);

        // On cache le bouton, car l'escalier est demandé automatiquement au début.
        vue.getBoutonDefinirEscalier().setVisible(false);
        vue.getBoutonDefinirEscalier().setManaged(false);

        calculerEchelle();

        vue.getInfoMessage().setText("Étape 1 : tracez l'escalier avec 2 clics.");

        mettreAJourAffichage();

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

        vue.getBoutonCatalogue().setOnAction(e -> {
            Stage fenetreCatalogue = new Stage();
            new CCatalogue(fenetreCatalogue);
        });

        vue.getBoutonOuvrirAppartement().setOnAction(e -> {
            Appartement appartementSelectionne = vue.getListeAppartements().getSelectionModel().getSelectedItem();

            if (appartementSelectionne == null) {
                afficherErreur("Veuillez sélectionner un appartement dans la liste.");
                return;
            }

            Stage nouvelleFenetre = new Stage();
            new CPlanAppartement(nouvelleFenetre, appartementSelectionne);
            nouvelleFenetre.show();
            nouvelleFenetre.setMaximized(true);
            nouvelleFenetre.setFullScreen(false);
        });

        vue.getBoutonSupprimerDernierAppartement().setOnAction(e -> {
            getNiveauCourant().supprimerDernierAppartement();
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Dernier appartement supprimé.");
            mettreAJourAffichage();
        });

        vue.getBoutonEtagePrecedent().setOnAction(e -> {
            if (indexNiveauCourant > 0) {
                indexNiveauCourant--;
                premierPoint = null;
                deuxiemePoint = null;
                mettreAJourAffichage();
            }
        });

        vue.getBoutonEtageSuivant().setOnAction(e -> {
            if (indexNiveauCourant < immeuble.getNiveaux().size() - 1) {
                indexNiveauCourant++;
                premierPoint = null;
                deuxiemePoint = null;
                mettreAJourAffichage();
            }
        });

        vue.getBoutonAjouterEtage().setOnAction(e -> {
            immeuble.ajouterNiveau();
            indexNiveauCourant = immeuble.getNiveaux().size() - 1;
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Nouvel étage créé.");
            mettreAJourAffichage();
        });

        vue.getBoutonSupprimerEtage().setOnAction(e -> {
            if (immeuble.getNiveaux().size() > 1) {
                immeuble.supprimerDernierNiveau();

                if (indexNiveauCourant >= immeuble.getNiveaux().size()) {
                    indexNiveauCourant = immeuble.getNiveaux().size() - 1;
                }

                premierPoint = null;
                deuxiemePoint = null;
                vue.getInfoMessage().setText("Dernier étage supprimé.");
                mettreAJourAffichage();
            } else {
                afficherErreur("Il faut garder au moins un étage.");
            }
        });

        vue.getBoutonFermer().setOnAction(e -> fenetre.close());
    }

    private Niveau getNiveauCourant() {
        return immeuble.getNiveaux().get(indexNiveauCourant);
    }

    private void calculerEchelle() {
        double largeurDisponible = vue.getPanePlan().getWidth() - (margeSecurite * 2);
        double hauteurDisponible = vue.getPanePlan().getHeight() - (margeSecurite * 2);

        if (largeurDisponible <= 0 || hauteurDisponible <= 0) {
            return;
        }

        double echelleX = largeurDisponible / immeuble.getXmax();
        double echelleY = hauteurDisponible / immeuble.getYmax();

        echelle = Math.min(echelleX, echelleY);
    }

    private void gererClicSouris(double xPixel, double yPixel) {
        double offsetX = (vue.getPanePlan().getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (vue.getPanePlan().getHeight() - (immeuble.getYmax() * echelle)) / 2;

        float xReel = (float) ((xPixel - offsetX) / echelle);
        float yReel = (float) ((yPixel - offsetY) / echelle);

        if (xReel < 0 || xReel > immeuble.getXmax() || yReel < 0 || yReel > immeuble.getYmax()) {
            vue.getInfoMessage().setText("Clic hors de l'étage.");
            return;
        }

        // Magnétisme seulement pour les appartements.
        if (modeCreation.equals("APPARTEMENT")) {
            xReel = appliquerMagnetisme(xReel, immeuble.getXmax());
            yReel = appliquerMagnetisme(yReel, immeuble.getYmax());
        }

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);

            if (modeCreation.equals("ESCALIER")) {
                vue.getInfoMessage().setText("Point 1 de l'escalier posé. Cliquez sur le coin opposé.");
            } else if (modeCreation.equals("COULOIR")) {
                vue.getInfoMessage().setText("Point 1 du couloir posé. Cliquez sur le coin opposé.");
            } else {
                vue.getInfoMessage().setText("Point 1 de l'appartement posé. Cliquez sur le coin opposé.");
            }

            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);

            if (modeCreation.equals("ESCALIER")) {
                afficherApercuPuisValiderEscalier();
            } else if (modeCreation.equals("COULOIR")) {
                afficherApercuPuisValiderCouloir();
            } else {
                finaliserAppartement();
            }
        }
    }

    private float appliquerMagnetisme(float valeur, float maximum) {
        if (Math.abs(valeur - 0) <= seuilMagnetisme) {
            return 0;
        }

        if (Math.abs(valeur - maximum) <= seuilMagnetisme) {
            return maximum;
        }

        return valeur;
    }

    private boolean zoneInvisible(Point p1, Point p2) {
        return p1.getX() == p2.getX() || p1.getY() == p2.getY();
    }

    private void afficherApercuPuisValiderEscalier() {
        if (zoneInvisible(premierPoint, deuxiemePoint)) {
            afficherErreur("Les deux clics donnent une zone invisible. Recommencez avec deux points différents.");
            premierPoint = null;
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        vue.getInfoMessage().setText("Aperçu de l'escalier. Confirmez si la position vous convient.");
        rafraichirPlan();

        Platform.runLater(() -> {
            boolean ok = demanderConfirmation(
                    "Confirmer l'escalier ?",
                    "Vérifiez l'aperçu sur le plan. Après validation, l'escalier ne pourra plus être modifié."
            );

            if (ok) {
                immeuble.definirEscalierCommun(premierPoint, deuxiemePoint);
                premierPoint = null;
                deuxiemePoint = null;
                modeCreation = "COULOIR";
                vue.getInfoMessage().setText("Étape 2 : tracez maintenant le couloir central lié à l'escalier.");
                mettreAJourAffichage();
            } else {
                premierPoint = null;
                deuxiemePoint = null;
                vue.getInfoMessage().setText("Escalier annulé. Tracez-le à nouveau.");
                rafraichirPlan();
            }
        });
    }

    private void afficherApercuPuisValiderCouloir() {
        if (zoneInvisible(premierPoint, deuxiemePoint)) {
            afficherErreur("Les deux clics donnent une zone invisible. Recommencez avec deux points différents.");
            premierPoint = null;
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        if (!zonesSeTouchent(premierPoint, deuxiemePoint, immeuble.getPointEscalier1(), immeuble.getPointEscalier2())) {
            afficherErreur("Le couloir doit toucher directement l'escalier.");
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Tracez le couloir : il doit toucher l'escalier.");
            rafraichirPlan();
            return;
        }

        vue.getInfoMessage().setText("Aperçu du couloir. Confirmez si la position vous convient.");
        rafraichirPlan();

        Platform.runLater(() -> {
            boolean ok = demanderConfirmation(
                    "Confirmer le couloir ?",
                    "Vérifiez l'aperçu sur le plan. Après validation, le couloir ne pourra plus être modifié."
            );

            if (ok) {
                immeuble.definirCouloirCommun(premierPoint, deuxiemePoint);
                premierPoint = null;
                deuxiemePoint = null;
                modeCreation = "APPARTEMENT";
                vue.getInfoMessage().setText("Escalier et couloir validés. Vous pouvez maintenant créer les appartements.");
                mettreAJourAffichage();
            } else {
                premierPoint = null;
                deuxiemePoint = null;
                vue.getInfoMessage().setText("Couloir annulé. Tracez-le à nouveau.");
                rafraichirPlan();
            }
        });
    }

    private void finaliserAppartement() {
        if (!modeCreation.equals("APPARTEMENT")) {
            afficherErreur("Vous devez d'abord définir l'escalier puis le couloir.");
            premierPoint = null;
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        String nomAppartement = demanderNomAppartement();

        // L'appartement est automatiquement ajusté pour aller du couloir jusqu'au bord extérieur.
        Appartement nouvelAppartement = creerAppartementAjusteAuCouloir(nomAppartement, premierPoint, deuxiemePoint);

        if (zoneInvisible(new Point(nouvelAppartement.getXMin(), nouvelAppartement.getYMin()),
                new Point(nouvelAppartement.getXMax(), nouvelAppartement.getYMax()))) {
            afficherErreur("La zone choisie est invisible. Cliquez sur deux points plus espacés.");
        } else if (chevaucheEscalier(nouvelAppartement)) {
            afficherErreur("Impossible de créer un appartement sur l'escalier.");
        } else if (chevaucheCouloir(nouvelAppartement)) {
            afficherErreur("Impossible de créer un appartement sur le couloir.");
        } else if (chevaucheUnAppartement(nouvelAppartement)) {
            afficherErreur("Cet appartement chevauche déjà un autre appartement.");
        } else {
            getNiveauCourant().ajouterAppartement(nouvelAppartement);

            vue.getInfoMessage().setText(
                    nomAppartement + " créé automatiquement jusqu'au couloir/bord extérieur."
            );
        }

        premierPoint = null;
        deuxiemePoint = null;

        mettreAJourAffichage();
    }

    private Appartement creerAppartementAjusteAuCouloir(String nom, Point p1, Point p2) {
        Point c1 = immeuble.getPointCouloir1();
        Point c2 = immeuble.getPointCouloir2();

        float xMinCouloir = Math.min(c1.getX(), c2.getX());
        float xMaxCouloir = Math.max(c1.getX(), c2.getX());
        float yMinCouloir = Math.min(c1.getY(), c2.getY());
        float yMaxCouloir = Math.max(c1.getY(), c2.getY());

        float largeurCouloir = xMaxCouloir - xMinCouloir;
        float hauteurCouloir = yMaxCouloir - yMinCouloir;

        float xMin = Math.min(p1.getX(), p2.getX());
        float xMax = Math.max(p1.getX(), p2.getX());
        float yMin = Math.min(p1.getY(), p2.getY());
        float yMax = Math.max(p1.getY(), p2.getY());

        float centreX = (xMin + xMax) / 2;
        float centreY = (yMin + yMax) / 2;

        // Couloir vertical : les appartements vont à gauche ou à droite du couloir.
        if (hauteurCouloir >= largeurCouloir) {
            float centreCouloirX = (xMinCouloir + xMaxCouloir) / 2;

            if (centreX < centreCouloirX) {
                return new Appartement(nom, new Point(0, yMin), new Point(xMinCouloir, yMax));
            } else {
                return new Appartement(nom, new Point(xMaxCouloir, yMin), new Point(immeuble.getXmax(), yMax));
            }
        }

        // Couloir horizontal : les appartements vont au-dessus ou en-dessous du couloir.
        float centreCouloirY = (yMinCouloir + yMaxCouloir) / 2;

        if (centreY < centreCouloirY) {
            return new Appartement(nom, new Point(xMin, 0), new Point(xMax, yMinCouloir));
        } else {
            return new Appartement(nom, new Point(xMin, yMaxCouloir), new Point(xMax, immeuble.getYmax()));
        }
    }

    private boolean chevaucheUnAppartement(Appartement nouvelAppartement) {
        for (Appartement appartementExistant : getNiveauCourant().getAppartements()) {
            boolean pasDeChevauchement =
                    nouvelAppartement.getXMax() <= appartementExistant.getXMin()
                    || nouvelAppartement.getXMin() >= appartementExistant.getXMax()
                    || nouvelAppartement.getYMax() <= appartementExistant.getYMin()
                    || nouvelAppartement.getYMin() >= appartementExistant.getYMax();

            if (!pasDeChevauchement) {
                return true;
            }
        }

        return false;
    }

    private boolean chevaucheEscalier(Appartement appartement) {
        return chevaucheZone(appartement, getNiveauCourant().getPointEscalier1(), getNiveauCourant().getPointEscalier2());
    }

    private boolean chevaucheCouloir(Appartement appartement) {
        return chevaucheZone(appartement, getNiveauCourant().getPointCouloir1(), getNiveauCourant().getPointCouloir2());
    }

    private boolean chevaucheZone(Appartement appartement, Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return false;
        }

        float xMinZone = Math.min(p1.getX(), p2.getX());
        float xMaxZone = Math.max(p1.getX(), p2.getX());
        float yMinZone = Math.min(p1.getY(), p2.getY());
        float yMaxZone = Math.max(p1.getY(), p2.getY());

        boolean pasDeChevauchement =
                appartement.getXMax() <= xMinZone
                || appartement.getXMin() >= xMaxZone
                || appartement.getYMax() <= yMinZone
                || appartement.getYMin() >= yMaxZone;

        return !pasDeChevauchement;
    }

    private boolean zonesSeTouchent(Point a1, Point a2, Point b1, Point b2) {
        if (a1 == null || a2 == null || b1 == null || b2 == null) {
            return false;
        }

        float axMin = Math.min(a1.getX(), a2.getX());
        float axMax = Math.max(a1.getX(), a2.getX());
        float ayMin = Math.min(a1.getY(), a2.getY());
        float ayMax = Math.max(a1.getY(), a2.getY());

        float bxMin = Math.min(b1.getX(), b2.getX());
        float bxMax = Math.max(b1.getX(), b2.getX());
        float byMin = Math.min(b1.getY(), b2.getY());
        float byMax = Math.max(b1.getY(), b2.getY());

        boolean separes =
                axMax < bxMin
                || axMin > bxMax
                || ayMax < byMin
                || ayMin > byMax;

        return !separes;
    }

    private boolean demanderConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> resultat = alert.showAndWait();

        return resultat.isPresent() && resultat.get() == ButtonType.OK;
    }

    private void mettreAJourAffichage() {
        vue.getInfoEtage().setText(
                "Étage " + getNiveauCourant().getNumeroNiveau()
                + " / Nombre total d'étages : "
                + immeuble.getNbreNiveaux()
        );

        vue.getListeAppartements().getItems().clear();
        vue.getListeAppartements().getItems().addAll(getNiveauCourant().getAppartements());

        rafraichirPlan();
    }

    private void rafraichirPlan() {
        Pane pane = vue.getPanePlan();

        pane.getChildren().clear();

        double offsetX = (pane.getWidth() - (immeuble.getXmax() * echelle)) / 2;
        double offsetY = (pane.getHeight() - (immeuble.getYmax() * echelle)) / 2;

        Rectangle contour = new Rectangle(
                offsetX,
                offsetY,
                immeuble.getXmax() * echelle,
                immeuble.getYmax() * echelle
        );

        contour.setFill(Color.web("#FFFDF9"));
        contour.setStroke(Color.web("#4A4A4A"));
        contour.setStrokeWidth(3);

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
            Line ligne = new Line(
                    offsetX + x * echelle,
                    offsetY,
                    offsetX + x * echelle,
                    offsetY + immeuble.getYmax() * echelle
            );

            ligne.setStroke(Color.web("#E8E8E8"));
            pane.getChildren().add(ligne);
        }

        for (int y = 0; y <= immeuble.getYmax(); y++) {
            Line ligne = new Line(
                    offsetX,
                    offsetY + y * echelle,
                    offsetX + immeuble.getXmax() * echelle,
                    offsetY + y * echelle
            );

            ligne.setStroke(Color.web("#E8E8E8"));
            pane.getChildren().add(ligne);
        }

        Text infoEchelle = new Text(
                offsetX,
                offsetY + immeuble.getYmax() * echelle + 25,
                "Échelle : 1 carreau = 1 m"
        );

        infoEchelle.setFill(Color.web("#555555"));
        infoEchelle.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        pane.getChildren().add(infoEchelle);
    }

    private void dessinerEscalier(Pane pane, double offsetX, double offsetY) {
        dessinerZone(
                pane,
                offsetX,
                offsetY,
                getNiveauCourant().getPointEscalier1(),
                getNiveauCourant().getPointEscalier2(),
                "#FF8A8A",
                "#B00020",
                "ESCALIER"
        );
    }

    private void dessinerCouloir(Pane pane, double offsetX, double offsetY) {
        dessinerZone(
                pane,
                offsetX,
                offsetY,
                getNiveauCourant().getPointCouloir1(),
                getNiveauCourant().getPointCouloir2(),
                "#CDB4DB",
                "#6A4C93",
                "COULOIR"
        );
    }

    private void dessinerApercu(Pane pane, double offsetX, double offsetY) {
        if (premierPoint == null || deuxiemePoint == null) {
            return;
        }

        if (modeCreation.equals("ESCALIER")) {
            dessinerZone(pane, offsetX, offsetY, premierPoint, deuxiemePoint, "#FFB3B3", "#B00020", "APERÇU");
        } else if (modeCreation.equals("COULOIR")) {
            dessinerZone(pane, offsetX, offsetY, premierPoint, deuxiemePoint, "#D8B4E2", "#6A4C93", "APERÇU");
        }
    }

    private void dessinerZone(Pane pane, double offsetX, double offsetY, Point p1, Point p2, String couleurFond, String couleurContour, String texte) {
        if (p1 == null || p2 == null) {
            return;
        }

        float xMin = Math.min(p1.getX(), p2.getX());
        float xMax = Math.max(p1.getX(), p2.getX());
        float yMin = Math.min(p1.getY(), p2.getY());
        float yMax = Math.max(p1.getY(), p2.getY());

        double largeur = (xMax - xMin) * echelle;
        double hauteur = (yMax - yMin) * echelle;

        double xRectangle = offsetX + xMin * echelle;
        double yRectangle = offsetY + yMin * echelle;

        Rectangle rectangle = new Rectangle(xRectangle, yRectangle, largeur, hauteur);

        rectangle.setFill(Color.web(couleurFond));
        rectangle.setOpacity(0.65);
        rectangle.setStroke(Color.web(couleurContour));
        rectangle.setStrokeWidth(2);

        pane.getChildren().add(rectangle);

        Text label = new Text(texte);

        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setFill(Color.web(couleurContour));

        label.setX(xRectangle + largeur / 2 - 35);
        label.setY(yRectangle + hauteur / 2);

        pane.getChildren().add(label);
    }

    private void dessinerAppartements(Pane pane, double offsetX, double offsetY) {
        for (Appartement appartement : getNiveauCourant().getAppartements()) {
            Rectangle rectangleAppartement = new Rectangle(
                    offsetX + appartement.getXMin() * echelle,
                    offsetY + appartement.getYMin() * echelle,
                    appartement.getLargeur() * echelle,
                    appartement.getHauteur() * echelle
            );

            rectangleAppartement.setFill(Color.web("#B8E0D2"));
            rectangleAppartement.setOpacity(0.65);
            rectangleAppartement.setStroke(Color.web("#52796F"));
            rectangleAppartement.setStrokeWidth(2);

            pane.getChildren().add(rectangleAppartement);
        }
    }

    private void dessinerPointEnCours(Pane pane, double offsetX, double offsetY) {
        if (premierPoint == null) {
            return;
        }

        Color couleurPoint = Color.web("#355070");

        if (modeCreation.equals("ESCALIER")) {
            couleurPoint = Color.web("#B00020");
        } else if (modeCreation.equals("COULOIR")) {
            couleurPoint = Color.web("#6A4C93");
        }

        Circle point1 = new Circle(
                offsetX + premierPoint.getX() * echelle,
                offsetY + premierPoint.getY() * echelle,
                5,
                couleurPoint
        );

        pane.getChildren().add(point1);
    }

    private String demanderNomAppartement() {
        TextInputDialog dialog = new TextInputDialog("Appartement");

        dialog.setTitle("Nouvel appartement");
        dialog.setHeaderText("Créer un appartement");
        dialog.setContentText("Nom de l'appartement :");

        Optional<String> resultat = dialog.showAndWait();

        if (resultat.isPresent() && !resultat.get().trim().isEmpty()) {
            return resultat.get().trim();
        }

        return "Appartement " + (getNiveauCourant().getNombreAppartements() + 1);
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}