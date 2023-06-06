package com.blackjack;

import java.util.ArrayList;
import java.util.List;

public class House {
	private List<Card> hand;

    public House() {
        this.hand = new ArrayList<>();
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

