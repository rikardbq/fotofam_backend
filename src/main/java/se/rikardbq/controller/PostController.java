package se.rikardbq.controller;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.image.UploadImageRequest;
import se.rikardbq.service.IImageService;

import java.util.List;
import java.util.Objects;

@RestController
public class PostController {

    @Autowired
    private IImageService<Image> imageService;

    @GetMapping("/posts")
    public ResponseEntity<List<Image>> getPosts(
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Image> images;
            if (Objects.isNull(limit) || Objects.isNull(offset)) {
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

    @GetMapping("/posts/{id}")
    public ResponseEntity<List<Image>> getPostWithId(@PathParam("id") Integer id) {
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

    @PostMapping("/posts")
    public ResponseEntity<Long> createPost(@RequestBody UploadImageRequest uploadImageRequest) {
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
}
