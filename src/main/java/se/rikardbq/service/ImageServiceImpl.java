package se.rikardbq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.SomeDataClass;
import se.rikardbq.exception.MyCustomException;

import java.util.List;

@Component
public class ImageServiceImpl implements ImageService<SomeDataClass> {

    @Autowired
    DatabaseServiceImpl databaseService;

    @Override
    public List<SomeDataClass> getImages() throws MyCustomException {
        return databaseService.query(SomeDataClass.class, "SELECT * FROM testing_table;");
    }

    @Override
    public SomeDataClass getImageById(int id) throws MyCustomException {
        return databaseService.query(SomeDataClass.class, "SELECT * FROM testing_table WHERE id = ?;", id).getFirst();
    }

    @Override
    public SomeDataClass getImageBySlug(String slug) throws MyCustomException {
        return databaseService.query(SomeDataClass.class, "SELECT * FROM testing_table WHERE slug = ?;", slug).getFirst();
    }
}
