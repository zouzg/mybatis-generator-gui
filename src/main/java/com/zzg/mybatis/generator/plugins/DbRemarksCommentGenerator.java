/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zzg.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 此插件使用数据库表中列的注释来生成Java Model中属性的注释
 *
 * @author Owen Zou
 * 
 */
public class DbRemarksCommentGenerator implements CommentGenerator {

    private Properties properties;
    private boolean columnRemarks;

    public DbRemarksCommentGenerator() {
        super();
        properties = new Properties();
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
        return;
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     */
    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
        return;
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        columnRemarks = isTrue(properties
                .getProperty("columnRemarks"));
    }

    public void addClassComment(InnerClass innerClass,
            IntrospectedTable introspectedTable) {
    }

    public void addEnumComment(InnerEnum innerEnum,
            IntrospectedTable introspectedTable) {
    }

    public void addFieldComment(Field field,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
        String columnRemarks = introspectedColumn.getRemarks();
        if (!this.columnRemarks || StringUtils.isEmpty(columnRemarks)) {
            return;
        }
        field.addJavaDocLine("/**");
        field.addJavaDocLine("* " + columnRemarks);
        field.addJavaDocLine("*/");
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    public void addGeneralMethodComment(Method method,
            IntrospectedTable introspectedTable) {
    }

    public void addGetterComment(Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
    }

    public void addSetterComment(Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
    }

    public void addClassComment(InnerClass innerClass,
            IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }
}
