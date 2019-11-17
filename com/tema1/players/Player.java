package com.tema1.players;

import com.tema1.common.Bag;
import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Player {
    protected List<Integer> handGoodsIds;
    protected List<Integer> boothGoodsIds;
    protected Map<Integer, Integer> frequency;
    protected final Bag bag;

    protected int money = Constants.START_AMOUNT;
    protected int id;

    private PlayersType type;

    private Player() {
        handGoodsIds = new ArrayList<>();
        boothGoodsIds = new ArrayList<>();
        frequency = new HashMap<>();
        bag = new Bag();
    }

    Player(final PlayersType type, final int id) {
        this();
        this.type = type;
        this.id = id;
    }

    public abstract void formBag(int round);

    public abstract void checkMerchants(List<Player> players, List<Integer> pileGoodsIds);

    public final void formFrequency() {
        for (int goodId : boothGoodsIds) {
            if (frequency.containsKey(goodId)) {
                frequency.put(goodId, frequency.get(goodId) + 1);
            } else {
                frequency.put(goodId, 1);
            }
        }
    }

    public final void drawGoods(final List<Integer> pileGoodsIds) {
        clearHand();
        for (int i = 0; i < Constants.MAX_HAND_SIZE; i++) {
            handGoodsIds.add(pileGoodsIds.get(0));
            pileGoodsIds.remove(0);
        }
    }

    public final void checkMerchant(final Player merchant, final List<Integer> pileGoodsIds) {
        Bag bag = merchant.getBag();

        // the sheriff checks only if he can afford to
        if (this.money >= Constants.MIN_AMOUNT) {
            List<Integer> bagGoodsIds = bag.getBagGoodsIds();
            int declaredGoodId = bag.getDeclaredGoodId();
            boolean honestMerchant = true;

            for (int goodId : bagGoodsIds) {
                if (goodId != declaredGoodId) {
                    honestMerchant = false;

                    // add the confiscated goods at the end of the pile
                    pileGoodsIds.add(goodId);

                    // the penalty for the dishonest merchant and the reward for the sheriff
                    int penalty = GoodsFactory.getInstance().getGoodsById(goodId).getPenalty();

                    merchant.setMoney(merchant.getMoney() - penalty);
                    this.money += penalty;
                } else {
                    // the merchant adds the declared good to his booth
                    merchant.addGoodToBooth(goodId);
                }
            }

            if (honestMerchant) {
                // the penalty for the sheriff and the reward for the honest merchant
                int penalty = bagGoodsIds.size()
                        * GoodsFactory.getInstance().getGoodsById(declaredGoodId).getPenalty();

                this.money -= penalty;
                merchant.setMoney(merchant.getMoney() + penalty);
            }
        } else {
            // the merchant adds all his unchecked goods to his booth
            merchant.addBagToBooth();
        }
    }

    public final void basicStrategyBag() {
        List<Integer> bagGoodsIds = new ArrayList<>();
        Map<Integer, Integer> frequency = new HashMap<>();
        int bestIllegalGoodId = -1;
        int bestLegalGoodId = -1;

        // calculates the frequency of each legal good and finds the most profitable illegal good
        for (int goodId : handGoodsIds) {
            if (GoodsFactory.getInstance().getGoodsById(goodId).getType() == GoodsType.Legal) {
                bestLegalGoodId = goodId;
                if (frequency.containsKey(goodId)) {
                    frequency.put(goodId, frequency.get(goodId) + 1);
                } else {
                    frequency.put(goodId, 1);
                }
            } else {
                if (bestIllegalGoodId == -1) {
                    bestIllegalGoodId = goodId;
                } else if (GoodsFactory.getInstance().getGoodsById(goodId).getProfit()
                        > GoodsFactory.getInstance().getGoodsById(bestIllegalGoodId).getProfit()) {
                    bestIllegalGoodId = goodId;
                }
            }
        }

        if (bestLegalGoodId != -1) {
            int quantity = frequency.get(bestLegalGoodId);

            // finds which is the most frequent, profitable, largest id (in this priority) good
            for (Map.Entry<Integer, Integer> x : frequency.entrySet()) {
                if (x.getValue() == quantity) {
                    Goods xGood = GoodsFactory.getInstance().getGoodsById(x.getKey());
                    Goods yGood = GoodsFactory.getInstance().getGoodsById(bestLegalGoodId);

                    if (xGood.getProfit() == yGood.getProfit()) {
                        if (xGood.getId() > yGood.getId()) {
                            bestLegalGoodId = x.getKey();
                        }
                    } else if (xGood.getProfit() > yGood.getProfit()) {
                        bestLegalGoodId = x.getKey();
                    }
                } else if (x.getValue() > quantity) {
                    quantity = x.getValue();
                    bestLegalGoodId = x.getKey();
                }
            }

            // put as many copies as of the anterior found good possible into the bag
            for (int i = 1; i <= quantity && i <= Constants.MAX_BAG_SIZE; ++i) {
                bagGoodsIds.add(bestLegalGoodId);
            }

            // sets up the bag
            bag.setBagGoodsIds(bagGoodsIds);
            bag.setDeclaredGoodId(bestLegalGoodId);
            bag.setBribe(0);
        } else {
            // with no legal goods in his hand the basic player will put the most profitable one
            if (bestIllegalGoodId != -1 && this.money
                    >= GoodsFactory.getInstance().getGoodsById(bestIllegalGoodId).getPenalty()) {
                bagGoodsIds.add(bestIllegalGoodId);
            }

            // sets up the bag
            bag.setBagGoodsIds(bagGoodsIds);
            bag.setDeclaredGoodId(0);
            bag.setBribe(0);
        }
    }

    public final void clearHand() {
        if (!handGoodsIds.isEmpty()) {
            handGoodsIds.clear();
        }
    }

    @Override
    public final String toString() {
        return id + " " + type + " " + money;
    }

    public final void addGoodToBooth(final int goodId) {
        boothGoodsIds.add(goodId);
    }

    public final void addBagToBooth() {
        boothGoodsIds.addAll(bag.getBagGoodsIds());
    }

    public final int getMoney() {
        return money;
    }

    public final void setMoney(int money) {
        this.money = money;
    }

    public final List<Integer> getBoothGoodsIds() {
        return boothGoodsIds;
    }

    public final int getId() {
        return id;
    }

    public final Bag getBag() {
        return bag;
    }

    public final Map<Integer, Integer> getFrequency() {
        return frequency;
    }
}
