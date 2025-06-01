package com.g15.veikryss;

/* Dette er en klasse som inneholder logikk for lysene i veikrysset.
 *
 *  NB! Biler som kommer fra høyre responderer ikke på trafikklyset! (Må fikses)
 */

// Importerer retningene for å plassere bilene i veikrysset
import static com.g15.veikryss.App.RETNING_NED;
import static com.g15.veikryss.App.RETNING_OPP;
import static com.g15.veikryss.App.RETNING_VENSTRE;
import static com.g15.veikryss.App.RETNING_HØYRE;

public class Lyslogikk {

    // Sjekker om bilen er nær veikrysset (sentrert rundt startX og startY)
    public static boolean bilenErNærKrysset(Bil bil, Veikryss veikryss) {
        try {
            double avstand = 50;
            double vinkel = bil.getVinkel();
            double bilX = bil.getXPos();
            double bilY = bil.getYPos();
            double kryssX = veikryss.getX();
            double kryssY = veikryss.getY();

            if (vinkel == RETNING_NED) {
                bilY += bil.getHøyde(); // Bilen er på vei nedover, så vi må justere Y-posisjonen
            } else if (vinkel == RETNING_VENSTRE) {
                bilX -= (bil.getBredde()-15); // Bilen er på vei til venstre, så vi må justere X-posisjonen
                bilY += bil.getHøyde() / 2; // NB ta hensyn til bilens høyde
            } else if (vinkel == RETNING_HØYRE) {
                bilX += (bil.getHøyde()-15); // Bilen er på vei til høyre, så vi må justere X-posisjonen
            }
            return Math.abs(bilX -kryssX) < avstand && Math.abs(bilY - kryssY) < avstand;
        } catch (Exception e) {
            System.err.println("Feil ved kontroll" + e.getMessage());
            return false;
        }
    }


    // Finner det lyset som bilen skal respondere til
    public static Trafikklys finnRelevantLys(Bil bil, Veikryss veikryss) {
        try {
            int retning = (int) bil.getVinkel();
            switch (retning) {
                case RETNING_NED: return veikryss.getTrafikklysTab().get(1); // Retning nedover
                case RETNING_HØYRE: return veikryss.getTrafikklysTab().get(2); // Retning høyre
                case RETNING_OPP: return veikryss.getTrafikklysTab().get(3); // Retning oppover
                case RETNING_VENSTRE: return veikryss.getTrafikklysTab().get(0); // Retning venstre
                default: return null; // Ingen lys funnet
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Feil ved indeks av trafikklys: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Feil ved kontroll: " + e.getMessage());
            return null;
        }
    }

    // Metode for å sjekke om bilen er inne i krysset.
    // Om lyset blir gult mens bilen er i krysset, la bilen "fullføre"/kjøre videre.
    // Om lyset blir gult før bilen er i krysset, stopp bilen.
    public static boolean bilErInneIKrysset(Bil bil, Veikryss veikryss) {
        double bilX = bil.getFigur().getTranslateX();
        double bilY = bil.getFigur().getTranslateY();
        double kryssX = veikryss.getX();
        double kryssY = veikryss.getY();
        int bredde = (int) veikryss.getBredde();

        boolean inneIKrysset = (bilX >= kryssX && bilX <= kryssX + bredde &&
                bilY >= kryssY && bilY <= kryssY + bredde);

        if (inneIKrysset) {
            return true;
        }
        return false;
    }

}