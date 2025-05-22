package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.util.List;

public interface IImageService<Image> {
    List<Image> getImages() throws SerfConnectorException;

    List<Image> getImagesWithParams(int limit, int offset) throws SerfConnectorException;

    List<Image> getImagesForUserWithParams(String username, int limit, int offset) throws SerfConnectorException;

    long insertImage(Image image) throws SerfConnectorException;
}
