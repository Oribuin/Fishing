package dev.oribuin.fishing.serializer.impl;

import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalSerializer {

    public static ConfigDataSerializer<BigDecimal> CONFIG = new ConfigDataSerializer<>() {
        /**
         * Write a data type into the config file
         *
         * @param section The config section to write to
         * @param path    The path to the value to write
         * @param value   The value to write into the config
         */
        @Override
        public void write(@NotNull ConfigurationSection section, @NotNull String path, @NotNull BigDecimal value) {
            section.set(path, value.doubleValue());
        }

        /**
         * Read the data type from the config file
         *
         * @param section The section to read from
         * @param path    The path to the object to read
         *
         * @return The returning value
         */
        @Override
        public @NotNull BigDecimal read(@NotNull ConfigurationSection section, @NotNull String path) {
            return BigDecimal.valueOf(section.getDouble(path));
        }
    };

    public static SQLDataSerializer<BigDecimal> SQL = new SQLDataSerializer<>() {
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
        public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull BigDecimal value) throws SQLException {
            statement.setBigDecimal(parameterIndex, value);
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
        public @NotNull BigDecimal read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
            return resultSet.getBigDecimal(columnLabel);
        }
    };

}
