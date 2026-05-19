/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Modele;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Stockage {

    public static ArrayList batiments = new ArrayList<>();

    public static String genererTexteDevis() {

        StringBuilder devis = new StringBuilder();

        double totalHT = 0;
        double hauteurPlafond = 2.5;

        devis.append("====================================================================================\n");
        devis.append("                                    BATIR & Co - DEVIS BATIMENT\n");
        devis.append("====================================================================================\n");
        devis.append("Raison sociale   : Entreprise BTP\n");
        devis.append("Adresse          : 14 rue de general Leclerc, 67000, STRASBOURG \n");
        devis.append("Téléphone        : 09 54 69 23 00\n");
        devis.append("Mail             : contact@BatirandCo-btp.fr\n");
        devis.append("====================================================================================\n\n");

        if (batiments.isEmpty()) {
            devis.append("Aucun bâtiment enregistré.\n");
            return devis.toString();
        }

        Batiment batiment = (Batiment) batiments.get(batiments.size() - 1);

        devis.append("INFORMATIONS GENERALES\n");
        devis.append("------------------------------------------------------------------------------------\n");
        devis.append("Type du bâtiment      : ")
                .append(batiment.getTypeBatiment())
                .append("\n");

        devis.append("Surface totale        : ")
                .append(format(batiment.getSuperficie()))
                .append(" m²\n");

        devis.append("Nombre de niveaux     : ")
                .append(batiment.getNbreNiveaux())
                .append("\n\n");

        devis.append("FACADES EXTERNES\n");
        devis.append("------------------------------------------------------------------------------------\n");

        devis.append(String.format(
                "%-38s %-8s %10s %12s %12s\n",
                "Désignation",
                "Unité",
                "Quantité",
                "Prix U.",
                "Total"
        ));

        devis.append("------------------------------------------------------------------------------------\n");

        double surfaceFacade =
                calculerSurfaceFacade(
                        batiment,
                        hauteurPlafond
                );

        double sousTotalFacade = 0;

        if (batiment.getRevtFacade() != null) {

            sousTotalFacade += ajouterLigne(
                    devis,
                    "Revêtement façade - "
                            + batiment.getRevtFacade().getNomRevt(),
                    "m²",
                    surfaceFacade,
                    batiment.getRevtFacade().getPrixRevt()
            );
        }

        if (batiment.getIsolantExt() != null) {

            sousTotalFacade += ajouterLigne(
                    devis,
                    "Isolant extérieur - "
                            + batiment.getIsolantExt().getNomRevt(),
                    "m²",
                    surfaceFacade,
                    batiment.getIsolantExt().getPrixRevt()
            );
        }

        devis.append("\nSous-total façades : ")
                .append(format(sousTotalFacade))
                .append(" €\n\n");

        totalHT += sousTotalFacade;

        devis.append("INTERIEUR\n");
        devis.append("------------------------------------------------------------------------------------\n");

        if (batiment instanceof Maison) {

            Maison maison = (Maison) batiment;

            totalHT += ajouterAppartement(
                    devis,
                    maison.getPlanMaison(),
                    hauteurPlafond
            );
        }

        if (batiment instanceof Immeuble) {

            Immeuble immeuble = (Immeuble) batiment;

            for (Object objNiveau : immeuble.getNiveaux()) {

                Niveau niveau = (Niveau) objNiveau;

                devis.append("\n");
                devis.append("NIVEAU ")
                        .append(niveau.getNumeroNiveau())
                        .append("\n");

                devis.append("====================================================================================\n");

                for (Object objAppartement : niveau.getAppartements()) {

                    Appartement appartement =
                            (Appartement) objAppartement;

                    totalHT += ajouterAppartement(
                            devis,
                            appartement,
                            hauteurPlafond
                    );
                }
            }

            double prixEscalierUnitaire = 3500;

            double totalEscalier =
                    immeuble.getNbreNiveaux()
                            * prixEscalierUnitaire;

            devis.append("\nESCALIER\n");
            devis.append("------------------------------------------------------------------------------------\n");

            devis.append(String.format(
                    "%-38s %-8s %10s %12s %12s\n",
                    "Désignation",
                    "Unité",
                    "Quantité",
                    "Prix U.",
                    "Total"
            ));

            devis.append("------------------------------------------------------------------------------------\n");

            devis.append(String.format(
                    "%-38s %-8s %10s %12s %12s\n",
                    "Escalier intérieur",
                    "u",
                    immeuble.getNbreNiveaux(),
                    format(prixEscalierUnitaire) + " €",
                    format(totalEscalier) + " €"
            ));

            devis.append("\nSous-total escalier : ")
                    .append(format(totalEscalier))
                    .append(" €\n");

            totalHT += totalEscalier;

            devis.append("\nCOULOIRS\n");
            devis.append("------------------------------------------------------------------------------------\n");

            devis.append("Revêtement sol      : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementSolCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Revêtement plafond  : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementPlafondCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Mur Nord            : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementMurNordCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Mur Est             : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementMurEstCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Mur Sud             : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementMurSudCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Mur Ouest           : ")
                    .append(
                            nomRevetement(
                                    immeuble.getRevetementMurOuestCouloir()
                            )
                    )
                    .append("\n");

            devis.append("Tarif inclus dans le coût global des murs.\n");
        }

        double tva = totalHT * 0.20;
        double totalTTC = totalHT + tva;

        devis.append("\n====================================================================================\n");

        devis.append(
                String.format(
                        "%75s\n",
                        "TOTAL HT : "
                                + format(totalHT)
                                + " €"
                )
        );

        devis.append(
                String.format(
                        "%75s\n",
                        "TVA 20% : "
                                + format(tva)
                                + " €"
                )
        );

        devis.append(
                String.format(
                        "%75s\n",
                        "TOTAL TTC : "
                                + format(totalTTC)
                                + " €"
                )
        );

        devis.append("====================================================================================\n");

        sauvegarderDevis(devis.toString());

        return devis.toString();
    }

    private static double ajouterAppartement(
            StringBuilder devis,
            Appartement appartement,
            double hauteurPlafond
    ) {

        double sousTotalAppartement = 0;

        devis.append("\n");
        devis.append("Appartement / logement : ")
                .append(appartement.getNom())
                .append("\n");

        devis.append("Surface : ")
                .append(format(appartement.getSuperficie()))
                .append(" m²\n");

        devis.append(String.format(
                "%-38s %-8s %10s %12s %12s\n",
                "Désignation",
                "Unité",
                "Quantité",
                "Prix U.",
                "Total"
        ));

        devis.append("------------------------------------------------------------------------------------\n");

        for (Object objPiece : appartement.getPieces()) {

            Piece piece = (Piece) objPiece;

            devis.append("\nPièce : ")
                    .append(piece.getUsage())
                    .append("\n");

            if (piece.getRevetementSol() != null) {

                sousTotalAppartement += ajouterLigne(
                        devis,
                        "Sol - "
                                + piece.getRevetementSol().getNomRevt(),
                        "m²",
                        piece.getSuperficie(),
                        piece.getRevetementSol().getPrixRevt()
                );
            }

            if (piece.getIsolantSol() != null) {

                sousTotalAppartement += ajouterLigne(
                        devis,
                        "Isolant sol - "
                                + piece.getIsolantSol().getNomRevt(),
                        "m²",
                        piece.getSuperficie(),
                        piece.getIsolantSol().getPrixRevt()
                );
            }

            if (piece.getRevetementPlafond() != null) {

                sousTotalAppartement += ajouterLigne(
                        devis,
                        "Plafond - "
                                + piece.getRevetementPlafond().getNomRevt(),
                        "m²",
                        piece.getSuperficie(),
                        piece.getRevetementPlafond().getPrixRevt()
                );
            }

            if (piece.getIsolantPlafond() != null) {

                sousTotalAppartement += ajouterLigne(
                        devis,
                        "Isolant plafond - "
                                + piece.getIsolantPlafond().getNomRevt(),
                        "m²",
                        piece.getSuperficie(),
                        piece.getIsolantPlafond().getPrixRevt()
                );
            }

            for (Object objMur : piece.getMurs()) {

                Mur mur = (Mur) objMur;

                double surfaceMur =
                        mur.calculerSurfaceNette(
                                hauteurPlafond
                        );

                if (mur.getRevetement() != null) {

                    sousTotalAppartement += ajouterLigne(
                            devis,
                            mur.getIdMur()
                                    + " - "
                                    + mur.getRevetement().getNomRevt(),
                            "m²",
                            surfaceMur,
                            mur.getRevetement().getPrixRevt()
                    );
                }

                if (mur.getIsolant() != null) {

                    sousTotalAppartement += ajouterLigne(
                            devis,
                            mur.getIdMur()
                                    + " - isolant "
                                    + mur.getIsolant().getNomRevt(),
                            "m²",
                            surfaceMur,
                            mur.getIsolant().getPrixRevt()
                    );
                }

                for (Object objOuverture : mur.getOuvertures()) {

                    Ouverture ouverture =
                            (Ouverture) objOuverture;

                    if (ouverture.getArticle() != null) {

                        sousTotalAppartement += ajouterLigne(
                                devis,
                                ouverture.getType()
                                        + " - "
                                        + ouverture.getArticle().getNomRevt(),
                                "u",
                                1,
                                ouverture.getArticle().getPrixRevt()
                        );
                    }
                }
            }
        }

        if (
                appartement.getPorteEntree() != null
                        &&
                        appartement.getPorteEntree().getArticle() != null
        ) {

            Ouverture porte =
                    appartement.getPorteEntree();

            sousTotalAppartement += ajouterLigne(
                    devis,
                    "Porte entrée - "
                            + porte.getArticle().getNomRevt(),
                    "u",
                    1,
                    porte.getArticle().getPrixRevt()
            );
        }

        devis.append("\nSous-total logement : ")
                .append(format(sousTotalAppartement))
                .append(" €\n");

        return sousTotalAppartement;
    }

    private static double ajouterLigne(
            StringBuilder devis,
            String designation,
            String unite,
            double quantite,
            double prixUnitaire
    ) {

        double total = quantite * prixUnitaire;

        devis.append(
                String.format(
                        "%-38s %-8s %10s %12s %12s\n",
                        raccourcir(designation),
                        unite,
                        format(quantite),
                        format(prixUnitaire) + " €",
                        format(total) + " €"
                )
        );

        return total;
    }

    private static double calculerSurfaceFacade(
            Batiment batiment,
            double hauteurPlafond
    ) {

        double perimetre =
                2 * (
                        batiment.getXmax()
                                + batiment.getYmax()
                );

        return perimetre * hauteurPlafond;
    }

    private static String nomRevetement(
            Revetement revetement
    ) {

        if (revetement == null) {
            return "aucun";
        }

        return revetement.getNomRevt()
                + " - "
                + format(revetement.getPrixRevt())
                + " €/m²";
    }

    private static void sauvegarderDevis(
            String texteDevis
    ) {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter("devis_batiment.txt")
                    );

            writer.print(texteDevis);

            writer.close();

        } catch (Exception e) {

            System.out.println(
                    "Erreur lors de la sauvegarde du devis."
            );

            e.printStackTrace();
        }
    }

    private static String format(double valeur) {
        return String.format("%.2f", valeur);
    }

    private static String raccourcir(String texte) {

        if (texte.length() > 37) {
            return texte.substring(0, 37);
        }

        return texte;
    }
}