package dev.oribuin.fishing.serializer.impl;

import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDSerializer {

    public static ConfigDataSerializer<UUID> CONFIG = new ConfigDataSerializer<>() {
        /**
         * Read the data type from the config file
         *
         * @param section The section to read from
         * @param path    The path to the object to read
         *
         * @return The returning value
         */
        @Override
        public @Nullable UUID read(@NotNull ConfigurationSection section, @NotNull String path) {
            String content = section.getString(path);
            return content != null ? UUID.fromString(content) : null;
        }
    };

    public static SQLDataSerializer<UUID> SQL = new SQLDataSerializer<>() {
        /**
         * Write a data value into a database prepared statement
         *
         * @param statement      The statement to write into
         * @param parameterIndex The parameter index of the value
         * @param value          The value to write
         *
         * @throws SQLException Any exceptions that may occur
         */
        @Override
        public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull UUID value) throws SQLException {
            statement.setString(parameterIndex, value.toString());
        }

        /**
         * Read a data value from a database prepared statement
         *
         * @param resultSet   The result set to read from
         * @param columnLabel The label of the column
         *
         * @return The value if available
         */
        @Override
        public @NotNull UUID read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
            return UUID.fromString(resultSet.getString(columnLabel));
        }
    };

}
