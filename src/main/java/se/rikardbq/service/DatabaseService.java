package se.rikardbq.service;

import java.util.List;

public interface DatabaseService<SomeDataClass> {

    List<SomeDataClass> getData();
}
