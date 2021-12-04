package ru.tsvlad.waydimage.restapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.document.ImageDocument;
import ru.tsvlad.waydimage.service.ImageService;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImagesController {

    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<String> saveImages(@RequestPart(name = "files") Flux<FilePart> parts, @AuthenticationPrincipal JwtPayload userInfo) {
        return imageService.saveImages(parts, userInfo).map(ImageDocument::getId);
    }

    @GetMapping("/{id}")
    public Mono<String> getImageUrl(@PathVariable String id, @RequestParam("miniature") boolean isMiniature, @AuthenticationPrincipal JwtPayload userInfo) {
        return imageService.getImageUrl(id, isMiniature, userInfo.getRoles());
    }
}
