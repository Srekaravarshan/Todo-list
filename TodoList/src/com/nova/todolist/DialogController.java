package com.nova.todolist;

import com.nova.todolist.datamodel.TodoData;
import com.nova.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker dateField;

    public TodoItem processResult(){
        String title = titleField.getText().trim();
        String detail = detailsField.getText().trim();
        LocalDate date = dateField.getValue();

        TodoItem todoItem = new TodoItem(title, detail, date);
        TodoData.addTodoItem(todoItem);
        return todoItem;
    }
}
