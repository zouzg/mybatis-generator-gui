package com.zzg.mybatis.generator.util;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;

import java.util.Set;
import java.util.TreeSet;

/**
 * <b><code>MethodGeneratorTool</code></b>
 * <p/>
 * Description: 方法生成器
 * <p/>
 * <b>Creation Time:</b> 2018/12/14 0:50.
 *
 * @author HuWeihui
 */
public class MethodGeneratorTool {

    private final static String BATCH_INSERT = "batchInsert";

    private final static String PARAMETER_NAME = "recordList";

    private final static String DELETE_PARAMETER_NAME = "ids";

    private final static String BATCH_UPDATE = "batchUpdate";

    private final static String BATCH_DELETE = "batchDelete";

    public final static Integer INSERT = 0;

    public final static Integer UPDATE = 1;
    /**
     * java方法生成构造器.
     *
     * @param methodName     the method name
     * @param visibility     the visibility
     * @param returnJavaType the return java type
     * @param parameters     the parameters
     * @author HuWeihui
     * @since hui_project v1
     */
    public static Method methodGenerator(String methodName,
                                         JavaVisibility visibility,
                                         FullyQualifiedJavaType returnJavaType,
                                         Parameter... parameters) {
        Method method = new Method();
        method.setName(methodName);
        method.setVisibility(visibility);
        method.setReturnType(returnJavaType);
        for (Parameter parameter : parameters) {
            method.addParameter(parameter);
        }
        return method;
    }

    /**
     * 导入基础的java类型
     *
     * @param introspectedTable the introspected table
     * @return the set
     * @author HuWeihui
     * @since hui_project v1
     */
    public static Set<FullyQualifiedJavaType> importedBaseTypesGenerator(IntrospectedTable introspectedTable){
        //获取实体类类型
        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        //@Param需要导入的类型
        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
        //Integer类型
        FullyQualifiedJavaType intInstance = FullyQualifiedJavaType.getIntInstance();
        //List<Entity>
        FullyQualifiedJavaType listParameterType = FullyQualifiedJavaType.getNewListInstance();

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(parameterType);
        importedTypes.add(intInstance);
        importedTypes.add(paramType);
        importedTypes.add(listParameterType);
        return importedTypes;
    }

    /**
     * 默认的批量新增/更新方法构造器.
     *
     * @param interfaze         the interfaze
     * @param introspectedTable the introspected table
     * @param context           the context
     * @author HuWeihui
     * @since hui_project v1
     */
    public static void defaultBatchInsertOrUpdateMethodGen(Integer type , Interface interfaze, IntrospectedTable introspectedTable, Context context){
        //JAVA导入基础包
        Set<FullyQualifiedJavaType> importedTypes = MethodGeneratorTool.importedBaseTypesGenerator(introspectedTable);

        //List<Entity>
        FullyQualifiedJavaType listParameterType = FullyQualifiedJavaType.getNewListInstance();
        listParameterType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());

        String methodName = BATCH_INSERT;
        //1.batchInsert
        if (type.equals(UPDATE)){
            methodName = BATCH_UPDATE;
        }
        Method insertMethod = MethodGeneratorTool.methodGenerator(methodName,
                JavaVisibility.DEFAULT,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(listParameterType, PARAMETER_NAME, "@Param(\"" + PARAMETER_NAME + "\")"));

        CommentGenerator commentGenerator = context.getCommentGenerator();
        commentGenerator.addGeneralMethodComment(insertMethod, introspectedTable);

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(insertMethod);
    }

    /**
     * 默认的批量删除方法构造器.
     *
     * @param interfaze         the interfaze
     * @param introspectedTable the introspected table
     * @param context           the context
     * @author HuWeihui
     * @since hui_project v1
     */
    public static void defaultBatchDeleteMethodGen(Interface interfaze,IntrospectedTable introspectedTable, Context context){
        //JAVA导入基础包
        Set<FullyQualifiedJavaType> importedTypes = MethodGeneratorTool.importedBaseTypesGenerator(introspectedTable);
        FullyQualifiedJavaType paramType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        Method batchDeleteMethod = MethodGeneratorTool.methodGenerator(BATCH_DELETE,
                JavaVisibility.DEFAULT,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(new FullyQualifiedJavaType(paramType.getFullyQualifiedName()+"[]"), DELETE_PARAMETER_NAME, "@Param(\""+DELETE_PARAMETER_NAME+"\")"));

        context.getCommentGenerator().addGeneralMethodComment(batchDeleteMethod,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(batchDeleteMethod);
    }

}
