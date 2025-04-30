package se.rikardbq.models.image;

import java.io.Serial;
import java.io.Serializable;

public class UploadImageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String imgB64;
    private Metadata metadata;

    public UploadImageRequest() {
    }

    public String getImgB64() {
        return imgB64;
    }

    public void setImgB64(String imgB64) {
        this.imgB64 = imgB64;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
