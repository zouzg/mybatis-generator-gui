package com.zzg.mybatis.generator.plugins;/**
 * @author PanMeiCheng
 * @date 2019-08-29
 * @company DM
 * @version 1.0
 */

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;


/**
 *
 *@author PanMeiCheng
 *@date 2019-08-29
 *@version 1.0
 */
public class PagePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成 List<实体类> selectByPage(@Param("offset") Long offset,@Param("limit") Long limit); 方法
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

            // 生成方法
            Method newMethod = new Method("selectByPage");
            // 设置方法类型
            newMethod.setVisibility(JavaVisibility.PUBLIC);

            // 设置方法返回值类型
            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">");
            newMethod.setReturnType(returnType);

            // 设置方法参数
            FullyQualifiedJavaType offsetJavaType = new FullyQualifiedJavaType("Long");
            Parameter offsetParameter = new Parameter(offsetJavaType, "offset");
            offsetParameter.addAnnotation("@Param(\"offset\")");
            newMethod.addParameter(0, offsetParameter);

            FullyQualifiedJavaType limitJavaType = new FullyQualifiedJavaType("Long");
            Parameter limitParameter = new Parameter(limitJavaType, "limit");
            limitParameter.addAnnotation("@Param(\"limit\")");
            newMethod.addParameter(1, limitParameter);

            // 添加相应的包
            interfaze.addImportedType(new FullyQualifiedJavaType(("org.apache.ibatis.annotations.Param")));
            interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
            interfaze.addMethod(newMethod);
            return super.clientGenerated(interfaze, topLevelClass, introspectedTable);

    }
    /**
     * 生成如下的分页查询sql语句
     * <select id="selectByPage" resultMap="BaseResultMap"> SELECT <include refid="Base_Column_List"/> FROM 表名 LIMIT #{offset}, #{limit} </select>
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        System.out.println("分页查询 "+introspectedTable.getTableType());
            XmlElement select = new XmlElement("select");
            select.addAttribute(new Attribute("id", "selectByPage"));
            select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            select.addElement(new TextElement("SELECT <include refid=\"Base_Column_List\" /> FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " LIMIT #{offset},#{limit}"));
            XmlElement parentElement = document.getRootElement();
            parentElement.addElement(select);
            return super.sqlMapDocumentGenerated(document, introspectedTable);
    }
}
