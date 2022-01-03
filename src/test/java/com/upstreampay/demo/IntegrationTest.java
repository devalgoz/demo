package com.upstreampay.demo;

import com.upstreampay.demo.domain.Transaction;
import com.upstreampay.demo.domain.enums.TransactionStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGet(){
        Transaction first = TransactionDataGenerator.first();
        Transaction second = TransactionDataGenerator.second();

        //Create Transaction
        webClient
                .post()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(first), Transaction.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists();

        //Update status, should get 404
        Transaction transaction = findAll().collectList().block().get(0);
        transaction.setStatus(TransactionStatus.CAPTURED);
        ObjectId id = transaction.getId();
        transaction.setId(new ObjectId("000000000000000000000000"));
        webClient
                .put()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), Transaction.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND);

        //Update status, should get 401
        transaction.setId(id);
        webClient
                .put()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), Transaction.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST);

        transaction.setStatus(TransactionStatus.AUTHORIZED);
        webClient
                .put()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), Transaction.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        transaction = findAll().collectList().block().get(0);
        assertEquals(TransactionStatus.AUTHORIZED,transaction.getStatus(), "Transaction status should be AUTHORIZED");

        transaction.setStatus(TransactionStatus.CAPTURED);
        webClient
                .put()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), Transaction.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        transaction = findAll().collectList().block().get(0);
        assertEquals(TransactionStatus.CAPTURED,transaction.getStatus(), "Transaction status should be CAPTURED ");

        //Create 2nd transaction
        webClient
                .post()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(second), Transaction.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists();

        first = TransactionDataGenerator.first();
        second = TransactionDataGenerator.second();
        webClient
                .get()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].status").isEqualTo("CAPTURED")
                .jsonPath("$[0].amount").isEqualTo(first.getAmount())
                .jsonPath("$[0].paymentMethod").isEqualTo(first.getPaymentMethod().toString())
                .jsonPath("$[0].order[0].productName").isEqualTo(first.getOrder().get(0).getProductName())
                .jsonPath("$[0].order[0].quantity").isEqualTo(first.getOrder().get(0).getQuantity())
                .jsonPath("$[0].order[0].price").isEqualTo(first.getOrder().get(0).getPrice())
                .jsonPath("$[0].order[1].productName").isEqualTo(first.getOrder().get(1).getProductName())
                .jsonPath("$[0].order[1].quantity").isEqualTo(first.getOrder().get(1).getQuantity())
                .jsonPath("$[0].order[1].price").isEqualTo(first.getOrder().get(1).getPrice())
                .jsonPath("$[1].status").isEqualTo("NEW")
                .jsonPath("$[1].amount").isEqualTo(second.getAmount())
                .jsonPath("$[1].paymentMethod").isEqualTo(second.getPaymentMethod().toString())
                .jsonPath("$[1].order[0].productName").isEqualTo(second.getOrder().get(0).getProductName())
                .jsonPath("$[1].order[0].quantity").isEqualTo(second.getOrder().get(0).getQuantity())
                .jsonPath("$[1].order[0].price").isEqualTo(second.getOrder().get(0).getPrice());
    }

    private Flux<Transaction> findAll() {
        return webClient
                .get()
                .uri("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Transaction.class)
                .getResponseBody();

    }
}
