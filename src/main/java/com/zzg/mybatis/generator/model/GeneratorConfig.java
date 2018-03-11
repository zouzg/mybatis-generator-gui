package com.zzg.mybatis.generator.model;

/**
 *
 * GeneratorConfig is the Config of mybatis generator config exclude database
 * config
 *
 * Created by Owen on 6/16/16.
 */
public class GeneratorConfig {

	/**
	 * 本配置的名称
	 */
	private String name;

	private String connectorJarPath;

	private String projectFolder;

	private String modelPackage;

	private String modelPackageTargetFolder;

	private String daoPackage;

	private String daoTargetFolder;

	private String mapperName;

	private String mappingXMLPackage;

	private String mappingXMLTargetFolder;

	private String tableName;

	private String domainObjectName;

	private boolean offsetLimit;

	private boolean comment;

	private boolean overrideXML;

	private boolean needToStringHashcodeEquals;

	private boolean needForUpdate;

	private boolean annotationDAO;

	private boolean annotation;

	private boolean useActualColumnNames;

	private boolean useExample;

	private String generateKeys;

	private String encoding;

	private boolean useTableNameAlias;

	private boolean useDAOExtendStyle;

    private boolean useSchemaPrefix;

    private boolean jsr310Support;

    public boolean isJsr310Support() {
        return jsr310Support;
    }

    public void setJsr310Support(boolean jsr310Support) {
        this.jsr310Support = jsr310Support;
    }

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

	public boolean isUseExample() {
		return useExample;
	}

	public void setUseExample(boolean useExample) {
		this.useExample = useExample;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDomainObjectName() {
		return domainObjectName;
	}

	public void setDomainObjectName(String domainObjectName) {
		this.domainObjectName = domainObjectName;
	}

	public String getConnectorJarPath() {
		return connectorJarPath;
	}

	public void setConnectorJarPath(String connectorJarPath) {
		this.connectorJarPath = connectorJarPath;
	}

	public String getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getModelPackageTargetFolder() {
		return modelPackageTargetFolder;
	}

	public void setModelPackageTargetFolder(String modelPackageTargetFolder) {
		this.modelPackageTargetFolder = modelPackageTargetFolder;
	}

	public String getDaoPackage() {
		return daoPackage;
	}

	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}

	public String getDaoTargetFolder() {
		return daoTargetFolder;
	}

	public void setDaoTargetFolder(String daoTargetFolder) {
		this.daoTargetFolder = daoTargetFolder;
	}

	public String getMappingXMLPackage() {
		return mappingXMLPackage;
	}

	public void setMappingXMLPackage(String mappingXMLPackage) {
		this.mappingXMLPackage = mappingXMLPackage;
	}

	public String getMappingXMLTargetFolder() {
		return mappingXMLTargetFolder;
	}

	public void setMappingXMLTargetFolder(String mappingXMLTargetFolder) {
		this.mappingXMLTargetFolder = mappingXMLTargetFolder;
	}

	public boolean isOffsetLimit() {
		return offsetLimit;
	}

	public void setOffsetLimit(boolean offsetLimit) {
		this.offsetLimit = offsetLimit;
	}

	public boolean isComment() {
		return comment;
	}

	public void setComment(boolean comment) {
		this.comment = comment;
	}

    public boolean isNeedToStringHashcodeEquals() {
        return needToStringHashcodeEquals;
    }

    public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
        this.needToStringHashcodeEquals = needToStringHashcodeEquals;
    }

	public boolean isNeedForUpdate() {
		return needForUpdate;
	}

	public void setNeedForUpdate(boolean needForUpdate) {
		this.needForUpdate = needForUpdate;
	}

	public boolean isAnnotationDAO() {
		return annotationDAO;
	}

	public void setAnnotationDAO(boolean annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

	public boolean isAnnotation() {
		return annotation;
	}

	public void setAnnotation(boolean annotation) {
		this.annotation = annotation;
	}

	public boolean isUseActualColumnNames() {
		return useActualColumnNames;
	}

	public void setUseActualColumnNames(boolean useActualColumnNames) {
		this.useActualColumnNames = useActualColumnNames;
	}

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}

	public String getGenerateKeys() {
		return generateKeys;
	}

	public void setGenerateKeys(String generateKeys) {
		this.generateKeys = generateKeys;
	}

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

	public boolean getUseTableNameAlias() {
		return useTableNameAlias;
	}

	public void setUseTableNameAlias(boolean useTableNameAlias) {
		this.useTableNameAlias = useTableNameAlias;
	}

	public boolean isUseTableNameAlias() {
		return useTableNameAlias;
	}

	public boolean isOverrideXML() {
		return overrideXML;
	}

	public void setOverrideXML(boolean overrideXML) {
		this.overrideXML = overrideXML;
	}

	public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
		this.useDAOExtendStyle = useDAOExtendStyle;
	}

	public boolean isUseDAOExtendStyle() {
		return useDAOExtendStyle;
	}
}
