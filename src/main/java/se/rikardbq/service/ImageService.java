package se.rikardbq.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ImageService<SomeDataClass> {

    List<SomeDataClass> getImages() throws JsonProcessingException;

    SomeDataClass getImage(int id) throws JsonProcessingException;
}
