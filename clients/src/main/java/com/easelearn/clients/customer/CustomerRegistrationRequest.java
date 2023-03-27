package com.easelearn.clients.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}
