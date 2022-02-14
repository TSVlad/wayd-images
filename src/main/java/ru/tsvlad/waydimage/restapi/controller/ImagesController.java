package ru.tsvlad.waydimage.restapi.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tsvlad.waydimage.commons.UserInfo;
import ru.tsvlad.waydimage.document.ImageDocument;
import ru.tsvlad.waydimage.restapi.dto.ImageIdToUrlDTO;
import ru.tsvlad.waydimage.service.AuthenticationService;
import ru.tsvlad.waydimage.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImagesController {

    private final ImageService imageService;
    private final AuthenticationService authenticationService;

    private final ModelMapper modelMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<List<String>> saveImages(@RequestPart(name = "files") Flux<FilePart> parts, Authentication authentication) {
        return imageService.saveImages(parts, authenticationService.getUserInfo(authentication))
                .map(ImageDocument::getId).collectList();
    }

    @GetMapping("/{id}")
    public Mono<ImageIdToUrlDTO> getImageUrl(@PathVariable String id, @RequestParam("miniature") boolean isMiniature,
                                             Authentication authentication) {
        UserInfo userInfo = authenticationService.getUserInfo(authentication);
        return imageService.getImageUrl(id, isMiniature, userInfo.getRoles(), userInfo.getId())
                .map(imageIdToUrl -> modelMapper.map(imageIdToUrl, ImageIdToUrlDTO.class));
    }

    @GetMapping
    public Flux<ImageIdToUrlDTO> getImageUrls(@RequestParam(name = "id") List<String> ids,
                                              @RequestParam("miniature") boolean isMiniature,
                                              Authentication authentication) {
        UserInfo userInfo = authenticationService.getUserInfo(authentication);
        return imageService.getImageUrls(ids, isMiniature, userInfo.getRoles(), userInfo.getId())
                .map(imageIdToUrl -> modelMapper.map(imageIdToUrl, ImageIdToUrlDTO.class));
    }
}
