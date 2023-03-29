package com.listener;

import com.contoller.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "fasCodeTopic",groupId = "fasCodeGroup")
    void listen(String data){
        System.out.println("Message received: "+data+" o");
    }
}
