package com.nova.todolist.datamodel;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.time.LocalDate;

public class TodoItem {

    private String title;
    private String description;
    private LocalDate date;

    public TodoItem(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return title;
    }
}
