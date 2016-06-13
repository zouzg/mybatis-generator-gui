package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.DbUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;

public class NewConnectionController extends BaseFXController {

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
    private CheckBox savePwdCheckBox;
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

        Configurations configs = new Configurations();
        try {
            // obtain the configuration
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder("config.xml");
            XMLConfiguration config = builder.getConfiguration();

            // update property
            config.addProperty(name + ".dbType", dbType);
            config.addProperty(name + ".host", host);
            config.addProperty(name + ".port", port);
            config.addProperty(name + ".userName", userName);
            config.addProperty(name + ".password", password);
            config.addProperty(name + ".encoding", encoding);

            // save configuration
            builder.save();

            getDialogStage().close();
            mainUIController.loadLeftDBTree();
        } catch (ConfigurationException cex) {
            // Something went wrong
            cex.printStackTrace();
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
        config.setDbType(dbType);
        config.setHost(host);
        config.setPort(port);
        config.setEncoding(encoding);
        String url = config.getConnectionUrl();
        System.out.println(url);
        try {
            DbUtil.getConnection(config);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Connection success");
            alert.show();
        } catch (Exception e) {
            //e.printStackTrace();
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
