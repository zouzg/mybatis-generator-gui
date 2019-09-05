package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 和oracle同源的分页插件，本项目中现未配置此插件，沿用的原作者的分页插件
 */
public class MySQLPaginationPlugin extends PluginAdapter
{
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
  {
    addLimit(topLevelClass, introspectedTable, "limitStart");
    addLimit(topLevelClass, introspectedTable, "limitEnd");
    return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
  }

  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
  {
    XmlElement isNotNullElement = new XmlElement("if");
    isNotNullElement.addAttribute(new Attribute("test", "limitStart != null and limitStart>=0"));

    isNotNullElement.addElement(new TextElement("limit #{limitStart} , #{limitEnd}"));
    element.addElement(isNotNullElement);
    return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
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

  public boolean validate(List<String> warnings) {
    return true;
  }
}
