package com.g15.veikryss;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

//Importerer vinduets breddefor å tegne passende trafikklystørrelse
import static com.g15.veikryss.App.VEI_BREDDE;

public class Trafikklys extends Figur{
    private Group gruppe = new Group(); //Gruppe som inneholder alle elementene i trafikklyset
    private Rectangle trafikklysBoks;   //Boksen til trafikklyset
    private Circle rød, gul, grønn;     //Det tre lysene (Rødt, gult og grønt)
    private int status; //status 0 = rødt, 1 = gult, 2 = grønt
    private double vinkel; // vinkel på trafikklyset

    public Trafikklys(double xPos, double yPos, double vinkel, int status) {
        super(xPos, yPos);
        this.vinkel = vinkel;
        this.status = status;

        double bredde = VEI_BREDDE/4;
        double høyde = VEI_BREDDE/2;
        double padding = 13;

        trafikklysBoks = new Rectangle(bredde, høyde-2, Color.BLACK);
        trafikklysBoks.setStroke(Color.rgb(120,120,120));
        trafikklysBoks.setArcWidth(12); //Runde kanter
        trafikklysBoks.setArcHeight(12); //Runde kanter

        //tegner rødt og grønt lys
        rød = new Circle(bredde/2, bredde/6+(padding)-5, bredde/4);
        gul = new Circle(bredde/2, bredde/6+(padding*2)-5, bredde/4);
        grønn = new Circle(bredde/2, bredde/6+(padding*3)-5, bredde/4);

        //Gir dem farge (+ mørkegrå kant)
        rød.setFill(Color.rgb(255, 0, 0));
        rød.setStroke(Color.rgb(60, 60, 60));
        gul.setFill(Color.rgb(255,255,0));
        gul.setStroke(Color.rgb(60, 60, 60));
        grønn.setFill(Color.rgb(9, 220, 0));
        grønn.setStroke(Color.rgb(60, 60, 60));

        //Plasserer alle elementene i gruppen
        gruppe.getChildren().addAll(trafikklysBoks, rød, gul, grønn);

        // Setter opp lysene i riktig posisjon og vinkel
        gruppe.setLayoutX(xPos); // punkt på X-aksen
        gruppe.setLayoutY(yPos); // punkt på Y-aksen
        gruppe.setRotate(vinkel); // Basert på veien det hører til
    }

    //metode som endrer status (farge) på lysene
    public void endreStatus() {
        status = (status + 1) % 3; // Loop (0 -> 1 -> 2 -> 0, osv.)
        oppdaterLys(); // Oppdaterer lysene basert på ny status
    }

    //metode som oppdaterer lysene basert på status
    private void oppdaterLys() {
        try {
            if (status == 0) { // Rødt lys
                rød.setFill(Color.rgb(255, 0, 0)); // På
                gul.setFill(Color.rgb(64, 64, 5)); // Av
                grønn.setFill(Color.rgb(9, 64, 0));  // Av
            } else if (status == 1) { // Gult lys
                rød.setFill(Color.rgb(120, 0, 0)); // Av
                gul.setFill(Color.rgb(255, 255, 0)); // På
                grønn.setFill(Color.rgb(9, 64, 0)); // Av
            } else if (status == 2) { // Grønt lys
                rød.setFill(Color.rgb(120, 0, 0)); // Av
                gul.setFill(Color.rgb(64, 64, 0)); // Av
                grønn.setFill(Color.rgb(17, 235, 0)); // På
            }
        } catch (Exception e) {
            System.err.println("Feil ved oppdatering av lys" + e.getMessage());
        }
    }

    //getMetode for gruppe (henter selve lyset)
    // pga. lagTrafikklys()-metoden ble flyttet inn i konstruktøren.
    public Group getGruppe() {
        return gruppe;
    }

    //setMetode for status
    public void setStatus(int nyStatus) {
        this.status = nyStatus;
        oppdaterLys();
    }

    //getMetode for trafikklys
    public Trafikklys getTrafikklys() {
        return this;
    }

    //getMetode for status
    public int getStatus() {
        return status;
    }

    //getMetode for vinkel
    public int getVinkel() {
        return (int) vinkel;
    }

    //metode som returngerer hele trafikklysgruppen
    @Override
    public Group getFigur() {
        return gruppe;
    }
}