package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DbConnectionController extends BaseFXController {

	private static final Logger _LOG = LoggerFactory.getLogger(DbConnectionController.class);

	@FXML
	private TextField nameField;
	@FXML
	private TextField hostField;
	@FXML
	private TextField portField;
	@FXML
	private TextField userNameField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField schemaField;
	@FXML
	private ChoiceBox<String> encodingChoice;
	@FXML
	private ChoiceBox<String> dbTypeChoice;
	private MainUIController mainUIController;
	private boolean isUpdate = false;
	private Integer primayKey;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	void saveConnection() {
		DatabaseConfig config = extractConfigForUI();
		if (config == null) {
			return;
		}
		try {
			ConfigHelper.saveDatabaseConfig(this.isUpdate, primayKey, config);
			getDialogStage().close();
			mainUIController.loadLeftDBTree();
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			AlertUtil.showErrorAlert(e.getMessage());
		}
	}

	@FXML
	void testConnection() {
		DatabaseConfig config = extractConfigForUI();
		if (config == null) {
			return;
		}
		try {
			DbUtil.getConnection(config);
			AlertUtil.showInfoAlert("连接成功");
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			AlertUtil.showWarnAlert("连接失败");
		}

	}

	@FXML
	void cancel() {
		getDialogStage().close();
	}

	void setMainUIController(MainUIController controller) {
		this.mainUIController = controller;
	}

	private DatabaseConfig extractConfigForUI() {
		String name = nameField.getText();
		String host = hostField.getText();
		String port = portField.getText();
		String userName = userNameField.getText();
		String password = passwordField.getText();
		String encoding = encodingChoice.getValue();
		String dbType = dbTypeChoice.getValue();
		String schema = schemaField.getText();
		DatabaseConfig config = new DatabaseConfig();
		config.setName(name);
		config.setDbType(dbType);
		config.setHost(host);
		config.setPort(port);
		config.setUsername(userName);
		config.setPassword(password);
		config.setSchema(schema);
		config.setEncoding(encoding);
		if (StringUtils.isAnyEmpty(name, host, port, userName, encoding, dbType, schema)) {
			AlertUtil.showWarnAlert("密码以外其他字段必填");
			return null;
		}
		return config;
	}

	public void setConfig(DatabaseConfig config) {
		isUpdate = true;
		primayKey = config.getId(); // save id for update config
		nameField.setText(config.getName());
		hostField.setText(config.getHost());
		portField.setText(config.getPort());
		userNameField.setText(config.getUsername());
		passwordField.setText(config.getPassword());
		encodingChoice.setValue(config.getEncoding());
		dbTypeChoice.setValue(config.getDbType());
		schemaField.setText(config.getSchema());
	}

}
