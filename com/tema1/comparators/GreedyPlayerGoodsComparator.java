package com.tema1.comparators;

import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.Comparator;

public final class GreedyPlayerGoodsComparator implements Comparator<Integer> {
    @Override
    public int compare(final Integer good1, final Integer good2) {
        final Goods xGood = GoodsFactory.getInstance().getGoodsById(good1.intValue());
        final Goods yGood = GoodsFactory.getInstance().getGoodsById(good2.intValue());

        if (xGood.getType() == yGood.getType()) {
                return yGood.getProfit() - xGood.getProfit();
        } else {
            return xGood.getType() == GoodsType.Illegal ? -1 : 1;
        }
    }
}
