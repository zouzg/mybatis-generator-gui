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
 * <b><code>OracleBatchUpdatePlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 1:12.
 *
 * @author HuWeihui
 */
public class OracleBatchUpdatePlugin extends PluginAdapter {

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


    public void addSqlMapper(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
        //primaryKey的JDBC名字
        String primaryKeyName = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        //primaryKey的JAVA名字
        String primaryKeyJavaName = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();

        String keyParameterClause = MyBatis3FormattingUtilities.getParameterClause(introspectedTable.getPrimaryKeyColumns().get(0), "item.");
        XmlElement baseElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.UPDATE,
                BATCH_UPDATE,
                FullyQualifiedJavaType.getNewListInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME,
                "item",
                "index",
                ";");

        foreachElement.addElement(new TextElement( String.format("update %s", tableName)));

        XmlElement setElement = new XmlElement("set");

        StringBuilder columnInfo = new StringBuilder();
        StringBuilder valuesInfo = new StringBuilder();

        for (int i = 0; i < columnList.size(); i++) {

            IntrospectedColumn introspectedColumn = columnList.get(i);
            if (introspectedColumn.isIdentity()) {
                continue;
            }
            columnInfo.append(introspectedColumn.getActualColumnName());
            valuesInfo.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            if (i != (columnList.size() - 1)) {
                valuesInfo.append(",");
            }
            String setSql = String.format(" %s = %s" ,columnInfo,valuesInfo);

            setElement.addElement(new TextElement(setSql));

            valuesInfo.delete(0, valuesInfo.length());
            columnInfo.delete(0, columnInfo.length());

        }

        foreachElement.addElement(setElement);

        String whereSql = String.format("where %s = %s",primaryKeyName,keyParameterClause);

        foreachElement.addElement(new TextElement(whereSql));

        baseElement.addElement(new TextElement("begin"));

        baseElement.addElement(foreachElement);

        baseElement.addElement(new TextElement(";end;"));

        //3.parent Add
        document.getRootElement().addElement(baseElement);
    }
}
