package com.zzg.mybatis.generator.util;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * <b><code>SqlMapperGeneratorTool</code></b>
 * <p/>
 * Description: 一些基础XMLELEMENT的构造器
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 18:40.
 *
 * @author HuWeihui
 */
public class SqlMapperGeneratorTool {

    /**
     * The constant INSERT.
     */
    public static final String INSERT = "insert";

    /**
     * The constant DELETE.
     */
    public static final String DELETE = "delete";

    /**
     * The constant UPDATE.
     */
    public static final String UPDATE = "update";

    /**
     * The constant SELECT.
     */
    public static final String SELECT = "select";


    /**
     * 基础XmlElement构造器.
     *
     * @param sqlElementType the sql element type
     * @param sqlMapperId    the sql mapper id
     * @param parameterType  the parameter type
     * @return the xml element
     * @author HuWeihui
     * @since hui_project v1
     */
    public static XmlElement baseElementGenerator(String sqlElementType, String sqlMapperId, FullyQualifiedJavaType parameterType){


        XmlElement baseElement = new XmlElement(sqlElementType);

        baseElement.addAttribute(new Attribute("id", sqlMapperId));

        baseElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        return baseElement;
    }


    /**
     *基础foreach Element构造器.
     *
     * @param collectionName the collection name
     * @param itemName       the item name
     * @param indexName      the index name
     * @param separatorName  the separator name
     * @return the xml element
     * @author HuWeihui
     * @since hui_project v1
     */
    public static XmlElement baseForeachElementGenerator(String collectionName,String itemName,String indexName ,String separatorName){
        XmlElement foreachElement = new XmlElement("foreach");
        if (null!=collectionName){
            foreachElement.addAttribute(new Attribute("collection", collectionName));
        }
        if (null!=itemName){
            foreachElement.addAttribute(new Attribute("item", itemName));
        }
        if (null!=indexName){
            foreachElement.addAttribute(new Attribute("index", indexName));
        }
        if (null!=separatorName){
            foreachElement.addAttribute(new Attribute("separator", separatorName));
        }
        return foreachElement;
    }


    /**
     * 基础IF Element构造器.
     *
     * @param columnJavaTypeName the column java type name
     * @param sql                the sql
     * @param judgeNull          the judge null
     * @return the xml element
     * @author HuWeihui
     * @since hui_project v1
     */
    public static XmlElement baseIfJudgeElementGen(String columnJavaTypeName,String sql ,boolean judgeNull){
        String colmunJudge = "";
        if (judgeNull){
            colmunJudge = columnJavaTypeName + " ==null ";
        }else {
            colmunJudge = columnJavaTypeName + " !=null ";
        }
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", colmunJudge));
        ifElement.addElement(new TextElement(sql));
        return ifElement;
    }

    /**
     * 基础Trim Element构造器.
     *
     * @param prefix          the prefix
     * @param suffix          the suffix
     * @param suffixOverrides the suffix overrides
     * @return the xml element
     * @author HuWeihui
     * @since hui_project v1
     */
    public static XmlElement baseTrimElement(String prefix,String suffix,String suffixOverrides){
        XmlElement trimElement = new XmlElement("trim");
        if (null != prefix){
            trimElement.addAttribute(new Attribute("prefix", prefix));
        }
        if (null != suffix){
            trimElement.addAttribute(new Attribute("suffix", suffix));
        }
        if (null!=suffixOverrides){
            trimElement.addAttribute(new Attribute("suffixOverrides", suffixOverrides));
        }
        return trimElement;
    }
}

