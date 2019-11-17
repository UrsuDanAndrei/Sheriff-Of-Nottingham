package com.tema1.main;

import com.tema1.common.Constants;
import com.tema1.comparators.PlayersComparator;

import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;
import com.tema1.goods.IllegalGoods;
import com.tema1.goods.GoodsType;

import com.tema1.players.BasicPlayer;
import com.tema1.players.BribedPlayer;
import com.tema1.players.GreedyPlayer;
import com.tema1.players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Main {
    private Main() {
        // just to trick checkstyle
    }

    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();

        List<Integer> pileGoodsIds = gameInput.getAssetIds();
        List<String> playeresNames = gameInput.getPlayerNames();
        int rounds = Math.min(gameInput.getRounds(), Constants.MAX_ROUNDS);
        int noPlayers = playeresNames.size();

        // creates a list of players
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < noPlayers; ++i) {
            switch (playeresNames.get(i)) {
                case "basic":
                    players.add(new BasicPlayer(i));
                    break;
                case "greedy":
                    players.add(new GreedyPlayer(i));
                    break;
                case "bribed":
                    players.add(new BribedPlayer(i));
                    break;
                default:
                    break;
            }
        }

        // simulates the game flow
        for (int round = 1; round <= rounds; ++round) {
            for (int subRound = 0; subRound < noPlayers; ++subRound) {
                // the subRound value is also the id of the sheriff
                int sheriffId = subRound;

                // all merchants form their bags
                for (int merchantId = 0; merchantId < noPlayers; ++merchantId) {
                    // the sheriff player is skipped
                    if (merchantId == sheriffId) {
                        continue;
                    }

                    players.get(merchantId).drawGoods(pileGoodsIds);
                    players.get(merchantId).formBag(round);
                }

                // the sheriff inspects the bags according to his strategy
                players.get(sheriffId).checkMerchants(players, pileGoodsIds);
            }
        }

        // adds the illegal bonuses to the booth
        for (Player player : players) {
            List<Integer> boothGoodIds = player.getBoothGoodsIds();
            List<Integer> bonusGoods = new ArrayList<Integer>();

            for (int goodId : boothGoodIds) {
                Goods good = GoodsFactory.getInstance().getGoodsById(goodId);

                if (good.getType() == GoodsType.Illegal) {
                    IllegalGoods illegalGood = (IllegalGoods) good;

                    Map<Goods, Integer> bonus = illegalGood.getIllegalBonus();
                    for (Map.Entry<Goods, Integer> goodBonus : bonus.entrySet()) {
                        for (int i = 0; i < goodBonus.getValue(); i++) {
                            bonusGoods.add(goodBonus.getKey().getId());
                        }
                    }
                }
            }

            boothGoodIds.addAll(bonusGoods);
        }

        // adds the profit from the goods that are on booth
        for (Player player : players) {
            for (int boothGoodId : player.getBoothGoodsIds()) {
                int profit = GoodsFactory.getInstance().getGoodsById(boothGoodId).getProfit();
                player.setMoney(player.getMoney() + profit);
            }
        }

        // gives the king / queen bonuses
        Map<Integer, Goods> allGoods = GoodsFactory.getInstance().getAllGoods();
        for (Map.Entry<Integer, Goods> idToGood : allGoods.entrySet()) {
            int goodId = idToGood.getKey();
            Goods good = idToGood.getValue();

            if (good.getType() == GoodsType.Illegal) {
                continue;
            }

            Player king = null;
            Player queen = null;

            int maxFrequencyKing = -1;
            int maxFrequencyQueen = -1;

            for (Player player : players) {
                player.formFrequency();
                Map<Integer, Integer> frequency = player.getFrequency();

                if (frequency.containsKey(goodId)) {
                    int freq = frequency.get(goodId);

                    if (freq > maxFrequencyKing) {
                        maxFrequencyQueen = maxFrequencyKing;
                        queen = king;

                        maxFrequencyKing = freq;
                        king = player;
                    } else if (freq > maxFrequencyQueen) {
                        maxFrequencyQueen = freq;
                        queen = player;
                    }
                }
            }

            if (maxFrequencyKing != -1) {
                LegalGoods legalGood = (LegalGoods) good;
                king.setMoney(king.getMoney() + legalGood.getKingBonus());
            }

            if (maxFrequencyQueen != -1) {
                LegalGoods legalGood = (LegalGoods) good;
                queen.setMoney(queen.getMoney() + legalGood.getQueenBonus());
            }
        }

        // sort and display the players
        Collections.sort(players, new PlayersComparator());
        for (Player player : players) {
            System.out.println(player);
        }
    }
}
