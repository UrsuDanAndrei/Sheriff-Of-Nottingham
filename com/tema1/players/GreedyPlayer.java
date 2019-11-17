package com.tema1.players;

import com.tema1.common.Bag;
import com.tema1.common.Constants;
import com.tema1.comparators.GreedyPlayerGoodsComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.List;
import java.util.Collections;

public final class GreedyPlayer extends Player {

    public GreedyPlayer(final int id) {
        super(PlayersType.GREEDY, id);
    }

    @Override
    public void formBag(final int round) {
        basicStrategyBag();

        if (round % 2 == 0 && bag.getSize() < Constants.MAX_BAG_SIZE) {
            List<Integer> bagGoodsIds = bag.getBagGoodsIds();
            int existingGoodId = bagGoodsIds.get(0);

            Collections.sort(handGoodsIds, new GreedyPlayerGoodsComparator());
            Goods first = GoodsFactory.getInstance().getGoodsById(handGoodsIds.get(0));
            Goods second = GoodsFactory.getInstance().getGoodsById(handGoodsIds.get(1));

            if (first.getType() == GoodsType.Illegal) {
                if (first.getId() != existingGoodId) {
                    bagGoodsIds.add(first.getId());
                    bag.setBagGoodsIds(bagGoodsIds);
                } else if (second.getType() == GoodsType.Illegal) {
                    bagGoodsIds.add(second.getId());
                    bag.setBagGoodsIds(bagGoodsIds);
                }
            }
       }
    }

    @Override
    public void checkMerchants(final List<Player> players, final List<Integer> pileGoodsIds) {
        for (Player merchant : players) {
            // the sheriff player is skipped
            if (merchant.getId() == this.id) {
                continue;
            }

            Bag bag = merchant.getBag();
            int bribe = bag.getBribe();
            // if there is no bribe the greedy player will check the bag
            if (bribe == 0) {
                checkMerchant(merchant, pileGoodsIds);
            } else {
                // the bribe is given to sheriff
                this.money += bribe;
                merchant.setMoney(merchant.getMoney() - bribe);

                merchant.addBagToBooth();
            }
        }
    }
}
