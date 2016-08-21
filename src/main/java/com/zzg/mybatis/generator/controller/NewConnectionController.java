package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class NewConnectionController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(NewConnectionController.class);

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
//    @FXML
//    private CheckBox savePwdCheckBox;
    @FXML
    private TextField schemaField;
    @FXML
    private ChoiceBox<String> encodingChoice;
    @FXML
    private ChoiceBox<String> dbTypeChoice;
    private MainUIController mainUIController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void saveConnection() {
        String name = nameField.getText();
        String host = hostField.getText();
        String port = portField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String encoding = encodingChoice.getValue();
        String dbType = dbTypeChoice.getValue();

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setHost(host);
        dbConfig.setPort(port);
        dbConfig.setDbType(dbType);
        dbConfig.setUsername(userName);
        dbConfig.setPassword(password);
//        if (savePwdCheckBox.isSelected()) {
//        }
        dbConfig.setSchema(schemaField.getText());
        dbConfig.setEncoding(encoding);
        try {
            ConfigHelper.saveDatabaseConfig(name, dbConfig);
            getDialogStage().close();
            mainUIController.loadLeftDBTree();
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    void testConnection() {
        String name = nameField.getText();
        String host = hostField.getText();
        String port = portField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String encoding = encodingChoice.getValue();
        String dbType = dbTypeChoice.getValue();
        DatabaseConfig config = new DatabaseConfig();
        config.setName(name);
        config.setDbType(dbType);
        config.setHost(host);
        config.setPort(port);
        config.setUsername(userName);
        config.setPassword(password);
        config.setSchema(schemaField.getText());
        config.setEncoding(encoding);
        String url = DbUtil.getConnectionUrlWithSchema(config);
        System.out.println(url);
        try {
            DbUtil.getConnection(config);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Connection success");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Connection failed");
            alert.show();
        }

    }

    @FXML
    void cancel() {
        getDialogStage().close();
    }
    
    void setMainUIController(MainUIController controller) {
        this.mainUIController = controller;
    }

}
