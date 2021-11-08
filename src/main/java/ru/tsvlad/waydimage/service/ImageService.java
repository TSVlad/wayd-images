package ru.tsvlad.waydimage.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.restapi.dto.ImageNamesDTO;

public interface ImageService {
    Flux<ImageNamesDTO> saveImages(Flux<FilePart> fileParts, JwtPayload userInfo);
    Mono<String> getImageUrl(String imageName);
}
