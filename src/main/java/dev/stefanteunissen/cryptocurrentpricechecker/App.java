package dev.stefanteunissen.cryptocurrentpricechecker;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class App {
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
        }, 0, 30000);
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
