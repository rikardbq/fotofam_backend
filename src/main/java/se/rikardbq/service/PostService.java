package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.Post;
import se.rikardbq.models.request.CreatePostRequest;

import java.time.Instant;
import java.util.List;

@Component
public class PostService implements IPostService<CreatePostRequest, Post> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public List<Post> getPosts() throws SerfConnectorException {
        return databaseService.query(Post.class, "SELECT id, image_name, description, created_at FROM posts ORDER BY created_at DESC;");
    }

    @Override
    public List<Post> getPostsWithParams(int limit, int offset) throws SerfConnectorException {
        return databaseService.query(
                Post.class,
                "SELECT id, image_name, description, created_at FROM posts ORDER BY created_at DESC LIMIT ? OFFSET ?;",
                limit,
                offset
        );
    }

    @Override
    public List<Post> getPostsForUserWithParams(int userId, int limit, int offset) throws SerfConnectorException {
        return databaseService.query(
                Post.class,
                "SELECT id, image_name, description, created_at FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?;",
                userId,
                limit,
                offset
        );
    }

    @Override
    public long insertPost(CreatePostRequest createPostRequest, int userId) throws SerfConnectorException {
        Image image = createPostRequest.getImage();
        Post post = createPostRequest.getPost();
        long now = Instant.now().getEpochSecond();

        MutationResponse response = databaseService.mutate("""
                        INSERT INTO images(name, width, height, base64, user_id, created_at) VALUES(?, ?, ?, ?, ?, ?);
                        INSERT INTO posts(image_name, description, user_id, created_at) VALUES(?, ?, ?, ?);
                        """,
                image.getName(),
                image.getWidth(),
                image.getHeight(),
                image.getBase64(),
                userId,
                now,
                image.getName(),
                post.getDescription(),
                userId,
                now
        );

        return response.getLastInsertRowId();
    }
}



/*
SELECT posts.title, posts.username, posts.description, posts.b64uri, posts.width, posts.height FROM posts p WHERE p.username = ? LEFT JOIN posts i ON i.username = p.username LIMIT ? OFFSET ?;
SELECT * FROM posts i WHERE i.username = ? RIGHT JOIN users u ON u.username = i.username LIMIT ? OFFSET ?;
 */
