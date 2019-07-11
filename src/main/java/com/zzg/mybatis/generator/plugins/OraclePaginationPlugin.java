package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * oracle分页生成
 * @author https://blog.csdn.net/xuyw10000/article/details/70617273
 */
public class OraclePaginationPlugin extends PluginAdapter
{
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        addLimit(topLevelClass, introspectedTable, "limitStart");
        addLimit(topLevelClass, introspectedTable, "limitEnd");
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
    {
        XmlElement parentElement = document.getRootElement();

        XmlElement paginationPrefixElement = new XmlElement("sql");
        paginationPrefixElement.addAttribute(
                new Attribute("id",
                        "OracleDialectPrefix"));
        XmlElement pageStart = new XmlElement("if");
        pageStart.addAttribute(new Attribute("test", "limitStart != null and limitStart>=1 and limitEnd != null and limitEnd>=1"));
        pageStart.addElement(
                new TextElement("select * from (select t.*, rownum r from ( "));
        paginationPrefixElement.addElement(pageStart);
        parentElement.addElement(paginationPrefixElement);

        XmlElement paginationSuffixElement = new XmlElement("sql");
        paginationSuffixElement.addAttribute(
                new Attribute("id",
                        "OracleDialectSuffix"));
        XmlElement pageEnd = new XmlElement("if");
        pageEnd.addAttribute(new Attribute("test", "limitStart != null and limitStart>=1 and limitEnd != null and limitEnd>=1"));
        pageEnd.addElement(
                new TextElement("<![CDATA[ ) t where rownum <= #{limitEnd}  )  where r >= #{limitStart}  ]]>"));
        paginationSuffixElement.addElement(pageEnd);
        parentElement.addElement(paginationSuffixElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        XmlElement pageStart = new XmlElement("include");
        pageStart.addAttribute(new Attribute("refid", "OracleDialectPrefix"));
        element.getElements().add(0, pageStart);

        XmlElement isNotNullElement = new XmlElement("include");
        isNotNullElement.addAttribute(
                new Attribute("refid",
                        "OracleDialectSuffix"));
        element.getElements().add(isNotNullElement);

        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }

    private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name)
    {
        CommentGenerator commentGenerator = this.context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(name);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(PrimitiveTypeWrapper.getIntegerInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    public boolean validate(List<String> warnings)
    {
        return true;
    }
}
