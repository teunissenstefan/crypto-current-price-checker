package dev.stefanteunissen.cryptocurrentpricechecker;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    public static final int DELAY = 0;
    public static final int PERIOD = 30000;
    public static final int ROUNDING_SCALE = 8;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static void main(String[] args) {
        PriceChecker priceChecker = new PriceChecker();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    priceChecker.readFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                priceChecker.getPrices();
                priceChecker.calculateWorths();
                clearConsole();
                priceChecker.print();
            }
        }, DELAY, PERIOD);
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
            }
        } catch (final Exception ignored) {
        }
    }

}
