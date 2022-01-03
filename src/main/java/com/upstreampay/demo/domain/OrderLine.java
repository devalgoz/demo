package com.upstreampay.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class OrderLine {

    private @NotNull String productName;
    private @NotNull  Integer quantity;
    private @NotNull Double price;
}
