package ru.tsvlad.waydimage.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.tsvlad.waydimage.config.security.JwtPayload;
import ru.tsvlad.waydimage.restapi.controller.advise.exceptions.BadImageException;
import ru.tsvlad.waydimage.restapi.controller.advise.exceptions.ServerException;
import ru.tsvlad.waydimage.restapi.dto.ImagePathsDTO;
import ru.tsvlad.waydimage.service.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${wayd.image.base-path}")
    private String basePath;

    @Value("${wayd.image.max-size}")
    private int maxSize;

    @Value("${wayd.image.small-max-size}")
    private int smallMaxSize;

    @Override
    public Flux<ImagePathsDTO> saveImages(Flux<FilePart> fileParts, JwtPayload userInfo) {
        return fileParts.flatMap(fp -> fp.content()
                .flatMap(buffer -> Flux.just(buffer.asByteBuffer().array()))
                .collectList()
                .map(this::byteArrayListToByteArray)
                .map(bytes ->  {
                    BufferedImage image = getImageFromBytes(bytes);
                    return saveImage(image, userInfo.getId());
                }));
    }

    private byte[] byteArrayListToByteArray(List<byte[]> list) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        list.forEach(byteArrayOutputStream::writeBytes);
        return byteArrayOutputStream.toByteArray();
    }

    private File createDirForUserIfNotExists(long userId) {
        Path pathDir = Path.of(basePath, String.valueOf(userId));
        File dir = new File(pathDir.toString());
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new ServerException();
            }
        }
        return dir;
    }

    private BufferedImage getImageFromBytes(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedImage image;
        try {
            image = getImageFromInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadImageException();
        }
        return image;
    }

    private BufferedImage getImageFromInputStream(InputStream imageInputStream) throws MetadataException, ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(imageInputStream);

        imageInputStream.reset();
        BufferedImage image  = ImageIO.read(imageInputStream);

        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (exifIFD0 != null) {
            int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            if (image == null) {
                throw new BadImageException();
            }
            image = rotateImageIfNeed(image, orientation);
        }

        image = resizeImageIfNeed(image, maxSize);

        return image;
    }

    private BufferedImage rotateImageIfNeed(BufferedImage image, int orientation) {
        switch (orientation) {
            case 1:
                return image;
            case 6:
                return Scalr.rotate(image, Scalr.Rotation.CW_90);
            case 3:
                return Scalr.rotate(image, Scalr.Rotation.CW_180);
            case 8:
                return Scalr.rotate(image, Scalr.Rotation.CW_270);
        }
        return image;
    }



    private ImagePathsDTO saveImage(BufferedImage image, long userId) {
        File dir = createDirForUserIfNotExists(userId);
        String uuidName = UUID.randomUUID().toString();
        Path fullPath = dir.toPath().resolve(uuidName + ".jpg");
        Path smallPath = dir.toPath().resolve(uuidName + "-small.jpg");

        writeImage(image, "jpg", fullPath.toFile());
        writeImage(getSmallImage(image), "jpg", smallPath.toFile());

        return ImagePathsDTO.builder()
                .path(fullPath.toString())
                .smallPath(smallPath.toString())
                .build();
    }

    private void writeImage(BufferedImage image, String format, File file) {
        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerException();
        }
    }

    private BufferedImage getSmallImage(BufferedImage image) {
        return resizeImageIfNeed(image, smallMaxSize);
    }

    private BufferedImage resizeImageIfNeed(BufferedImage image, int maxSize) {
        double yScale = (maxSize + 0.0)/image.getHeight();
        double xScale = (maxSize + 0.0)/image.getWidth();

        double scale = Math.min(xScale, yScale);
        if (scale < 1) {
            return Scalr.resize(image, (int)(image.getWidth() * scale), (int)(image.getHeight() * scale));
        }
        return image;
    }
}
