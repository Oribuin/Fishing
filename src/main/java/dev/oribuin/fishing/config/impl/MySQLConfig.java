package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class MySQLConfig {

    public static MySQLConfig get() {
        return FishingPlugin.get().getConfigLoader().get(MySQLConfig.class);
    }
    
    @Comment("Should MySQL be enabled for database storage?")
    private boolean enabled = false;

    
    @Comment("The MySQL Host Name")
    private String hostname = "127.0.0.1";

    @Comment("The MySQL Port Number")
    private int port = 3306;

    @Comment("The MySQL Database Name")
    private String databaseName = "";

    @Comment("The MySQL Username")
    private String username = "";

    @Comment("The MySQL Password")
    private String password = "";

    @Comment("Should MySQL use ssl to connect to the database")
    private boolean ssl = false;

    @Comment("The number of connections to make to the database")
    private int connectionPoolSize = 3;

    public boolean isEnabled() {
        return enabled;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean useSSL() {
        return ssl;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }
}
