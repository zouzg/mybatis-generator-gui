package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 批量插入和批量更新插件 (MySQL CASE WHEN 版本)
 * * 特性：
 * 1. batchInsert: 高性能全量插入 (insert values (),()...)
 * 2. batchUpdate: Selective模式，使用 CASE WHEN 语法，不需要 allowMultiQueries=true
 */
public class BatchPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 1. 生成 batchInsert 接口
        Method batchInsert = new Method("batchInsert");
        batchInsert.setReturnType(FullyQualifiedJavaType.getIntInstance());
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
        parameterType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        batchInsert.addParameter(new Parameter(parameterType, "list", "@Param(\"list\")"));
        batchInsert.addJavaDocLine("/**");
        batchInsert.addJavaDocLine(" * 批量插入 (全量模式)");
        batchInsert.addJavaDocLine(" * @param list 实体列表");
        batchInsert.addJavaDocLine(" */");
        interfaze.addMethod(batchInsert);

        // 2. 生成 batchUpdate 接口 (仅当有主键时)
        if (introspectedTable.hasPrimaryKeyColumns()) {
            Method batchUpdate = new Method("batchUpdate");
            batchUpdate.setReturnType(FullyQualifiedJavaType.getIntInstance());
            batchUpdate.addParameter(new Parameter(parameterType, "list", "@Param(\"list\")"));
            batchUpdate.addJavaDocLine("/**");
            batchUpdate.addJavaDocLine(" * 批量更新 (Selective 模式)");
            batchUpdate.addJavaDocLine(" * 使用 CASE WHEN 语法，null 值不更新，不需开启 allowMultiQueries");
            batchUpdate.addJavaDocLine(" * @param list 实体列表");
            batchUpdate.addJavaDocLine(" */");
            interfaze.addMethod(batchUpdate);
        }

        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        // 生成 XML
        addBatchInsertXml(document, introspectedTable);
        if (introspectedTable.hasPrimaryKeyColumns()) {
            addBatchUpdateXml(document, introspectedTable);
        }
        return true;
    }

    // --- 批量插入 (保持原样，最高效) ---
    private void addBatchInsertXml(Document document, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        XmlElement insertElement = new XmlElement("insert");
        insertElement.addAttribute(new Attribute("id", "batchInsert"));
        insertElement.addAttribute(new Attribute("parameterType", "java.util.List"));

        if (introspectedTable.getGeneratedKey() != null) {
            insertElement.addAttribute(new Attribute("useGeneratedKeys", "true"));
            String keyColumn = introspectedTable.getGeneratedKey().getColumn();
            for (IntrospectedColumn col : columns) {
                if (col.getActualColumnName().equals(keyColumn)) {
                    insertElement.addAttribute(new Attribute("keyProperty", col.getJavaProperty()));
                    break;
                }
            }
        }

        StringBuilder colsBuilder = new StringBuilder("insert into " + tableName + " (");
        StringBuilder valsBuilder = new StringBuilder("(");

        int index = 0;
        for (IntrospectedColumn column : columns) {
            if (column.isIdentity()) {
                continue;
            }
            if (index > 0) {
                colsBuilder.append(", ");
                valsBuilder.append(", ");
            }
            colsBuilder.append(getMyBatis3QuoteString(column.getActualColumnName()));
            valsBuilder.append("#{item.").append(column.getJavaProperty())
                    .append(",jdbcType=").append(column.getJdbcTypeName()).append("}");
            index++;
        }
        colsBuilder.append(") values ");
        valsBuilder.append(")");

        insertElement.addElement(new TextElement(colsBuilder.toString()));

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addElement(new TextElement(valsBuilder.toString()));
        insertElement.addElement(foreachElement);

        document.getRootElement().addElement(insertElement);
    }

    // --- 批量更新 (CASE WHEN 策略) ---
    private void addBatchUpdateXml(Document document, IntrospectedTable introspectedTable) {
        XmlElement updateElement = new XmlElement("update");
        updateElement.addAttribute(new Attribute("id", "batchUpdate"));
        updateElement.addAttribute(new Attribute("parameterType", "java.util.List"));

        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        updateElement.addElement(new TextElement("update " + tableName));

        XmlElement trimSetElement = new XmlElement("trim");
        trimSetElement.addAttribute(new Attribute("prefix", "set"));
        trimSetElement.addAttribute(new Attribute("suffixOverrides", ","));

        List<IntrospectedColumn> primaryKeys = introspectedTable.getPrimaryKeyColumns();
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();

        // 1. 遍历所有非主键列，为每一列生成 CASE WHEN
        for (IntrospectedColumn column : allColumns) {
            if (primaryKeys.contains(column)) {
                continue;
            }

            String columnName = getMyBatis3QuoteString(column.getActualColumnName());

            // <trim prefix="col = case" suffix="end,">
            XmlElement colTrim = new XmlElement("trim");
            colTrim.addAttribute(new Attribute("prefix", columnName + " = case"));
            colTrim.addAttribute(new Attribute("suffix", "end,"));

            // <foreach collection="list" item="item">
            XmlElement foreach = new XmlElement("foreach");
            foreach.addAttribute(new Attribute("collection", "list"));
            foreach.addAttribute(new Attribute("item", "item"));
            foreach.addAttribute(new Attribute("index", "index"));

            // <if test="item.field != null">
            XmlElement ifNotNull = new XmlElement("if");
            ifNotNull.addAttribute(new Attribute("test", "item." + column.getJavaProperty() + " != null"));

            // when id = #{item.id} then #{item.field}
            StringBuilder whenClause = new StringBuilder();
            whenClause.append("when ");

            // 构建复合主键匹配 logic: when pk1=val1 and pk2=val2 ...
            for (int i = 0; i < primaryKeys.size(); i++) {
                IntrospectedColumn pk = primaryKeys.get(i);
                if (i > 0) {
                    whenClause.append(" and ");
                }
                whenClause.append(getMyBatis3QuoteString(pk.getActualColumnName()));
                whenClause.append(" = #{item.");
                whenClause.append(pk.getJavaProperty()).append("}");
            }

            whenClause.append(" then #{item.").append(column.getJavaProperty());
            whenClause.append(",jdbcType=").append(column.getJdbcTypeName()).append("}");

            ifNotNull.addElement(new TextElement(whenClause.toString()));
            foreach.addElement(ifNotNull);
            colTrim.addElement(foreach);

            // 这里的 else 保证了 selective：如果上面没有命中(即值为null)，则赋值为原列名(保持不变)
            colTrim.addElement(new TextElement("else " + columnName));

            trimSetElement.addElement(colTrim);
        }

        updateElement.addElement(trimSetElement);

        // 2. 构建 Where ID IN (...)
        StringBuilder whereClause = new StringBuilder("where ");

        // 处理单主键和复合主键的 WHERE IN 写法
        if (primaryKeys.size() == 1) {
            IntrospectedColumn pk = primaryKeys.get(0);
            whereClause.append(getMyBatis3QuoteString(pk.getActualColumnName()));
            whereClause.append(" in ");
            updateElement.addElement(new TextElement(whereClause.toString()));

            XmlElement whereForeach = new XmlElement("foreach");
            whereForeach.addAttribute(new Attribute("collection", "list"));
            whereForeach.addAttribute(new Attribute("item", "item"));
            whereForeach.addAttribute(new Attribute("separator", ","));
            whereForeach.addAttribute(new Attribute("open", "("));
            whereForeach.addAttribute(new Attribute("close", ")"));

            whereForeach.addElement(new TextElement("#{item." + pk.getJavaProperty() + "}"));
            updateElement.addElement(whereForeach);
        } else {
            // 复合主键 WHERE (pk1, pk2) IN ((v1, v2), (v3, v4))
            whereClause.append("(");
            for (int i = 0; i < primaryKeys.size(); i++) {
                if (i > 0) {
                    whereClause.append(",");
                }
                whereClause.append(getMyBatis3QuoteString(primaryKeys.get(i).getActualColumnName()));
            }
            whereClause.append(") in ");
            updateElement.addElement(new TextElement(whereClause.toString()));

            XmlElement whereForeach = new XmlElement("foreach");
            whereForeach.addAttribute(new Attribute("collection", "list"));
            whereForeach.addAttribute(new Attribute("item", "item"));
            whereForeach.addAttribute(new Attribute("separator", ","));
            whereForeach.addAttribute(new Attribute("open", "("));
            whereForeach.addAttribute(new Attribute("close", ")"));

            StringBuilder pkVals = new StringBuilder("(");
            for (int i = 0; i < primaryKeys.size(); i++) {
                if (i > 0) {
                    pkVals.append(",");
                }
                pkVals.append("#{item.").append(primaryKeys.get(i).getJavaProperty()).append("}");
            }
            pkVals.append(")");
            whereForeach.addElement(new TextElement(pkVals.toString()));
            updateElement.addElement(whereForeach);
        }

        document.getRootElement().addElement(updateElement);
    }

    private String getMyBatis3QuoteString(String column) {
        return "`" + column + "`";
    }
}