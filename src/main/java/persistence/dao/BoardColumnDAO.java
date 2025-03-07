package persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import persistence.entity.BoardColumnEntity;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getOrder());
            statement.setString(3, entity.getKind().name());
            statement.setLong(4, entity.getBoard().getId());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

}
