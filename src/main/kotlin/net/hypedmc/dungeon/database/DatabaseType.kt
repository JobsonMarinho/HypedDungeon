package net.hypedmc.dungeon.database

import org.jetbrains.annotations.NotNull

/**
 * Enum representing different database types supported by the plugin.
 * Each type contains specific configuration for connection and character encoding.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class DatabaseType(
    /**
     * The JDBC driver class for the database type.
     */
    @NotNull
    val driverClass: String,

    /**
     * The default port number for the database type.
     * MySQL/MariaDB: 3306
     * PostgreSQL: 5432
     */
    @NotNull
    val defaultPort: Int,

    /**
     * The SQL query template to create a new database.
     * The '?' placeholder will be replaced with the actual database name.
     */
    @NotNull
    val createDatabaseSql: String,

    /**
     * List of SQL queries to set UTF-8 encoding for the database connection.
     */
    @NotNull
    val setUtf8Sql: List<String>
) {
    /**
     * MySQL database configuration.
     * Uses the MySQL Connector/J driver and UTF-8 character encoding.
     */
    MYSQL(
        driverClass = "com.mysql.cj.jdbc.Driver",
        defaultPort = 3306,
        createDatabaseSql = "CREATE DATABASE IF NOT EXISTS ? CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
        setUtf8Sql = listOf(
            "SET NAMES utf8mb4",
            "SET CHARACTER SET utf8mb4",
            "SET character_set_connection=utf8mb4"
        )
    ) {
        /**
         * Generates a JDBC URL for MySQL connection.
         *
         * @param host The database server host
         * @param port The database server port
         * @param database Optional database name
         * @return Complete JDBC URL with connection parameters
         */
        @NotNull
        override fun getJdbcUrl(@NotNull host: String, @NotNull port: Int, database: String?): String {
            val baseUrl = "jdbc:mysql://$host:$port"
            return if (database != null) {
                "$baseUrl/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
            } else {
                "$baseUrl?useSSL=false&serverTimezone=UTC"
            }
        }

        /**
         * Returns the SQL query to check if a database exists in MySQL.
         *
         * @return SQL query string
         */
        @NotNull
        override fun getDatabaseExistsQuery(): String {
            return "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
        }
    },

    /**
     * MariaDB database configuration.
     * Uses the MariaDB Connector/J driver and UTF-8 character encoding.
     */
    MARIADB(
        driverClass = "org.mariadb.jdbc.Driver",
        defaultPort = 3306,
        createDatabaseSql = "CREATE DATABASE IF NOT EXISTS ? CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
        setUtf8Sql = listOf(
            "SET NAMES utf8mb4",
            "SET CHARACTER SET utf8mb4",
            "SET character_set_connection=utf8mb4"
        )
    ) {
        /**
         * Generates a JDBC URL for MariaDB connection.
         *
         * @param host The database server host
         * @param port The database server port
         * @param database Optional database name
         * @return Complete JDBC URL with connection parameters
         */
        @NotNull
        override fun getJdbcUrl(@NotNull host: String, @NotNull port: Int, database: String?): String {
            val baseUrl = "jdbc:mariadb://$host:$port"
            return if (database != null) {
                "$baseUrl/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
            } else {
                "$baseUrl?useSSL=false&serverTimezone=UTC"
            }
        }

        /**
         * Returns the SQL query to check if a database exists in MariaDB.
         *
         * @return SQL query string
         */
        @NotNull
        override fun getDatabaseExistsQuery(): String {
            return "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
        }
    },

    /**
     * PostgreSQL database configuration.
     * Uses the PostgreSQL JDBC driver and UTF-8 character encoding.
     */
    POSTGRESQL(
        driverClass = "org.postgresql.Driver",
        defaultPort = 5432,
        createDatabaseSql = "CREATE DATABASE ? WITH ENCODING 'UTF8' LC_COLLATE 'C' LC_CTYPE 'C' TEMPLATE template0",
        setUtf8Sql = listOf(
            "SET client_encoding = 'UTF8'"
        )
    ) {
        /**
         * Generates a JDBC URL for PostgreSQL connection.
         *
         * @param host The database server host
         * @param port The database server port
         * @param database Optional database name
         * @return Complete JDBC URL with connection parameters
         */
        @NotNull
        override fun getJdbcUrl(@NotNull host: String, @NotNull port: Int, database: String?): String {
            val baseUrl = "jdbc:postgresql://$host:$port"
            return if (database != null) {
                "$baseUrl/$database"
            } else {
                "$baseUrl/postgres"
            }
        }

        /**
         * Returns the SQL query to check if a database exists in PostgreSQL.
         *
         * @return SQL query string
         */
        @NotNull
        override fun getDatabaseExistsQuery(): String {
            return "SELECT datname FROM pg_database WHERE datname = ?"
        }
    };

    /**
     * Generates a JDBC URL for the specific database type.
     *
     * @param host The database server host
     * @param port The database server port
     * @param database Optional database name, if null connects to default database
     * @return Complete JDBC URL with connection parameters
     */
    @NotNull
    abstract fun getJdbcUrl(@NotNull host: String, @NotNull port: Int, database: String? = null): String

    /**
     * Returns the SQL query to check if a database exists for the specific database type.
     *
     * @return SQL query string
     */
    @NotNull
    abstract fun getDatabaseExistsQuery(): String
}
