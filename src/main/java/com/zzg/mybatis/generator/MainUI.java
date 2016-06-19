package com.zzg.mybatis.generator;

import java.net.URL;

import com.zzg.mybatis.generator.controller.MainUIController;

import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.XMLConfigHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/MainUI.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        MainUIController controller = fxmlLoader.getController();
        XMLConfigHelper.createEmptyFiles();
        GeneratorConfig config = XMLConfigHelper.loadGeneratorConfig();
        if (config != null) {
            controller.setGeneratorConfigIntoUI(config);
        }
        controller.setPrimaryStage(primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            GeneratorConfig generatorConfig = controller.getGeneratorConfigFromUI();
            try {
                XMLConfigHelper.saveGeneratorConfig(generatorConfig);
            } catch (Exception e) {
                e.printStackTrace(); //TODO
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}
