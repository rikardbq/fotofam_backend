package se.rikardbq.models.image;

import java.io.Serial;
import java.io.Serializable;

public class Metadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String slug;
    private int width;
    private int height;

    public Metadata() {}

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
