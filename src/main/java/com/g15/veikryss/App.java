package com.g15.veikryss;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaFX App
 */
public class App extends Application {
    //Her står globale variabler/konstanter
    public ExecutorService executor = Executors.newCachedThreadPool(); //har ikke brukt enda
    public final static int VINDU_BREDDE = 800; //Vinduets bredde i px
    public final static int VINDU_HØYDE = 600; //Vinduets høyde i px
    public final static int VEI_BREDDE = 100; //Vinduets bredde i px

    // Konstanter for retningene
    final static int RETNING_NED = 0; // Beveger seg nedover
    final static int RETNING_VENSTRE = 90; // Beveger seg til venstre
    final static int RETNING_OPP = 180; // Beveger seg oppover
    final static int RETNING_HØYRE = 270; // Beveger seg til høyre

    private Pane hovedpanel = new Pane(); //Hovedpanelet (Der alt legges til)
    private ArrayList<Bil> bilerTab = new ArrayList<>();
    private final int margin = 10;
    private ArrayList<Veikryss> veikryssTab = new ArrayList<>();
    private ArrayList<Trafikklys> nordSørLys = new ArrayList<>();
    private ArrayList<Trafikklys> østVestLys = new ArrayList<>();
    private Logger logger; // Ny logg-komponent

    @Override
    public void start(Stage stage) throws IOException {
        // Tegner gress (bakgrunn i vinduet)
        Rectangle gress = new Rectangle(0, 0, VINDU_BREDDE, VINDU_HØYDE);
        gress.setFill(Color.GREEN);
        hovedpanel.getChildren().add(gress);

        // Logger
        TextArea loggOmråde = new TextArea();
        loggOmråde.setPrefWidth(250);
        logger = new Logger(loggOmråde);

        // Tegner veikryssene
        veikryssTab.add(new Veikryss(hovedpanel, 200, 150, VEI_BREDDE, true, false, false, true)); // Øvre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 150, VEI_BREDDE, true, true, false, false)); // Øvre høyre
        veikryssTab.add(new Veikryss(hovedpanel, 200, 450, VEI_BREDDE, false, false, true, true)); // Nedre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 450, VEI_BREDDE, false, true, true, false)); // Nedre høyre

        // Start trafikklys og bil-logikk for hvert kryss
        for (Veikryss kryss : veikryssTab) {
            startTrafikklysLogikk();
            startBilLogikk(kryss);
        }

        // Start trafikkoppdatering
        oppdaterTrafikkLogikk();

        // Flytt biler
        flyttBil();

        //Fjerner biler som fortsetter utenfor kartet
        fjernBilerUtenforKartet();

        // Starter statistikklogging
        loggStatistikk();

        //Setter opp panel
        hovedpanel.setPrefSize(VINDU_BREDDE, VINDU_HØYDE);
        loggOmråde.setPrefSize(250, VINDU_HØYDE);
        HBox hovedPanel = new HBox(hovedpanel, loggOmråde);

        //Setter opp scenen
        Scene scene = new Scene(hovedPanel, VINDU_BREDDE + 250, VINDU_HØYDE);
        stage.setScene(scene);
        stage.setTitle("Veikryss");
        stage.setResizable(false);
        stage.show();

        logger.logg("Simulering startet med " + veikryssTab.size() + " veikryss");
        logger.logg("Trafikksimulering klar - " + new Date());
    }

    public static void main(String[] args) {
        launch();
    }

    private void startTrafikklysLogikk() {
        // Del trafikklysene inn i grupper basert på retning
        for (Veikryss kryss : veikryssTab) {
            for (Trafikklys lys : kryss.getTrafikklysTab()) {
                if (lys.getVinkel() == 0 || lys.getVinkel() == 180) { // Nord/Sør
                    nordSørLys.add(lys);
                } else if (lys.getVinkel() == 90 || lys.getVinkel() == 270) { // Øst/Vest
                    østVestLys.add(lys);
                }
            }
        }

        // Starter en tråd for å oppdatere trafikklysene
        new Thread(() -> {
            try {
                while (true) {
                    // 💡 Grønt for nord/sør – rødt for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(2); // Grønt
                        for (Trafikklys lys : østVestLys) lys.setStatus(0); // Rødt
                        logger.logg("Trafikklys: Nord/Sør = GRØNT, Øst/Vest = RØDT");
                    });
                    Thread.sleep(5000);

                    // 💡 Gult for nord/sør
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(1); // Gult
                        logger.logg("Trafikklys: Nord/Sør = GULT");
                    });
                    Thread.sleep(2000);

                    // 💡 Rødt for nord/sør – grønt for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(0); // Rødt
                        for (Trafikklys lys : østVestLys) lys.setStatus(2); // Grønt
                        logger.logg("Trafikklys: Nord/Sør = RØDT, Øst/Vest = GRØNT");
                    });
                    Thread.sleep(5000);

                    // 💡 Gult for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : østVestLys) lys.setStatus(1); // Gult
                        logger.logg("Trafikklys: Øst/Vest = GULT");
                    });
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Metode som starter bil-logikken for hvert veikryss
    private void startBilLogikk(Veikryss kryss) {
        new Thread(() -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        Bil nyBil = kryss.genererBil(); // Generer en ny bil
                        if (nyBil != null) {
                            synchronized (bilerTab) {
                                bilerTab.add(nyBil); // Legg til bilen i bilerTab
                                logger.logg("Bil generert ved kryss (" + kryss.getX() + ", " + kryss.getY() + ")");
                                logger.logg("Totalt antall biler: " + bilerTab.size());
                            }
                        }
                    });
                    Thread.sleep(5000); // Juster bilspawn-intervall her
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /*Denne metoden flytter bilene. Alle bilene som opprettes i
     * tegnbiler() metoden legges i bilerTab og i tråden under
     * flyttes alle biler som ligger i bilerTab kontinuerlig ved
     * bruk av en foor-løkke og flyttbil() metoden
     * som ligger i Bil klassen. Farten på bilene kan justeres i
     * Thread.sleep(antal millisekund). Jo færre millisekund jo
     * raskere */
    private void flyttBil() {
        //flytt bil
        new Thread(() -> {
            try {
                while (true) {
                    synchronized (bilerTab) {
                        for (Bil b : bilerTab) {
                            for (Veikryss kryss : veikryssTab) {
                                if (Lyslogikk.bilenErNærKrysset(b, kryss)) {
                                    // Hent relevant trafikklys og sjekk status
                                    Trafikklys relevantLys = Lyslogikk.finnRelevantLys(b, kryss);
                                    int status = relevantLys.getStatus();
                                    if (status == 0 || status == 1) { // Rødt eller gult lys
                                        if (!b.erStoppet()) {
                                            b.stoppVedRødtLys(); // Stopp bilen
                                            logger.logg("Bil stoppet ved " + (status == 0 ? "RØDT" : "GULT") +
                                                    " lys ved kryss (" + kryss.getX() + ", " + kryss.getY() + ")");
                                        }
                                    } else {
                                        if (b.erStoppet()) {
                                            b.startVedGrøntLys(); // Fortsett å kjøre
                                            b.sving(relevantLys); // Sving bilen
                                            logger.logg("Bil kjører videre på GRØNT lys ved kryss (" +
                                                    kryss.getX() + ", " + kryss.getY() + ")");
                                        }
                                    }
                                }
                            }
                            Platform.runLater(() -> b.flyttBil());
                        }
                    }
                    Thread.sleep(8); //juster farten her
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // metode som oppdaterer trafikklogikken for alle veikryss
    private void oppdaterTrafikkLogikk() {
        new Thread(() -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        for (Veikryss kryss : veikryssTab) {
                            kryss.oppdaterTrafikk(); // Oppdater trafikken for hvert veikryss
                        }
                    });
                    Thread.sleep(100); // Juster oppdateringsfrekvensen (100 ms her)
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Metode for å sjekke og slette biler som er utenfor kartet
    private void fjernBilerUtenforKartet() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(2000); // Sjekk hvert 2. sekund
                    Platform.runLater(() -> {
                        synchronized (bilerTab) {
                            // Lager en liste for biler som skal fjernes
                            ArrayList<Bil> bilTilFjerning = new ArrayList<>();
                            for (Bil b : bilerTab) {
                                // Sjekker om bilen er utenfor kartets grenser
                                double x = b.getXPos();
                                double y = b.getYPos();

                                // Grense på 200px
                                boolean utenforKartet = x < -200 || x > VINDU_BREDDE + 200 ||
                                        y < -200 || y > VINDU_HØYDE + 200;

                                if (utenforKartet) {
                                    bilTilFjerning.add(b);
                                    hovedpanel.getChildren().remove(b.getFigur()); // Fjern fra GUI
                                }
                            }

                            // Fjerner bilene fra listen
                            if (!bilTilFjerning.isEmpty()) {
                                bilerTab.removeAll(bilTilFjerning);
                                //logger.logg("Fjernet " + bilTilFjerning.size() + " biler utenfor kartet");
                                //Trenger ikke å logge dette
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Metode for å logge statistikk om simuleringen
    private void loggStatistikk() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(10000); // Hvert 10. sekund
                    Platform.runLater(() -> {
                        synchronized (bilerTab) {
                            int antallStoppedeBiler = 0;
                            int totaltAntallBiler = bilerTab.size();

                            for (Bil b : bilerTab) {
                                if (b.erStoppet()) {
                                    antallStoppedeBiler++;
                                }
                            }

                            int prosentStopp = (totaltAntallBiler > 0) ?
                                    (antallStoppedeBiler * 100 / totaltAntallBiler) : 0;

                            logger.logg("STATISTIKK: " + totaltAntallBiler + " biler i trafikken, " +
                                    antallStoppedeBiler + " står stille (" + prosentStopp + "%)");
                        }
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}