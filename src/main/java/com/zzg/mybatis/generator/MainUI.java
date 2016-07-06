package com.zzg.mybatis.generator;

import java.net.URL;

import com.zzg.mybatis.generator.controller.MainUIController;

import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.XMLConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainUI extends Application {

    private static final Logger _LOG = LoggerFactory.getLogger(MainUI.class);

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
                _LOG.error(e.getMessage(), e);
                AlertUtil.showErrorAlert(e.getMessage());
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}
