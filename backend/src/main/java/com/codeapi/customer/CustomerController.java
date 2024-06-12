package com.codeapi.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<Customer> getAllCustomer() {
        return customerService.getAllCustomers();
    }

    @GetMapping ("{id}")
    public Customer selectCustomerById(@PathVariable ("id") Long id) {
        return customerService.selectCustomerById(id);
    }

    @PostMapping
    public void insertCustomer(@RequestBody CustomerDto dto) {
        customerService.insertCustomer(dto);
    }

    @DeleteMapping ("{id}")
    public void deleteCustomer(@PathVariable ("id") Long id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping ("{id}")
    public void updateCustomer(@PathVariable ("id") Long id, @RequestBody CustomerDto dto) {
        customerService.updateCustomer(id, dto);
    }

}
