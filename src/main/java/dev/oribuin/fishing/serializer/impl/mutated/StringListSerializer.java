package dev.oribuin.fishing.serializer.impl.mutated;

import com.google.gson.Gson;
import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StringListSerializer {

    private static final Gson GSON = new Gson();

    public static ConfigDataSerializer<List<String>> CONFIG = new ConfigDataSerializer<>() {
        /**
         * Read the data type from the config file
         *
         * @param section The section to read from
         * @param path    The path to the object to read
         *
         * @return The returning value
         */
        @Override
        public @Nullable List<String> read(@NotNull ConfigurationSection section, @NotNull String path) {
            return section.getStringList(path);
        }
    };

    public static SQLDataSerializer<List<String>> SQL = new SQLDataSerializer<>() {
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
        public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull List<String> value) throws SQLException {
            statement.setString(1, GSON.toJson(new StringList(value)));
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
        public @Nullable List<String> read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
            return GSON.fromJson(resultSet.getString(columnLabel), StringList.class).values();
        }
    };

    private record StringList(List<String> values) {}
}
