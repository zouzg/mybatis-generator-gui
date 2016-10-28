package com.zzg.mybatis.generator.view;

import javafx.scene.control.Alert;

/**
 * Created by Owen on 6/21/16.
 */
public class AlertUtil {

    public static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    public static void showWarnAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * build both OK and Cancel buttons for the user
     * to click on to dismiss the dialog.
     *
     * @param message
     */
    public static Alert buildConfirmationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        return alert;
    }

}
