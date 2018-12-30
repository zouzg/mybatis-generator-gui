package com.zzg.mybatis.generator.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

/**
 * Project: mybatis-generator-gui
 *
 * @author slankka on 2018/12/30.
 */
public class OverSshController extends BaseFXController {
    private Logger logger = LoggerFactory.getLogger(OverSshController.class);
    @FXML
    public TextField sshUserField;
    @FXML
    public PasswordField sshPasswordField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField sshdPortField;
    @FXML
    private TextField lportField;
    @FXML
    private TextField rportField;

    private DbConnectionController dbConnectionController;

    private DatabaseConfig databaseConfig;

    public void setDbConnectionController(DbConnectionController dbConnectionController) {
        this.dbConnectionController = dbConnectionController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setDbConnectionConfig(DatabaseConfig extractConfigForUI) {
        this.databaseConfig = extractConfigForUI;
        this.sshdPortField.setText(this.databaseConfig.getSshPort());
        this.hostField.setText(this.databaseConfig.getSshHost());
        this.lportField.setText(this.databaseConfig.getLport());
        this.rportField.setText(this.databaseConfig.getRport());
        this.sshUserField.setText(this.databaseConfig.getSshUser());
        this.sshPasswordField.setText(this.databaseConfig.getSshPassword());
        //例如：默认从本机的 3306 -> 转发到 3306
        if (StringUtils.isBlank(this.lportField.getText())){
            this.lportField.setText(this.databaseConfig.getPort());
        }
        if (StringUtils.isBlank(this.rportField.getText())) {
            this.rportField.setText(this.databaseConfig.getPort());
        }
    }

    private DatabaseConfig extractConfigFromUi() {
        DatabaseConfig config = new DatabaseConfig();
        config.setSshHost(this.hostField.getText());
        config.setSshPort(this.sshdPortField.getText());
        config.setLport(this.lportField.getText());
        config.setRport(this.rportField.getText());
        config.setSshUser(this.sshUserField.getText());
        config.setSshPassword(this.sshPasswordField.getText());
        return config;
    }

    @FXML
    public void saveConfig(ActionEvent actionEvent) {
        databaseConfig.setSshHost(this.hostField.getText());
        databaseConfig.setSshPort(this.sshdPortField.getText());
        databaseConfig.setLport(this.lportField.getText());
        databaseConfig.setRport(this.rportField.getText());
        databaseConfig.setSshUser(this.sshUserField.getText());
        databaseConfig.setSshPassword(this.sshPasswordField.getText());
        this.dbConnectionController.saveConnection();
    }

    @FXML
    public void testSSH(ActionEvent actionEvent) {
        Session session = DbUtil.getSSHSession(extractConfigFromUi());
        if (session == null) {
            AlertUtil.showErrorAlert("请检查主机，端口，用户名，以及密码是否填写正确");
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
        } catch (Exception e) {
            AlertUtil.showErrorAlert("请检查主机，端口，用户名，以及密码是否填写正确: " + e.getMessage());
        }finally {
            DbUtil.shutdownPortForwarding(session);
        }
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        this.sshUserField.clear();
        this.sshPasswordField.clear();
        this.sshdPortField.clear();
        this.hostField.clear();
        this.lportField.clear();
        this.rportField.clear();
    }
}
