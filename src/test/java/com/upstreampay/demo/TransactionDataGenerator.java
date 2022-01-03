package com.upstreampay.demo;

import com.upstreampay.demo.domain.OrderLine;
import com.upstreampay.demo.domain.Transaction;
import com.upstreampay.demo.domain.enums.PaymentMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionDataGenerator {

    public static Transaction first(){
        return new Transaction(
                PaymentMethod.CREDIT_CARD,
                54.80D,
                Arrays.asList(
                        new OrderLine("Ski gloves", 4, 10D),
                        new OrderLine("Wool cap", 1, 14.80D)
                )
        );
    }

    public static Transaction second(){
        return  new Transaction(
                PaymentMethod.PAYPAL,
                208D,
                Collections.singletonList(new OrderLine("Bicycle", 1, 208D))
        );
    }
}
