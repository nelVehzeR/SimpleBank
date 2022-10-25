package com.api.bank;

import java.sql.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBase {
    private static final String url = "jdbc:postgresql://localhost:5432/bank";
    private static final String user = "postgres";
    private static final String password = "qwerty123";

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

    public static String getBalance (int client_id) throws SQLException, ClassNotFoundException {
        String Cash = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String sql = "select pay_balance from client where id = " + client_id;
        Statement st = getSQLStatement();

        try (ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                Balance balance = new Balance();
                balance.setBalance(rs.getString("pay_balance"));

                Cash = objectMapper.writeValueAsString(balance);
                rs.close();
            } else {
                throw new Exception("Client with id " + client_id + " is not exists.");
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            st.close();
        }

        return Cash;
    }

    public static String putMoney (int client_id, int amount) throws SQLException, ClassNotFoundException {
        String NOT_EXISTS_ERR = "Unable to put " + amount + " money to client with id " + client_id + ". Client does not exists.";
        String status = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String sql = "select pay_balance from client where id = " + client_id;
        Statement check = getSQLStatement();

        try (ResultSet rs = check.executeQuery(sql)) {
            if (rs.next()) {
                Status st = new Status();
                sql = "update client set pay_balance = pay_balance + " + amount + " where id = " + client_id;
                Statement upd = getSQLStatement();

                st.setStatus(upd.executeUpdate(sql));
                status = objectMapper.writeValueAsString(st);

                upd.close();
                rs.close();
            } else {
                throw new Exception(NOT_EXISTS_ERR);
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            check.close();
        }

        return status;
    }

    public static String takeMoney (int client_id, int amount) throws SQLException, ClassNotFoundException {
        String NOT_EXISTS_ERR = "Unable to take " + amount + " money from client with id " + client_id + ". Client does not exists.";
        String status = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String sql = "select pay_balance from client where id = " + client_id;
        Statement check = getSQLStatement();

        try (ResultSet rs = check.executeQuery(sql)) {
            if (rs.next()) {
                if (rs.getInt("pay_balance") <= 0 || amount > rs.getInt("pay_balance")) {
                    throw new Exception("Oops! Not enough money");
                } else {
                    Status st = new Status();
                    sql = "update client set pay_balance = pay_balance - " + amount + " where id = " + client_id;
                    Statement upd = getSQLStatement();

                    st.setStatus(upd.executeUpdate(sql));
                    status = objectMapper.writeValueAsString(st);

                    upd.close();
                }

                rs.close();
            } else {
                throw new Exception(NOT_EXISTS_ERR);
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            check.close();
        }

        return status;
    }

    public static String transferMoney (int from_client, int to_client, int amount) throws ClassNotFoundException, SQLException {
        String status = null;
        ObjectMapper objectMapper = new ObjectMapper();

        /* Проверка отправителя */
        int check_from = 0;
        String sql = "select pay_balance from client where id = " + from_client;
        Statement check = getSQLStatement();
        ResultSet rs = check.executeQuery(sql);

        if (rs.next()) {
            check_from = 1;
        }
        rs.close();

        /* Проверка получателя */
        int check_to = 0;
        sql = "select pay_balance from client where id = " + to_client;
        check = getSQLStatement();
        rs = check.executeQuery(sql);

        if (rs.next()) {
            check_to = 1;
        }
        rs.close();

        check.close();

        try {
            if (check_from == 1 && check_to == 1) {
                if (rs.getInt("pay_balance") <= 0 || amount > rs.getInt("pay_balance")) {
                    throw new Exception("You trying to transfer money to another client? Think you have any?");
                } else {
                    Status st = new Status();
                    /*  Снятие суммы со счёта отправителя */
                    sql = "update client set pay_balance = pay_balance - " + amount + " where id = " + from_client;
                    Statement upd = getSQLStatement();
                    st.setStatus(upd.executeUpdate(sql));
                    upd.close();

                    /* Обновление счёта получателя  */
                    sql = "update client set pay_balance = pay_balance + " + amount + " where id = " + to_client;
                    upd = getSQLStatement();
                    st.setStatus(upd.executeUpdate(sql));
                    upd.close();

                    status = objectMapper.writeValueAsString(st);

                    if (status == null) {
                        throw new Exception("Something gone wrong...");
                    }
                }
            } else {
                throw new Exception("One or both clients are not registered in the system.");
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }

        return status;
    }

    /*public static void registerOperation (int client_id, String pay_id, int amount, int type_operation) throws SQLException, ClassNotFoundException {
        String sql = "";
        switch (type_operation) {
            case 1: /*  Снять со счёта  *//*
                sql = "insert into operation (client_id, date_created, amount, oper_type_id, proceed_from) values (" + client_id + ", now(), " + amount + ", " + type_operation + ", '" + pay_id + "')";
                break;
            case 2: /*  Внести на счёт  *//*
                sql = "insert into operation (client_id, date_created, amount, oper_type_id, proceed_to) values (" + client_id + ", now(), " + amount + ", " + type_operation + ", '" + pay_id + "')";
                break;
        }

        Statement st = getSQLStatement();

        int rs = st.executeUpdate(sql);
        st.close();
    }*/
}