package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException {

        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final Long cardId, final String reason) throws SQLException{
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
        }
    }
}
