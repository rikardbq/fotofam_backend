package se.rikardbq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.rikardbq.SomeDataClass;

import java.util.List;

@Component
public class ImageServiceImpl implements ImageService<SomeDataClass> {

    @Autowired
    DatabaseServiceImpl databaseService;

    @Override
    public List<SomeDataClass> getImages() throws JsonProcessingException {
        return databaseService.query(SomeDataClass.class, "SELECT * FROM testing_table;");
    }

    @Override
    public SomeDataClass getImage(int id) throws JsonProcessingException {
        return databaseService.query(SomeDataClass.class, "SELECT * FROM testing_table WHERE id = ?;", id).getFirst();
    }
}
