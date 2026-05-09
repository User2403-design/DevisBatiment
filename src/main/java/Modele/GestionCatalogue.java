/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Modele;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de gestion du catalogue avec diagnostic intégré.
 */
public class GestionCatalogue {
    
    private Map<String, List<Revetement>> rubriques;
    // Note : Pour Maven, le fichier doit être dans src/main/resources
    private static final String CHEMIN_CATALOGUE = "/Catalogue.txt";
    
    public GestionCatalogue() {
        this.rubriques = new HashMap<>();
        chargerDonnees();
    }
    
    private void chargerDonnees() {
        System.out.println("Tentative de chargement du catalogue depuis : " + CHEMIN_CATALOGUE);
        
        try (InputStream is = getClass().getResourceAsStream(CHEMIN_CATALOGUE);
             BufferedReader reader = (is == null) ? null : new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                System.err.println("CRITIQUE : Fichier " + CHEMIN_CATALOGUE + " introuvable dans le classpath !");
                System.err.println("Vérifiez qu'il est bien dans src/main/resources/ et non dans un dossier ressources/ à la racine.");
                return;
            }

            String ligne;
            String rubriqueCourante = "Divers";
            int compteurId = 1; 
            int lignesLues = 0;

            while ((ligne = reader.readLine()) != null) {
                lignesLues++;
                ligne = ligne.trim();
                
                if (ligne.isEmpty() || ligne.startsWith("//")) continue;

                // Détection de la rubrique (ex: # Revetement)
                if (ligne.startsWith("#") || ligne.startsWith("Rubrique:")) {
                    if (ligne.startsWith("#")) {
                        rubriqueCourante = ligne.substring(1).trim();
                    } else {
                        rubriqueCourante = ligne.substring(9).trim();
                    }
                    rubriques.putIfAbsent(rubriqueCourante, new ArrayList<>());
                    System.out.println("Rubrique trouvée : [" + rubriqueCourante + "]");
                } 
                else {
                    String[] data = ligne.split(";");
                    if (data.length >= 3) {
                        try {
                            String categorieData = data[0].trim();
                            String nom = data[1].trim();
                            float prix = Float.parseFloat(data[2].trim());

                            boolean estUnIsolant = categorieData.equalsIgnoreCase("Isolant");

                            Revetement rev = new Revetement(compteurId++, nom,  estUnIsolant, prix);
                            
                            if (rubriques.containsKey(rubriqueCourante)) {
                                rubriques.get(rubriqueCourante).add(rev);
                            } else {
                                // Cas où des données sont présentes avant toute rubrique
                                rubriques.putIfAbsent("Divers", new ArrayList<>());
                                rubriques.get("Divers").add(rev);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Ligne " + lignesLues + " : Erreur de prix -> " + ligne);
                        }
                    }
                }
            }
            System.out.println("Chargement terminé. " + rubriques.size() + " rubriques créées.");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Revetement> getProduits(String nomRubrique) {
        List<Revetement> liste = rubriques.get(nomRubrique);
        if (liste == null || liste.isEmpty()) {
            // C'est ici que votre message d'erreur est généré
            System.err.println("ERREUR : La rubrique '" + nomRubrique + "' est inconnue ou vide.");
            System.err.println("Rubriques disponibles dans le système : " + rubriques.keySet());
            return new ArrayList<>();
        }
        return liste;
    }

    public List<String> getNomsRubriques() {
        return new ArrayList<>(rubriques.keySet());
    }

    public Revetement trouverParId(int id) {
        for (List<Revetement> liste : rubriques.values()) {
            for (Revetement r : liste) {
                if (r.getIdRevt() == id) return r;
            }
        }
        return null;
    }
}

