package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;
import se.rikardbq.models.image.Metadata;
import se.rikardbq.models.image.UploadImageRequest;

import java.util.List;

public interface ImageService<Image> {

    List<Image> getImages() throws SerfConnectorException;

    Image getImageById(int id) throws SerfConnectorException;

    Image getImageBySlug(String slug) throws SerfConnectorException;

    long insertImage(byte[] file) throws SerfConnectorException;

    long insertImage(String file) throws SerfConnectorException;

    long insertImage(UploadImageRequest imageRequest) throws SerfConnectorException;
}
