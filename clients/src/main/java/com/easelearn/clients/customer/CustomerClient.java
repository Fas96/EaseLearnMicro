package com.easelearn.clients.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "CUSTOMER",
        path = "api/v1/customers"
)
public interface CustomerClient {
    @PostMapping
    void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest);
}
