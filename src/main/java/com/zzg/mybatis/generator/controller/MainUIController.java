package com.zzg.mybatis.generator.controller;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.util.StringUtils;
import com.zzg.mybatis.generator.util.XMLConfigHelper;
import com.zzg.mybatis.generator.view.LeftDbTreeCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.*;
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
    private TextField connectorPathField;
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
    private TreeView<String> leftDBTree;
    @FXML
    private TextArea consoleTextArea;

    private DatabaseConfig selectedDatabaseConfig;

    private String tableName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        connectionLabel.setGraphic(dbImage);
        connectionLabel.setOnMouseClicked(event -> {
            NewConnectionController controller = loadFXMLPage("New Connection", FXMLPage.NEW_CONNECTION);
            controller.setMainUIController(this);
        });

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
        leftDBTree.setCellFactory((TreeView<String> tv) -> {
            TreeCell<String> cell = defaultCellFactory.call(tv);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() == 2) {
                    int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
                    TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
                    TreeItem<String> item = treeCell.getTreeItem();
                    if (level == 1) {
                        DatabaseConfig selectedConfig = (DatabaseConfig) item.getGraphic().getUserData();
                        // Accept clicks only on node cells, and not on empty spaces of the TreeView
                        leftDBTree.getSelectionModel().getSelectedItem().setExpanded(true);
                        System.out.println("Node click: " + selectedConfig);
                        List<String> schemas = null;
                        try {
                            schemas = DbUtil.getSchemas(selectedConfig);
                            System.out.println(schemas);
                            if (schemas != null && schemas.size() > 0) {
                                ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                children.clear();
                                for (String schema : schemas) {
                                    TreeItem<String> treeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/database.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    treeItem.setGraphic(imageView);
                                    treeItem.setValue(schema);
                                    children.add(treeItem);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (level == 2) {
                        System.out.println("index: " + leftDBTree.getSelectionModel().getSelectedIndex());
                        DatabaseConfig selectedConfig = (DatabaseConfig) item.getParent().getGraphic().getUserData();
                        String schema = treeCell.getTreeItem().getValue();
                        item.setExpanded(true);
                        try {
                            List<String> tables = DbUtil.getTableNames(selectedConfig, schema);
                            if (tables != null && tables.size() > 0) {
                                for (String tableName : tables) {
                                    ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                    TreeItem<String> treeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/table.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    treeItem.setGraphic(imageView);
                                    treeItem.setValue(tableName);
                                    children.add(treeItem);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (level == 3) {
                        String tableName = treeCell.getTreeItem().getValue();
                        selectedDatabaseConfig = (DatabaseConfig)item.getParent().getParent().getGraphic().getUserData();
                        String schema = (String)item.getParent().getValue();
                        selectedDatabaseConfig.setSchema(schema);
                        this.tableName = tableName;
                        tableNameField.setText(tableName);
                        domainObjectNameField.setText(StringUtils.dbStringToCamelStyle(tableName));
                    }
                }
            });
            return cell;
        });
        loadLeftDBTree();
    }

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        List<DatabaseConfig> dbConfigs = null;
        try {
            dbConfigs = XMLConfigHelper.loadDatabaseConfig();
            for (DatabaseConfig dbConfig : dbConfigs) {
                TreeItem<String> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig.getName());
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(dbConfig);
                treeItem.setGraphic(dbImage);
                rootTreeItem.getChildren().add(treeItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // show error TODO
        }
    }

    @FXML
    public void chooseConnectorFile() {
        FileChooser directoryChooser = new FileChooser();
        File selectedFolder = directoryChooser.showOpenDialog(getPrimaryStage());
        if (selectedFolder != null) {
            connectorPathField.setText(selectedFolder.getAbsolutePath());
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
        config.addClasspathEntry(connectorPathField.getText());
        Context context = new Context(ModelType.CONDITIONAL);
        config.addContext(context);
        // Table config
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(tableNameField.getText());
        tableConfig.setDomainObjectName(domainObjectNameField.getText());
        // JDBC config
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setDriverClass(DbType.valueOf(selectedDatabaseConfig.getDbType()).getDriverClass());
        jdbcConfig.setConnectionURL(DbUtil.getConnectionUrlWithSchema(selectedDatabaseConfig));
        jdbcConfig.setUserId(selectedDatabaseConfig.getUsername());
        jdbcConfig.setPassword(selectedDatabaseConfig.getPassword());
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
        // Comment
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.addProperty("suppressAllComments", "true");
        commentConfig.addProperty("suppressDate", "true");

        context.setId("myid");
        context.addTableConfiguration(tableConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJavaModelGeneratorConfiguration(modelConfig);
        context.setSqlMapGeneratorConfiguration(mapperConfig);
        context.setJavaClientGeneratorConfiguration(daoConfig);
        context.setCommentGeneratorConfiguration(commentConfig);
        // limit/offset插件
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
        pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
        context.addPluginConfiguration(pluginConfiguration);

        context.setTargetRuntime("MyBatis3");

        List<String> warnings = new ArrayList<>();
        Set<String> fullyqualifiedTables = new HashSet<String>();
        Set<String> contexts = new HashSet<>();
        ProgressCallback progressCallback = new VerboseProgressCallback();

        ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
        myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
    }

    public GeneratorConfig getGeneratorConfigFromUI() {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setConnectorJarPath(connectorPathField.getText());
        generatorConfig.setProjectFolder(projectFolderField.getText());
        generatorConfig.setModelPackage(modelTargetPackage.getText());
        generatorConfig.setModelPackageTargetFolder(modelTargetProject.getText());
        generatorConfig.setDaoPackage(daoTargetPackage.getText());
        generatorConfig.setDaoTargetFolder(daoTargetProject.getText());
        generatorConfig.setMappingXMLPackage(mapperTargetPackage.getText());
        generatorConfig.setMappingXMLTargetFolder(mappingTargetProject.getText());
        return generatorConfig;
    }

    public void setGeneratorConfigIntoUI(GeneratorConfig generatorConfig) {
        connectorPathField.setText(generatorConfig.getConnectorJarPath());
        projectFolderField.setText(generatorConfig.getProjectFolder());
        modelTargetPackage.setText(generatorConfig.getModelPackage());
        modelTargetProject.setText(generatorConfig.getModelPackage());
        daoTargetPackage.setText(generatorConfig.getDaoPackage());
        daoTargetProject.setText(generatorConfig.getDaoPackage());
        mapperTargetPackage.setText(generatorConfig.getMappingXMLPackage());
        mappingTargetProject.setText(generatorConfig.getMappingXMLTargetFolder());
    }

    @FXML
    public void openTableColumnCustomizationPage() {
        SelectTableColumnController controller = loadFXMLPage("Select Columns", FXMLPage.SELECT_TABLE_COLUMN);
        controller.setMainUIController(this);
        try {
            List<String> tableColumns = DbUtil.getTableColumns(selectedDatabaseConfig, selectedDatabaseConfig.getSchema(), tableName);
            controller.setColumnList(tableColumns);
        } catch (Exception e) {
            e.printStackTrace();
            _LOG.error(e.getMessage(), e);
        }
    }

}
