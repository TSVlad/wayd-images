package ru.tsvlad.waydimage.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.config.security.Role;
import ru.tsvlad.waydimage.document.ImageDocument;
import ru.tsvlad.waydimage.messaging.consumer.msg.dto.ModeratorDecision;
import ru.tsvlad.waydimage.messaging.consumer.msg.dto.Validity;

import java.util.List;

public interface ImageService {
    Flux<ImageDocument> saveImages(Flux<FilePart> fileParts, JwtPayload userInfo);
    Flux<String> getImageUrls(List<String> ids, boolean isMiniature, List<Role> userRoles, long userId);
    Mono<String> getImageUrl(String id, boolean isMiniature, List<Role> userRoles, long userId);
    void updateImageValidity(String id, Validity validity);
    void moderateImage(String id, ModeratorDecision decision);
}
