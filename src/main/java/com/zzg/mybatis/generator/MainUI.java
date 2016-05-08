package com.zzg.mybatis.generator;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		URL skeletonResource = classLoader.getResource("MainUI.fxml");

		Parent root = FXMLLoader.load(skeletonResource);
		
		primaryStage.setScene(new Scene(root));

		primaryStage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
