package com.codeapi.customer;

import com.codeapi.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class
CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(getJdbcTemplate());
    }

    @Test
    void selectAllCustomers() {
        generateCustomer();
        List<Customer> actual = underTest.selectAllCustomers();
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        var customer = generateCustomer();
        generateCustomer();

        Long id = getId(customer);

        Optional<Customer> result = underTest.selectCustomerById(id).stream().findFirst();
        assertThat(result).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        long id = -1L;
        Optional<Customer> customer = underTest.selectCustomerById(id);
        assertThat(customer).isEmpty();
    }

    @Test
    void insertCustomer() {
        var customer = generateCustomer();

        long id = getId(customer);

        Optional<Customer> result = underTest.selectCustomerById(id).stream().findFirst();
        assertThat(result).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existPersonWithEmail() {
        var customer = generateCustomer();

        String existEmail = underTest.selectAllCustomers().stream().map(Customer::getEmail)
                                     .filter(cEmail -> cEmail.equals(customer.getEmail()))
                                     .findFirst().orElseThrow();

        boolean result = underTest.existPersonWithEmail(existEmail);
        assertThat(result).isTrue();
    }

    @Test
    void notExistPersonWithEmail() {
        String notExistEmail = "test@test.com";

        boolean result = underTest.existPersonWithEmail(notExistEmail);
        assertThat(result).isFalse();
    }

    @Test
    void deleteCustomer() {
        var customer = generateCustomer();

        Long id = getId(customer);

        underTest.deleteCustomer(id);

        Optional<Customer> result = underTest.selectCustomerById(id);
        assertThat(result).isEmpty();
    }

    @Test
    void existsCustomerById() {
        var customer = generateCustomer();

        Long id = getId(customer);

        boolean isNotExistId = underTest.existsCustomerById(id);
        assertThat(isNotExistId).isEqualTo(true);
    }

    @Test
    void notExistsCustomerById() {
        Long notExistId = -1L;

        boolean isExistId = underTest.existsCustomerById(notExistId);
        assertThat(isExistId).isFalse();
    }

    @Test
    void updateCustomerName() {
        var customer = generateCustomer();

        Customer existCustomer = underTest.selectAllCustomers().stream()
                                          .filter(c -> c.getEmail().equals(customer.getEmail())).findFirst().orElseThrow();

        String newName = "TEST";
        existCustomer.setName(newName);
        underTest.updateCustomer(existCustomer);

        Optional<Customer> result = underTest.selectCustomerById(existCustomer.getId()).stream().findFirst();
        assertThat(result).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(existCustomer.getId());
            assertThat(c.getName()).isEqualTo(newName);
        });
    }

    @Test
    void updateCustomerEmail() {
        var customer = generateCustomer();

        Customer existCustomer = underTest.selectAllCustomers().stream()
                                          .filter(c -> c.getEmail().equals(customer.getEmail())).findFirst().orElseThrow();

        String newEmail = "someTest.test@test.com";
        existCustomer.setEmail(newEmail);
        underTest.updateCustomer(existCustomer);

        Optional<Customer> result = underTest.selectCustomerById(existCustomer.getId()).stream().findFirst();
        assertThat(result).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(existCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void updateCustomerAge() {
        var customer = generateCustomer();

        Customer existCustomer = underTest.selectAllCustomers().stream()
                                          .filter(c -> c.getEmail().equals(customer.getEmail())).findFirst().orElseThrow();

        int newAge = 99;
        existCustomer.setAge(newAge);
        underTest.updateCustomer(existCustomer);

        Optional<Customer> result = underTest.selectCustomerById(existCustomer.getId()).stream().findFirst();
        assertThat(result).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(existCustomer.getId());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateCustomer() {
        var customer = generateCustomer();

        Customer existCustomer = underTest.selectAllCustomers().stream()
                           .filter(c -> c.getEmail().equals(customer.getEmail())).findFirst().orElseThrow();

        existCustomer.setName("TEST");
        existCustomer.setEmail("test@test.com");
        existCustomer.setAge(99);
        underTest.updateCustomer(existCustomer);

        Optional<Customer> result = underTest.selectCustomerById(existCustomer.getId()).stream().findFirst();
        assertThat(result).isPresent().hasValue(existCustomer);
    }


    private long getId(Customer customer) {
        return underTest.selectAllCustomers().stream()
                        .filter(c -> c.getEmail().equals(customer.getEmail()))
                        .map(Customer::getId).findFirst().orElseThrow();
    }

    private Customer generateCustomer() {
        var firstName = FAKER.name().firstName();
        var lastName = FAKER.name().lastName();
        var email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
        int randomAge = new Random().nextInt(10, 99);
        Customer customer = new Customer(firstName + " " + lastName, email, randomAge);
        underTest.insertCustomer(customer);
        return customer;
    }
}
