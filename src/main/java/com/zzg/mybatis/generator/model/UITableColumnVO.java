package com.zzg.mybatis.generator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Owen on 6/22/16.
 */
public class UITableColumnVO {

    private BooleanProperty checked = new SimpleBooleanProperty();

    private String columnName;

    private String jdbcType;

    private String propertyName;

    private StringProperty typeHandle = new SimpleStringProperty();

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public Boolean getChecked() {
        return this.checked.get();
    }

    public void setChecked(Boolean checked) {
        this.checked.set(checked);
    }

    public StringProperty typeHandleProperty() {
        return typeHandle;
    }

    public String getTypeHandle() {
        return typeHandle.get();
    }

    public void setTypeHandle(String typeHandle) {
        this.typeHandle.set(typeHandle);
    }

}
