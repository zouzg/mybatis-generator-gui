package com.zzg.mybatis.generator.controller;

import com.jcraft.jsch.Session;
import com.zzg.mybatis.generator.bridge.MybatisGeneratorBridge;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.util.MyStringUtils;
import com.zzg.mybatis.generator.view.AlertUtil;
import com.zzg.mybatis.generator.view.UIProgressCallback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.IgnoredColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainUIController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(MainUIController.class);
    private static final String FOLDER_NO_EXIST = "部分目录不存在，是否创建";
    // tool bar buttons
    @FXML
    private Label connectionLabel;
    @FXML
    private Label configsLabel;
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
    private TextField generateKeysField;	//主键ID
    @FXML
    private TextField modelTargetProject;
    @FXML
    private TextField mappingTargetProject;
    @FXML
    private TextField daoTargetProject;
    @FXML
    private TextField mapperName;
    @FXML
    private TextField projectFolderField;
    @FXML
    private CheckBox offsetLimitCheckBox;
    @FXML
    private CheckBox commentCheckBox;
    @FXML
	private CheckBox overrideXML;
    @FXML
    private CheckBox needToStringHashcodeEquals;
    @FXML
    private CheckBox forUpdateCheckBox;
    @FXML
    private CheckBox annotationDAOCheckBox;
    @FXML
    private CheckBox useTableNameAliasCheckbox;
    @FXML
    private CheckBox annotationCheckBox;
    @FXML
    private CheckBox useActualColumnNamesCheckbox;
    @FXML
    private CheckBox useExample;
    @FXML
    private CheckBox useDAOExtendStyle;
    @FXML
    private CheckBox useSchemaPrefix;
    @FXML
    private CheckBox jsr310Support;
    @FXML
    private TreeView<String> leftDBTree;
    // Current selected databaseConfig
    private DatabaseConfig selectedDatabaseConfig;
    // Current selected tableName
    private String tableName;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    @FXML
    private ChoiceBox<String> encodingChoice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        connectionLabel.setGraphic(dbImage);
        connectionLabel.setOnMouseClicked(event -> {
            TabPaneController controller = (TabPaneController) loadFXMLPage("新建数据库连接", FXMLPage.NEW_CONNECTION, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });
        ImageView configImage = new ImageView("icons/config-list.png");
        configImage.setFitHeight(40);
        configImage.setFitWidth(40);
        configsLabel.setGraphic(configImage);
        configsLabel.setOnMouseClicked(event -> {
            GeneratorConfigController controller = (GeneratorConfigController) loadFXMLPage("配置", FXMLPage.GENERATOR_CONFIG, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });
		useExample.setOnMouseClicked(event -> {
			if (useExample.isSelected()) {
				offsetLimitCheckBox.setDisable(false);
			} else {
				offsetLimitCheckBox.setDisable(true);
			}
		});

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
        leftDBTree.setCellFactory((TreeView<String> tv) -> {
            TreeCell<String> cell = defaultCellFactory.call(tv);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
                TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
                TreeItem<String> treeItem = treeCell.getTreeItem();
                if (level == 1) {
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("关闭连接");
                    item1.setOnAction(event1 -> treeItem.getChildren().clear());
	                MenuItem item2 = new MenuItem("编辑连接");
	                item2.setOnAction(event1 -> {
		                DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        TabPaneController controller = (TabPaneController) loadFXMLPage("编辑数据库连接", FXMLPage.NEW_CONNECTION, false);
		                controller.setMainUIController(this);
		                controller.setConfig(selectedConfig);
		                controller.showDialogStage();
	                });
                    MenuItem item3 = new MenuItem("删除连接");
                    item3.setOnAction(event1 -> {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        try {
                            ConfigHelper.deleteDatabaseConfig(selectedConfig);
                            this.loadLeftDBTree();
                        } catch (Exception e) {
                            AlertUtil.showErrorAlert("Delete connection failed! Reason: " + e.getMessage());
                        }
                    });
                    contextMenu.getItems().addAll(item1, item2, item3);
                    cell.setContextMenu(contextMenu);
                }
                if (event.getClickCount() == 2) {
                    if(treeItem == null) {
                        return ;
                    }
                    treeItem.setExpanded(true);
                    if (level == 1) {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        try {
                            List<String> tables = DbUtil.getTableNames(selectedConfig);
                            if (tables != null && tables.size() > 0) {
                                ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                children.clear();
                                for (String tableName : tables) {
                                    TreeItem<String> newTreeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/table.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    newTreeItem.setGraphic(imageView);
                                    newTreeItem.setValue(tableName);
                                    children.add(newTreeItem);
                                }
                            }
                        } catch (SQLRecoverableException e) {
                            _LOG.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert("连接超时");
                        } catch (Exception e) {
                            _LOG.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert(e.getMessage());
                        }
                    } else if (level == 2) { // left DB tree level3
                        String tableName = treeCell.getTreeItem().getValue();
                        selectedDatabaseConfig = (DatabaseConfig) treeItem.getParent().getGraphic().getUserData();
                        this.tableName = tableName;
                        tableNameField.setText(tableName);
                        domainObjectNameField.setText(MyStringUtils.dbStringToCamelStyle(tableName));
                        mapperName.setText(domainObjectNameField.getText().concat("DAO"));
                    }
                }
            });
            return cell;
        });
        loadLeftDBTree();
		setTooltip();
		//默认选中第一个，否则如果忘记选择，没有对应错误提示
        encodingChoice.getSelectionModel().selectFirst();
	}

	private void setTooltip() {
		encodingChoice.setTooltip(new Tooltip("生成文件的编码，必选"));
		generateKeysField.setTooltip(new Tooltip("insert时可以返回主键ID"));
		offsetLimitCheckBox.setTooltip(new Tooltip("是否要生成分页查询代码"));
		commentCheckBox.setTooltip(new Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
		useActualColumnNamesCheckbox.setTooltip(new Tooltip("是否使用数据库实际的列名作为实体类域的名称"));
		useTableNameAliasCheckbox.setTooltip(new Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询"));
		overrideXML.setTooltip(new Tooltip("重新生成时把原XML文件覆盖，否则是追加"));
        useDAOExtendStyle.setTooltip(new Tooltip("将通用接口方法放在公共接口中，DAO接口留空"));
        forUpdateCheckBox.setTooltip(new Tooltip("在Select语句中增加for update后缀"));
	}

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        try {
            List<DatabaseConfig> dbConfigs = ConfigHelper.loadDatabaseConfig();
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
            _LOG.error("connect db failed, reason: {}", e);
            AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
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
    public void generateCode() {
        if (tableName == null) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        String result = validateConfig();
		if (result != null) {
			AlertUtil.showErrorAlert(result);
			return;
		}
        GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
        if (!checkDirs(generatorConfig)) {
            return;
        }

        MybatisGeneratorBridge bridge = new MybatisGeneratorBridge();
        bridge.setGeneratorConfig(generatorConfig);
        bridge.setDatabaseConfig(selectedDatabaseConfig);
        bridge.setIgnoredColumns(ignoredColumns);
        bridge.setColumnOverrides(columnOverrides);
		UIProgressCallback alert = new UIProgressCallback(Alert.AlertType.INFORMATION);
		bridge.setProgressCallback(alert);
		alert.show();
		try {
            //Engage PortForwarding
            Session sshSession = DbUtil.getSSHSession(selectedDatabaseConfig);
            DbUtil.engagePortForwarding(sshSession, selectedDatabaseConfig);
            PictureProcessStateController pictureProcessStateController = null;
            if (sshSession != null) {
                pictureProcessStateController = new PictureProcessStateController();
                pictureProcessStateController.setDialogStage(getDialogStage());
                pictureProcessStateController.startPlay();
            }

            bridge.generate();

            if (pictureProcessStateController != null) {
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000);
                        return null;
                    }
                };
                PictureProcessStateController finalPictureProcessStateController = pictureProcessStateController;
                task.setOnSucceeded(event -> {
                    finalPictureProcessStateController.close();
                });
                new Thread(task).start();
            }
        } catch (Exception e) {
			e.printStackTrace();
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

	private String validateConfig() {
		String projectFolder = projectFolderField.getText();
		if (StringUtils.isEmpty(projectFolder))  {
			return "项目目录不能为空";
		}
		if (StringUtils.isEmpty(domainObjectNameField.getText()))  {
			return "类名不能为空";
		}
		if (StringUtils.isAnyEmpty(modelTargetPackage.getText(), mapperTargetPackage.getText(), daoTargetPackage.getText())) {
			return "包名不能为空";
		}

		return null;
	}

	@FXML
    public void saveGeneratorConfig() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("保存当前配置");
        dialog.setContentText("请输入配置名称");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (StringUtils.isEmpty(name)) {
                AlertUtil.showErrorAlert("名称不能为空");
                return;
            }
            _LOG.info("user choose name: {}", name);
            try {
                GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
                generatorConfig.setName(name);
                ConfigHelper.deleteGeneratorConfig(name);
                ConfigHelper.saveGeneratorConfig(generatorConfig);
            } catch (Exception e) {
                AlertUtil.showErrorAlert("保存配置失败");
            }
        }
    }

    public GeneratorConfig getGeneratorConfigFromUI() {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setProjectFolder(projectFolderField.getText());
        generatorConfig.setModelPackage(modelTargetPackage.getText());
        generatorConfig.setGenerateKeys(generateKeysField.getText());
        generatorConfig.setModelPackageTargetFolder(modelTargetProject.getText());
        generatorConfig.setDaoPackage(daoTargetPackage.getText());
        generatorConfig.setDaoTargetFolder(daoTargetProject.getText());
        generatorConfig.setMapperName(mapperName.getText());
        generatorConfig.setMappingXMLPackage(mapperTargetPackage.getText());
        generatorConfig.setMappingXMLTargetFolder(mappingTargetProject.getText());
        generatorConfig.setTableName(tableNameField.getText());
        generatorConfig.setDomainObjectName(domainObjectNameField.getText());
        generatorConfig.setOffsetLimit(offsetLimitCheckBox.isSelected());
        generatorConfig.setComment(commentCheckBox.isSelected());
        generatorConfig.setOverrideXML(overrideXML.isSelected());
        generatorConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEquals.isSelected());
        generatorConfig.setUseTableNameAlias(useTableNameAliasCheckbox.isSelected());
        generatorConfig.setNeedForUpdate(forUpdateCheckBox.isSelected());
        generatorConfig.setAnnotationDAO(annotationDAOCheckBox.isSelected());
        generatorConfig.setAnnotation(annotationCheckBox.isSelected());
        generatorConfig.setUseActualColumnNames(useActualColumnNamesCheckbox.isSelected());
        generatorConfig.setEncoding(encodingChoice.getValue());
        generatorConfig.setUseExample(useExample.isSelected());
        generatorConfig.setUseDAOExtendStyle(useDAOExtendStyle.isSelected());
        generatorConfig.setUseSchemaPrefix(useSchemaPrefix.isSelected());
        generatorConfig.setJsr310Support(jsr310Support.isSelected());
        return generatorConfig;
    }

    public void setGeneratorConfigIntoUI(GeneratorConfig generatorConfig) {
        projectFolderField.setText(generatorConfig.getProjectFolder());
        modelTargetPackage.setText(generatorConfig.getModelPackage());
        generateKeysField.setText(generatorConfig.getGenerateKeys());
        modelTargetProject.setText(generatorConfig.getModelPackageTargetFolder());
        daoTargetPackage.setText(generatorConfig.getDaoPackage());
		daoTargetProject.setText(generatorConfig.getDaoTargetFolder());
		mapperName.setText(generatorConfig.getMapperName());
		mapperTargetPackage.setText(generatorConfig.getMappingXMLPackage());
        mappingTargetProject.setText(generatorConfig.getMappingXMLTargetFolder());
        tableNameField.setText(generatorConfig.getTableName());
        domainObjectNameField.setText(generatorConfig.getDomainObjectName());
        offsetLimitCheckBox.setSelected(generatorConfig.isOffsetLimit());
        commentCheckBox.setSelected(generatorConfig.isComment());
        overrideXML.setSelected(generatorConfig.isOverrideXML());
        needToStringHashcodeEquals.setSelected(generatorConfig.isNeedToStringHashcodeEquals());
        useTableNameAliasCheckbox.setSelected(generatorConfig.getUseTableNameAlias());
        forUpdateCheckBox.setSelected(generatorConfig.isNeedForUpdate());
        annotationDAOCheckBox.setSelected(generatorConfig.isAnnotationDAO());
        annotationCheckBox.setSelected(generatorConfig.isAnnotation());
        useActualColumnNamesCheckbox.setSelected(generatorConfig.isUseActualColumnNames());
        encodingChoice.setValue(generatorConfig.getEncoding());
        useExample.setSelected(generatorConfig.isUseExample());
        useDAOExtendStyle.setSelected(generatorConfig.isUseDAOExtendStyle());
        useSchemaPrefix.setSelected(generatorConfig.isUseSchemaPrefix());
        jsr310Support.setSelected(generatorConfig.isJsr310Support());
        
    }

    @FXML
    public void openTableColumnCustomizationPage() {
        if (tableName == null) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        SelectTableColumnController controller = (SelectTableColumnController) loadFXMLPage("定制列", FXMLPage.SELECT_TABLE_COLUMN, true);
        controller.setMainUIController(this);
        try {
            // If select same schema and another table, update table data
            if (!tableName.equals(controller.getTableName())) {
                List<UITableColumnVO> tableColumns = DbUtil.getTableColumns(selectedDatabaseConfig, tableName);
                controller.setColumnList(FXCollections.observableList(tableColumns));
                controller.setTableName(tableName);
            }
            controller.showDialogStage();
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }

    /**
     * 检查并创建不存在的文件夹
     *
     * @return
     */
    private boolean checkDirs(GeneratorConfig config) {
		List<String> dirs = new ArrayList<>();
		dirs.add(config.getProjectFolder());
		dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getModelPackageTargetFolder())));
		dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getDaoTargetFolder())));
		dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getMappingXMLTargetFolder())));
		boolean haveNotExistFolder = false;
		for (String dir : dirs) {
			File file = new File(dir);
			if (!file.exists()) {
				haveNotExistFolder = true;
			}
		}
		if (haveNotExistFolder) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText(FOLDER_NO_EXIST);
			Optional<ButtonType> optional = alert.showAndWait();
			if (optional.isPresent()) {
				if (ButtonType.OK == optional.get()) {
					try {
						for (String dir : dirs) {
							FileUtils.forceMkdir(new File(dir));
						}
						return true;
					} catch (Exception e) {
						AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
					}
				} else {
					return false;
				}
			}
		}
        return true;
    }

    @FXML
    public void openTargetFolder() {
        GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
        String projectFolder = generatorConfig.getProjectFolder();
        try {
            Desktop.getDesktop().browse(new File(projectFolder).toURI());
        }catch (Exception e) {
            AlertUtil.showErrorAlert("打开目录失败，请检查目录是否填写正确" + e.getMessage());
        }

    }
}
