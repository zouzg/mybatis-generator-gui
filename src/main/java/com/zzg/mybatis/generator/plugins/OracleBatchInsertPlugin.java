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
 * <b><code>OracleBatchInsertPlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 1:12.
 *
 * @author HuWeihui
 */
public class OracleBatchInsertPlugin extends PluginAdapter {

    private final static String BATCH_INSERT = "batchInsert";

    private final static String PARAMETER_NAME = "recordList";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3) {
            MethodGeneratorTool.defaultBatchInsertOrUpdateMethodGen(MethodGeneratorTool.INSERT, interfaze, introspectedTable, context);
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


    private void addSqlMapper(Document document, IntrospectedTable introspectedTable) {
        //1.Batchinsert
        XmlElement baseElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.INSERT,
                BATCH_INSERT,
                FullyQualifiedJavaType.getNewListInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME,
                "item",
                "index",
                "union all");


        //tableName
        baseElement.addElement(new TextElement(String.format("insert into %s (", introspectedTable.getFullyQualifiedTableNameAtRuntime())));

        foreachElement.addElement(new TextElement("("));
        foreachElement.addElement(new TextElement("select"));

        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            //column信息
            IntrospectedColumn introspectedColumn = introspectedTable.getAllColumns().get(i);

            String columnInfo = "";
            String valueInfo = "";

            columnInfo = introspectedColumn.getActualColumnName();
            valueInfo = MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.");
            if (introspectedColumn.isIdentity()) {
                String nextval = introspectedTable.getFullyQualifiedTableNameAtRuntime()+"_SEQUENCE.nextval" ;
                valueInfo = nextval;
            }
            if (i != (introspectedTable.getAllColumns().size() - 1)) {
                columnInfo += (",");
                valueInfo += ",";
            }
            baseElement.addElement(new TextElement(columnInfo));
            foreachElement.addElement(new TextElement(valueInfo));
        }
        foreachElement.addElement(new TextElement("from dual"));
        foreachElement.addElement(new TextElement(")"));


        baseElement.addElement(new TextElement(")"));

        baseElement.addElement(new TextElement("("));
        baseElement.addElement(foreachElement);
        baseElement.addElement(new TextElement(")"));

        //3.parent Add
        document.getRootElement().addElement(baseElement);
    }

}
