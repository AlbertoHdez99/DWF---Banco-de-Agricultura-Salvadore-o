package com.banco.agricultura.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.banco.agricultura")
public class AppConfig {

    // ─── 1. DataSource con HikariCP ───────────────────────────────────────────
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/Banco_de_Agricultura" +
                "?serverTimezone=America/El_Salvador" +
                "&useSSL=false" +
                "&allowPublicKeyRetrieval=true" +
                "&characterEncoding=UTF-8");
        ds.setUsername("root");
        ds.setPassword("");

        // Pool settings
        ds.setMaximumPoolSize(10);
        ds.setMinimumIdle(2);
        ds.setConnectionTimeout(30000);   // 30 segundos
        ds.setIdleTimeout(600000);        // 10 minutos
        ds.setMaxLifetime(1800000);       // 30 minutos
        ds.setPoolName("BancoAgriculturaPool");

        return ds;
    }

    // ─── 2. EntityManagerFactory (JPA + Hibernate) ───────────────────────────
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em =
                new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource());
        em.setPersistenceUnitName("BancoAgriculturaPU");
        em.setPackagesToScan("com.banco.agricultura.entity");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        em.setJpaVendorAdapter(adapter);

        em.setJpaProperties(hibernateProperties());
        return em;
    }

    // ─── 3. Propiedades de Hibernate ─────────────────────────────────────────
    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect",        "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto",   "validate");  // solo valida, no modifica BD
        props.put("hibernate.show_sql",       "true");
        props.put("hibernate.format_sql",     "true");
        props.put("hibernate.use_sql_comments", "true");    // útil para depuración
        return props;
    }

    // ─── 4. TransactionManager ────────────────────────────────────────────────
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}