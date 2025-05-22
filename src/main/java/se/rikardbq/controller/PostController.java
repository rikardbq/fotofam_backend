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
import se.rikardbq.models.User;
import se.rikardbq.models.UserDao;
import se.rikardbq.models.request.CreatePostRequest;
import se.rikardbq.service.IAuthService;
import se.rikardbq.service.IPostService;
import se.rikardbq.service.IUserService;
import se.rikardbq.util.Constants;
import se.rikardbq.util.Env;
import se.rikardbq.util.Token;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class PostController {

    @Autowired
    private IAuthService<DecodedJWT> authService;
    @Autowired
    private IPostService<CreatePostRequest, Post> postService;
    @Autowired
    private IUserService<UserDao> userService;

    @GetMapping("/posts/{userId}")
    public ResponseEntity<List<Post>> getPosts(
            @RequestHeader Map<String, String> requestHeaders,
            @PathVariable(name = "userId", required = false) Integer userId,
            @RequestParam(name = "limit", required = true) Integer limit,
            @RequestParam(name = "offset", required = true) Integer offset
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

            if (Objects.isNull(dbRefreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (!Objects.isNull(userId) && userId != userService.getUserWithUsername(username).getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Post> posts;
            if (Objects.isNull(userId)) {
                posts = postService.getPostsWithParams(limit, offset);
            } else {
                posts = postService.getPostsForUserWithParams(userId, limit, offset);
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
            String dbRefreshToken = authService.getRefreshToken(username, headerToken);

            if (Objects.isNull(dbRefreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            long lastRowId = postService.insertPost(createPostRequest);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(lastRowId);
        } catch (JWTVerificationException | SerfConnectorException e) {
            if (e instanceof JWTVerificationException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            throw new RuntimeException(e);
        }
    }
}
