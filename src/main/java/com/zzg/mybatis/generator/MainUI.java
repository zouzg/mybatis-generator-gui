package com.zzg.mybatis.generator;

import java.net.URL;

import com.zzg.mybatis.generator.controller.MainUIController;

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
		//primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		
		MainUIController controller = fxmlLoader.getController();
		controller.setPrimaryStage(primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
