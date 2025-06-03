package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.util.List;
import java.util.Optional;

public interface IImageService<Image> {
    List<Image> getImages() throws SerfConnectorException;

    List<Image> getImagesWithParams(int limit, int offset) throws SerfConnectorException;

    List<Image> getImagesForUserWithParams(String username, int limit, int offset) throws SerfConnectorException;

    Optional<Image> getImageWithName(String imageName) throws SerfConnectorException;

    long insertImage(Image image) throws SerfConnectorException;
}
