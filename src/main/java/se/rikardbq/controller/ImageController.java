package se.rikardbq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.SomeDataClass;
import se.rikardbq.exception.MyCustomException;
import se.rikardbq.models.Hello;
import se.rikardbq.service.ImageServiceImpl;

import java.util.List;

@RestController
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;

    @GetMapping("/greeting")
    public Hello greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        List<SomeDataClass> images = null;
        SomeDataClass image = null;
        SomeDataClass imageBySlug = null;
        try {
            images = imageService.getImages();
            image = imageService.getImageById(2);
            imageBySlug = imageService.getImageBySlug(name);
        } catch (MyCustomException e) {
            throw new RuntimeException(e);
        }
        System.out.println(images.getFirst().toString());
        System.out.println(image);
        System.out.println(imageBySlug);
        return new Hello(String.format(images.toString()));
    }
}