package com.upstreampay.demo.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upstreampay.demo.domain.enums.PaymentMethod;
import com.upstreampay.demo.domain.enums.TransactionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Document
@Data
@NoArgsConstructor
public class Transaction implements Serializable {

    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private @Id ObjectId id;

    private @NotNull TransactionStatus status;
    private @NotNull PaymentMethod paymentMethod;
    private @NotNull Double amount;
    private @NotNull List<OrderLine> order;

    public Transaction(PaymentMethod paymentMethod, Double amount, List<OrderLine> order) {
        this(null, TransactionStatus.NEW, paymentMethod, amount, order);
    }

    @PersistenceConstructor
    public Transaction(ObjectId id, @NotNull TransactionStatus status, @NotNull PaymentMethod paymentMethod, @NotNull Double amount, @NotNull List<OrderLine> order) {
        this.id = id;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.order = order;
    }

}


