package com.nova.todolist;

import com.nova.todolist.datamodel.TodoData;
import com.nova.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea details;
    @FXML
    private Label due;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private ToggleButton filterTasks;

    private FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> allItems, todayItems;

    @FXML
    public void initialize() {

        contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> deleteItem(todoListView.getSelectionModel().getSelectedItem()));
        contextMenu.getItems().add(delete);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        todoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                details.setText(newValue.getDescription());
                due.setText(dateTimeFormatter.format(newValue.getDate()));
            }
        });

        allItems = todoItem -> true;
        todayItems = todoItem -> (todoItem.getDate().equals(LocalDate.now()));

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), allItems);

        SortedList<TodoItem> sortedList = new SortedList<>(filteredList, Comparator.comparing(TodoItem::getDate));

//        todoListView.setItems(TodoData.getInstance().getTodoItems()); //.getItems().addAll(TodoData.getInstance().getTodoItems());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        DateTimeFormatter dateTimeFormatterTail = DateTimeFormatter.ofPattern("MMM d, yy");

        todoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> listCell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean empty) {
                        super.updateItem(todoItem, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            String tailDate;
                            if (todoItem.getDate().equals(LocalDate.now().plusDays(1))) {
                                tailDate = "Tomorrow";
                            } else if (todoItem.getDate().equals(LocalDate.now())) {
                                tailDate = "Today";
                            } else if (todoItem.getDate().equals(LocalDate.now().minusDays(1))) {
                                tailDate = "Yesterday";
                            } else {
                                tailDate = dateTimeFormatterTail.format(todoItem.getDate());
                            }
                            setText(todoItem.getTitle() + "  (" + tailDate + ")");
                            if (todoItem.getDate().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDate().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.ORANGERED);
                            }
                        }
                    }
                };
                listCell.emptyProperty().addListener((obs, wasEmpty, nowEmpty) -> {
                    if (nowEmpty) {
                        listCell.setContextMenu(null);
                    } else {
                        listCell.setContextMenu(contextMenu);
                    }
                });
                return listCell;
            }
        });
    }

    @FXML
    public void handleDeleteKeyPressed(KeyEvent key){
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null && key.getCode().equals(KeyCode.DELETE)){
            deleteItem(selectedItem);
        }
    }

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(borderPane.getScene().getWindow());
        dialog.setTitle("Add new TODO item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResult();
            todoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void handleFilterToggle(){
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (filterTasks.isSelected()) {
            filteredList.setPredicate(todayItems);
            if(filteredList.isEmpty()){
                details.clear();
                due.setText("");
            } else if(filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(allItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }

    public void deleteItem(TodoItem todoItem){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete task");
        alert.setHeaderText("Confirm delete: " + todoItem.getTitle());
        alert.setContentText("Are you sure? then click OK button to delete");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            TodoData.getInstance().deleteTask(todoItem);
        }
    }

}
