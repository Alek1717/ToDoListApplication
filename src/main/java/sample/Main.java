package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Api.init();

    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("sample.fxml")));
    primaryStage.setTitle("To-Do List");
    var scene = new Scene(root, 800, 400);
    primaryStage.setScene(scene);
    primaryStage.show();


    String css = this.getClass().getResource("/styling.css").toExternalForm();
    scene.getStylesheets().add(css);
  }


  public static void main(String[] args) {
    launch(args);
  }
}
