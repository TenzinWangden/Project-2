

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Card {
    private final String suit;
    final String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
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

    public int getHandValue() {
        int value = 0;
        int numAces = 0;

        for (Card card : cards) {
            String rank = card.rank;
            if (rank.equals("A")) {
                value += 11;
                numAces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                value += 10;
            } else {
                value += Integer.parseInt(rank);
            }
        }

        while (value > 21 && numAces > 0) {
            value -= 10;
            numAces--;
        }

        return value;
    }

    public List<Card> getCards() {
        return cards;
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
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty. Cannot draw a card.");
        }
        return cards.remove(cards.size() - 1);
    }
}

class Player {
    private final String name;
    private final String pinCode;
    private int balance;

    public Player(String name, String pinCode, int balance) {
        this.name = name;
        this.pinCode = pinCode;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public String getPinCode() {
        return pinCode;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Deck deck = new Deck();

    public static void main(String[] args) {
        System.out.println("--- Welcome to Blackjack! ---");
        System.out.print("Are you a returning player? (Y/N): ");
        String returningPlayerInput = scanner.nextLine().toUpperCase();

        if (returningPlayerInput.equals("Y")) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your pin code: ");
            String pinCode = scanner.nextLine();

            Player player = loadPlayerData(name, pinCode);
            if (player == null) {
                System.out.println("Player not found. Starting as a new player.");
                createNewPlayer();
            } else {
                System.out.println("\n--- Welcome back, " + player.getName() + "! ---");
                System.out.println("Your current balance: $" + player.getBalance());
                playGame(player);
            }
        } else if (returningPlayerInput.equals("N")) {
            createNewPlayer();
        } else {
            System.out.println("Invalid input. Exiting the game.");
        }
    }

    private static void createNewPlayer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Create a new pin: ");
        String pinCode = scanner.nextLine();

        Player player = new Player(name, pinCode, 100);
        System.out.println("\n--- Welcome, " + player.getName() + "! ---");
        System.out.println("Your current balance: $" + player.getBalance());
        playGame(player);
    }

    private static Player loadPlayerData(String name, String pinCode) {
        try {
            File playerDirectory = new File("player_record");
            if (!playerDirectory.exists()) {
                playerDirectory.mkdir();
                System.out.println("Generating files");
            }

            File playerFile = new File("player_record/" + name + ".txt");
            if (!playerFile.exists()) {
                return null;
            }

            Scanner fileScanner = new Scanner(playerFile);
            String savedName = fileScanner.nextLine();
            String savedPinCode = fileScanner.nextLine();
            int savedBalance = Integer.parseInt(fileScanner.nextLine());
            fileScanner.close();

            if (savedName.equals(name) && savedPinCode.equals(pinCode)) {
                return new Player(savedName, savedPinCode, savedBalance);
            } else {
                System.out.println("Player name and pin code do not match.");
                System.out.print("Would you like to re-enter your name? (Y/N): ");
                String reEnterNameInput = scanner.nextLine().toUpperCase();
                if (reEnterNameInput.equals("Y")) {
                    System.out.print("Enter your name: ");
                    String reEnteredName = scanner.nextLine();
                    return loadPlayerData(reEnteredName, pinCode);
                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static void savePlayerData(Player player) {
        try {
            File playerFile = new File("player_record/" + player.getName() + ".txt");
            PrintWriter writer = new PrintWriter(new FileWriter(playerFile));
            writer.println(player.getName());
            writer.println(player.getPinCode());
            writer.println(player.getBalance());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void playGame(Player player) {
        System.out.println("\n--- New Round ---");
        System.out.print("Enter your bet amount: ");
        System.out.println("Your current balance: $" + player.getBalance());
        int betAmount = getPlayerBet(player.getBalance());

        Hand playerHand = new Hand();
        Hand houseHand = new Hand();

        playerHand.addCard(deck.drawCard());
        houseHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        houseHand.addCard(deck.drawCard());

        displayHands(playerHand, houseHand, false);

        if (playerHand.getHandValue() == 21) {
            System.out.println("Congratulations! You have Blackjack!");
            player.setBalance(player.getBalance() + (int) (betAmount * 1.5));
            endRound(player);
            return;
        }

        while (true) {
            System.out.print("Do you want to hit or stand? (H/S): ");
            String action = scanner.nextLine().toUpperCase();
            if (action.equals("H")) {
                playerHand.addCard(deck.drawCard());
                displayHands(playerHand, houseHand, false);
                if (playerHand.getHandValue() > 21) {
                    System.out.println("Busted! You lose.");
                    endRound(player);
                    return;
                }
            } else if (action.equals("S")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'H' or 'S'.");
            }
        }

        while (houseHand.getHandValue() < 17) {
            houseHand.addCard(deck.drawCard());
            displayHands(playerHand, houseHand, false);
        }

        displayHands(playerHand, houseHand, true);

        if (houseHand.getHandValue() > 21) {
            System.out.println("House busted! You win!");
            player.setBalance(player.getBalance() + betAmount);
        } else if (playerHand.getHandValue() > houseHand.getHandValue()) {
            System.out.println("You win!");
            player.setBalance(player.getBalance() + betAmount);
        } else if (playerHand.getHandValue() < houseHand.getHandValue()) {
            System.out.println("You lose!");
            player.setBalance(player.getBalance() - betAmount);
        } else {
            System.out.println("It's a tie.");
        }

        endRound(player);
    }

    private static void endRound(Player player) {
        System.out.println("\n--- Play Again ---");
        if (player.getBalance() == 0) {
            System.out.println("Your balance is $0.");
            System.out.print("Would you like to add money? (Y/N): ");
            String addMoneyInput = scanner.nextLine().toUpperCase();
            if (addMoneyInput.equals("N")) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        }
        System.out.print("Do you want to play again? (Y/N): ");
        String playAgainInput = scanner.nextLine().toUpperCase();
        if (playAgainInput.equals("Y")) {
            playGame(player);
        } else {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private static int getPlayerBet(int balance) {
        while (true) {
            int betAmount = Integer.parseInt(scanner.nextLine());
            if (betAmount > 0 && betAmount <= balance) {
                return betAmount;
            } else {
                System.out.println("Invalid bet amount. Your balance is $" + balance);
                System.out.print("Enter your bet amount: ");
            }
        }
    }

    private static void displayHands(Hand playerHand, Hand houseHand, boolean revealHouseCard) {
        System.out.println("\nPlayer's Hand: " + formatHand(playerHand.getCards()));
        System.out.println("Player's Hand Value: " + playerHand.getHandValue());
        if (revealHouseCard) {
            System.out.println("\nHouse's Hand: " + formatHand(houseHand.getCards()));
            System.out.println("House's Hand Value: " + houseHand.getHandValue());
        } else {
            System.out.println("\nHouse's Hand: " + houseHand.getCards().get(0) + " [Hidden]");
        }
    }

    private static String formatHand(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append(" ");
        }
        return sb.toString().trim();
    }
}
