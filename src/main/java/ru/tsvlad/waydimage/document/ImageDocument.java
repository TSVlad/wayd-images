package ru.tsvlad.waydimage.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.tsvlad.waydimage.enums.ImageStatus;
import ru.tsvlad.waydimage.messaging.consumer.msg.dto.Validity;

@Document(collection = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDocument {
    @Id
    private String id;
    @Version
    private long version;

    @Indexed(unique = true)
    private String name;
    @Indexed(unique = true)
    private String miniatureName;

    private Validity validity;
    private ImageStatus status;

    private long ownerId;

    public static ImageDocument createNewImage(String name, String miniatureName, long ownerId) {
        return ImageDocument.builder()
                .name(name)
                .miniatureName(miniatureName)
                .ownerId(ownerId)
                .validity(Validity.NOT_VALIDATED)
                .status(ImageStatus.ON_VALIDATION)
                .build();
    }

    public void validate(Validity validity) {
        this.setValidity(validity);
        switch (validity) {
            case NOT_VALID:
                this.setStatus(ImageStatus.BANNED);
                break;
            case VALID:
                this.setStatus(ImageStatus.ON_MODERATION);
                break;
        }
    }
}
