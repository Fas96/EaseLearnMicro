package com.easelearn.notification;

import com.easelearn.amqp.RabbitMQMessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.easelearn.*","com.easelearn.amqp"})
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.easelearn.clients"})
public class NotificationApplication {



        public static void main(String[] args) {
            SpringApplication.run(NotificationApplication.class, args);
        }
//    @Bean
//    CommandLineRunner commandLineRunner(
//            RabbitMQMessageProducer rabbitMQMessageProducer,
//            NotificationConfig notificationConfig
//            ) {
//        return args -> {
//            rabbitMQMessageProducer.publish(
//                    new Person("FAS_RABBIT", 30),
//                    notificationConfig.getInternalExchange(),
//                    notificationConfig.getInternalNotificationRoutingKey());
//        };
//    }
//
//    record Person(String name, int age){}


}
