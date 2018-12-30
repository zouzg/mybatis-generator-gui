package com.zzg.mybatis.generator.bridge;

import com.jcraft.jsch.Session;
import com.zzg.mybatis.generator.controller.PictureProcessStateController;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.plugins.DbRemarksCommentGenerator;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The bridge between GUI and the mybatis generator. All the operation to  mybatis generator should proceed through this
 * class
 * <p>
 * Created by Owen on 6/30/16.
 */
public class MybatisGeneratorBridge {

	private static final Logger _LOG = LoggerFactory.getLogger(MybatisGeneratorBridge.class);

    private GeneratorConfig generatorConfig;

    private DatabaseConfig selectedDatabaseConfig;

    private ProgressCallback progressCallback;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    public MybatisGeneratorBridge() {
    }

    public void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.selectedDatabaseConfig = databaseConfig;
    }

    public void generate() throws Exception {
        Configuration configuration = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
        configuration.addContext(context);
		
        context.addProperty("javaFileEncoding", "UTF-8");
        
		String dbType = selectedDatabaseConfig.getDbType();
		String connectorLibPath = ConfigHelper.findConnectorLibPath(dbType);
	    _LOG.info("connectorLibPath: {}", connectorLibPath);
	    configuration.addClasspathEntry(connectorLibPath);
        // Table configuration
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(generatorConfig.getTableName());
        tableConfig.setDomainObjectName(generatorConfig.getDomainObjectName());
        if(!generatorConfig.isUseExample()) {
            tableConfig.setUpdateByExampleStatementEnabled(false);
            tableConfig.setCountByExampleStatementEnabled(false);
            tableConfig.setDeleteByExampleStatementEnabled(false);
            tableConfig.setSelectByExampleStatementEnabled(false);
        }

		context.addProperty("autoDelimitKeywords", "true");
		if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)) {
			tableConfig.setSchema(selectedDatabaseConfig.getSchema());
			// 由于beginningDelimiter和endingDelimiter的默认值为双引号(")，在Mysql中不能这么写，所以还要将这两个默认值改为`
			context.addProperty("beginningDelimiter", "`");
			context.addProperty("endingDelimiter", "`");
		} else {
            tableConfig.setCatalog(selectedDatabaseConfig.getSchema());
	    }
        if (generatorConfig.isUseSchemaPrefix()) {
            if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)) {
                tableConfig.setSchema(selectedDatabaseConfig.getSchema());
            } else if (DbType.Oracle.name().equals(dbType)) {
                //Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
                tableConfig.setSchema(selectedDatabaseConfig.getUsername());
            } else {
                tableConfig.setCatalog(selectedDatabaseConfig.getSchema());
            }
        }
        // 针对 postgresql 单独配置
		if (DbType.PostgreSQL.name().equals(dbType)) {
            tableConfig.setDelimitIdentifiers(true);
        }

        //添加GeneratedKey主键生成
		if (StringUtils.isNotEmpty(generatorConfig.getGenerateKeys())) {
            String dbType2 = dbType;
            if (DbType.MySQL.name().equals(dbType2) || DbType.MySQL_8.name().equals(dbType)) {
                dbType2 = "JDBC";
                //dbType为JDBC，且配置中开启useGeneratedKeys时，Mybatis会使用Jdbc3KeyGenerator,
                //使用该KeyGenerator的好处就是直接在一次INSERT 语句内，通过resultSet获取得到 生成的主键值，
                //并很好的支持设置了读写分离代理的数据库
                //例如阿里云RDS + 读写分离代理
                //无需指定主库
                //当使用SelectKey时，Mybatis会使用SelectKeyGenerator，INSERT之后，多发送一次查询语句，获得主键值
                //在上述读写分离被代理的情况下，会得不到正确的主键
            }
			tableConfig.setGeneratedKey(new GeneratedKey(generatorConfig.getGenerateKeys(), dbType2, true, null));
		}

        if (generatorConfig.getMapperName() != null) {
            tableConfig.setMapperName(generatorConfig.getMapperName());
        }
        // add ignore columns
        if (ignoredColumns != null) {
            ignoredColumns.stream().forEach(ignoredColumn -> {
                tableConfig.addIgnoredColumn(ignoredColumn);
            });
        }
        if (columnOverrides != null) {
            columnOverrides.stream().forEach(columnOverride -> {
                tableConfig.addColumnOverride(columnOverride);
            });
        }
        if (generatorConfig.isUseActualColumnNames()) {
			tableConfig.addProperty("useActualColumnNames", "true");
        }

		if(generatorConfig.isUseTableNameAlias()){
            tableConfig.setAlias(generatorConfig.getTableName());
        }

        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)) {
	        jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
        }
        jdbcConfig.setDriverClass(DbType.valueOf(dbType).getDriverClass());
        jdbcConfig.setConnectionURL(DbUtil.getConnectionUrlWithSchema(selectedDatabaseConfig));
        jdbcConfig.setUserId(selectedDatabaseConfig.getUsername());
        jdbcConfig.setPassword(selectedDatabaseConfig.getPassword());
        if(DbType.Oracle.name().equals(dbType)){
            jdbcConfig.getProperties().setProperty("remarksReporting", "true");
        }
        // java model
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        modelConfig.setTargetPackage(generatorConfig.getModelPackage());
        modelConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getModelPackageTargetFolder());
        // Mapper configuration
        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
        mapperConfig.setTargetPackage(generatorConfig.getMappingXMLPackage());
        mapperConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getMappingXMLTargetFolder());
        // DAO
        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        daoConfig.setTargetPackage(generatorConfig.getDaoPackage());
        daoConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getDaoTargetFolder());


        context.setId("myid");
        context.addTableConfiguration(tableConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJavaModelGeneratorConfiguration(modelConfig);
        context.setSqlMapGeneratorConfiguration(mapperConfig);
        context.setJavaClientGeneratorConfiguration(daoConfig);
        // Comment
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.setConfigurationType(DbRemarksCommentGenerator.class.getName());
        if (generatorConfig.isComment()) {
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (generatorConfig.isAnnotation()) {
            commentConfig.addProperty("annotations", "true");
        }
        context.setCommentGeneratorConfiguration(commentConfig);
        // set java file encoding
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, generatorConfig.getEncoding());

        //实体添加序列化
        PluginConfiguration serializablePluginConfiguration = new PluginConfiguration();
        serializablePluginConfiguration.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
        serializablePluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(serializablePluginConfiguration);
        // toString, hashCode, equals插件
        if (generatorConfig.isNeedToStringHashcodeEquals()) {
            PluginConfiguration pluginConfiguration1 = new PluginConfiguration();
            pluginConfiguration1.addProperty("type", "org.mybatis.generator.plugins.EqualsHashCodePlugin");
            pluginConfiguration1.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
            context.addPluginConfiguration(pluginConfiguration1);
            PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
            pluginConfiguration2.addProperty("type", "org.mybatis.generator.plugins.ToStringPlugin");
            pluginConfiguration2.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
            context.addPluginConfiguration(pluginConfiguration2);
        }
        // limit/offset插件
        if (generatorConfig.isOffsetLimit()) {
            if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)
		            || DbType.PostgreSQL.name().equals(dbType)) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration();
                pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
                pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
                context.addPluginConfiguration(pluginConfiguration);
            }
        }
        //for JSR310
        if (generatorConfig.isJsr310Support()) {
            JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
            javaTypeResolverConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.JavaTypeResolverJsr310Impl");
            context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        }
        //forUpdate 插件
        if(generatorConfig.isNeedForUpdate()) {
            if (DbType.MySQL.name().equals(dbType)
                    || DbType.PostgreSQL.name().equals(dbType)) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration();
                pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.MySQLForUpdatePlugin");
                pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.MySQLForUpdatePlugin");
                context.addPluginConfiguration(pluginConfiguration);
            }
        }
        //repository 插件
        if(generatorConfig.isAnnotationDAO()) {
            if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)
                    || DbType.PostgreSQL.name().equals(dbType)) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration();
                pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.RepositoryPlugin");
                pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.RepositoryPlugin");
                context.addPluginConfiguration(pluginConfiguration);
            }
        }
        if (generatorConfig.isUseDAOExtendStyle()) {
            if (DbType.MySQL.name().equals(dbType) || DbType.MySQL_8.name().equals(dbType)
                    || DbType.PostgreSQL.name().equals(dbType)) {
                PluginConfiguration pluginConfiguration = new PluginConfiguration();
				pluginConfiguration.addProperty("useExample", String.valueOf(generatorConfig.isUseExample()));
				pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.CommonDAOInterfacePlugin");
                pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.CommonDAOInterfacePlugin");
                context.addPluginConfiguration(pluginConfiguration);
            }
        }

        context.setTargetRuntime("MyBatis3");

        List<String> warnings = new ArrayList<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        Set<String> contexts = new HashSet<>();
        ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);
        // if overrideXML selected, delete oldXML ang generate new one
		if (generatorConfig.isOverrideXML()) {
			String mappingXMLFilePath = getMappingXMLFilePath(generatorConfig);
			File mappingXMLFile = new File(mappingXMLFilePath);
			if (mappingXMLFile.exists()) {
				mappingXMLFile.delete();
			}
		}
        myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
    }

    private String getMappingXMLFilePath(GeneratorConfig generatorConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append(generatorConfig.getProjectFolder()).append("/");
		sb.append(generatorConfig.getMappingXMLTargetFolder()).append("/");
		String mappingXMLPackage = generatorConfig.getMappingXMLPackage();
		if (StringUtils.isNotEmpty(mappingXMLPackage)) {
			sb.append(mappingXMLPackage.replace(".", "/")).append("/");
		}
		if (StringUtils.isNotEmpty(generatorConfig.getMapperName())) {
			sb.append(generatorConfig.getMapperName()).append(".xml");
		} else {
			sb.append(generatorConfig.getDomainObjectName()).append("Mapper.xml");
		}

		return sb.toString();
	}

	public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }
}
