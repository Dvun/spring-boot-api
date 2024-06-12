package com.codeapi.journey;

import com.codeapi.customer.Customer;
import com.codeapi.customer.CustomerDto;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@RunWith (SpringRunner.class)
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();
    String CUSTOMER_URI = "/api/v1/customers";


    @Test
    public void canRegisterACustomer() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = (firstName + "." + lastName + "@gmail.com").toLowerCase();
        int age = RANDOM.nextInt(10, 99);
        CustomerDto dto = new CustomerDto(firstName + " " + lastName, email, age);

        webTestClient.post().uri(CUSTOMER_URI)
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(dto), CustomerDto.class)
                     .exchange()
                     .expectStatus()
                     .isOk();

        Customer expected = new Customer(firstName + " " + lastName, email, age);

        List<Customer> allCustomers = webTestClient
                .get().uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expected);

        assert allCustomers != null;
        var customerId = allCustomers
                .stream()
                .filter(c -> c.getEmail().equals(expected.getEmail())).map(Customer::getId)
                .findFirst().orElseThrow();

        expected.setId(customerId);

        webTestClient.get()
                     .uri(CUSTOMER_URI + "/{id}", customerId)
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(new ParameterizedTypeReference<Customer>() {
                     })
                     .isEqualTo(expected);

    }

    @Test
    public void canDeleteACustomer() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = (firstName + "." + lastName + "@gmail.com").toLowerCase();
        int age = RANDOM.nextInt(10, 99);
        CustomerDto dto = new CustomerDto(firstName + " " + lastName, email, age);

        // save customer
        webTestClient.post().uri(CUSTOMER_URI)
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(dto), CustomerDto.class)
                     .exchange()
                     .expectStatus()
                     .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient
                .get().uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get customer id
        assert allCustomers != null;
        var customerId = allCustomers
                .stream()
                .filter(c -> c.getEmail().equals(dto.email())).map(Customer::getId)
                .findFirst().orElseThrow();

        // delete customer
        webTestClient.delete()
                     .uri(CUSTOMER_URI + "/{id}", customerId)
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isOk();

        // check that customer is deleted
        webTestClient.get()
                     .uri(CUSTOMER_URI + "/{id}", customerId)
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isNotFound();
    }

    @Test
    public void canUpdateCustomer() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = (firstName + "." + lastName + "@gmail.com").toLowerCase();
        int age = RANDOM.nextInt(10, 99);
        CustomerDto dto = new CustomerDto(firstName + " " + lastName, email, age);

        // save customer
        webTestClient.post().uri(CUSTOMER_URI)
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(dto), CustomerDto.class)
                     .exchange()
                     .expectStatus()
                     .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient
                .get().uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get customer id
        assert allCustomers != null;
        var customerId = allCustomers
                .stream()
                .filter(c -> c.getEmail().equals(dto.email())).map(Customer::getId)
                .findFirst().orElseThrow();

        // update customer
        String updatedName = "UpdatedName";
        CustomerDto updateCustomer = new CustomerDto(updatedName, null, null);

        webTestClient.put()
                     .uri(CUSTOMER_URI + "/{id}", customerId)
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(updateCustomer), CustomerDto.class)
                     .exchange()
                     .expectStatus()
                     .isOk();

        // check that customer is updated
        Customer uCustomer = webTestClient.get()
                     .uri(CUSTOMER_URI + "/{id}", customerId)
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(Customer.class)
                     .returnResult()
                     .getResponseBody();

        Customer expectedCustomer = new Customer(customerId, updatedName, email, age);

        assertThat(uCustomer).isEqualTo(expectedCustomer);
    }

}
