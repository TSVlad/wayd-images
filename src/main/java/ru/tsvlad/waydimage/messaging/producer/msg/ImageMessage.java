package ru.tsvlad.waydimage.messaging.producer.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.tsvlad.waydimage.messaging.AbstractMessage;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ImageMessage extends AbstractMessage {
    private ImageMessageType type;
    private String imageId;
    private byte[] image;
}
