package dev.stefanteunissen.cryptocurrentpricechecker;

import java.util.Comparator;

public class PositionNameComparator implements Comparator<Position> {
    @Override
    public int compare(Position position, Position t1) {
        return position.getMarket().compareTo(t1.getMarket());
    }
}
