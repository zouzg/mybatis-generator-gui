package com.zzg.mybatis.generator.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

/**
 * Project: mybatis-generator-gui
 *
 * @author slankka on 2018/12/30.
 */
public class OverSshController extends DbConnectionController {

    private Logger logger = LoggerFactory.getLogger(OverSshController.class);

    @FXML
    public HBox pubkeyBox;
    @FXML
    public Label lPortLabel;
    @FXML
    public TextField sshUserField;
    @FXML
    public ChoiceBox<String> authTypeChoice;
    @FXML
    public Label sshPasswordLabel;
    @FXML
    public PasswordField sshPasswordField;
    @FXML
    private TextField sshHostField;
    @FXML
    private TextField sshdPortField;
    @FXML
    private TextField lportField;
    @FXML
    private TextField rportField;
    @FXML
    private Label note;
    @FXML
    private Label pubkeyBoxLabel;
    @FXML
    private TextField sshPubKeyField;
    @FXML
    public PasswordField sshPubkeyPasswordField;
    @FXML
    public Label sshPubkeyPasswordLabel;
    @FXML
    public Label sshPubkeyPasswordNote;

    private FileChooser fileChooser = new FileChooser();

    private File privateKey;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.setTitle("选择SSH秘钥文件");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        authTypeChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("PubKey".equals(newValue)) {
                //公钥认证
                sshPasswordField.setVisible(false);
                sshPasswordLabel.setVisible(false);
                pubkeyBox.setVisible(true);
                pubkeyBoxLabel.setVisible(true);
                sshPubkeyPasswordField.setVisible(true);
                sshPubkeyPasswordLabel.setVisible(true);
                sshPubkeyPasswordNote.setVisible(true);
            }else {
                //密码认证
                pubkeyBox.setVisible(false);
                pubkeyBoxLabel.setVisible(false);
                sshPubkeyPasswordField.setVisible(false);
                sshPubkeyPasswordLabel.setVisible(false);
                sshPubkeyPasswordNote.setVisible(false);
                sshPasswordLabel.setVisible(true);
                sshPasswordField.setVisible(true);
            }
        });
    }

    public void setDbConnectionConfig(DatabaseConfig databaseConfig) {
        if (databaseConfig == null) {
            return;
        }
        isUpdate = true;
        super.setConfig(databaseConfig);
        this.sshdPortField.setText(databaseConfig.getSshPort());
        this.sshHostField.setText(databaseConfig.getSshHost());
        this.lportField.setText(databaseConfig.getLport());
        this.rportField.setText(databaseConfig.getRport());
        this.sshUserField.setText(databaseConfig.getSshUser());
        this.sshPasswordField.setText(databaseConfig.getSshPassword());
        //例如：默认从本机的 3306 -> 转发到 3306
        if (StringUtils.isBlank(this.lportField.getText())) {
            this.lportField.setText(databaseConfig.getPort());
        }
        if (StringUtils.isBlank(this.rportField.getText())) {
            this.rportField.setText(databaseConfig.getPort());
        }
        if (StringUtils.isNotBlank(databaseConfig.getPrivateKey())) {
            this.sshPubKeyField.setText(databaseConfig.getPrivateKey());
            this.sshPubkeyPasswordField.setText(databaseConfig.getPrivateKeyPassword());
            authTypeChoice.getSelectionModel().select("PubKey");
        }
        checkInput();
    }

    @FXML
    public void checkInput() {
        DatabaseConfig databaseConfig = extractConfigFromUi();
        if (authTypeChoice.getValue().equals("Password") && (
            StringUtils.isBlank(databaseConfig.getSshHost())
                || StringUtils.isBlank(databaseConfig.getSshPort())
                || StringUtils.isBlank(databaseConfig.getSshUser())
                || StringUtils.isBlank(databaseConfig.getSshPassword())
        )
            || authTypeChoice.getValue().equals("PubKey") && (
            StringUtils.isBlank(databaseConfig.getSshHost())
                || StringUtils.isBlank(databaseConfig.getSshPort())
                || StringUtils.isBlank(databaseConfig.getSshUser())
                || StringUtils.isBlank(databaseConfig.getPrivateKey())
        )
        ) {
            note.setText("当前SSH配置输入不完整，OVER SSH不生效");
            note.setTextFill(Paint.valueOf("#ff666f"));
        } else {
            note.setText("当前SSH配置生效");
            note.setTextFill(Paint.valueOf("#5da355"));
        }
    }

    public void setLPortLabelText(String text) {
        lPortLabel.setText(text);
    }

    public void recoverNotice() {
        this.lPortLabel.setText("注意不要填写被其他程序占用的端口");
    }

    public DatabaseConfig extractConfigFromUi() {
        String name = nameField.getText();
        String host = hostField.getText();
        String port = portField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String encoding = encodingChoice.getValue();
        String dbType = dbTypeChoice.getValue();
        String schema = schemaField.getText();
        String authType = authTypeChoice.getValue();
        DatabaseConfig config = new DatabaseConfig();
        config.setName(name);
        config.setDbType(dbType);
        config.setHost(host);
        config.setPort(port);
        config.setUsername(userName);
        config.setPassword(password);
        config.setSchema(schema);
        config.setEncoding(encoding);
        config.setSshHost(this.sshHostField.getText());
        config.setSshPort(this.sshdPortField.getText());
        config.setLport(this.lportField.getText());
        config.setRport(this.rportField.getText());
        config.setSshUser(this.sshUserField.getText());
        config.setSshPassword(this.sshPasswordField.getText());
        if ("PubKey".equals(authType)) {
            config.setPrivateKey(this.privateKey.getAbsolutePath());
            config.setPrivateKeyPassword(this.sshPubkeyPasswordField.getText());
        }
        return config;
    }

    public void saveConfig() {
        DatabaseConfig databaseConfig = extractConfigFromUi();
        if (StringUtils.isAnyEmpty(
                databaseConfig.getName(),
                databaseConfig.getHost(),
                databaseConfig.getPort(),
                databaseConfig.getUsername(),
                databaseConfig.getEncoding(),
                databaseConfig.getDbType(),
                databaseConfig.getSchema())) {
            AlertUtil.showWarnAlert("密码以外其他字段必填");
            return;
        }
        try {
            ConfigHelper.saveDatabaseConfig(this.isUpdate, primayKey, databaseConfig);
            this.tabPaneController.getDialogStage().close();
            mainUIController.loadLeftDBTree();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void testSSH() {
        Session session = DbUtil.getSSHSession(extractConfigFromUi());
        if (session == null) {
            AlertUtil.showErrorAlert("请检查主机，端口，用户名，以及密码/秘钥是否填写正确");
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> result = executorService.submit(() -> {
            try {
                session.connect();
            } catch (JSchException e) {
                logger.error("Connect Over SSH failed", e);
                throw new RuntimeException(e.getMessage());
            }
        });
        executorService.shutdown();
        try {
            boolean b = executorService.awaitTermination(5, TimeUnit.SECONDS);
            if (!b) {
                throw new TimeoutException("连接超时");
            }
            result.get();
            AlertUtil.showInfoAlert("连接SSH服务器成功，恭喜你可以使用OverSSH功能");
            recoverNotice();
        } catch (Exception e) {
            AlertUtil.showErrorAlert("请检查主机，端口，用户名，以及密码/秘钥是否填写正确: " + e.getMessage());
        } finally {
            DbUtil.shutdownPortForwarding(session);
        }
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        this.sshUserField.clear();
        this.sshPasswordField.clear();
        this.sshdPortField.clear();
        this.sshHostField.clear();
        this.lportField.clear();
        this.rportField.clear();
        this.sshPubKeyField.clear();
        recoverNotice();
    }

    public void choosePubKey(ActionEvent actionEvent) {
        this.privateKey = fileChooser.showOpenDialog(getDialogStage());
        sshPubKeyField.setText(this.privateKey.getAbsolutePath());
    }
}
