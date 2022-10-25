package com.api.bank;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.sql.SQLException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class App {
    private static final int svPort = 8000;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(svPort), 0);

        // http://localhost:8000/balance?client_id=1
        server.createContext("/balance", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            int id = Integer.parseInt(params.get("client_id").stream().findFirst().orElse(null));

            String respText = null;

            try {
                respText = DBase.getBalance(id);
                exchange.sendResponseHeaders(200, respText.getBytes().length);

                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }));

        // http://localhost:8000/put?client_id=1&amount=345
        server.createContext("/put", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            int id = Integer.parseInt(params.get("client_id").stream().findFirst().orElse(null));
            int amount = Integer.parseInt(params.get("amount").stream().findFirst().orElse(null));

            String respText = null;

            try {
                respText = DBase.putMoney(id, amount);

                exchange.sendResponseHeaders(200, respText.getBytes().length);

                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }));

        // http://localhost:8000/take?client_id=1&amount=345
        server.createContext("/take", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            int id = Integer.parseInt(params.get("client_id").stream().findFirst().orElse(null));
            int amount = Integer.parseInt(params.get("amount").stream().findFirst().orElse(null));

            String respText = null;

            try {
                respText = DBase.takeMoney(id, amount);

                exchange.sendResponseHeaders(200, respText.getBytes().length);

                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }));

        // http://localhost:8000/transfer?client_from=1&to_client=2&amount=345
        server.createContext("/transfer", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            int client_from = Integer.parseInt(params.get("client_from").stream().findFirst().orElse(null));
            int to_client = Integer.parseInt(params.get("to_client").stream().findFirst().orElse(null));
            int amount = Integer.parseInt(params.get("amount").stream().findFirst().orElse(null));

            String respText = null;

            try {
                respText = DBase.transferMoney(client_from, to_client, amount);

                exchange.sendResponseHeaders(200, respText.getBytes().length);

                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }));

        server.start();
    }

    public static Map<String, List<String>> splitQuery (String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode (final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Wrong encoding! Need: UTF-8.", e);
        }
    }

    /*public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println(DBase.getBalance(1, "92938475928319238475"));
        //System.out.println(DBase.putMoney(1, "92938475928319238475", 23499));
        //System.out.println(DBase.takeMoney(1, "92938475928319238475", 15499));
    }*/

    /*public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(svPort), 0);
        server.createContext("/api/hello", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            String noNameText = "Anonymous";
            String name = params.getOrDefault("name", List.of(noNameText)).stream().findFirst().orElse(noNameText);
            String respText = String.format("Hello %s!", name);
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        }));

        server.start();
    }

    public static Map<String, List<String>> splitQuery (String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode (final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Кодировка содержимого не соответствует требуемой: UTF-8.", e);
        }
    }*/
}
