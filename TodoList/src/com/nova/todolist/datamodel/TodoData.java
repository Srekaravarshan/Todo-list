package com.nova.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class TodoData {

    private static final TodoData instance = new TodoData();
    private final String filename = "TodoData.txt";

    private static ObservableList<TodoItem> todoItems;

    private TodoData(){}

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void loadTodoItems() throws IOException {

        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String input;
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");
                String title = itemPieces[0];
                String details = itemPieces[1];
                LocalDate date = LocalDate.parse(itemPieces[2]);
                TodoItem item = new TodoItem(title, details, date);
                todoItems.add(item);
            }
        }
    }

    public void saveTodoList() throws IOException {
        Path path = Paths.get(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (TodoItem item : todoItems) {
                bw.write(String.format("%s\t%s\t%s", item.getTitle(), item.getDescription(), item.getDate()));
                bw.newLine();
            }
        }
    }

    public void deleteTask(TodoItem item){
        todoItems.remove(item);
    }

    public static void addTodoItem(TodoItem todoItem){
        todoItems.add(todoItem);
    }
}
