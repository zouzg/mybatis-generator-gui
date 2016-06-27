package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.util.StringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                        setGraphic(checkBox);
                    }
                }
            };
        });
        propertyNameColumn.setCellFactory(column ->  {
            return new TableCell<UITableColumnVO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        TextField textField = new TextField();
                        textField.setText(item);
                        setGraphic(textField);
                    }
                }
            };
        });
        typeHandlerColumn.setCellFactory(column ->  {
            return new TableCell<UITableColumnVO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        TextField textField = new TextField();
                        setGraphic(textField);
                    }
                }
            };
        });
    }

    @FXML
    public void ok() {
        getDialogStage().close();
    }

    public void setColumnList(ObservableList<UITableColumnVO> columns) {
        columnListView.setItems(columns);
    }

    public void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }


}
