package SnakeLadderGame;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class SnakesAndLaddersController extends SnakesAndLadders {

    private final Map<Integer, Integer> ladders = new HashMap<>();
    private final Map<Integer, Integer> snakes = new HashMap<>();

    public SnakesAndLaddersController(Stage stage) {
        diceImageView.setOnMouseClicked(e -> rollDice());
        Platform.runLater(() -> {
            initializeSnakesAndLadders();
            drawBoard(gc);
        });

        updateDiceImage(1);  
        updateAvatarHighlight();  
    }

    private void rollDice() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 10; i++) {
            final int roll = i % 6 + 1;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 100), e -> updateDiceImage(roll)));
        }
        timeline.setOnFinished(e -> {
            int diceRoll = random.nextInt(6) + 1;
            updateDiceImage(diceRoll);
            handleDiceRoll(diceRoll);
        });
        timeline.play();
    }

    private void handleDiceRoll(int diceRoll) {
        if (player1Turn) {
            player1Pos += diceRoll;
            if (player1Pos > 100) {
                player1Pos = 100;
            }
            player1Pos = checkForSnakesOrLadders(player1Pos);
            movePlayer(player1, player1Pos);
            if (player1Pos == 100) {
                showEndGameDialog("Player 1");
            }
        } else {
            player2Pos += diceRoll;
            if (player2Pos > 100) {
                player2Pos = 100;
            }
            player2Pos = checkForSnakesOrLadders(player2Pos);
            movePlayer(player2, player2Pos);
            if (player2Pos == 100) {
                showEndGameDialog("Player 2");
            }
        }
        player1Turn = !player1Turn;
        updateAvatarHighlight();
    }

    private void updateDiceImage(int diceValue) {
        String imagePath = String.format("/SnakeLadderGame/media/dice%d.png", diceValue);
        diceImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    private void updateAvatarHighlight() {
        if (player1Turn) {
            avatar1ImageView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 10, 0, 0, 0);");
            avatar2ImageView.setStyle("-fx-effect: none;");
        } else {
            avatar1ImageView.setStyle("-fx-effect: none;");
            avatar2ImageView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 10, 0, 0, 0);");
        }
    }

    private int checkForSnakesOrLadders(int position) {
        if (ladders.containsKey(position)) {
            return ladders.get(position);
        } else if (snakes.containsKey(position)) {
            return snakes.get(position);
        }
        return position;
    }

    private void movePlayer(Circle player, int position) {
        if (position > 100) {
            position = 100; 
        }

        int row = (position - 1) / 10; 
        int col = (position - 1) % 10; 

       
        if (row % 2 == 1) { 
            col = 9 - col;
        }

        int reversedRow = 9 - row; 
        GridPane.setConstraints(player, col, reversedRow);
    }

    private void initializeSnakesAndLadders() {
    
        ladders.put(2, 23); 
        ladders.put(4, 68);
        ladders.put(6, 45);
        ladders.put(20, 59);
        ladders.put(30, 96);
        ladders.put(52, 72);
        ladders.put(57, 96);
        ladders.put(71, 92);

        snakes.put(98, 40); 
        snakes.put(87, 49);
        snakes.put(84, 58);
        snakes.put(73, 15);
        snakes.put(56, 8);
        snakes.put(50, 5);
        snakes.put(43, 17);
    }

    private void drawBoard(GraphicsContext gc) {
        
    }

    private void showEndGameDialog(String winner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(winner + " wins!");
            alert.setContentText("Would you like to play again or quit?");

            ButtonType playAgainButton = new ButtonType("Play Again");
            ButtonType quitButton = new ButtonType("Quit");

            alert.getButtonTypes().setAll(playAgainButton, quitButton);

            alert.showAndWait().ifPresent(type -> {
                if (type == playAgainButton) {
                    resetGame();
                } else {
                    Platform.exit();
                }
            });
        });
    }

    private void resetGame() {
        player1Pos = 1;
        player2Pos = 1;
        player1Turn = true;
        movePlayer(player1, player1Pos);
        movePlayer(player2, player2Pos);
        updateAvatarHighlight();
    }
}
