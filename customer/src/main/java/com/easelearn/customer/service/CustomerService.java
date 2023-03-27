package com.easelearn.customer.service;

import com.easelearn.amqp.RabbitMQMessageProducer;
import com.easelearn.clients.fraud.FraudClient;
import com.easelearn.clients.notification.NotificationRequest;
import com.easelearn.customer.model.Customer;
import com.easelearn.clients.fraud.FraudCheckResponse;
import com.easelearn.clients.customer.CustomerRegistrationRequest;
import com.easelearn.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private  final  CustomerRepository customerRepository;
    private  final   FraudClient fraudClient;
    private  final   RabbitMQMessageProducer rabbitMQMessageProducer;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        //TODO: check if the email is already registered and valid

        //TODO: check if the customer is a fraudster
        customerRepository.saveAndFlush(customer);


//        UriComponents builder = UriComponentsBuilder.fromUriString("http://FRAUD/api/v1/fraud-check/{customerId}")
//                .buildAndExpand(customer.getId());
//        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(builder.toUri(), FraudCheckResponse.class);

        FraudCheckResponse fraudCheckResponse=fraudClient.isFraudster(customer.getId());
        assert fraudCheckResponse != null;
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("Customer is a fraudster");
        }
        NotificationRequest notification= new NotificationRequest(customer.getId(), customer.getEmail(),
                String.format("Hi ! %s %s Welcome to easelearn", customer.getFirstName(), customer.getLastName()));

        //TODO: send a notification to the customer
        //WE don not use this anymore because it compromises the single responsibility principle
        //when the notification service is down, the customer registration will fail
//        notificationClient.sendNotification(notification);


        //publishing the message to the queue
        rabbitMQMessageProducer.publish(notification,
                "internal.exchange",
                "internal.notification.routing-key");
    }
}
