/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.util.ArrayList;

public class Stockage {

    // Liste qui garde tous les bâtiments créés
    public static ArrayList batiments = new ArrayList<>();

    public static String genererTexteDevis() {

        StringBuilder devis = new StringBuilder();

        devis.append("==================================================\n");
        devis.append("                  DEVIS BATIMENT\n");
        devis.append("==================================================\n\n");

        if (batiments.isEmpty()) {
            devis.append("Aucun bâtiment enregistré.\n");
            return devis.toString();
        }

        Batiment batiment = (Batiment) batiments.get(batiments.size() - 1);

        devis.append("INFORMATIONS GENERALES\n");
        devis.append("Type du bâtiment : ").append(batiment.getTypeBatiment()).append("\n");
        devis.append("Surface totale du bâtiment : ").append(format(batiment.getSuperficie())).append(" m²\n");
        devis.append("Nombre de niveaux : ").append(batiment.getNbreNiveaux()).append("\n\n");

        devis.append("--------------------------------------------------\n");
        devis.append("FACADES EXTERNES\n");
        devis.append("--------------------------------------------------\n");

        if (batiment.getRevtFacade() != null) {
            devis.append("Revêtement façade : ")
                    .append(batiment.getRevtFacade().getNomRevt())
                    .append(" | Prix : ")
                    .append(format(batiment.getRevtFacade().getPrixRevt()))
                    .append(" €/m²\n");
        }

        if (batiment.getIsolantExt() != null) {
            devis.append("Isolant extérieur : ")
                    .append(batiment.getIsolantExt().getNomRevt())
                    .append(" | Prix : ")
                    .append(format(batiment.getIsolantExt().getPrixRevt()))
                    .append(" €/m²\n");
        }

        devis.append("\n--------------------------------------------------\n");
        devis.append("INTERIEUR\n");
        devis.append("--------------------------------------------------\n");

        if (batiment instanceof Maison) {
            Maison maison = (Maison) batiment;
            devis.append("Logement : ").append(maison.getPlanMaison().getNom()).append("\n");
            devis.append("Surface : ").append(format(maison.getPlanMaison().getSuperficie())).append(" m²\n");
            devis.append("\nPièces :\n");

            for (Object objPiece : maison.getPlanMaison().getPieces()) {
                Piece piece = (Piece) objPiece;
                devis.append("- ").append(piece.getUsage())
                        .append(" | Surface : ")
                        .append(format(piece.getSuperficie()))
                        .append(" m²\n");
            }
        }

        if (batiment instanceof Immeuble) {
            Immeuble immeuble = (Immeuble) batiment;

            devis.append("Nombre de niveaux : ").append(immeuble.getNbreNiveaux()).append("\n\n");

            for (Object objNiveau : immeuble.getNiveaux()) {
                Niveau niveau = (Niveau) objNiveau;

                devis.append("NIVEAU ").append(niveau.getNumeroNiveau()).append("\n");

                for (Object objAppartement : niveau.getAppartements()) {
                    Appartement appartement = (Appartement) objAppartement;

                    devis.append("Appartement : ").append(appartement.getNom()).append("\n");
                    devis.append("Surface : ").append(format(appartement.getSuperficie())).append(" m²\n");

                    for (Object objPiece : appartement.getPieces()) {
                        Piece piece = (Piece) objPiece;
                        devis.append("- ").append(piece.getUsage())
                                .append(" | Surface : ")
                                .append(format(piece.getSuperficie()))
                                .append(" m²\n");
                    }

                    devis.append("\n");
                }
            }

            devis.append("--------------------------------------------------\n");
            devis.append("ESCALIER\n");
            devis.append("--------------------------------------------------\n");
            devis.append("Prix fixe escalier : 3500 € x ")
                    .append(immeuble.getNbreNiveaux())
                    .append(" niveaux\n");

            devis.append("\n--------------------------------------------------\n");
            devis.append("COULOIRS\n");
            devis.append("--------------------------------------------------\n");
            devis.append("Revêtement sol : ").append(nomRevetement(immeuble.getRevetementSolCouloir())).append("\n");
            devis.append("Revêtement plafond : ").append(nomRevetement(immeuble.getRevetementPlafondCouloir())).append("\n");
            devis.append("Mur Nord : ").append(nomRevetement(immeuble.getRevetementMurNordCouloir())).append("\n");
            devis.append("Mur Est : ").append(nomRevetement(immeuble.getRevetementMurEstCouloir())).append("\n");
            devis.append("Mur Sud : ").append(nomRevetement(immeuble.getRevetementMurSudCouloir())).append("\n");
            devis.append("Mur Ouest : ").append(nomRevetement(immeuble.getRevetementMurOuestCouloir())).append("\n");
            devis.append("Tarif inclus dans le coût global des murs.\n");
        }

        devis.append("\n==================================================\n");
        devis.append("DEVIS A COMPLETER AVEC LES PRIX DETAILLES\n");
        devis.append("==================================================\n");

        return devis.toString();
    }

    private static String nomRevetement(Revetement revetement) {
        if (revetement == null) {
            return "aucun";
        }

        return revetement.getNomRevt()
                + " ("
                + format(revetement.getPrixRevt())
                + " €)";
    }

    private static String format(double valeur) {
        return String.format("%.2f", valeur);
    }
}