package se.rikardbq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.service.IImageService;

import java.util.List;
import java.util.Objects;

@RestController
public class ImageController {

    @Autowired
    private IImageService<Image> imageService;

    @GetMapping("/images")
    public ResponseEntity<List<Image>> getImages(
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Image> images;
            if (Objects.isNull(limit)) {
                images = imageService.getImages();
            } else {
                images = imageService.getImagesWithParams(limit, offset);
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(images);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/images")
    public ResponseEntity<Long> uploadImage(@RequestBody Image image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            long lastRowId = imageService.insertImage(image);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(lastRowId);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    // use this for get mappings
//    @GetMapping("/images")
//    public void getImages(HttpServletResponse response) {
//        response.setContentType("application/json");
//
//        List<Image> images = imageService.getImages();
//        try (
//                ServletOutputStream outputStream = response.getOutputStream();
//                PrintWriter writer = new PrintWriter(outputStream)
//        ) {
//            writer.println();
//            response.setStatus(HttpStatus.OK.value());
//            response.flushBuffer();
//        } catch (IOException e) {
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }

// streaming response, could be good for loading video entries. I.E buffer stream parts
// use for streaming getMapping
//    @PostMapping("/images_3")
//    public ResponseEntity<StreamingResponseBody> uploadImage3(@RequestBody byte[] uploadImageRequest) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG);
//        StreamingResponseBody stream = outputStream -> {
//            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
//                writer.println(Arrays.toString(uploadImageRequest));
//
//                writer.flush(); // Ensure all data is sent
//            }
//        };
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(stream);
//    }
}