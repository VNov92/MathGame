module com.vunt.mathgame {
  requires javafx.controls;
  requires javafx.fxml;
  requires lombok;
  requires java.desktop;

  opens com.vunt.mathgame to javafx.fxml;
  exports com.vunt.mathgame;
}