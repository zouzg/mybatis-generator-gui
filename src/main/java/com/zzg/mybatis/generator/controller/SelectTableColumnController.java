package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.UITableColumnVO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Owen on 6/20/16.
 */
public class SelectTableColumnController extends BaseFXController {

    @FXML
    private TableView<UITableColumnVO> columnListView;

    private MainUIController mainUIController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void ok() {
        getDialogStage().close();
    }

    public void setColumnList(List<String> columns) {
        columnListView.setItems(null);
    }

    public void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }


}
