package se.rikardbq.models.request;

import se.rikardbq.models.Image;
import se.rikardbq.models.Post;

import java.io.Serial;
import java.io.Serializable;

public class CreatePostRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Post post;
    private Image image;

    public CreatePostRequest() {
    }
    
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
