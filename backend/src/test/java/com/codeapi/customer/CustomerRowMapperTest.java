package com.codeapi.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRowMapperTest {


    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("TEST");
        when(resultSet.getString("email")).thenReturn("test@test.com");
        when(resultSet.getInt("age")).thenReturn(19);

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        Customer expected = new Customer(1L, "TEST", "test@test.com", 19);
        assertThat(actual).isEqualTo(expected);
    }
}
