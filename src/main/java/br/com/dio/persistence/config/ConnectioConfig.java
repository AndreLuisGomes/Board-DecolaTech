package br.com.dio.persistence.config;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class ConnectioConfig {


    public static Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/board",
                "board",
                "board");
        connection.setAutoCommit(false);
        return connection;
    }
}
