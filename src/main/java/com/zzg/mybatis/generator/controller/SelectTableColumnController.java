package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.IgnoredColumn;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Owen on 6/20/16.
 */
public class SelectTableColumnController extends BaseFXController {

    @FXML
    private TableView<UITableColumnVO> columnListView;
    @FXML
    private TableColumn<UITableColumnVO, Boolean> checkedColumn;
    @FXML
    private TableColumn<UITableColumnVO, String> columnNameColumn;
    @FXML
    private TableColumn<UITableColumnVO, String> jdbcTypeColumn;
    @FXML
    private TableColumn<UITableColumnVO, String> propertyNameColumn;
    @FXML
    private TableColumn<UITableColumnVO, String> typeHandlerColumn;

    private MainUIController mainUIController;

    private String tableName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnListView.setFocusTraversable(false);

        checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        columnNameColumn.setCellValueFactory(cellData -> cellData.getValue().columnNameProperty());
        jdbcTypeColumn.setCellValueFactory(cellData -> cellData.getValue().jdbcTypeProperty());
        propertyNameColumn.setCellValueFactory(cellData -> cellData.getValue().propertyNameProperty());
        typeHandlerColumn.setCellValueFactory(cellData -> cellData.getValue().typeHandleProperty());
        checkedColumn.setCellFactory(column -> {
            return new TableCell<UITableColumnVO, Boolean>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        CheckBox checkBox = new CheckBox();
                        checkBox.setSelected(item);
                        checkBox.setFocusTraversable(false);
                        ObservableList<UITableColumnVO> items = this.getTableView().getItems();
                        UITableColumnVO element = items.get(this.getIndex());
                        checkBox.selectedProperty().bindBidirectional(element.checkedProperty());
                        setGraphic(checkBox);
                    }
                }
            };
        });
        typeHandlerColumn.setCellFactory(column -> {
            return new TableCell<UITableColumnVO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        TextField textField = new TextField();
                        textField.setFocusTraversable(false);
                        ObservableList<UITableColumnVO> items = this.getTableView().getItems();
                        UITableColumnVO element = items.get(this.getIndex());
                        textField.textProperty().bindBidirectional(element.typeHandleProperty());
                        setGraphic(textField);
                    }
                }
            };
        });
    }

    @FXML
    public void ok() {
        ObservableList<UITableColumnVO> items = columnListView.getItems();
        if (items != null && items.size() > 0) {
            List<IgnoredColumn> ignoredColumns = new ArrayList<>();
            List<ColumnOverride> columnOverrides = new ArrayList<>();
            items.stream().forEach(item -> {
                if (!item.getChecked()) {
                    IgnoredColumn ignoredColumn = new IgnoredColumn(item.getColumnName());
                    ignoredColumns.add(ignoredColumn);
                } else if (item.getTypeHandle() != null) { // unchecked and have typeHandler value
                    ColumnOverride columnOverride = new ColumnOverride(item.getColumnName());
                    columnOverride.setTypeHandler(item.getTypeHandle());
                    //columnOverride.setJavaProperty(item.getPropertyName());
                    //columnOverride.setJavaType(item.getJdbcType());
                    columnOverrides.add(columnOverride);
                }
            });
            mainUIController.setIgnoredColumns(ignoredColumns);
            mainUIController.setColumnOverrides(columnOverrides);
        }
        getDialogStage().close();
    }

    @FXML
    public void cancel() {
        getDialogStage().close();
    }

    public void setColumnList(ObservableList<UITableColumnVO> columns) {
        columnListView.setItems(columns);
    }

    public void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


}
