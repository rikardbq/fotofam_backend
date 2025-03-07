package se.rikardbq.service;

import se.rikardbq.exception.MyCustomException;

import java.util.List;

public interface ImageService<SomeDataClass> {

    List<SomeDataClass> getImages() throws MyCustomException;

    SomeDataClass getImageById(int id) throws MyCustomException;

    SomeDataClass getImageBySlug(String slug) throws MyCustomException;
}
