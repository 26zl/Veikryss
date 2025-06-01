package com.g15.veikryss;

/*
 * Denne klassen er laget for å lagre ulike startposisjoner til biler,
 * samt retning i form av grader. Det er opprettet fire forskjellige
 * startposisjoner i App.java. En for hver vei og i 0, 90, 180, og 270 graders vinkel.
 *
 * */

public class StartPosisjon {
    private double startX;
    private double startY;
    private double retning;

    public StartPosisjon(double startX, double startY, double retning) {
        this.startX = startX;
        this.startY = startY;
        this.retning = retning;
    }

    public double getStartX() {
        return this.startX;
    }

    public double getStartY() {
        return this.startY;
    }

    public double getRetning() {
        return retning;
    }
}