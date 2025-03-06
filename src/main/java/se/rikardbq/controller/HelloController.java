package se.rikardbq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.SomeDataClass;
import se.rikardbq.models.Hello;
import se.rikardbq.service.ImageServiceImpl;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private ImageServiceImpl imageService;
    private static final String template = "Hello, %s!";

    @GetMapping("/greeting")
    public Hello greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        List<SomeDataClass> images = null;
        SomeDataClass image = null;
        try {
            images = imageService.getImages();
            image = imageService.getImage(2);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(images.getFirst().toString());
        System.out.println(image);
        return new Hello(String.format(images.toString()));
    }
}