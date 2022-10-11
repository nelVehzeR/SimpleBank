package com.api.bank;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class DBase {
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String user = "postgres";
    private static String password = "qwerty123";

    private static Connection getConnection () throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);

        return conn;
    }

    public static Statement getSQLStatement (String sqlRequest) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();

        Statement st = conn.createStatement();
        return st;
    }
}
