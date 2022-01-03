package com.upstreampay.demo.controller;

import com.upstreampay.demo.domain.Transaction;
import com.upstreampay.demo.exception.InvalidInputException;
import com.upstreampay.demo.exception.NotFoundException;
import com.upstreampay.demo.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@Configuration
@AllArgsConstructor
public class RouterConfig {

    private final TransactionService transactionService;

    @Bean
    public RouterFunction<ServerResponse> findAll() {
        return route()
                .GET("/transactions", this::handleFindAll)
                .POST("/transactions", this::handleCreate)
                .PUT("/transactions", this::handleUpdate)
                .onError(e -> e instanceof InvalidInputException, (e, request) -> ServerResponse.status(HttpStatus.BAD_REQUEST).body(e.getMessage()))
                .onError(e -> e instanceof NotFoundException, (e, request) -> ServerResponse.status(HttpStatus.NOT_FOUND).body(e.getMessage()))
                .build();
    }

    private ServerResponse handleFindAll(ServerRequest req) {
        return ok().contentType(MediaType.APPLICATION_JSON).body(transactionService.findAll().buffer());
    }

    private ServerResponse handleCreate(ServerRequest req) throws javax.servlet.ServletException, java.io.IOException {
        return ok().body(transactionService.create(req.body(Transaction.class)));
    }


    private ServerResponse handleUpdate(ServerRequest req) throws javax.servlet.ServletException, java.io.IOException {
        return ok().body(transactionService.update(req.body(Transaction.class)));
    }
}
