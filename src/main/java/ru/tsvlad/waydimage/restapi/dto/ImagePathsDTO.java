package ru.tsvlad.waydimage.restapi.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImagePathsDTO {
    private String path;
    private String smallPath;
}
