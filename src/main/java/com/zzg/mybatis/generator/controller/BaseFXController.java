package com.zzg.mybatis.generator.controller;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class BaseFXController implements Initializable {
    private static final Logger _LOG = LoggerFactory.getLogger(BaseFXController.class);

    private Stage primaryStage;
    private Stage dialogStage;

    private static Map<String, SoftReference<Parent>> cacheNodeMap = new HashMap<String, SoftReference<Parent>>();

    public <T extends BaseFXController> T loadFXMLPage(String title, FXMLPage fxmlPage) {
        URL skeletonResource = Thread.currentThread().getContextClassLoader().getResource(fxmlPage.getFxml());
        FXMLLoader loader = new FXMLLoader(skeletonResource);
        Parent loginNode;
        try {
            loginNode = loader.load();
            T controller = loader.getController();
            final Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(getPrimaryStage());
            dialogStage.setScene(new Scene(loginNode));
            dialogStage.setMaximized(false);
            dialogStage.setResizable(false);
            dialogStage.show();
            controller.setDialogStage(dialogStage);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            _LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private Parent getParentNode(String fxmlFileName) {
        Parent parentNode;
        SoftReference<Parent> parentNodeRef = cacheNodeMap.get(fxmlFileName);
        if (parentNodeRef == null || parentNodeRef.get() == null) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(fxmlFileName);
            FXMLLoader loader = new FXMLLoader(url);
            try {
                parentNode = loader.load();
                BaseFXController controller = loader.getController();
                controller.setPrimaryStage(getPrimaryStage());
                cacheNodeMap.put(fxmlFileName, new SoftReference<>(parentNode));
            } catch (IOException e) {
                _LOG.error(e.getMessage(), e);
                throw new RuntimeException("load fxml error");
            }
        } else {
            parentNode = parentNodeRef.get();
        }
        return parentNode;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
