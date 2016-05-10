package com.zzg.mybatis.generator.controller;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class BaseFXController implements Initializable {
	private static final Logger _LOG = LoggerFactory.getLogger(BaseFXController.class);
	public static final String ASSIT_NAME = "";
	public static final int WIDTH = 864;

	static final String BASE_DIR = "fxml/";
	static final String FXML_SETTING = "fxml/setting.fxml";

	private Stage primaryStage;
	private Stage dialogStage;

	private static Map<String, SoftReference<Parent>> cacheNodeMap = new HashMap<String, SoftReference<Parent>>();

	public void loadNewFXMLPageContent(String fxmlFileName) {
		Stage primaryStage = getPrimaryStage();
		primaryStage.setTitle(ASSIT_NAME);
		primaryStage.setWidth(WIDTH);

		BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
		Parent parentNode = getParentNode(fxmlFileName);
		root.setCenter(parentNode);

		primaryStage.show();
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
