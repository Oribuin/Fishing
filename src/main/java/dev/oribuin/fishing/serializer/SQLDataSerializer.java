package dev.oribuin.fishing.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLDataSerializer<T> {

    /**
     * Write a data value into a database prepared statement
     *
     * @param statement      The statement to write into
     * @param parameterIndex The parameter index of the value
     * @param value          The value to write
     *
     * @throws SQLException Any exceptions that may occur
     */
    void write(@NotNull PreparedStatement statement, int parameterIndex, @NotNull T value) throws SQLException;

    /**
     * Read a data value from a database prepared statement
     *
     * @param resultSet   The result set to read from
     * @param columnLabel The label of the column
     *
     * @return The value if available
     */
    @Nullable
    T read(@NotNull ResultSet resultSet, @NotNull String columnLabel) throws SQLException;

}
