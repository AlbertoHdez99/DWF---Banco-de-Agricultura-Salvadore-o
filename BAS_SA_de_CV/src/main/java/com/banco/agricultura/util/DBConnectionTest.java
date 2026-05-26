package com.banco.agricultura.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DBConnectionTest {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Conexión a MySQL exitosa: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}