package ru.venidiktov.producer;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.venidiktov.utils.Util;

/**
 * Producer через который можно в реальном времени в разные топики отправлять разное количество сообщений с разным ключом
 */
@Slf4j
public class Ex4Producer {
    public static void main(String[] args) throws InterruptedException {
        try (var producer = new KafkaProducer<String, String>(Util.producerConfig)) {

            var scanner = new Scanner(System.in);

            while (true) {
                Thread.sleep(1000);
                System.out.print("Enter topic: ");
                var topic = scanner.next();
                System.out.print("Enter keyFrom: ");
                var keyFrom = scanner.nextInt();
                System.out.print("Enter keyTo: ");
                var keyTo = scanner.nextInt();
                System.out.print("Enter count message: ");
                var count = scanner.nextInt();

                for (int key = keyFrom; key < keyTo; key++) {
                    for (int i = 0; i < count; i++) {
                        producer.send(new ProducerRecord<>(topic, Integer.toString(key), "some data"));
                    }
                    log.info("{} messages sent to topic {}, from key:{}, to key:{}", count, topic, keyFrom, keyTo);
                }
                producer.flush();
            }
        }
    }
}
