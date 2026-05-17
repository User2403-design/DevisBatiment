//module pif.devisbatimenthamonnopre {
//    requires javafx.controls;
//   
//    opens Controleur;
//    opens Vue;
//    opens Modele;
//
//    exports pif.devisbatimenthamonnopre;
//}

module DevisBatimentHamonNOPRE {
    requires javafx.controls;
    
    exports Modele;
    exports Controleur;
    exports Vue;
    exports pif.devisbatimenthamonnopre;
}