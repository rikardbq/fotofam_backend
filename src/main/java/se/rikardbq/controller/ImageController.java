package se.rikardbq.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.UserDao;
import se.rikardbq.service.IAuthService;
import se.rikardbq.service.IImageService;
import se.rikardbq.service.IUserService;
import se.rikardbq.util.Constants;
import se.rikardbq.util.Env;
import se.rikardbq.util.Token;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ImageController {

    @Autowired
    private IAuthService<DecodedJWT> authService;
    @Autowired
    private IUserService<UserDao> userService;
    @Autowired
    private IImageService<Image> imageService;

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Image> getPosts(
            @RequestHeader Map<String, String> requestHeaders,
            @PathVariable(name = "imageName", required = true) String imageName
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (!authService.checkApiKeyValid(requestHeaders) || !authService.checkOriginValid(requestHeaders)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String headerToken = authService.getHeaderToken(requestHeaders);
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.RT, Env.FFBE_S);
            String username = decodedJWT.getClaim(Constants.Token.Claim.X_UNAME).asString();
            String dbRefreshToken = authService.getRefreshToken(username, headerToken);
            UserDao userDao = userService.getUserWithUsername(username);

            if (Objects.isNull(dbRefreshToken) || Objects.isNull(userDao)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Image image = imageService.getImageWithName(imageName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(image);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

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