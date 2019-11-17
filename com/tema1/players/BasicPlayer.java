package com.tema1.players;

import java.util.List;

public final class BasicPlayer extends Player {

    public BasicPlayer(final int id) {
        super(PlayersType.BASIC, id);
    }

    // chooses the goods to be put in the bag based on the basic strategy
    @Override
    public void formBag(final int round) {
        basicStrategyBag();
    }

    @Override
    public void checkMerchants(final List<Player> players, final List<Integer> pileGoodsIds) {
        // the basic player checks all merchants that he can afford to
        for (Player merchant : players) {
            // the sheriff player is skipped
            if (merchant.getId() == this.id) {
                continue;
            }

            checkMerchant(merchant, pileGoodsIds);
        }
    }
}
