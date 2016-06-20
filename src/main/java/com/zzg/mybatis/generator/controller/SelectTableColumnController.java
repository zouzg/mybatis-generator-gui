package com.zzg.mybatis.generator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Owen on 6/20/16.
 */
public class SelectTableColumnController extends BaseFXController {

    @FXML
    private ListView<String> columnListView;

    private MainUIController mainUIController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void ok() {
        getDialogStage().close();
    }

    public void setColumnList(List<String> columns) {
        columnListView.setItems(FXCollections.observableArrayList(columns));
    }

    public void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }


}
