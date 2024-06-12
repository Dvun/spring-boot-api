package com.codeapi.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.*;

@Repository ("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT c.id, c.name, c.email, c.age FROM customer c
                """;
        return jdbcTemplate.query(sql, new CustomerRowMapper());
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        String sql = """
                SELECT * FROM customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql, new CustomerRowMapper(), id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        String sql = """
                SELECT COUNT(id) FROM customer WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomer(Long id) {
        String sql = """
                DELETE FROM customer WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsCustomerById(Long id) {
        String sql = """
                SELECT COUNT(id) FROM customer WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer customer) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE customer SET ");
        List<Object> params = new ArrayList<>();

        Field[] fields = Customer.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldName.equals("id")) {
                sqlBuilder.append(fieldName).append(" = ?, ");
                try {
                    Method getter = Customer.class.getMethod("get" + capitalize(fieldName));
                    params.add(getter.invoke(customer));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        sqlBuilder.setLength(sqlBuilder.length() - 2);
        sqlBuilder.append(" WHERE id = ?");
        try {
            Method getId = Customer.class.getMethod("getId");
            params.add(getId.invoke(customer));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        jdbcTemplate.update(sqlBuilder.toString(), params.toArray());
    }

}
