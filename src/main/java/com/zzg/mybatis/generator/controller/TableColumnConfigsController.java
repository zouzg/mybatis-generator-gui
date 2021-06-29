package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定制列配置UI Controller
 *
 * @author xueqi
 * @date 2021-06-24
 */
public class TableColumnConfigsController extends BaseFXController {

	private static final Logger _LOG                  = LoggerFactory.getLogger(TableColumnConfigsController.class);
	private static final String COL_NAME_PREFIX_REGEX = "(?<=%s)[^\"]+";   // pattern regex and split prefix: (?<=aggregate_|f_)[^"]+  f_ or d_ prefix
	private static final String OR_REGEX              = "|";

	@FXML
	private Label     currentTableNameLabel;
	@FXML
	private TextField columnNamePrefixTextLabel;

	private TableView<UITableColumnVO> columnListView;
	private String                     tableName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// do nothing
	}

	@FXML
	public void cancel() {
		this.closeDialogStage();
	}

	@FXML
	public void confirm() {
		try {
			// 1. generator bean propert name
			this.genProertyNameByColumnNamePrefix();

			// close window
			this.closeDialogStage();
		} catch (Exception e) {
			_LOG.error("confirm throw exception.", e);
			AlertUtil.showErrorAlert(e.getMessage());
		}
	}

	public void setColumnListView(TableView<UITableColumnVO> columnListView) {
		this.columnListView = columnListView;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		currentTableNameLabel.setText(tableName);
	}

	private void genProertyNameByColumnNamePrefix() {
		String columnNamePrefix = this.columnNamePrefixTextLabel.getText();
		if (StringUtils.isNotBlank(columnNamePrefix)) {
			if (StringUtils.endsWith(columnNamePrefix.trim(), OR_REGEX)) {
				columnNamePrefix = StringUtils.removeEnd(columnNamePrefix.trim(), OR_REGEX);
			}

			String regex = String.format(COL_NAME_PREFIX_REGEX, columnNamePrefix);
			_LOG.info("table:{}, column_name_prefix:{}, regex:{}", this.tableName, columnNamePrefix, regex);

			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

			ObservableList<UITableColumnVO> items = columnListView.getItems();
			if (CollectionUtils.isNotEmpty(items)) {
				items.stream().forEach(item -> {
					String  columnName = item.getColumnName();
					Matcher matcher    = pattern.matcher(columnName);
					if (matcher.find()) {
						// use first match result
						String regexColumnName = matcher.group();
						if (StringUtils.isNotBlank(regexColumnName)) {
							String propertyName = JavaBeansUtil.getCamelCaseString(regexColumnName, false);
							_LOG.debug("table:{} column_name:{} regex_column_name:{} property_name:{}", tableName, columnName, regexColumnName, propertyName);

							if (StringUtils.isNotBlank(propertyName)) item.setPropertyName(propertyName);
						} else {
							_LOG.warn("table:{} column_name:{} regex_column_name is blank", tableName, columnName);
						}
					} else {
						// if not match, set property name is null
						item.setPropertyName(null);
					}
				});
			}
		}
	}

}
