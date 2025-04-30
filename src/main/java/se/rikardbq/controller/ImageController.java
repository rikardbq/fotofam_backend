package se.rikardbq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.image.UploadImageRequest;
import se.rikardbq.service.ImageService;

import java.util.List;

@RestController
public class ImageController {

    @Autowired
    private ImageService<Image> imageService;

    @GetMapping("/images")
    public ResponseEntity<List<Image>> getImages() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Image> images = imageService.getImages();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(images);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

//    @GetMapping("/images/{slug}")
//    public Image getImages(@PathParam("slug") String slug) {
//        try {
//            return imageService.getImageBySlug(slug);
//        } catch (SerfConnectorException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @PostMapping("/images")
    public ResponseEntity<Long> uploadImage(@RequestBody UploadImageRequest uploadImageRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            long lastRowId = imageService.insertImage(uploadImageRequest);

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