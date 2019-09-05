package com.zzg.mybatis.generator.plugins;

import com.zzg.mybatis.generator.util.MethodGeneratorTool;
import com.zzg.mybatis.generator.util.SqlMapperGeneratorTool;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 * <b><code>PostgreBatchUpdatePlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 1:12.
 *
 * @author HuWeihui
 */
public class PostgreBatchUpdatePlugin extends PluginAdapter {

    private final static String BATCH_UPDATE = "batchUpdate";

    private final static String PARAMETER_NAME = "recordList";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            MethodGeneratorTool.defaultBatchInsertOrUpdateMethodGen(MethodGeneratorTool.UPDATE,interfaze,introspectedTable,context);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addSqlMapper(document, introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


    private void addSqlMapper(Document document, IntrospectedTable introspectedTable){
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();

        String primaryKeyName = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        //primaryKey的JAVA名字
        String primaryKeyJavaName = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();

        XmlElement updateElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.UPDATE,
                BATCH_UPDATE,
                FullyQualifiedJavaType.getNewListInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME,
                "item",
                "index",
                ",");

        String baseSql = String.format("update %s", tableName);

        updateElement.addElement(new TextElement(baseSql));

        XmlElement setElement = new XmlElement("set");


        StringBuilder columnInfo = new StringBuilder();
        StringBuilder valuesInfo = new StringBuilder();

        StringBuilder columnInfoTotal = new StringBuilder();
        for (int i = 0; i < columnList.size(); i++) {

            IntrospectedColumn introspectedColumn = columnList.get(i);

            columnInfo.append(introspectedColumn.getActualColumnName());
            columnInfoTotal.append(introspectedColumn.getActualColumnName());
            if (introspectedColumn.getFullyQualifiedJavaType().equals(FullyQualifiedJavaType.getDateInstance())){
                valuesInfo.append("to_timestamp(");
                valuesInfo.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
                valuesInfo.append(",'yyyy-MM-dd hh24:mi:ss')");
            }else {
                valuesInfo.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            }

            String setSql = String.format(" %s = %s," ,columnInfo,"temp."+columnInfo);
            if (i == (columnList.size() - 1)) {
                setSql = setSql.substring(0, setSql.length() - 1);
            }
            if (!introspectedColumn.isIdentity() && !introspectedColumn.getActualColumnName().equals(primaryKeyName)) {
                setElement.addElement(new TextElement(setSql));
            }

            if (i != (columnList.size() - 1)) {
                valuesInfo.append(",");
                columnInfo.append(",");
                columnInfoTotal.append(",");
            }

            columnInfo.delete(0, valuesInfo.length());
        }

        foreachElement.addElement(new TextElement("("));

        foreachElement.addElement(new TextElement(valuesInfo.toString()));

        foreachElement.addElement(new TextElement(")"));

        updateElement.addElement(setElement);

        updateElement.addElement(new TextElement("from (values"));

        updateElement.addElement(foreachElement);

        updateElement.addElement(new TextElement(String.format(") as temp (%s) where %s.%s=temp.%s;",columnInfoTotal,tableName,primaryKeyName,primaryKeyName)));

        //3.parent Add
        document.getRootElement().addElement(updateElement);
    }
}
