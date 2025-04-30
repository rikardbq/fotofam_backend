package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.Image;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.image.UploadImageRequest;

import java.time.Instant;
import java.util.List;

@Component
public class ImageServiceImpl implements ImageService<Image> {

    @Autowired
    DatabaseService databaseService;

    @Override
    public List<Image> getImages() throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images WHERE id = ?;", 23);
    }

    @Override
    public Image getImageById(int id) throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images WHERE id = ?;", id).getFirst();
    }

    @Override
    public Image getImageBySlug(String slug) throws SerfConnectorException {
        return databaseService.query(Image.class, "SELECT * FROM images WHERE slug = ?;", slug).getFirst();
    }

    @Override
    public long insertImage(byte[] file) throws SerfConnectorException {
        MutationResponse response = databaseService.mutate("INSERT INTO images(file, path, slug) VALUES(?, ?, ?);", file, Instant.now().toString(), Instant.now().toEpochMilli());

        return response.getLastInsertRowId();
    }

    @Override
    public long insertImage(String file) throws SerfConnectorException {
        MutationResponse response = databaseService.mutate("INSERT INTO images(file_str, path, slug) VALUES(?, ?, ?);", file, Instant.now().toString(), Instant.now().toEpochMilli());

        return response.getLastInsertRowId();
    }

    @Override
    public long insertImage(UploadImageRequest imageRequest) throws SerfConnectorException {
        MutationResponse response = databaseService.mutate("INSERT INTO images(file_str, path, slug) VALUES(?, ?, ?);", imageRequest.getImgB64(), imageRequest.getMetadata().getName(), imageRequest.getMetadata().getSlug());

        return response.getLastInsertRowId();
    }
}
