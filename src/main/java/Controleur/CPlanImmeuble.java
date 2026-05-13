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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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

    private boolean modeCreationEscalier = true;

    public CPlanImmeuble(Stage fenetre, Immeuble immeuble) {
        this.fenetre = fenetre;
        this.immeuble = immeuble;
        this.indexNiveauCourant = 0;

        vue = new VuePlanImmeuble();

        Scene scene = new Scene(vue.getRoot(), 1300, 800);
        fenetre.setScene(scene);
        fenetre.setTitle("Éditeur d'immeuble");
        fenetre.setMaximized(true);
        fenetre.show();

        calculerEchelle();
        vue.getInfoMessage().setText("Définissez d'abord l'escalier : cliquez sur 2 points.");
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

        vue.getBoutonDefinirEscalier().setOnAction(e -> {
            modeCreationEscalier = true;
            premierPoint = null;
            deuxiemePoint = null;
            vue.getInfoMessage().setText("Cliquez sur 2 points pour redéfinir l'escalier.");
            rafraichirPlan();
        });

        vue.getBoutonOuvrirAppartement().setOnAction(e -> {
            Appartement appartementSelectionne = vue.getListeAppartements().getSelectionModel().getSelectedItem();

            if (appartementSelectionne == null) {
                afficherErreur("Veuillez sélectionner un appartement dans la liste.");
                return;
            }

            Stage nouvelleFenetre = new Stage();
            new CPlanAppartement(nouvelleFenetre, appartementSelectionne);
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

        xReel = appliquerMagnetisme(xReel, immeuble.getXmax());
        yReel = appliquerMagnetisme(yReel, immeuble.getYmax());

        if (xReel < 0 || xReel > immeuble.getXmax() || yReel < 0 || yReel > immeuble.getYmax()) {
            vue.getInfoMessage().setText("Clic hors de l'étage.");
            return;
        }

        if (immeuble.getPointEscalier1() == null || immeuble.getPointEscalier2() == null) {
            modeCreationEscalier = true;
        }

        if (premierPoint == null) {
            premierPoint = new Point(xReel, yReel);

            if (modeCreationEscalier) {
                vue.getInfoMessage().setText("Point 1 de l'escalier enregistré. Cliquez sur le coin opposé.");
            } else {
                vue.getInfoMessage().setText("Point 1 enregistré. Cliquez sur le coin opposé.");
            }

            rafraichirPlan();
        } else {
            deuxiemePoint = new Point(xReel, yReel);

            if (modeCreationEscalier) {
                finaliserEscalier();
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

    private void finaliserEscalier() {
        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoMessage().setText("Erreur : escalier impossible.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        for (Niveau niveau : immeuble.getNiveaux()) {
            for (Appartement appartement : niveau.getAppartements()) {
                if (chevaucheZone(appartement, premierPoint, deuxiemePoint)) {
                    afficherErreur("Impossible de placer l'escalier ici : il chevauche déjà un appartement.");
                    premierPoint = null;
                    deuxiemePoint = null;
                    modeCreationEscalier = true;
                    mettreAJourAffichage();
                    return;
                }
            }
        }

        immeuble.definirEscalierCommun(premierPoint, deuxiemePoint);

        vue.getInfoMessage().setText("Escalier créé en rouge sur tous les étages. Vous pouvez maintenant créer les appartements.");

        premierPoint = null;
        deuxiemePoint = null;
        modeCreationEscalier = false;

        mettreAJourAffichage();
    }

    private void finaliserAppartement() {
        if (immeuble.getPointEscalier1() == null || immeuble.getPointEscalier2() == null) {
            afficherErreur("Vous devez définir l'escalier avant de créer des appartements.");
            vue.getInfoMessage().setText("Définissez d'abord l'escalier : cliquez sur 2 points.");
            premierPoint = null;
            deuxiemePoint = null;
            modeCreationEscalier = true;
            rafraichirPlan();
            return;
        }

        if (premierPoint.getX() == deuxiemePoint.getX() || premierPoint.getY() == deuxiemePoint.getY()) {
            vue.getInfoMessage().setText("Erreur : rectangle impossible.");
            deuxiemePoint = null;
            rafraichirPlan();
            return;
        }

        String nomAppartement = demanderNomAppartement();

        Appartement nouvelAppartement = new Appartement(nomAppartement, premierPoint, deuxiemePoint);

        if (chevaucheEscalier(nouvelAppartement)) {
            vue.getInfoMessage().setText("Erreur : impossible de créer un appartement sur l'escalier.");
            afficherErreur("Impossible de créer un appartement sur l'escalier.");
        } else if (chevaucheUnAppartement(nouvelAppartement)) {
            vue.getInfoMessage().setText("Erreur : cet appartement chevauche un autre appartement.");
        } else {
            getNiveauCourant().ajouterAppartement(nouvelAppartement);

            vue.getInfoMessage().setText(
                    nomAppartement + " créé ("
                    + String.format("%.2f", nouvelAppartement.getSuperficie())
                    + " m²)"
            );
        }

        premierPoint = null;
        deuxiemePoint = null;

        mettreAJourAffichage();
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
        Point p1 = getNiveauCourant().getPointEscalier1();
        Point p2 = getNiveauCourant().getPointEscalier2();

        if (p1 == null || p2 == null) {
            return false;
        }

        return chevaucheZone(appartement, p1, p2);
    }

    private boolean chevaucheZone(Appartement appartement, Point p1, Point p2) {
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

        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(3);

        pane.getChildren().add(contour);

        dessinerEscalier(pane, offsetX, offsetY);

        for (Appartement appartement : getNiveauCourant().getAppartements()) {
            Rectangle rectangleAppartement = new Rectangle(
                    offsetX + appartement.getXMin() * echelle,
                    offsetY + appartement.getYMin() * echelle,
                    appartement.getLargeur() * echelle,
                    appartement.getHauteur() * echelle
            );

            rectangleAppartement.setFill(Color.LIGHTGREEN);
            rectangleAppartement.setOpacity(0.4);
            rectangleAppartement.setStroke(Color.DARKGREEN);

            pane.getChildren().add(rectangleAppartement);
        }

        if (premierPoint != null) {
            Color couleurPoint = modeCreationEscalier ? Color.RED : Color.BLUE;

            Circle point1 = new Circle(
                    offsetX + premierPoint.getX() * echelle,
                    offsetY + premierPoint.getY() * echelle,
                    4,
                    couleurPoint
            );

            pane.getChildren().add(point1);
        }
    }

   private void dessinerEscalier(Pane pane, double offsetX, double offsetY) {

    Point p1 = getNiveauCourant().getPointEscalier1();
    Point p2 = getNiveauCourant().getPointEscalier2();

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

    Rectangle rectangleEscalier = new Rectangle(
            xRectangle,
            yRectangle,
            largeur,
            hauteur
    );

    rectangleEscalier.setFill(Color.RED);
    rectangleEscalier.setOpacity(0.45);
    rectangleEscalier.setStroke(Color.DARKRED);
    rectangleEscalier.setStrokeWidth(2);

    pane.getChildren().add(rectangleEscalier);

    Text texteEscalier = new Text("ESCALIER");

    texteEscalier.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    texteEscalier.setFill(Color.DARKRED);

    double centreX = xRectangle + largeur / 2;
    double centreY = yRectangle + hauteur / 2;

    texteEscalier.setX(centreX - 40);
    texteEscalier.setY(centreY);

    pane.getChildren().add(texteEscalier);
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
