package se.rikardbq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.rikardbq.models.Hello;

@RestController
public class HelloController {

    private static final String template = "Hello, %s!";


    @GetMapping("/greeting")
    public Hello greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Hello(String.format(template, name));
    }
}