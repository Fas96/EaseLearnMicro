package com.contoller;

import java.time.LocalDateTime;

public record Message(String message, LocalDateTime createdAt) {
}
