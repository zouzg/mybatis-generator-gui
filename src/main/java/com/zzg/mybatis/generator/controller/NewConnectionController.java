package com.zzg.mybatis.generator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.net.URL;
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

        Configurations configs = new Configurations();
        try {
            // obtain the configuration
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder("config.xml");
            XMLConfiguration config = builder.getConfiguration();

            // update property
            config.addProperty(name + ".host", host);
            config.addProperty(name + ".port", port);
            config.addProperty(name + ".userName", userName);
            config.addProperty(name + ".password", password);
            config.addProperty(name + ".encoding", encoding);

            // save configuration
            builder.save();

            getDialogStage().close();

            Iterator<String> iterator = config.getKeys("config");
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (ConfigurationException cex) {
            // Something went wrong
            cex.printStackTrace();
        }
    }

    @FXML
    void cancel() {
        getDialogStage().close();
    }

}
