package se.rikardbq.service;

import se.rikardbq.exception.SerfConnectorException;

import java.io.IOException;
import java.util.List;

public interface IPostService<CreatePostRequest, Post> {
    List<Post> getPosts() throws SerfConnectorException;

    List<Post> getPostsWithParams(int limit, int offset) throws SerfConnectorException;

    List<Post> getPostsForUserWithParams(int userId, int limit, int offset) throws SerfConnectorException;

    long insertPost(CreatePostRequest createPostRequest, int userId) throws SerfConnectorException, IOException;
}
