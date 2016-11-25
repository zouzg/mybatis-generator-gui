package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 扩展api自带的序列化插件实现生成的实体类和example类序列化
 *
 * @author huxiong
 * @date 2016-11-23 14:25
 */
public class SerializablePlugin extends org.mybatis.generator.plugins.SerializablePlugin {
    private FullyQualifiedJavaType serializable;

    public SerializablePlugin() {
        super();
        serializable = new FullyQualifiedJavaType("java.io.Serializable"); //$NON-NLS-1$
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.makeSerializable(topLevelClass, introspectedTable);
        for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) { //$NON-NLS-1$
                innerClass.addSuperInterface(serializable);
            }
            if ("Criteria".equals(innerClass.getType().getShortName())) { //$NON-NLS-1$
                innerClass.addSuperInterface(serializable);
            }
            if ("Criterion".equals(innerClass.getType().getShortName())) { //$NON-NLS-1$
                innerClass.addSuperInterface(serializable);
            }
        }
        return true;
    }
}
