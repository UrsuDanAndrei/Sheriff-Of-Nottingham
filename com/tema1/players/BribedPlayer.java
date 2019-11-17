package com.tema1.players;

import com.tema1.common.Bag;
import com.tema1.common.Constants;
import com.tema1.comparators.BribedPlayerGoodsComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class BribedPlayer extends Player {
    public BribedPlayer(final int id) {
        super(PlayersType.BRIBED, id);
    }

    private boolean illegalGoodsExistInHand() {
        for (int goodId : handGoodsIds) {
            if (GoodsFactory.getInstance().getGoodsById(goodId).getType() == GoodsType.Illegal) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void formBag(final int round) {
        List<Integer> bagGoodsIds = new ArrayList<Integer>();
        Collections.sort(handGoodsIds, new BribedPlayerGoodsComparator());

        // if no illegal goods can be added to bag, the player will follow the basic strategy
        if (this.illegalGoodsExistInHand() && this.money > Constants.SMALL_BRIBE) {
            int possiblePenalty = 0;
            int noIllegalGoods = 0;

            for (int goodId : handGoodsIds) {
                Goods good = GoodsFactory.getInstance().getGoodsById(goodId);

                // checks if the merchant can afford the penalty if his bag is checked
                if (possiblePenalty + good.getPenalty() < this.money) {
                    if (good.getType() == GoodsType.Illegal) {
                        // the merchant will take only as many illegal items as he can pay bribe for
                        if (noIllegalGoods >= Constants.MAX_NO_ILLEGAL_GOODS_SMALL_BRIBE
                                && this.money <= Constants.BIG_BRIBE) {
                            continue;
                        }
                        ++noIllegalGoods;
                    }

                    possiblePenalty += good.getPenalty();
                    bagGoodsIds.add(goodId);
                }

                if (bagGoodsIds.size() == Constants.MAX_BAG_SIZE) {
                    break;
                }
            }

            // sets up the bag
            bag.setBagGoodsIds(bagGoodsIds);
            bag.setDeclaredGoodId(0);

            // sets up the bribe
            if (noIllegalGoods > Constants.MAX_NO_ILLEGAL_GOODS_SMALL_BRIBE) {
                bag.setBribe(Constants.BIG_BRIBE);
            } else {
                bag.setBribe(Constants.SMALL_BRIBE);
            }
        } else {
            basicStrategyBag();
        }
    }

    @Override
    public void checkMerchants(final List<Player> players, final List<Integer> pileGoodsIds) {
        // the bribed player only checks the left and right merchants (if he can afford to)

        Player merchantRight = players.get((this.id + 1) % players.size());
        Player merchantLeft =  players.get((this.id - 1 + players.size()) % players.size());

        checkMerchant(merchantLeft, pileGoodsIds);
        if (merchantLeft.getId() != merchantRight.getId()) {
            checkMerchant(merchantRight, pileGoodsIds);
        }

        for (Player merchant : players) {
            if (id == merchant.getId() || merchantLeft.getId() == merchant.getId()
                    || merchantRight.getId() == merchant.getId()) {
                continue;
            }

            Bag bag = merchant.getBag();
            int bribe = bag.getBribe();

            // the bribed sheriff will take the bribe if offered
            if (bribe != 0) {
                // the bribe is given to sheriff
                this.money += bribe;
                merchant.setMoney(merchant.getMoney() - bribe);
            }

            // he will not check the bag even if a bribe is not offered by the merchant
            merchant.addBagToBooth();
        }
    }
}
