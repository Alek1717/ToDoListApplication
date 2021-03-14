package sample;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sample.model.Category;
import sample.model.ToDo;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button addCategoryButton;
    @FXML
    public VBox categoriesVBox;
    @FXML
    public Button addToDoButton;
    @FXML
    public VBox ToDosVBox;
    @FXML
    public Label counter;
    @FXML
    public Label time;
    @FXML
    public Label nameOfCategory;



    private int totalToDos = 0;
    private int totalChecked = 0;
    private List<Category> categories = new ArrayList<>();
    private final ArrayList<HBox> groupOfCategories = new ArrayList<>();
    private final HashMap<Integer, ArrayList<ToDo>> categoryTodosMap = new HashMap<>();
    private int selectedCategory = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            time.setText("The current time is " + currentTime.getHour() + ":" + fixMinute(currentTime.getMinute()) + ":" + currentTime.getSecond());
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        initCategoryData();
        initData();

        selectCategory(0);
        updateCounter();

        addCategoryButton.setOnAction(event ->
                addCategory(-1, "Enter your new category here")
        );
        addToDoButton.setOnAction(event ->
                addToDo(-1, "Enter new to-do here", categories.get(selectedCategory).getId(), false)
        );
    }

    private void initCategoryData() {
        categories = Api.getInstance().getCategories();
        for (Category category : categories) {
            addCategory(category.getId(), category.getTitle());
        }
    }

    private void initData() {
        List<ToDo> toDoList = Api.getInstance().getToDos();
        for (ToDo toDo : toDoList) {
            addToDo(toDo.getId(), toDo.getTitle(), toDo.getCategoryId(), toDo.getCompleted());
        }
    }

    private void addCategory(int id, String title) {
        if (id == -1) {
            id = Api.getInstance().addCategory(title);
            categories.add(new Category(id, title));
        }
        categoryTodosMap.put(id, new ArrayList<>());

        var hbox = new HBox();
        categoriesVBox.getChildren().add(hbox);
        VBox.setVgrow(hbox, Priority.ALWAYS);

        var textField = new TextField(title);
        hbox.getChildren().add(textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

        int indexOfTextField = categoriesVBox.getChildren().indexOf(hbox);
        textField.setOnMouseClicked(mouseEvent -> {
            // if category has not been given a name yet
            if (textField.getText().equals("Enter your new category here")) {
                textField.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        // new category added
                        selectCategory(indexOfTextField);
                    }
                });
            } else {
                selectCategory(indexOfTextField);
            }
        });

        int categoryId = id;
        textField.textProperty().addListener((observableValue, oldValue, newValue) ->
                Api.getInstance().updateCategory(categoryId, newValue));

        groupOfCategories.add(hbox);
    }

    private void addToDo(int id, String title, int categoryId, boolean completed) {
        if (id == -1) {
            id = Api.getInstance().addToDo(title, categoryId);
        }
        ToDo toDo = new ToDo(id, title, categoryId, completed);

        var categoryToDos = categoryTodosMap.get(categoryId);
        if (categoryToDos != null) {
            categoryToDos.add(toDo);
        }

        drawToDo(toDo);
    }

    private String fixMinute(int minute) {
        if (minute < 10) {
            return "0" + minute;
        }
        return String.valueOf(minute);
    }

    private void selectCategory(int index) {
        HBox category = groupOfCategories.get(index);
        if (selectedCategory >= 0) {
            HBox prevCategory = groupOfCategories.get(selectedCategory);
            prevCategory.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        }
        TextField textField = (TextField) category.getChildren().get(0);
        nameOfCategory.setText(textField.getText());

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), nameOfCategory);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);

        ToDosVBox.getChildren().clear();
        totalToDos = 0;
        totalChecked = 0;
        ArrayList<ToDo> categoryToDos = categoryTodosMap.get(categories.get(index).getId());
        if (categoryToDos != null) {
            for (ToDo toDo : categoryToDos) {
                drawToDo(toDo);
            }
        }
        updateCounter();
        selectedCategory = index;
        category.setBackground(new Background(new BackgroundFill(Color.valueOf("#FCECA3"), new CornerRadii(12), new Insets(5))));
    }

    private void updateCounter() {
        counter.setText("You have completed " + totalChecked + " from " + totalToDos);
    }

    private void drawToDo(ToDo toDo) {
        var hbox = new HBox();
        ToDosVBox.getChildren().add(hbox);
        VBox.setVgrow(hbox, Priority.ALWAYS);

        var textField = new TextField(toDo.getTitle());
        hbox.getChildren().add(textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

        var checkBox = new CheckBox();
        hbox.getChildren().add(checkBox);
        checkBox.selectedProperty().setValue(toDo.getCompleted());
        HBox.setHgrow(checkBox, Priority.NEVER);

        checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Api.getInstance().updateToDo(toDo.getId(), toDo.getTitle(), newValue);
            if (newValue) {
                totalChecked++;
            } else {
                totalChecked--;
            }
            updateCounter();
            toDo.setCompleted(newValue);
        });

        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            Api.getInstance().updateToDo(toDo.getId(), newValue, toDo.getCompleted());
            toDo.setTitle(newValue);
        });

        if (toDo.getCompleted()) {
            totalChecked++;
        }
        totalToDos++;
        updateCounter();
    }
}
