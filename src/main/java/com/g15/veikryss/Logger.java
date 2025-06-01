package com.g15.veikryss;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Logger {
    private TextArea loggOmråde;
    private int maksLinjer; // Maksimalt antall linjer i loggen

    public Logger(TextArea loggOmråde) {
        this(loggOmråde, 1000); // Standard maks 1000 linjer
    }

    public Logger(TextArea loggOmråde, int maksLinjer) {
        this.loggOmråde = loggOmråde;
        this.loggOmråde.setEditable(false);
        this.maksLinjer = maksLinjer;
    }

    public void logg(String melding) {
        if (melding == null) {
            melding = "NULL";
        }
        final String logMessage = melding;
        Platform.runLater(() -> {
            try {
                // Legger til meldingen
                loggOmråde.appendText(logMessage + "\n");
                // Begrenser antall linjer i loggen
                begrensMaksLinjer();
                // Ruller loggområdet til bunnen for å vise nyeste melding
                loggOmråde.setScrollTop(Double.MAX_VALUE);
            } catch (Exception e) {
                System.err.println("En feil har oppstått" + e.getMessage());
            }
        });
    }

    private void begrensMaksLinjer() {
        try {
            String tekst = loggOmråde.getText();
            String[] linjer = tekst.split("\n");

            if (linjer.length > maksLinjer) {
                // Bygger ny tekst med bare de nyeste linjene
                StringBuilder nyTekst = new StringBuilder();
                for (int i = linjer.length - maksLinjer; i < linjer.length; i++) {
                    nyTekst.append(linjer[i]).append("\n");
                }
                // Oppdaterer loggområdet
                loggOmråde.setText(nyTekst.toString());
            }
        } catch (Exception e) {
            System.err.println("En feil har oppstått" + e.getMessage());
        }
    }
}