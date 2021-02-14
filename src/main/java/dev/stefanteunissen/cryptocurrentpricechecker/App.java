package dev.stefanteunissen.cryptocurrentpricechecker;

import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    public static final int DELAY = 0;
    public static int PERIOD = 30000;
    public static final int ROUNDING_SCALE = 8;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static void main(String[] args) {
        if (handleArguments(args))
            return;
        PriceChecker priceChecker = new PriceChecker();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    priceChecker.readFile();
                    priceChecker.getPrices();
                    priceChecker.calculateWorths();
                    clearConsole();
                    priceChecker.print();
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        }, DELAY, PERIOD);
    }

    public static boolean handleArguments(String[] args) {
        for (String arg : args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                printHelp();
                return true;
            } else if (arg.startsWith("--period=") || arg.startsWith("-p=")) {
                PERIOD = Integer.parseInt(arg.split("=")[1])*1000;
            }
        }
        return false;
    }

    public static void printHelp() {
        String commandFormat = "  %-20s%s%n";
        System.out.printf(commandFormat, "-p=<seconds>","");
        System.out.printf(commandFormat, "--period=<seconds>","How often the application should refresh");
        System.out.printf(commandFormat, "-h","");
        System.out.printf(commandFormat, "--help","Show this information");
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
