package dev.oribuin.fishing.serializer.impl.spigot;

import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemStackArraySerializer {

    public static ConfigDataSerializer<ItemStack[]> CONFIG = new ConfigDataSerializer<>() {
        /**
         * Write a data type into the config file
         *
         * @param section The config section to write to
         * @param path    The path to the value to write
         * @param value   The value to write into the config
         */
        @Override
        public void write(@NotNull ConfigurationSection section, @NotNull String path, @NotNull ItemStack[] value) {
            section.set(path, ItemStack.serializeItemsAsBytes(value));
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
        public @NotNull ItemStack[] read(@NotNull ConfigurationSection section, @NotNull String path) {
            try {
                return ItemStack.deserializeItemsFromBytes(path.getBytes(path));
            } catch (UnsupportedEncodingException e) {
                return new ItemStack[]{};
            }
        }
    };

    public static SQLDataSerializer<ItemStack[]> SQL = new SQLDataSerializer<>() {
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
        public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull ItemStack[] value) throws SQLException {
            statement.setBytes(parameterIndex, ItemStack.serializeItemsAsBytes(value));
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
        public @NotNull ItemStack[] read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
            return ItemStack.deserializeItemsFromBytes(resultSet.getBytes(columnLabel));
        }
    };

}
