module pif.devisbatimenthamonnopre {
    requires javafx.controls;
   
    opens Controleur;
    opens Vue;
    opens Modele;

    exports pif.devisbatimenthamonnopre;
}