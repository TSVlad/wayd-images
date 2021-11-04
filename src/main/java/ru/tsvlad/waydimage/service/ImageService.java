package ru.tsvlad.waydimage.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.restapi.dto.ImagePathsDTO;

public interface ImageService {
    Flux<ImagePathsDTO> saveImages(Flux<FilePart> fileParts, JwtPayload userInfo);
}
