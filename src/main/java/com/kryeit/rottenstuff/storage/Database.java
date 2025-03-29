package com.kryeit.rottenstuff.storage;

import com.kryeit.rottenstuff.ConfigReader;
import com.kryeit.rottenstuff.lives.Lives;
import com.kryeit.rottenstuff.lives.LivesMapper;
import com.mojang.logging.LogUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final Jdbi JDBI;
    private static final HikariDataSource dataSource;

    static {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(ConfigReader.DB_USER);
        hikariConfig.setPassword(ConfigReader.DB_PASSWORD);
        hikariConfig.setJdbcUrl(ConfigReader.DB_URL);

        try {
            dataSource = new HikariDataSource(hikariConfig);
            JDBI = Jdbi.create(dataSource);
            JDBI.registerRowMapper(Lives.class, new LivesMapper());

            JDBI.installPlugin(new Jackson2Plugin());
        } catch (Exception e) {
            logger.error("Failed to initialize database connection", e);
            throw new ExceptionInInitializerError(e);
        }

    }

    public static Jdbi getJdbi() {
        return JDBI;
    }

    public static void closeDataSource() {
        LogUtils.getLogger().info("Closing database connection...");
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}