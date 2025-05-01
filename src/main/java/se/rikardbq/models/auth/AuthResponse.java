package se.rikardbq.models.auth;

import java.io.Serial;
import java.io.Serializable;

public class AuthResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
