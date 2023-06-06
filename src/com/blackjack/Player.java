package com.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private String name;
    private int earnings;
    private List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.earnings = 0;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getEarnings() {
        return earnings;
    }

    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void clearHand() {
        hand.clear();
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public int calculateHandValue() {
        int sum = 0;
        int numAces = 0;

        for (Card card : hand) {
            if (card.getValue() == Card.Value.ACE) {
                numAces++;
            }
            sum += card.getValue().getNumericValue();
        }

        while (sum > 21 && numAces > 0) {
            sum -= 10;
            numAces--;
        }

        return sum;
    }
}
