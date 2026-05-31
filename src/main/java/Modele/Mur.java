
package Modele;

import java.util.ArrayList;
import java.util.List;

public class Mur {

    private String idMur;
    private Point p1;
    private Point p2;
    private Revetement revetement;
    private Revetement isolant;
    private ArrayList<Ouverture> ouvertures;
    private boolean estExterieur;

    public Mur(String id, Point p1, Point p2, boolean estExterieur) {
        this.idMur = id;
        this.p1 = p1;
        this.p2 = p2;
        this.estExterieur = estExterieur;
        this.ouvertures = new ArrayList<>();
    }

    public double calculerLongueur() {
        return Math.sqrt(Math.pow(getX2() - getX1(), 2) + Math.pow(getY2() - getY1(), 2));
    }

    //nouvelle surface en incluant les ouvertures
    public double calculerSurfaceNette(double hauteurPlafond) {
        double surfaceBrute = calculerLongueur() * hauteurPlafond;
        double surfaceOuvertures = 0;
        for (Ouverture o : ouvertures) {
            surfaceOuvertures += (o.getLargeur() * o.getHauteur());
        }
        return Math.max(0, surfaceBrute - surfaceOuvertures);
    }

    public double calculerPrix(double hauteurPlafond) {
        double prix = 0;
        double surface = calculerSurfaceNette(hauteurPlafond);
        
        if (revetement != null) {
            prix += surface * revetement.getPrixRevt();
        }
        
        if (isolant != null) {
            prix += surface * isolant.getPrixRevt();
        }
        
        return prix;
    }

    @Override
    public String toString() {
        String type = estExterieur ? " [Façade]" : " [Intérieur]";
        return idMur + type + " - " + String.format("%.2f", calculerSurfaceNette(2.5)) + " m²";
    }

    
    public float getX1() { 
        return p1.getX(); 
    }
    
    public float getY1() { 
        return p1.getY(); 
    }
    
    public float getX2() { 
        return p2.getX(); 
    }
    
    public float getY2() { 
        return p2.getY(); 
    }
    
    public String getIdMur() { 
        return idMur; 
    }
    
    public void setIdMur(String idMur) { 
        this.idMur = idMur; 
    }
    
    public Point getP1() { 
        return p1; 
    }
    
    public Point getP2() { 
        return p2; 
    }
    
    public Revetement getRevetement() { 
        return revetement; 
    }
    
    public void setRevetement(Revetement r) { 
        this.revetement = r; 
    }
    
    public Revetement getIsolant() { 
        return isolant; 
    }
    
    public void setIsolant(Revetement isolant) { 
        this.isolant = isolant; 
    }
    
    public boolean estExterieur() { 
        return estExterieur; 
    }
    
    public void setEstExterieur(boolean estExterieur) { 
        this.estExterieur = estExterieur; 
    }
    
    public void ajouterOuverture(Ouverture o) { 
        this.ouvertures.add(o); 
    }
    
    public List<Ouverture> getOuvertures() { 
        return ouvertures; 
    }
}
