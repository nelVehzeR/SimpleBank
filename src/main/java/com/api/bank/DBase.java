package com.api.bank;

import java.sql.*;

public class DBase {
    private static String url = "jdbc:postgresql://localhost:5432/bank";
    private static String user = "postgres";
    private static String password = "qwerty123";

    private static Connection getConnection () throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);

        return conn;
    }

    public static Statement getSQLStatement () throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();

        Statement st = conn.createStatement();
        return st;
    }

    public static String getBalance (int client_id, String pay_id) throws SQLException, ClassNotFoundException {
        String Cash = null;
        String sql = "select * from pay_acount pa where client_id = " + client_id + " and pay_id = '" + pay_id + "'";
        Statement st = getSQLStatement();

        try (ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                Cash = rs.getString("cash");
                return Cash;
            } else {
                throw new Exception("Данные о балансе для выбранного клиента отсутствуют. Расчётный счёт не зарегистрирован.");
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            st.close();
        }
    }

    public static String putMoney (int client_id, String pay_id, int amount) throws SQLException, ClassNotFoundException {
        String sql = "update pay_acount set cash = cash + " + amount + "::money where client_id = " + client_id + " and pay_id = '" + pay_id + "'";
        Statement st = getSQLStatement();

        int rs = st.executeUpdate(sql);

        st.close();

        registerOperation(client_id, pay_id, amount, 2);
        return getBalance(client_id, pay_id);
    }

    public static String takeMoney (int client_id, String pay_id, int amount) throws SQLException, ClassNotFoundException {
        String sql = "update pay_acount set cash = cash - " + amount + "::money where client_id = " + client_id + " and pay_id = '" + pay_id + "'";
        Statement st = getSQLStatement();

        int rs = st.executeUpdate(sql);

        st.close();

        registerOperation(client_id, pay_id, amount, 1);
        return getBalance(client_id, pay_id);
    }

    public static void registerOperation (int client_id, String pay_id, int amount, int type_operation) throws SQLException, ClassNotFoundException {
        String sql = "";
        switch (type_operation) {
            case 1: /*  Снять со счёта  */
                sql = "insert into operation (client_id, date_created, amount, oper_type_id, proceed_from) values (" + client_id + ", now(), " + amount + ", " + type_operation + ", '" + pay_id + "')";
                break;
            case 2: /*  Внести на счёт  */
                sql = "insert into operation (client_id, date_created, amount, oper_type_id, proceed_to) values (" + client_id + ", now(), " + amount + ", " + type_operation + ", '" + pay_id + "')";
                break;
        }

        Statement st = getSQLStatement();

        int rs = st.executeUpdate(sql);
        st.close();
    }
}
