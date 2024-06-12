package com.codeapi.customer;

import com.codeapi.exception.DuplicateResourceException;
import com.codeapi.exception.NotModifiedException;
import com.codeapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }


    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer selectCustomerById(Long id) {
        return customerDao.selectCustomerById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
    }

    public void insertCustomer(CustomerDto dto) {
        if (customerDao.existPersonWithEmail(dto.email())) {
            throw new DuplicateResourceException("Email %s is already exist".formatted(dto.email()));
        }

        customerDao.insertCustomer(
                new Customer(dto.name(), dto.email().toLowerCase(), dto.age())
        );
    }

    public void deleteCustomer(Long id) {
        if (!customerDao.existsCustomerById(id)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
        customerDao.deleteCustomer(id);
    }

    public void updateCustomer(Long id, CustomerDto dto) {
        Customer customer = getCustomer(id);
        boolean changes = false;

        if (dto.name() != null && !dto.name().equals(customer.getName())) {
             customer.setName(dto.name());
             changes = true;
        }

        if (dto.age() != null && !dto.age().equals(customer.getAge())) {
            customer.setAge(dto.age());
            changes = true;
        }

        if (dto.email() != null && !dto.email().equals(customer.getEmail())) {
            if (customerDao.existPersonWithEmail(dto.email())) {
                throw new DuplicateResourceException("Email already exists!");
            }
            customer.setEmail(dto.email());
            changes = true;
        }

        if (!changes) throw new NotModifiedException("Nothing to update!");

        customerDao.updateCustomer(customer);
    }

    private Customer getCustomer(Long id) {
        return customerDao.selectCustomerById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id))
        );
    }

}
