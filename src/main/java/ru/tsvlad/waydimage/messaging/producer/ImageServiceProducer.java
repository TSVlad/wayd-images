package ru.tsvlad.waydimage.messaging.producer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tsvlad.waydimage.messaging.producer.msg.ImageMessage;
import ru.tsvlad.waydimage.messaging.producer.msg.ImageMessageType;

@Service
@AllArgsConstructor
public class ImageServiceProducer {
    private final KafkaTemplate<Long, ImageMessage> kafkaTemplate;

    public void newImage(byte[] image, String imageId) {
        send(ImageMessage.builder()
                .type(ImageMessageType.NEW_IMAGE)
                .image(image)
                .imageId(imageId)
                .build());
    }

    public void invalidImage(String id) {
        send(ImageMessage.builder()
                .type(ImageMessageType.INVALID_IMAGE)
                .imageId(id)
                .build());
    }

    private void send(ImageMessage message) {
        kafkaTemplate.send("image-topic", message);
    }
}
