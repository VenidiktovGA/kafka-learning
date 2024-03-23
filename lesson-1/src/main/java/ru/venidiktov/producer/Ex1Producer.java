package ru.venidiktov.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.venidiktov.utils.KafkaProperties;
import ru.venidiktov.utils.Util;

@Slf4j
public class Ex1Producer {
    private static final String topic = KafkaProperties.topic();

    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, String>(Util.producerConfig)) { // Закрываем producer после использования или при exception!
            for (int i = 0; i < 2; i++) {
                String value = "|%s| payload = %s".formatted(Ex1Producer.class.getName(), i);
                String key = Integer.toString(i);
                producer.send(new ProducerRecord<>(topic, key, value));
            }
        }
    }
}
