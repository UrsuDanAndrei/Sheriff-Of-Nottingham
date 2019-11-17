package com.tema1.common;
import java.util.ArrayList;
import java.util.List;

public final class Bag {
    private List<Integer> bagGoodsIds;
    private int declaredGoodId;
    private int bribe;

    public Bag() {
        bagGoodsIds = new ArrayList<Integer>();
        declaredGoodId = 0;
        bribe = 0;
    }

    public int getDeclaredGoodId() {
        return declaredGoodId;
    }

    public void setDeclaredGoodId(int declaredGoodId) {
        this.declaredGoodId = declaredGoodId;
    }

    public List<Integer> getBagGoodsIds() {
        return bagGoodsIds;
    }

    public void setBagGoodsIds(List<Integer> bagGoodsIds) {
        this.bagGoodsIds = bagGoodsIds;
    }

    public int getBribe() {
        return bribe;
    }

    public void setBribe(int bribe) {
        this.bribe = bribe;
    }

    public int getSize() {
        return bagGoodsIds.size();
    }
}
