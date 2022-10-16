package com.api.bank;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println(DBase.getBalance(1, "92938475928319238475"));
        System.out.println(DBase.putMoney(1, "92938475928319238475", 23499));
        System.out.println(DBase.takeMoney(1, "92938475928319238475", 15499));
    }
}
