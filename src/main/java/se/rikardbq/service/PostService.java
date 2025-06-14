package se.rikardbq.service;

import io.trbl.blurhash.BlurHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.Post;
import se.rikardbq.models.request.CreatePostRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class PostService implements IPostService<CreatePostRequest, Post> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public List<Post> getPosts() throws SerfConnectorException {
        return databaseService.query(Post.class, "SELECT id, image_name, blurhash, description, created_at FROM posts ORDER BY created_at DESC;");
    }

    @Override
    public List<Post> getPostsWithParams(int limit, int offset) throws SerfConnectorException {
        return databaseService.query(
                Post.class,
                "SELECT id, image_name, blurhash, description, created_at FROM posts ORDER BY created_at DESC LIMIT ? OFFSET ?;",
                limit,
                offset
        );
    }

    @Override
    public List<Post> getPostsForUserWithParams(int userId, int limit, int offset) throws SerfConnectorException {
        return databaseService.query(
                Post.class,
                "SELECT id, image_name, blurhash, description, created_at FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?;",
                userId,
                limit,
                offset
        );
    }

    @Override
    public long insertPost(CreatePostRequest createPostRequest, int userId) throws SerfConnectorException, IOException {
        Image image = createPostRequest.getImage();
        Post post = createPostRequest.getPost();
        long now = Instant.now().getEpochSecond();

        byte[] imageB64Bytes = Base64.getDecoder().decode(image.getBase64().split(",")[1]);
        String imageBlurhash = BlurHash.encode(this.resizeImage(imageB64Bytes, 20), 4, 3);

        MutationResponse response = databaseService.mutate("""
                        INSERT INTO images(name, width, height, base64, user_id, created_at) VALUES(?, ?, ?, ?, ?, ?);
                        INSERT INTO posts(image_name, blurhash, description, user_id, created_at) VALUES(?, ?, ?, ?, ?);
                        """,
                image.getName(),
                image.getWidth(),
                image.getHeight(),
                image.getBase64(),
                userId,
                now,
                image.getName(),
                imageBlurhash,
                post.getDescription(),
                userId,
                now
        );

        return response.getLastInsertRowId();
    }

    private BufferedImage resizeImage(byte[] imageBytes, int divisions) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        int width = image.getWidth() / divisions;
        int height = image.getHeight() / divisions;
        java.awt.Image tmp = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }
}



/*
SELECT posts.title, posts.username, posts.description, posts.b64uri, posts.width, posts.height FROM posts p WHERE p.username = ? LEFT JOIN posts i ON i.username = p.username LIMIT ? OFFSET ?;
SELECT * FROM posts i WHERE i.username = ? RIGHT JOIN users u ON u.username = i.username LIMIT ? OFFSET ?;
 */
