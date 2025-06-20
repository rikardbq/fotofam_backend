package se.rikardbq.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Post;
import se.rikardbq.models.UserDao;
import se.rikardbq.models.request.CreatePostRequest;
import se.rikardbq.service.IAuthService;
import se.rikardbq.service.IPostService;
import se.rikardbq.service.IUserService;
import se.rikardbq.util.Constants;
import se.rikardbq.util.Env;
import se.rikardbq.util.Token;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class PostController {

    @Autowired
    private IAuthService<DecodedJWT> authService;
    @Autowired
    private IPostService<CreatePostRequest, Post> postService;
    @Autowired
    private IUserService<UserDao> userService;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getPosts(
            @RequestHeader Map<String, String> requestHeaders,
            @RequestParam(name = "limit") Integer limit,
            @RequestParam(name = "offset") Integer offset
    ) {
        return getPosts(requestHeaders, null, limit, offset);
    }

    @GetMapping("/posts/{username}")
    public ResponseEntity<List<Post>> getPosts(
            @RequestHeader Map<String, String> requestHeaders,
            @PathVariable(required = false) String pathUsername,
            @RequestParam(name = "limit") Integer limit,
            @RequestParam(name = "offset") Integer offset
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
            Optional<String> dbRefreshToken = authService.getRefreshToken(username, headerToken);
            Optional<UserDao> userDao = userService.getUserWithUsername(username);

            if (dbRefreshToken.isEmpty() || userDao.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (!Objects.isNull(pathUsername) && !Objects.equals(pathUsername, username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Post> posts;
            if (Objects.isNull(pathUsername)) {
                posts = postService.getPostsWithParams(limit, offset);
            } else {
                posts = postService.getPostsForUserWithParams(userDao.get().getId(), limit, offset);
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(posts);
        } catch (SerfConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/posts")
    public ResponseEntity<Long> createPost(@RequestHeader Map<String, String> requestHeaders, @RequestBody CreatePostRequest createPostRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (!authService.checkApiKeyValid(requestHeaders) || !authService.checkOriginValid(requestHeaders)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String headerToken = authService.getHeaderToken(requestHeaders);
            DecodedJWT decodedJWT = authService.getDecodedToken(headerToken, Token.Type.RT, Env.FFBE_S);
            String username = decodedJWT.getClaim(Constants.Token.Claim.X_UNAME).asString();
            Optional<String> dbRefreshToken = authService.getRefreshToken(username, headerToken);
            Optional<UserDao> userDao = userService.getUserWithUsername(username);

            if (dbRefreshToken.isEmpty() || userDao.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            long lastRowId = postService.insertPost(createPostRequest, userDao.get().getId());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(lastRowId);
        } catch (JWTVerificationException | SerfConnectorException | IOException e) {
            if (e instanceof JWTVerificationException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            throw new RuntimeException(e);
        }
    }
}
