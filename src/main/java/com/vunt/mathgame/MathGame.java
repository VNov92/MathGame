package com.vunt.mathgame;

import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.util.Random;
import javafx.util.Duration;

public class MathGame extends Application {

  private static final int MAX_NUMBER = 50;
  private static final int QUESTION_COUNT = 2;
  private static final Random random = new Random();

  private int correctAnswers = 0;
  private int currentQuestion = 0;
  private MathQuestion[] questions = new MathQuestion[QUESTION_COUNT];

  private Label questionLabel, scoreLabel, feedbackLabel;
  private RadioButton[] answerButtons;
  @FXML
  private Button checkButton, nextButton, stopButton;
  private ToggleGroup answerGroup;
  private VBox root;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Math Game");
    createMathGame();
    Scene scene = new Scene(root, 400, 300);

    scene.getStylesheets().add("com/vunt/mathgame/static/css/file.css");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void createMathGame() {
    initializeQuestions();

    createUI();

    nextQuestion();

    for (RadioButton answerButton : answerButtons) {
      answerButton.setOnAction(event -> {
        clearAnswerButtons();
        answerButton.getStyleClass().add("selected-answer");
      });
    }
  }

  @FXML
  private void checkAnswer() {
    int selectedAnswer = getSelectedAnswer();
    if (selectedAnswer == -1) {
      // Nếu trẻ chưa chọn câu trả lời, hiển thị thông báo lỗi
      feedbackLabel.setText("Please select an answer!");
      feedbackLabel.setVisible(true);
    } else {
      // Nếu trẻ đã chọn câu trả lời, kiểm tra đáp án và hiển thị feedback
      feedbackLabel.setVisible(true);
      MathQuestion question = questions[currentQuestion];
      // Nếu trả lời đúng, tăng biến correctAnswers và hiển thị nút nextQuestion
      if (selectedAnswer == question.getAnswer()) {
        currentQuestion++;
        correctAnswers++;
        feedbackLabel.setText("Correct!");
        nextButton.setDisable(false);
        checkButton.setDisable(true);
      } else {
        feedbackLabel.setText("Incorrect!");
      }

      // Hiển thị số câu trả lời đúng
      scoreLabel.setVisible(true);
      scoreLabel.setText("Score: " + correctAnswers + "/" + QUESTION_COUNT);
    }
    if (currentQuestion == QUESTION_COUNT) {
      int score = correctAnswers * 10; // 1 câu trả lời đúng được tính 10 điểm
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Kết quả");
      alert.setHeaderText("Số điểm của bạn là: " + score);
      alert.setContentText("Bạn có muốn chơi lại?");

      ButtonType continueButton = new ButtonType("Tiếp tục");
      ButtonType stopButton1 = new ButtonType("Dừng");

      alert.getButtonTypes().setAll(continueButton, stopButton1);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent()) {
        if (result.get() == continueButton) {
          resetGame();
        } else if (result.get() == stopButton1) {
          stopGame();
        }
      }
    } else {
      // Tiếp tục trò chơi với câu hỏi mới
    }

  }

  private void resetGame() {
    currentQuestion = 0;
    correctAnswers = 0;
    scoreLabel.setVisible(false);
    initializeQuestions();
    nextQuestion();
  }


  private int getSelectedAnswer() {
    for (RadioButton answerButton : answerButtons) {
      if (answerButton.getStyleClass().contains("selected-answer")) {
        return Integer.parseInt(answerButton.getText());
      }
    }
    return -1;
  }

  @FXML
  private void nextQuestion() {
    // Ẩn feedback và xóa lựa chọn trên giao diện
    feedbackLabel.setVisible(false);
    clearToggleGroup();
    toggleAnswerButtons(true);
    clearAnswerButtons();
    // Ẩn nút nextQuestion và hiển thị nút checkAnswer
    nextButton.setDisable(true);
    checkButton.setDisable(false);

    // Tạo câu hỏi mới và hiển thị lên giao diện
    MathQuestion question = questions[currentQuestion];
    questionLabel.setText(question.getQuestion());

    int correctButtonIndex = random.nextInt(4);
    answerButtons[correctButtonIndex].setText(Integer.toString(question.getAnswer()));

    int currentAnswer = question.getAnswer();
    for (int i = 0, j = 0; i < 4; i++) {
      if (i != correctButtonIndex) {
        int wrongAnswer = currentAnswer + (random.nextBoolean() ? -1 : 1) * random.nextInt(10) + 1;
        answerButtons[i].setText(Integer.toString(wrongAnswer));
      }
    }
  }
  private void createUI() {
    questionLabel = new Label();
    questionLabel.setId("question-label");
    scoreLabel = new Label();
    scoreLabel.setId("score-label");
    feedbackLabel = new Label();
    feedbackLabel.setId("feedback-label");
    answerButtons = new RadioButton[4];
    answerGroup = new ToggleGroup();

    for (int i = 0; i < 4; i++) {
      answerButtons[i] = new RadioButton();
      answerButtons[i].setToggleGroup(answerGroup);
      answerButtons[i].getStyleClass().add("answer-button");
    }

    HBox labelBox = new HBox();
    labelBox.getChildren().add(questionLabel);
    labelBox.setAlignment(Pos.CENTER);

    HBox answerBox = new HBox(20);
    answerBox.getChildren().addAll(answerButtons);
    answerBox.setAlignment(Pos.CENTER);


    nextButton = new Button("Next");
    nextButton.setOnAction(event -> nextQuestion());
    // Ẩn nút nextQuestion ban đầu
    nextButton.setDisable(true);
    nextButton.setId("next-button");

    stopButton = new Button("Stop");
    stopButton.setOnAction(event -> stopGame());
    stopButton.setId("stop-button");

    // Tạo nút checkAnswer và đặt sự kiện khi nhấn
    checkButton = new Button("Check Answer");
    checkButton.setOnAction(event -> checkAnswer());
    checkButton.setDisable(false);
    checkButton.setId("check-button");
    HBox checkButtonBox = new HBox();
    checkButtonBox.getChildren().add(checkButton);
    checkButtonBox.setAlignment(Pos.CENTER);

    HBox labelScore = new HBox();
    labelScore.getChildren().add(scoreLabel);
    labelScore.setAlignment(Pos.CENTER);

    HBox labelFeedback = new HBox();
    labelFeedback.getChildren().add(feedbackLabel);
    labelFeedback.setAlignment(Pos.CENTER);

    HBox buttonBox = new HBox(10, nextButton, stopButton);
    buttonBox.setAlignment(Pos.BOTTOM_CENTER);

    root = new VBox(
        10,
        labelBox,
        answerBox,
        labelFeedback,
        checkButtonBox,
        labelScore,
        buttonBox
    );
    root.setId("root");
  }

  private void initializeQuestions(){
    for (int i = 0; i < QUESTION_COUNT; i++) {
      int a = random.nextInt(MAX_NUMBER + 1);
      int b = random.nextInt(MAX_NUMBER + 1);
      int sum;
      if(a>=b){
        sum = random.nextBoolean() ? a + b : a - b;
      }else{
        sum = a + b;
      }
      questions[i] = new MathQuestion(a, b, sum);
    }
  }

  private void toggleAnswerButtons(boolean enabled) {
    for (RadioButton answerButton : answerButtons) {
      answerButton.setDisable(!enabled);
    }
  }
  private void clearToggleGroup() {
    answerGroup.selectToggle(null);
  }


  private void clearAnswerButtons() {
    for (RadioButton answerButton : answerButtons) {
      answerButton.getStyleClass().remove("selected-answer");
    }
  }

  @FXML
  private void stopGame() {
    System.exit(0);
  }

  private record MathQuestion(int a, int b, int sum) {

    public int getAnswer() {
        return sum;
      }

      public String getQuestion() {
        return a + (sum >= a ? " + " : " - ") + b + " = ?";
      }
    }
}
