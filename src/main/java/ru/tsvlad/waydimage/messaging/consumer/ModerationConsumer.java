package ru.tsvlad.waydimage.messaging.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tsvlad.waydimage.messaging.consumer.msg.ModerationMessage;
import ru.tsvlad.waydimage.service.ImageService;

@Component
@Slf4j
@AllArgsConstructor
public class ModerationConsumer {
    private ImageService imageService;

    @KafkaListener(topics = {"moderation-to-image"}, containerFactory = "singleFactory")
    public void consume(ModerationMessage message) {
        switch (message.getType()) {
            case IMAGE_MODERATION_DECISION:
                imageService.moderateImage(message.getImageId(), message.getDecision());
        }
    }
}
