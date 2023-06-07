import java.util.*;

class Player {
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

class House {
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

class Card {
    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
    public enum Value { TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
                        JACK(10), QUEEN(10), KING(10), ACE(11);

        private int numericValue;

        Value(int numericValue) {
            this.numericValue = numericValue;
        }

        public int getNumericValue() {
            return numericValue;
        }
    }

    private Suit suit;
    private Value value;

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}

class Deck {
    private List<Card> cards;
    private Random random;

    public Deck() {
        this.cards = new ArrayList<>();
        this.random = new Random();

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                cards.add(new Card(suit, value));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty. Cannot deal a card.");
        }
        return cards.remove(0);
    }
}

class Game {
    private static final int INITIAL_EARNINGS = 100;
    private static final int BET_AMOUNT = 10;
    private static final int BLACKJACK_VALUE = 21;
    private static final int DEALER_MINIMUM = 17;
    private static final String PLAY_AGAIN_PROMPT = "Do you want to play again? (y/n): ";
    private static final String PLAYER_WIN_MESSAGE = "Congratulations! You won!";
    private static final String PLAYER_LOSE_MESSAGE = "Sorry! You lost!";
    private static final String TIE_MESSAGE = "It's a tie!";

    private Scanner scanner;
    private Player player;
    private House house;
    private Deck deck;

    public Game() {
        this.scanner = new Scanner(System.in);
        this.player = null;
        this.house = new House();
        this.deck = new Deck();
    }

    public void start() {
        System.out.println("Welcome to the Blackjack game!");

        String playerName = getPlayerName();
        player = new Player(playerName);
        player.setEarnings(INITIAL_EARNINGS);

        boolean continuePlaying = true;
        while (continuePlaying) {
            playRound();
            continuePlaying = askToPlayAgain();
            deck.shuffle();
        }

        System.out.println("Thank you for playing!");
    }

    private String getPlayerName() {
        System.out.print("Please enter your name: ");
        return scanner.nextLine();
    }

    private void playRound() {
        System.out.println("\n--- Round Start ---");
        player.clearHand();
        house.clearHand();

        if (player.getEarnings() <= 0) {
            System.out.println("Insufficient earnings to place a bet. Game over!");
            return;
        }

        int bet = placeBet();
        dealInitialCards();

        if (player.calculateHandValue() == BLACKJACK_VALUE) {
            System.out.println("Blackjack! You win!");
            player.setEarnings(player.getEarnings() + (int) (bet * 1.5));
        } else {
            playerTurn();
            houseTurn();
            determineWinner();
        }

        System.out.println("Your earnings: $" + player.getEarnings());
    }

    private int placeBet() {
        int bet = 0;
        boolean validBet = false;

        while (!validBet) {
            System.out.println("Your earnings: $" + player.getEarnings());
            System.out.print("Place your bet ($" + BET_AMOUNT + " minimum): ");
            String input = scanner.nextLine();

            try {
                bet = Integer.parseInt(input);
                if (bet >= BET_AMOUNT && bet <= player.getEarnings()) {
                    validBet = true;
                } else {
                    System.out.println("Invalid bet amount. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid bet amount. Please try again.");
            }
        }

        player.setEarnings(player.getEarnings() - bet);
        return bet;
    }

    private void dealInitialCards() {
        for (int i = 0; i < 2; i++) {
            player.addCardToHand(deck.dealCard());
            house.addCardToHand(deck.dealCard());
        }

        System.out.println("Your cards: " + player.getHand());
        System.out.println("House cards: " + house.getHand().get(0) + " and [Hidden]");
    }

    private void playerTurn() {
        while (true) {
            System.out.print("Do you want to hit or stay? (h/s): ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("h")) {
                Card card = deck.dealCard();
                player.addCardToHand(card);
                System.out.println("You drew a " + card);
                System.out.println("Your cards: " + player.getHand());

                int handValue = player.calculateHandValue();
                if (handValue > BLACKJACK_VALUE) {
                    System.out.println("Busted! You lose.");
                    break;
                } else if (handValue == BLACKJACK_VALUE) {
                    System.out.println("You have 21!");
                    break;
                }
            } else if (choice.equalsIgnoreCase("s")) {
                System.out.println("You chose to stay.");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void houseTurn() {
        System.out.println("House cards: " + house.getHand());

        while (house.calculateHandValue() < DEALER_MINIMUM) {
            Card card = deck.dealCard();
            house.addCardToHand(card);
            System.out.println("House drew a " + card);
            System.out.println("House cards: " + house.getHand());
        }
    }

    private void determineWinner() {
        int playerHandValue = player.calculateHandValue();
        int houseHandValue = house.calculateHandValue();

        System.out.println("Your hand value: " + playerHandValue);
        System.out.println("House hand value: " + houseHandValue);

        if (playerHandValue > BLACKJACK_VALUE) {
            System.out.println(PLAYER_LOSE_MESSAGE);
        } else if (houseHandValue > BLACKJACK_VALUE) {
            System.out.println(PLAYER_WIN_MESSAGE);
            player.setEarnings(player.getEarnings() + 2 * BET_AMOUNT);
        } else if (playerHandValue > houseHandValue) {
            System.out.println(PLAYER_WIN_MESSAGE);
            player.setEarnings(player.getEarnings() + 2 * BET_AMOUNT);
        } else if (playerHandValue < houseHandValue) {
            System.out.println(PLAYER_LOSE_MESSAGE);
        } else {
            System.out.println(TIE_MESSAGE);
            player.setEarnings(player.getEarnings() + BET_AMOUNT);
        }
    }

    private boolean askToPlayAgain() {
        while (true) {
            System.out.print(PLAY_AGAIN_PROMPT);
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("y")) {
                return true;
            } else if (choice.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

public class MainGame {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
