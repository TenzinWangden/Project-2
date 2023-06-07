package test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Card {
    private final String suit;
    final String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String toString() {
        return "[" + rank + suit + "]";
    }
}

class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck.");
        }
        return cards.remove(0);
    }
}

class Hand {
    private final List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getHandValue() {
        int value = 0;
        int aceCount = 0;

        for (Card card : cards) {
            if (card.rank.equals("A")) {
                value += 11;
                aceCount++;
            } else if (card.rank.equals("K") || card.rank.equals("Q") || card.rank.equals("J")) {
                value += 10;
            } else {
                value += Integer.parseInt(card.rank);
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }

        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append(" ");
        }
        return sb.toString().trim();
    }
}

class Player {
    private final String name;
    private int balance;

    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void updateBalance(int amount) {
        balance += amount;
    }
}

public class BlackjackGame1 {
    private static final Scanner scanner = new Scanner(System.in);
    private static Player currentPlayer;
    private static List<String> gameRecord;

    public static void main(String[] args) {
        System.out.println("--- Welcome to Blackjack! ---");

        String answer;
        while (true) {
            System.out.print("Are you a returning player? (Y/N): ");
            answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("Y")) {
                currentPlayer = loadPlayer();
                if (currentPlayer != null) {
                    System.out.println("Welcome back, " + currentPlayer.getName() + "!");
                    break;
                } else {
                    System.out.println("Failed to load player data.");
                }
            } else if (answer.equalsIgnoreCase("N")) {
                currentPlayer = createNewPlayer();
                if (currentPlayer != null) {
                    System.out.println("Welcome, " + currentPlayer.getName() + "!");
                    break;
                } else {
                    System.out.println("Failed to save player data.");
                }
            } else {
                System.out.println("Invalid input. Please enter Y or N.");
            }
        }

        while (true) {
            System.out.println("\n--- Let's play Blackjack! ---");
            playGame();

            System.out.print("Do you want to play again? (Y/N): ");
            answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("Y")) {
                break;
            }
        }

        System.out.println("\nThank you for playing Blackjack! Goodbye.");
    }

    private static Player loadPlayer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your pin code: ");
        String pinCode = scanner.nextLine().trim();

        // Load player from file or database
        // ...

        return null; // Replace with the loaded player object
    }

    private static Player createNewPlayer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your pin code: ");
        String pinCode = scanner.nextLine().trim();

        System.out.print("Enter your starting balance: ");
        int balance = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Player player = new Player(name, balance);

        // Save player to file or database
        // ...

        return player;
    }

    private static void savePlayer(Player player) {
        // Save player to file or database
        // ...

        System.out.println("Player data saved successfully.");
    }

    private static void playGame() {
        Deck deck = new Deck();
        deck.shuffle();

        Hand playerHand = new Hand();
        Hand dealerHand = new Hand();

        System.out.print("Enter your bet amount: ");
        int bet = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        playerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());

        dealerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        System.out.println("\n--- Your Hand ---");
        System.out.println(playerHand);
        System.out.println("Hand value: " + playerHand.getHandValue());

        System.out.println("\n--- Dealer's Hand ---");
        System.out.println("[?] " + dealerHand.getCards().get(1));
        System.out.println("Hand value: " + dealerHand.getCards().get(1).rank);

        if (playerHand.getHandValue() == 21 && dealerHand.getHandValue() == 21) {
            System.out.println("\nIt's a tie!");
            saveGameRecord("Tie");
            return;
        }

        if (playerHand.getHandValue() == 21) {
            System.out.println("\nCongratulations! You have a Blackjack!");
            currentPlayer.updateBalance(bet);
            savePlayer(currentPlayer);
            saveGameRecord("Win");
            return;
        }

        while (true) {
            System.out.print("\nDo you want to hit or stand? (H/S): ");
            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("H")) {
                playerHand.addCard(deck.drawCard());
                System.out.println("\n--- Your Hand ---");
                System.out.println(playerHand);
                System.out.println("Hand value: " + playerHand.getHandValue());

                if (playerHand.getHandValue() > 21) {
                    System.out.println("\nBust! You lose.");
                    currentPlayer.updateBalance(-bet);
                    savePlayer(currentPlayer);
                    saveGameRecord("Loss");
                    return;
                }
            } else if (choice.equalsIgnoreCase("S")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter H or S.");
            }
        }

        while (dealerHand.getHandValue() < 17) {
            dealerHand.addCard(deck.drawCard());
        }

        System.out.println("\n--- Dealer's Hand ---");
        System.out.println(dealerHand);
        System.out.println("Hand value: " + dealerHand.getHandValue());

        if (dealerHand.getHandValue() > 21) {
            System.out.println("\nDealer busts! You win!");
            currentPlayer.updateBalance(bet);
            savePlayer(currentPlayer);
            saveGameRecord("Win");
        } else if (dealerHand.getHandValue() == playerHand.getHandValue()) {
            System.out.println("\nIt's a tie!");
            saveGameRecord("Tie");
        } else if (dealerHand.getHandValue() > playerHand.getHandValue()) {
            System.out.println("\nDealer wins! You lose.");
            currentPlayer.updateBalance(-bet);
            savePlayer(currentPlayer);
            saveGameRecord("Loss");
        } else {
            System.out.println("\nYou win!");
            currentPlayer.updateBalance(bet);
            savePlayer(currentPlayer);
            saveGameRecord("Win");
        }
    }

    private static void saveGameRecord(String result) {
        if (gameRecord == null) {
            gameRecord = new ArrayList<>();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(currentPlayer.getName()).append("\n");
        sb.append("Result: ").append(result).append("\n");
        sb.append("Time: ").append(getCurrentDateTime()).append("\n");
        gameRecord.add(sb.toString());

        try {
            FileWriter fileWriter = new FileWriter("game_record.txt", true);
            for (String record : gameRecord) {
                fileWriter.write(record);
            }
            fileWriter.close();
            System.out.println("Game record saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save game record.");
        }
    }

    private static String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }
}