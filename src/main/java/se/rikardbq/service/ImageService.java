package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.image.UploadImageRequest;

import java.util.List;

@Component
public class ImageService implements IImageService<Image> {

    @Autowired
    IDatabaseService databaseService;

    @Override
    public List<Image> getImages() throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images;");
    }

    @Override
    public List<Image> getImagesWithParams(int limit, int offset) throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images LIMIT ? OFFSET ?;", limit, offset);
    }

    @Override
    public List<Image> getImagesForUserWithParams(String username, int limit, int offset) throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images WHERE username = ? LIMIT ? OFFSET ?;", username, limit, offset);
    }

    @Override
    public long insertImage(UploadImageRequest imageRequest) throws SerfConnectorException {
        MutationResponse response = databaseService.mutate("INSERT INTO images(file_str, path, slug) VALUES(?, ?, ?);", imageRequest.getImgB64(), imageRequest.getMetadata().getName(), imageRequest.getMetadata().getSlug());

        return response.getLastInsertRowId();
    }
}



/*
SELECT posts.title, posts.username, posts.description, images.b64uri, images.width, images.height FROM posts p WHERE p.username = ? LEFT JOIN images i ON i.username = p.username LIMIT ? OFFSET ?;
SELECT * FROM images i WHERE i.username = ? RIGHT JOIN users u ON u.username = i.username LIMIT ? OFFSET ?;
 */
