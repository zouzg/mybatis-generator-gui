package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML based config file help class
 *
 * Created by Owen on 6/16/16.
 */
public class XMLConfigHelper {

    private static final String CONFIG_FILE = "config.xml";

    public static List<DatabaseConfig> loadDatabaseConfig() {
        List<DatabaseConfig> dbs = new ArrayList<>();
        Configurations configs = new Configurations();
        try {
            XMLConfiguration config = configs.xml(new File("config.xml"));
            List<HierarchicalConfiguration<ImmutableNode>> list = config.childConfigurationsAt("");
            System.out.println(list);
            for (HierarchicalConfiguration<ImmutableNode> hc : list) {
                String name = hc.getRootElementName();
                DatabaseConfig dbConfig = new DatabaseConfig();
                dbConfig.setName(name);
                dbConfig.setHost(hc.getString("host"));
                dbConfig.setPort(hc.getString("port"));
                dbConfig.setUsername(hc.getString("userName"));
                dbConfig.setPassword(hc.getString("password"));
                dbConfig.setEncoding(hc.getString("encoding"));
                dbConfig.setDbType(hc.getString("dbType"));
                dbs.add(dbConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbs;
    }

    public static void saveDatabaseConfig(String name, DatabaseConfig dbConfig) {
        Configurations configs = new Configurations();
        try {
            // obtain the configuration
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder("config.xml");
            XMLConfiguration config = builder.getConfiguration();

            // update property
            config.addProperty(name + ".dbType", dbConfig.getDbType());
            config.addProperty(name + ".host", dbConfig.getHost());
            config.addProperty(name + ".port", dbConfig.getPort());
            config.addProperty(name + ".userName", dbConfig.getUsername());
            config.addProperty(name + ".password", dbConfig.getPassword());
            config.addProperty(name + ".encoding", dbConfig.getEncoding());

            // save configuration
            builder.save();
        } catch (ConfigurationException cex) {
            // Something went wrong
            cex.printStackTrace();
        }
    }


}
