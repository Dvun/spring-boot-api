package com.codeapi;

import com.codeapi.customer.Customer;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(JdbcTemplate jdbcTemplate) {
        return args -> {
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com",
                    new Random().nextInt(10, 100)
            );

            String sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
            jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
        };
    }

}
