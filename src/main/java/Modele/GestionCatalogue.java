/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author seb12
 */

//Classe du Modèle représentant le catalogue complet des matériaux.
//Elle charge les données depuis le fichier texte et les organise par rubriques.
public class GestionCatalogue {
    
    // On utilise une Map pour classer les revêtements par nom de rubrique (ex: "Sols", "Murs")
    private Map<String, List<Revetement>> rubriques;
    private static final String CHEMIN_CATALOGUE = "/Catalogue.txt";
    
    public GestionCatalogue() {
    this.rubriques = new HashMap<>();
    chargerDonnees();
    }
    
    
//Méthode interne pour charger le fichier Catalogue.txt situé dans les ressources
private void chargerDonnees() {
    try (InputStream is = getClass().getResourceAsStream(CHEMIN_CATALOGUE);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

        if (is == null) {
            System.err.println("Erreur : Fichier Catalogue.txt introuvable dans les ressources.");
            return;
        }

        String ligne;
        String rubriqueCourante = "Divers";

        while ((ligne = reader.readLine()) != null) {
            ligne = ligne.trim();

            // On ignore les lignes vides et les commentaires
            if (ligne.isEmpty() || ligne.startsWith("#")) continue;

            // Détection d'un changement de categorie
            if (ligne.startsWith("Rubrique:")) {
                rubriqueCourante = ligne.substring(9).trim();
                rubriques.putIfAbsent(rubriqueCourante, new ArrayList<>());
            } 
            // Extraction d'un produit (id;nom;unité;prix) : 4 données qui le caracterisent
            else {
                String[] data = ligne.split(";");
                if (data.length >= 4) {
                    try {
                        int id = Integer.parseInt(data[0]);
                        String nom = data[1];
                        String unite = data[2];
                        float prix = Float.parseFloat(data[3]);

                        Revetement rev = new Revetement(id, nom, unite, prix);
                        rubriques.get(rubriqueCourante).add(rev);
                    } catch (NumberFormatException e) {
                        System.err.println("Ligne mal formée dans le catalogue : " + ligne);
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

/**
 * Permet de récupérer la liste des revêtements pour une rubrique donnée.
 * @param nomRubrique Le nom de la rubrique (ex: "Revetements interieurs")
 * @return La liste des produits ou une liste vide si la rubrique n'existe pas.
 */
public List<Revetement> getProduits(String nomRubrique) {
    return rubriques.getOrDefault(nomRubrique, new ArrayList<>());
}

/**
 * Retourne la liste de tous les noms de rubriques disponibles.
 */
public List<String> getNomsRubriques() {
    return new ArrayList<>(rubriques.keySet());
}

/**
 * Recherche un revêtement spécifique par son ID (utile pour la sauvegarde/chargement).
 */
public Revetement trouverParId(int id) {
    for (List<Revetement> liste : rubriques.values()) {
        for (Revetement r : liste) {
            if (r.getIdRevt() == id) return r;
        }
    }
    return null;
}
}
    
    

