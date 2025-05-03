package se.rikardbq.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    @JsonProperty("real_name")
    private String realName;

    public User() {}

    public User(int id, String username, String password, String realName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRealName() {
        return realName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
