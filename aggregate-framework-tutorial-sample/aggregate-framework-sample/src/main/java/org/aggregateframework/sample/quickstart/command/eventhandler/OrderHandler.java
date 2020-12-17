package org.aggregateframework.sample.quickstart.command.eventhandler;

import org.aggregateframework.eventhandling.annotation.EventHandler;
import org.aggregateframework.eventhandling.annotation.TransactionCheck;
import org.aggregateframework.sample.quickstart.command.domain.entity.Payment;
import org.aggregateframework.sample.quickstart.command.domain.event.OrderConfirmedEvent;
import org.aggregateframework.sample.quickstart.command.domain.event.OrderPlacedEvent;
import org.aggregateframework.sample.quickstart.command.domain.factory.PaymentFactory;
import org.aggregateframework.sample.quickstart.command.domain.repository.PaymentRepository;
import org.aggregateframework.sample.quickstart.command.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by changming.xie on 4/7/16.
 */
@Service
public class OrderHandler {


    @Autowired
    OrderService orderService;

    @Autowired
    PaymentRepository paymentRepository;

    @EventHandler
    public void handleOrderCreatedEvent(List<OrderPlacedEvent> events) {

       System.out.println(events);
    }

    @EventHandler
    public void handleOrderCreatedEvent(OrderPlacedEvent event) {

        Payment payment = PaymentFactory.buildPayment(event.getPricedOrder().getId(),
                String.format("p000%s", event.getPricedOrder().getId()), event.getPricedOrder().getTotalAmount());

        paymentRepository.save(payment);
    }


    @EventHandler(asynchronous = true, postAfterTransaction = true, isTransactionMessage = true, transactionCheck = @TransactionCheck(checkTransactionStatusMethod = "checkOrderIsConfirmed"))
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {

        System.out.println("order confirmed event handled");
    }


    @EventHandler(asynchronous = true, postAfterTransaction = true, isTransactionMessage = true, transactionCheck = @TransactionCheck(checkTransactionStatusMethod = "checkOrderIsConfirmed"))
    public void handleOrderConfirmedEvent(List<OrderConfirmedEvent> events) {

        System.out.println("transactional send to mq list,size:"+events.size());
    }

    @EventHandler(asynchronous = true)
    public void handleOrderConfirmedEvent2(List<OrderConfirmedEvent> events) {

        System.out.println("send to mq list,size:"+events.size());
    }



    public boolean checkOrderIsConfirmed(OrderConfirmedEvent event) {

        return true;
    }
}