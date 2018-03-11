package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * Project: mybatis-generator-gui
 *
 * @author slankka on 2017/12/13.
 */
public class RepositoryPlugin extends PluginAdapter {

    private FullyQualifiedJavaType annotationRepository;
    private String annotation = "@Repository";

    public RepositoryPlugin () {
        annotationRepository = new FullyQualifiedJavaType("org.springframework.stereotype.Repository"); //$NON-NLS-1$
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(annotationRepository);
        interfaze.addAnnotation(annotation);
        return true;
    }
}
