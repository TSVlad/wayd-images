package ru.tsvlad.waydimage.restapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.restapi.dto.ImagePathsDTO;
import ru.tsvlad.waydimage.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImagesController {

    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<ImagePathsDTO> saveImages(@RequestPart(name = "files") Flux<FilePart> parts, @AuthenticationPrincipal JwtPayload userInfo) {
        return imageService.saveImages(parts, userInfo);
    }
}
