package com.blackjack;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainGameGUI extends Application {
    private Game game;
    private Label playerNameLabel;
    private Label earningsLabel;
    private Label betStatusLabel;
    private Button hitButton;
    private Button stayButton;
    private Button playAgainButton;
    private TextArea playerHandTextArea;
    private TextArea houseHandTextArea;

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        primaryStage.setTitle("Blackjack Game");

        // Player Info Pane
        HBox playerInfoPane = new HBox();
        playerInfoPane.setPadding(new Insets(10));
        playerInfoPane.setAlignment(Pos.CENTER);
        playerInfoPane.setSpacing(10);

        Label nameLabel = new Label("Player Name:");
        playerNameLabel = new Label();
        earningsLabel = new Label();

        playerInfoPane.getChildren().addAll(nameLabel, playerNameLabel, earningsLabel);

        // Game Status Pane
        HBox gameStatusPane = new HBox();
        gameStatusPane.setPadding(new Insets(10));
        gameStatusPane.setAlignment(Pos.CENTER);
        gameStatusPane.setSpacing(10);

        Label betLabel = new Label("Bet Status:");
        betStatusLabel = new Label();

        gameStatusPane.getChildren().addAll(betLabel, betStatusLabel);

        // Button Pane
        HBox buttonPane = new HBox();
        buttonPane.setPadding(new Insets(10));
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(10);

        hitButton = new Button("Hit");
        stayButton = new Button("Stay");
        playAgainButton = new Button("Play Again");

        hitButton.setDisable(true);
        stayButton.setDisable(true);
        playAgainButton.setDisable(true);

        buttonPane.getChildren().addAll(hitButton, stayButton, playAgainButton);

        // Card Pane
        VBox cardPane = new VBox();
        cardPane.setPadding(new Insets(10));
        cardPane.setAlignment(Pos.CENTER);
        cardPane.setSpacing(10);

        playerHandTextArea = new TextArea();
        playerHandTextArea.setEditable(false);
        playerHandTextArea.setPrefRowCount(4);

        houseHandTextArea = new TextArea();
        houseHandTextArea.setEditable(false);
        houseHandTextArea.setPrefRowCount(4);

        cardPane.getChildren().addAll(playerHandTextArea, houseHandTextArea);

        // Root Pane
        VBox rootPane = new VBox();
        rootPane.setPadding(new Insets(10));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setSpacing(10);

        rootPane.getChildren().addAll(playerInfoPane, gameStatusPane, buttonPane, cardPane);

        Scene scene = new Scene(rootPane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Event Handlers
        hitButton.setOnAction(e -> {
            game.playerTurn();
            updateGUI();
            checkGameStatus();
        });

        stayButton.setOnAction(e -> {
            game.houseTurn();
            updateGUI();
            determineWinner();
            checkGameStatus();
        });

        playAgainButton.setOnAction(e -> {
            game.start();
            updateGUI();
        });

        // Start the game
        game.start();
        updateGUI();
    }

    private void updateGUI() {
        Player player = game.getPlayer();
        House house = game.getHouse();

        playerNameLabel.setText(player.getName());
        earningsLabel.setText("Earnings: $" + player.getEarnings());

        StringBuilder playerHand = new StringBuilder();
        for (Card card : player.getHand()) {
            playerHand.append(card.toString()).append("\n");
        }