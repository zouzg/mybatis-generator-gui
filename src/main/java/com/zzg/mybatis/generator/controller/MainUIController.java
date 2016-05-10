package com.zzg.mybatis.generator.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzg.mybatis.generator.model.DatabaseDTO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainUIController extends BaseFXController {
	
	private static final Logger _LOG = LoggerFactory.getLogger(MainUIController.class);
	
	// tool bar buttons
	@FXML
	private Label connectionLabel;

	@FXML
	private ChoiceBox<DatabaseDTO> dbTypeChoice;

	@FXML
	private TextField driverClassField;

	@FXML
	private TextField connectorFileField;
	@FXML
	private TextField connectionUrlField;
	@FXML
	private TextField userNameField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField modelFolderField;
	@FXML
	private TextField mapperFolderField;
	@FXML
	private TextField daoFolderField;
	@FXML
	private TextField tableNameField;
	@FXML
	private TextField domainObjectNameField;
	@FXML
	private TextField packageNameField;
	@FXML
	private TextField projectFolderField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ImageView dbImage = new ImageView("icons/database.png");
		dbImage.setFitHeight(40);
		dbImage.setFitWidth(40);
		connectionLabel.setGraphic(dbImage);
		connectionLabel.setOnMouseClicked(event -> {
			URL skeletonResource = Thread.currentThread().getContextClassLoader().getResource("fxml/newConnection.fxml");
			FXMLLoader loader = new FXMLLoader(skeletonResource);
			Parent loginNode;
			try {
				loginNode = loader.load();
				NewConnectionController controller = loader.getController();
				final Stage dialogStage = new Stage();
				dialogStage.setTitle("New Connection");
				dialogStage.initModality(Modality.APPLICATION_MODAL);
				dialogStage.initOwner(getPrimaryStage());
				dialogStage.setScene(new Scene(loginNode));
				dialogStage.show();
				controller.setDialogStage(dialogStage);
			} catch (Exception e) {
				_LOG.error(e.getMessage(), e);
			}
		});
		
		dbTypeChoice.setOnKeyPressed(event -> {
			String driverClass = dbTypeChoice.getSelectionModel().getSelectedItem().getDriverClass();
			driverClassField.setText(driverClass);
		});
		driverClassField.setText("com.mysql.jdbc.Driver");
	}

	@FXML
	public void chooseConnectorFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select " + dbTypeChoice.getSelectionModel().getSelectedItem().getName() + " Connector jar file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("jar file", "*.jar"));
		File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
		if (selectedFile != null) {
			connectorFileField.setText(selectedFile.getAbsolutePath());
		}
	}

	@FXML
	public void chooseModelFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
		if (selectedFolder != null) {
			modelFolderField.setText(selectedFolder.getAbsolutePath());
		}
	}

	@FXML
	public void chooseMapperFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
		if (selectedFolder != null) {
			mapperFolderField.setText(selectedFolder.getAbsolutePath());
		}
	}

	@FXML
	public void chooseDaoFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
		if (selectedFolder != null) {
			daoFolderField.setText(selectedFolder.getAbsolutePath());
		}
	}
	
	@FXML
	public void chooseProjectFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
		if (selectedFolder != null) {
			projectFolderField.setText(selectedFolder.getAbsolutePath());
		}
	}

	@FXML
	public void generateCode() throws Exception {
		Configuration config = new Configuration();
		config.addClasspathEntry(connectorFileField.getText());
		Context context = new Context(ModelType.CONDITIONAL);
		config.addContext(context);
		// Table config
		TableConfiguration tableConfig = new TableConfiguration(context);
		tableConfig.setTableName(tableNameField.getText());
		tableConfig.setDomainObjectName(domainObjectNameField.getText());
		// JDBC config
		JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
		jdbcConfig.setDriverClass(driverClassField.getText());
		jdbcConfig.setConnectionURL(connectionUrlField.getText());
		jdbcConfig.setUserId(userNameField.getText());
		jdbcConfig.setPassword(passwordField.getText());
		// java model
		JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
		modelConfig.setTargetPackage(packageNameField.getText());
		modelConfig.setTargetProject(projectFolderField.getText());
		// Mapper config
		SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
		mapperConfig.setTargetPackage(packageNameField.getText());
		mapperConfig.setTargetProject(projectFolderField.getText());
		// DAO
		JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
		daoConfig.setConfigurationType("XMLMAPPER");
		daoConfig.setTargetPackage(packageNameField.getText());
		daoConfig.setTargetProject(projectFolderField.getText());

		context.setId("myid");
		context.addTableConfiguration(tableConfig);
		context.setJdbcConnectionConfiguration(jdbcConfig);
		context.setJdbcConnectionConfiguration(jdbcConfig);
		context.setJavaModelGeneratorConfiguration(modelConfig);
		context.setSqlMapGeneratorConfiguration(mapperConfig);
		context.setJavaClientGeneratorConfiguration(daoConfig);
		
		context.setTargetRuntime("MyBatis3");

		List<String> warnings = new ArrayList<>();
		Set<String> fullyqualifiedTables = new HashSet<String>();
		Set<String> contexts = new HashSet<String>();
		ProgressCallback progressCallback = new VerboseProgressCallback();
		
		ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback , warnings);
		myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
	}

}
