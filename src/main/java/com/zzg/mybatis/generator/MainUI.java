package com.zzg.mybatis.generator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zzg.mybatis.generator.controller.MainUIController;

import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * 这是本软件的主入口,要运行本软件请直接运行本类就可以了,不用传入任何参数
 */
public class MainUI extends Application {

	private static final Logger _LOG = LoggerFactory.getLogger(MainUI.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConfigHelper.createEmptyFiles();
		URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/MainUI.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		Parent root = fxmlLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		MainUIController controller = fxmlLoader.getController();
		controller.setPrimaryStage(primaryStage);
	}

	public static void main(String[] args) {
		String version = System.getProperty("java.version");
		if (Integer.parseInt(version.substring(2, 3)) >= 8 && Integer.parseInt(version.substring(6)) >= 60) {
			launch(args);
		} else {
			JFrame jFrame = new JFrame("版本错误");
			jFrame.setSize(500, 100);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel jPanel = new JPanel();
			JLabel jLabel = new JLabel("JDK的版本不能低于1.8.0.60，请升级至最近的JDK 1.8再运行此软件");
			jPanel.add(jLabel);
			jFrame.add(jPanel);
			jFrame.setLocationRelativeTo(null);
			jFrame.setVisible(true);

		}
	}

}
