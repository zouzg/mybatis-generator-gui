package com.zzg.mybatis.generator.view;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.mybatis.generator.api.ProgressCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen on 6/21/16.
 */
public class UIProgressCallback implements ProgressCallback {

    private TextArea consoleTextArea;

    private StringBuilder sb;

    public UIProgressCallback(TextArea textArea) {
        sb = new StringBuilder();
        this.consoleTextArea = textArea;
    }

    @Override
    public void introspectionStarted(int totalTasks) {
        sb.append("introspection started\n");
        consoleTextArea.setText(sb.toString());
    }

    @Override
    public void generationStarted(int totalTasks) {
        sb.append("generate started\n");
        consoleTextArea.setText(sb.toString());
    }

    @Override
    public void saveStarted(int totalTasks) {
        sb.append("save started\n");
        consoleTextArea.setText(sb.toString());
    }

    @Override
    public void startTask(String taskName) {
        sb.append("start task\n");
        consoleTextArea.setText(sb.toString());
    }

    @Override
    public void done() {
        sb.append("generation done\n");
        consoleTextArea.setText(sb.toString());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Generation Completed");
        alert.show();
    }

    @Override
    public void checkCancel() throws InterruptedException {
    }
}
