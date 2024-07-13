package ru.venidiktov.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import ru.venidiktov.utils.KafkaProperties;
import ru.venidiktov.utils.Util;

@Slf4j
public class Ex3Producer {
    private static final String topic = KafkaProperties.topic();

    /**
     * Пример работает аналогично Ex2Producer, но
     * здесь отправка сообщений в kafka происходит принудительно через вызов метода flush()
     * Тут лоиги с Send будут в перемешку с логами из callBack кода из метода send()
     * <p>
     * При использовании прочитать комменты над методом flush, там есть интересные моменты про его работу!
     */
    public static void main(String[] args) {
        try (var producer = new KafkaProducer<Integer, String>(Util.createProducerConfig(m ->
                m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class)
        ))) {

            for (int i = 0; i < 200; i++) {
                log.info("Send {}", i);
                String value = "|%s| payload = %s".formatted(Ex3Producer.class.getName(), i);

                var record = new ProducerRecord<>(topic, i, value);
                producer.send(record,
                        (metadata, error) -> { // callBck отработает когда произойдет отправка или exception
                            log.info("Sending record {}", record);
                            log.info("Complete {}", record.key());
                            log.info("Metadata: offset = {}, timestamp = {}, partition = {}",
                                    metadata.offset(), metadata.timestamp(), metadata.partition());
                        });

                if (i % 50 == 0) {
                    log.info("===== FLUSH =====");
                    producer.flush();
                }
            }
        }
    }
}
