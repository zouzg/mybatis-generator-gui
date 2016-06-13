package com.zzg.mybatis.generator.controller;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.util.DbUtil;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.ImmutableNode;
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
    private TextField modelTargetPackage;
    @FXML
    private TextField mapperTargetPackage;
    @FXML
    private TextField daoTargetPackage;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField domainObjectNameField;
    @FXML
    private TextField modelTargetProject;
    @FXML
    private TextField mappingTargetProject;
    @FXML
    private TextField daoTargetProject;
    @FXML
    private TextField projectFolderField;
    @FXML
    private TreeView<DatabaseConfig> leftDBTree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
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
                dialogStage.setMaximized(false);
                dialogStage.setResizable(false);
                dialogStage.show();
                controller.setDialogStage(dialogStage);
                controller.setMainUIController(this);
            } catch (Exception e) {
                _LOG.error(e.getMessage(), e);
            }
        });

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        loadLeftDBTree();
    }

    void loadLeftDBTree() {
        Configurations configs = new Configurations();
        try {
            XMLConfiguration config = configs.xml(new File("config.xml"));
            List<HierarchicalConfiguration<ImmutableNode>> list = config.childConfigurationsAt("");
            System.out.println(list);
            for (HierarchicalConfiguration<ImmutableNode> hc : list) {
                String name = hc.getRootElementName();
                DatabaseConfig dbConfig = new DatabaseConfig();
                dbConfig.setName(name);
                dbConfig.setHost(hc.getString("host"));
                dbConfig.setPort(hc.getString("port"));
                dbConfig.setUsername(hc.getString("userName"));
                dbConfig.setPassword(hc.getString("password"));
                dbConfig.setEncoding(hc.getString("encoding"));
                dbConfig.setDbType(hc.getString("dbType"));
                TreeItem<DatabaseConfig> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig);
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                treeItem.setGraphic(dbImage);
                treeItem.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    System.out.println("==============source: " + event.getSource());
                });
                leftDBTree.getRoot().getChildren().add(treeItem);
            }
            EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
                System.out.println(event.getSource());
                System.out.println(event.getTarget());
                Node node = event.getPickResult().getIntersectedNode();
                System.out.println("node: " + node);
                // Accept clicks only on node cells, and not on empty spaces of the TreeView
                if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                    DatabaseConfig selectedConfig = leftDBTree.getSelectionModel().getSelectedItem().getValue();
                    System.out.println("Node click: " + selectedConfig);
                    List<String> schemas = null;
                    try {
                        schemas = DbUtil.getSchemas(selectedConfig);
                        System.out.println(schemas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            leftDBTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
//            leftDBTree.getSelectionModel()
//                    .selectedItemProperty()
//                    .addListener((observable, oldValue, newValue) -> {
//                                DatabaseConfig selectedConfig = newValue.getValue();
//                                try {
//                                    List<String> schemas = DbUtil.getSchemas(selectedConfig);
//                                    System.out.println(schemas);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                    );
        } catch (Exception e) {
            e.printStackTrace();
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
        MultipleSelectionModel<TreeItem<DatabaseConfig>> selectionModel = leftDBTree.getSelectionModel();
        DatabaseConfig dbConfig = selectionModel.getSelectedItem().getValue();
        String url = getDatabaseUrl(dbConfig);
        Configuration config = new Configuration();
        config.addClasspathEntry("mysql-connector-javaa-5.1.38.jar");
        Context context = new Context(ModelType.CONDITIONAL);
        config.addContext(context);
        // Table config
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(tableNameField.getText());
        tableConfig.setDomainObjectName(domainObjectNameField.getText());
        // JDBC config
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setDriverClass("com.mysql.jdbc.Driver");
        jdbcConfig.setConnectionURL("jdbc:mysql://localhost:3306/test?user=root&password=&useUnicode=true&characterEncoding=utf8&autoReconnect=true");
        jdbcConfig.setUserId("root");
        jdbcConfig.setPassword("root");
        // java model
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        modelConfig.setTargetPackage(modelTargetPackage.getText());
        modelConfig.setTargetProject(projectFolderField.getText() + "/" + modelTargetProject.getText());
        // Mapper config
        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
        mapperConfig.setTargetPackage(mapperTargetPackage.getText());
        mapperConfig.setTargetProject(projectFolderField.getText() + "/" + mappingTargetProject.getText());
        // DAO
        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        daoConfig.setTargetPackage(daoTargetPackage.getText());
        daoConfig.setTargetProject(projectFolderField.getText() + "/" + daoTargetProject.getText());

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
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
        myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
    }

    private String getDatabaseUrl(DatabaseConfig dbConfig) {
        return "jdbc:mysql://localhost:3306/test?user=root&password=&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
    }

}
