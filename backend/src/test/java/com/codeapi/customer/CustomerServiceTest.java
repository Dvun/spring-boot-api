package com.codeapi.customer;

import com.codeapi.exception.DuplicateResourceException;
import com.codeapi.exception.NotModifiedException;
import com.codeapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }


    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        long id = 19;
        Customer customer = new Customer(id, "Test", "test@test.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer result = underTest.selectCustomerById(id);
        assertThat(result).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        long id = 19;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.selectCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void insertCustomer() {
        String email = "test@test.com";
        when(customerDao.existPersonWithEmail(email)).thenReturn(false);

        CustomerDto dto = new CustomerDto("TEST", email, 20);
        underTest.insertCustomer(dto);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer captorValue = customerArgumentCaptor.getValue();

        assertThat(captorValue.getId()).isNull();
        assertThat(captorValue.getName()).isEqualTo(dto.name());
        assertThat(captorValue.getEmail()).isEqualTo(dto.email());
        assertThat(captorValue.getAge()).isEqualTo(dto.age());

    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "test@test.com";
        when(customerDao.existPersonWithEmail(email)).thenReturn(true);

        CustomerDto dto = new CustomerDto("TEST", email, 20);
        assertThatThrownBy(() -> underTest.insertCustomer(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email %s is already exist".formatted(dto.email()));

        verify(customerDao, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomer() {
        long id = 22L;

        when(customerDao.existsCustomerById(id)).thenReturn(true);

        underTest.deleteCustomer(id);
        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void willThrowWhenCustomerIdNotFound() {
        long id = 22L;

        when(customerDao.existsCustomerById(id)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
        verify(customerDao, never()).deleteCustomer(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        long id = 10L;
        String newEmail = "test2@test2.com";
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existPersonWithEmail(newEmail)).thenReturn(false);

        CustomerDto updatedCustomer = new CustomerDto("TEST2", newEmail, 40);
        underTest.updateCustomer(id, updatedCustomer);


        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getAge()).isEqualTo(updatedCustomer.age());
        assertThat(captorValue.getName()).isEqualTo(updatedCustomer.name());
        assertThat(captorValue.getEmail()).isEqualTo(updatedCustomer.email());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        long id = 10L;
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDto updatedCustomer = new CustomerDto("TEST2", null, null);
        underTest.updateCustomer(id, updatedCustomer);

        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getAge()).isEqualTo(customer.getAge());
        assertThat(captorValue.getName()).isEqualTo(updatedCustomer.name());
        assertThat(captorValue.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        long id = 10L;
        String email = "test2@test2.com";
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existPersonWithEmail(email)).thenReturn(false);

        CustomerDto updatedCustomer = new CustomerDto(null, email, null);
        underTest.updateCustomer(id, updatedCustomer);

        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getAge()).isEqualTo(customer.getAge());
        assertThat(captorValue.getName()).isEqualTo(customer.getName());
        assertThat(captorValue.getEmail()).isEqualTo(updatedCustomer.email());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        long id = 10L;
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDto updatedCustomer = new CustomerDto(null, null, 40);
        underTest.updateCustomer(id, updatedCustomer);

        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getAge()).isEqualTo(updatedCustomer.age());
        assertThat(captorValue.getName()).isEqualTo(customer.getName());
        assertThat(captorValue.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryToUpdateCustomerEmailWhenAlreadyTaken() {
        long id = 10L;
        String email = "test2@test2.com";
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existPersonWithEmail(email)).thenReturn(true);

        CustomerDto updatedCustomer = new CustomerDto(null, email, null);
        assertThatThrownBy(() -> underTest.updateCustomer(id, updatedCustomer))
                .isInstanceOf(DuplicateResourceException.class).hasMessage("Email already exists!");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHaveNoAnyChanges() {
        long id = 10L;
        Customer customer = new Customer(id, "TEST", "test@test.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDto updatedCustomer = new CustomerDto(customer.getName(), customer.getEmail(), customer.getAge());
        assertThatThrownBy(() -> underTest.updateCustomer(id, updatedCustomer))
                .isInstanceOf(NotModifiedException.class)
                .hasMessage("Nothing to update!");


        verify(customerDao, never()).updateCustomer(any());
    }

}
