package net.hypedmc.dungeon.database

import com.google.inject.Inject
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.hypedmc.dungeon.HypedDungeon
import org.jetbrains.annotations.NotNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * Manages database connections and operations for the plugin.
 * Handles connection pooling, database creation, and table management.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class DatabaseManager @Inject constructor(
    @NotNull private val plugin: HypedDungeon
) {
    private lateinit var dataSource: HikariDataSource

    /**
     * Initializes the database connection and creates necessary tables.
     * This method should be called during plugin startup.
     */
    fun init() {
        // Load database configuration
        val dbConfig = loadDatabaseConfig()
        
        // Try to create database if it doesn't exist
        createDatabaseIfNotExists(dbConfig)
        
        // Configure HikariCP
        val config = HikariConfig().apply {
            driverClassName = dbConfig.type.driverClass
            jdbcUrl = dbConfig.type.getJdbcUrl(dbConfig.host, dbConfig.port, dbConfig.database)
            username = dbConfig.username
            password = dbConfig.password
            
            maximumPoolSize = dbConfig.poolConfig.maxPoolSize
            minimumIdle = dbConfig.poolConfig.minIdle
            idleTimeout = dbConfig.poolConfig.idleTimeout
            maxLifetime = dbConfig.poolConfig.maxLifetime
            connectionTimeout = dbConfig.poolConfig.connectionTimeout
            
            // MySQL/MariaDB specific settings
            if (dbConfig.type in listOf(DatabaseType.MYSQL, DatabaseType.MARIADB)) {
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
                addDataSourceProperty("useServerPrepStmts", "true")
                addDataSourceProperty("useLocalSessionState", "true")
                addDataSourceProperty("rewriteBatchedStatements", "true")
                addDataSourceProperty("cacheResultSetMetadata", "true")
                addDataSourceProperty("cacheServerConfiguration", "true")
                addDataSourceProperty("elideSetAutoCommits", "true")
                addDataSourceProperty("maintainTimeStats", "false")
            }
        }

        // Create connection pool
        dataSource = HikariDataSource(config)

        // Connect and create tables
        Database.connect(dataSource)
        createTables(dbConfig.type)

        plugin.logger.info("Successfully connected to database!")
    }

    /**
     * Creates the database if it doesn't exist.
     *
     * @param config The database configuration
     * @throws Exception if database creation fails
     */
    private fun createDatabaseIfNotExists(@NotNull config: DatabaseConfig) {
        try {
            // Register JDBC driver
            Class.forName(config.type.driverClass)

            // Create temporary connection without specifying database
            val jdbcUrl = config.type.getJdbcUrl(config.host, config.port)
            
            DriverManager.getConnection(jdbcUrl, config.username, config.password).use { connection ->
                // Check if database exists
                val resultSet = connection.prepareStatement(
                    config.type.getDatabaseExistsQuery()
                ).apply {
                    setString(1, config.database)
                }.executeQuery()

                if (!resultSet.next()) {
                    // Create database if it doesn't exist
                    connection.createStatement().use { statement ->
                        val createDbSql = config.type.createDatabaseSql.replace("?", config.database)
                        statement.executeUpdate(createDbSql)
                    }
                    plugin.logger.info("Database '${config.database}' created successfully!")
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Error trying to create database: ${e.message}")
            throw e
        }
    }

    /**
     * Closes the database connection pool.
     * This method should be called during plugin shutdown.
     */
    fun shutdown() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }

    /**
     * Creates database tables and sets up UTF-8 encoding.
     *
     * @param type The database type
     */
    private fun createTables(@NotNull type: DatabaseType) {
        transaction {
            // Configure database to use UTF-8
            type.setUtf8Sql.forEach { sql ->
                exec(sql)
            }

            SchemaUtils.createMissingTablesAndColumns(
                Players,
                PlayerStats,
                DungeonCompletions,
                PlayerAchievements
            )
        }
    }

    /**
     * Loads database configuration from the config file.
     *
     * @return The database configuration
     */
    @NotNull
    private fun loadDatabaseConfig(): DatabaseConfig {
        val file = File(plugin.dataFolder, "database.yml")
        if (!file.exists()) {
            plugin.saveResource("database.yml", false)
        }

        val config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file)
        return DatabaseConfig(
            type = DatabaseType.valueOf(config.getString("type", "MYSQL")?.uppercase() ?: "MYSQL"),
            host = config.getString("host", "localhost") ?: "localhost",
            port = config.getInt("port", -1).let { port ->
                if (port == -1) {
                    DatabaseType.valueOf(config.getString("type", "MYSQL")?.uppercase() ?: "MYSQL").defaultPort
                } else {
                    port
                }
            },
            database = config.getString("database", "hypeddungeon") ?: "hypeddungeon",
            username = config.getString("username", "root") ?: "root",
            password = config.getString("password", "") ?: "",
            poolConfig = PoolConfig(
                maxPoolSize = config.getInt("pool.maxPoolSize", 10),
                minIdle = config.getInt("pool.minIdle", 2),
                idleTimeout = config.getLong("pool.idleTimeout", 300000),
                maxLifetime = config.getLong("pool.maxLifetime", 600000),
                connectionTimeout = config.getLong("pool.connectionTimeout", 5000)
            )
        )
    }

    /**
     * Data class representing database configuration.
     *
     * @property type The database type
     * @property host The database host
     * @property port The database port
     * @property database The database name
     * @property username The database username
     * @property password The database password
     * @property poolConfig The connection pool configuration
     */
    data class DatabaseConfig(
        @NotNull val type: DatabaseType,
        @NotNull val host: String,
        @NotNull val port: Int,
        @NotNull val database: String,
        @NotNull val username: String,
        @NotNull val password: String,
        @NotNull val poolConfig: PoolConfig
    )

    /**
     * Data class representing connection pool configuration.
     *
     * @property maxPoolSize Maximum number of connections in the pool
     * @property minIdle Minimum number of idle connections
     * @property idleTimeout Time in milliseconds after which idle connections are removed
     * @property maxLifetime Maximum lifetime of a connection in milliseconds
     * @property connectionTimeout Maximum time to wait for a connection in milliseconds
     */
    data class PoolConfig(
        @NotNull val maxPoolSize: Int,
        @NotNull val minIdle: Int,
        @NotNull val idleTimeout: Long,
        @NotNull val maxLifetime: Long,
        @NotNull val connectionTimeout: Long
    )

    enum class DatabaseType(
        val driverClass: String,
        val defaultPort: Int,
        val getJdbcUrl: (String, Int, String) -> String,
        val getDatabaseExistsQuery: () -> String,
        val createDatabaseSql: String,
        val setUtf8Sql: List<String>
    ) {
        MYSQL(
            "com.mysql.cj.jdbc.Driver",
            3306,
            { host, port, database -> "jdbc:mysql://$host:$port/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8" },
            { "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?" },
            "CREATE DATABASE IF NOT EXISTS ? CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
            listOf("SET NAMES utf8mb4", "SET CHARACTER SET utf8mb4", "SET character_set_connection=utf8mb4")
        ),
        MARIADB(
            "org.mariadb.jdbc.Driver",
            3306,
            { host, port, database -> "jdbc:mariadb://$host:$port/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8" },
            { "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?" },
            "CREATE DATABASE IF NOT EXISTS ? CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
            listOf("SET NAMES utf8mb4", "SET CHARACTER SET utf8mb4", "SET character_set_connection=utf8mb4")
        ),
        POSTGRESQL(
            "org.postgresql.Driver",
            5432,
            { host, port, database -> "jdbc:postgresql://$host:$port/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8" },
            { "SELECT datname FROM pg_database WHERE datname = ?" },
            "CREATE DATABASE ? WITH ENCODING 'UTF8'",
            listOf("SET NAMES utf8", "SET CLIENT_ENCODING TO 'UTF8'")
        ),
        SQLITE(
            "org.sqlite.JDBC",
            -1,
            { _, _, database -> "jdbc:sqlite:$database" },
            { "SELECT name FROM sqlite_master WHERE type='table' AND name=?" },
            "",
            listOf()
        )
    }
}
