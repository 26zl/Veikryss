package com.g15.veikryss;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import static com.g15.veikryss.App.VINDU_HØYDE; //import av VINDU_HØYDE. Brukes for å tegne veikrysset
import static com.g15.veikryss.App.VEI_BREDDE; //import av VEI_BREDDE. Brukes for å tegne veikrysset
// Importerer retningene for å plassere bilene i veikrysset
import static com.g15.veikryss.App.RETNING_NED;
import static com.g15.veikryss.App.RETNING_OPP;
import static com.g15.veikryss.App.RETNING_VENSTRE;
import static com.g15.veikryss.App.RETNING_HØYRE;

public class Veikryss {

    // Instansvariabler
    private ArrayList<Trafikklys> trafikklysTab = new ArrayList<>();//de fire trafikklysene i veikrysset
    private ArrayList<Bil> bilerTab = new ArrayList<>(); //bilene som tilhører veikrysset
    private ArrayList<StartPosisjon> gyldigeStartPosTab = new ArrayList<>(); //gyldige startposisjoner
    private Pane panel;
    private double veiBredde;
    private double startX, startY; //Brukes for å plassere veikrysset (x og y = midten av veikrysset)

    private double motVenstreY;
    private double motVenstreX;
    private double motHøyreY;
    private double motHøyreX;
    private double motOppY;
    private double motOppX;
    private double motNedY;
    private double motNedX;

    /* Konstruktør for Veikryss
     * Når et nytt veikryss opprettes så sendes det inn:
     * - Hvilket panel det skal legges i
     * - Hvor senter av veikrysset skal være (startX, startY)
     * - Bredde på veien (veiBredde)
     * - Hvor det skal kunne spawne biler fra (spawnTopp, spawnHøyre, spawnBunn, spawnVenstre)
     *   (true/false for hver retning)
     */
    public Veikryss(Pane panel, double startX, double startY, double veiBredde,
                    boolean spawnTopp, boolean spawnHøyre, boolean spawnBunn, boolean spawnVenstre) {
        this.panel = panel;
        this.startX = startX;
        this.startY = startY;
        this.veiBredde = veiBredde;

        // Kall på tre metoder (forklart lenger ned)
        tegnVeier(panel);
        opprettTrafikklys(startX, startY);
        opprettGyldigeStartPosisjoner(startX, startY, spawnTopp, spawnHøyre, spawnBunn, spawnVenstre);
    }

    // Tegner veiene i veikrysset
    // (Originalversjon opprettet av KI, men har blitt justert etterpå)
    private void tegnVeier(Pane pane) {
        int halvLengde = VINDU_HØYDE/2;

        //Tegner veikrysset
        Rectangle vertikalVei = new Rectangle(startX - veiBredde/2, startY - halvLengde, veiBredde, VINDU_HØYDE);
        Rectangle horisontalVei = new Rectangle(startX - halvLengde, startY - veiBredde/2, VINDU_HØYDE, veiBredde);
        vertikalVei.setFill(Color.rgb(105, 105, 105));
        horisontalVei.setFill(Color.rgb(105,105,105));
        pane.getChildren().addAll(vertikalVei, horisontalVei);

        for (int y = (int) startY - halvLengde; y < startY + halvLengde; y += 20) {
            Line vLinje = new Line(startX, y, startX, y + 10);
            vLinje.setStroke(Color.YELLOW);
            vLinje.setStrokeWidth(2);
            pane.getChildren().add(vLinje);
        }

        for (int x = (int) startX - halvLengde; x < startX + halvLengde; x += 20) {
            Line hLinje = new Line(x, startY, x + 10, startY);
            hLinje.setStroke(Color.YELLOW);
            hLinje.setStrokeWidth(2);
            pane.getChildren().add(hLinje);
        }
    }

    // Finner midtsonen (der veiene overlapper i veikrysset)
    // setStroke brukt til å markere/definere midten av krysset
    // slått av og på underveis i koding slik at det bl.a. ble lettere
    // å se hvor grensene til krysset gikk og hvor nær bilene var.
    public Rectangle midtSone() {
        double x = startX - veiBredde / 2;
        double y = startY - veiBredde / 2;
        double lengde = veiBredde;

        Rectangle midten = new Rectangle(x, y, lengde, lengde);
        // midten.setStroke(Color.rgb(0, 0, 0)); // Slå på (fjern kommentarstreker) for å se midten
        midten.setFill(null); // Gjennomsiktig
        panel.getChildren().add(midten);
        return midten;
    }


    // Oppretter fire trafikklys for veikrysset
    private void opprettTrafikklys(double startX, double startY) {
        // Justere avstand, skal endre etterhvert så det blir dynamisk
        double avstand = veiBredde / 2 + (VINDU_HØYDE/20); // NB! skal endre
        double yJustering = -25;   // NB! skal endre (unngå hardkoding)
        double xJustering = -15;   // NB! skal endre (unngå hardkoding)

        // Trafikklysene plasseres rundt veikrysset
        Trafikklys t1 = new Trafikklys(startX + avstand + xJustering, startY - avstand + yJustering, RETNING_HØYRE, 1);  // For biler fra høyre mot venstre
        Trafikklys t2 = new Trafikklys(startX - avstand + xJustering, startY - avstand + yJustering, RETNING_OPP, 1); // For biler som kjører ned
        Trafikklys t3 = new Trafikklys(startX - avstand + xJustering, startY + avstand + yJustering, RETNING_VENSTRE, 1); // For biler fra venstre mot høyre
        Trafikklys t4 = new Trafikklys(startX + avstand + xJustering, startY + avstand + yJustering, RETNING_NED, 1); // For biler som kjører opp

        // Legg til trafikklysene i listen
        trafikklysTab.add(t1);
        trafikklysTab.add(t2);
        trafikklysTab.add(t3);
        trafikklysTab.add(t4);

        // Legg trafikklysene til panelet
        panel.getChildren().addAll(t1.getGruppe(), t2.getGruppe(), t3.getGruppe(), t4.getGruppe());
    }

    // Oppretter gyldige startposisjoner for biler i veikrysset
    // En bil i f.eks. nedre venstre hjørne skal ikke kunne komme (spawne) oppe eller til høyre.
    private void opprettGyldigeStartPosisjoner(double startX, double startY,
                                               boolean spawnTopp, boolean spawnHøyre,
                                               boolean spawnBunn, boolean spawnVenstre) {

        //Justeringer for å plassere bilene i riktig kjørefelt
        double kjørefeltOffset = veiBredde / 4;
        double spawnAvstand = veiBredde + 60;
        double xJustering = 15;
        double yJustering = 30;

        if (spawnTopp) {
            motNedX = (startX  - kjørefeltOffset) - xJustering;
            motNedY = startY - ( 1.5 * spawnAvstand);
            gyldigeStartPosTab.add(new StartPosisjon(motNedX, motNedY, RETNING_NED)); // Nedover
        }
        if (spawnHøyre) {
            motVenstreX = startX + spawnAvstand + (2 * xJustering);
            motVenstreY = startY - kjørefeltOffset - (yJustering); // Justert Y-koordinat
            gyldigeStartPosTab.add(new StartPosisjon(motVenstreX, motVenstreY, RETNING_VENSTRE)); // Høyre
        }
        if (spawnBunn) {
            motOppX = (startX + kjørefeltOffset) - xJustering;
            motOppY = startY + spawnAvstand;
            gyldigeStartPosTab.add(new StartPosisjon(motOppX, motOppY, RETNING_OPP)); // Oppover
        }
        if (spawnVenstre) {
            motHøyreX = startX - (1.5 * spawnAvstand);
            motHøyreY = startY + kjørefeltOffset - yJustering;
            gyldigeStartPosTab.add(new StartPosisjon(motHøyreX, motHøyreY, RETNING_HØYRE)); // Venstre
        }
    }

    // metode som legger til bil i panelet
    public void leggTilBil(Bil bil) {
        bilerTab.add(bil);
        panel.getChildren().add(bil.lagBilGruppe());
    }

    // getMetode for bilerTab
    public ArrayList<Bil> getBilerTab() {
        return bilerTab;
    }

    // getMetode for trafikklysTab
    public ArrayList<Trafikklys> getTrafikklysTab() {
        return trafikklysTab;
    }

    // Spawner en ny bil i veikrysset
    public Bil genererBil() {
        if (gyldigeStartPosTab.isEmpty()) {
            return null;
        }
        int tilfeldig = (int) (Math.random() * gyldigeStartPosTab.size()); // Tilfeldig startposisjon
        StartPosisjon startPos = gyldigeStartPosTab.get(tilfeldig);

        Bil bil = new Bil(
                startPos.getStartX(),
                startPos.getStartY(),
                Color.rgb((int) (Math.random() * 256),
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256)),
                (int) startPos.getRetning(),
                null
        );
        leggTilBil(bil);
        return bil;
    }

    // getMetode for startposisjonX
    public double getX() {
        return startX;
    }

    // getMetode for startPosisjoner
    // (slik at bilen kan legge seg i rikig
    // kjørefelt (koordinater) etter at bilen har svingt.
    public double getY() {
        return startY;
    }
    public double getMotVenstreX() {
        return motVenstreX;
    }
    public double getMotVenstreY() {
        return motVenstreY;
    }
    public double getMotHøyreX() {
        return motHøyreX;
    }
    public double getMotHøyreY() {
        return motHøyreY;
    }
    public double getMotOppX() {
        return motOppX;
    }
    public double getMotOppY() {
        return motOppY;
    }
    public double getMotNedX() {
        return motNedX;
    }
    public double getMotNedY() {
        return motNedY;
    }


    // getMetode for veiBredde
    public double getBredde() {
        return veiBredde;
    }

    // getMetode for tabell med gyldige startposisjoner for det veikrysset
    public ArrayList<StartPosisjon> getGyldigeStartPosTab() {
        return gyldigeStartPosTab;
    }

    // Metode for å håndtere trafikklys og bevegelse
    public void oppdaterTrafikk() {
        Rectangle midtsone = midtSone(); // Hent midtsonen for dette veikrysset

        for (Bil bil : bilerTab) {
            Trafikklys trafikkLys = bil.getTrafikklys(); // Hent bilens tilhørende trafikklys

            if (trafikkLys == null) {
                continue; // Hvis bilen ikke har et trafikklys, hopp over
            }
        }
    }

}