package dev.oribuin.fishing.serializer.impl.mutated;

import dev.oribuin.fishing.serializer.ConfigDataSerializer;
import dev.oribuin.fishing.serializer.SQLDataSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumSerializer {

    public static <T extends Enum<T>> ConfigDataSerializer<Enum<T>> asConfig(Class<T> enumClass) {
        return new ConfigDataSerializer<>() {
            /**
             * Read the data type from the config file
             *
             * @param section The section to read from
             * @param path    The path to the object to read
             *
             * @return The returning value
             */
            @Override
            public @Nullable Enum<T> read(@NotNull ConfigurationSection section, @NotNull String path) {
                try {
                    String content = section.getString(path);
                    if (content == null) return null;

                    return Enum.valueOf(enumClass, content);
                } catch (IllegalArgumentException | NullPointerException e) {
                    return null;
                }
            }
        };
    }

    public static <T extends Enum<T>> SQLDataSerializer<Enum<T>> asSql(Class<T> enumClass) {
        return new SQLDataSerializer<>() {
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
            public void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull Enum<T> value) throws SQLException {
                statement.setString(parameterIndex, value.name());
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
            public @NotNull Enum<T> read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException {
                return Enum.valueOf(enumClass, resultSet.getString(columnLabel));
            }
        };
    }
}
