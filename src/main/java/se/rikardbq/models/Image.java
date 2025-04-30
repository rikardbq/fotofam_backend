package se.rikardbq.models;

public record Image(int id, String path, String slug, byte[] file, String file_str) {}
