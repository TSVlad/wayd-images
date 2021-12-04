package ru.tsvlad.waydimage.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.document.ImageDocument;

public interface ImageService {
    Flux<ImageDocument> saveImages(Flux<FilePart> fileParts, JwtPayload userInfo);
    Mono<String> getImageUrl(String imageName);
}
