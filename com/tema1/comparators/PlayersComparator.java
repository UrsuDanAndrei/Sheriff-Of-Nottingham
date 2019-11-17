package com.tema1.comparators;

import com.tema1.players.Player;

import java.util.Comparator;

public final class PlayersComparator implements Comparator<Player> {
    @Override
    public int compare(final Player a, final Player b) {
        if (a.getMoney() == b.getMoney()) {
            return a.getId() - b.getId();
        } else {
            return b.getMoney() - a.getMoney();
        }
    }
}
