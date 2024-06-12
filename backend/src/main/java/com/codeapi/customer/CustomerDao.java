package com.codeapi.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existPersonWithEmail(String email);
    void deleteCustomer(Long id);
    boolean existsCustomerById(Long id);
    void updateCustomer(Customer customer);

}
