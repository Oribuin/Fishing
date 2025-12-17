package dev.oribuin.fishing.serializer.impl;

import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerSerializer {

    public static ConfigDataSerializer<Integer> CONFIG = new ConfigDataSerializer<>() {
        /**
         * Read the data type from the config file
         *
         * @param section The section to read from
         * @param path    The path to the object to read
         *
         * @return The returning value
         */
        @Override
        public @NotNull Integer read(@NotNull ConfigurationSection section, @NotNull String path) {
            return section.getInt(path);
        }
    };

    public static SQLDataSerializer<Integer> SQL = new SQLDataSerializer<>() {
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
        public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull Integer value) throws SQLException {
            statement.setInt(parameterIndex, value);
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
        public @NotNull Integer read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
            return resultSet.getInt(columnLabel);
        }
    };

}
