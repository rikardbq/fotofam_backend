CREATE TABLE posts (
    id INTEGER PRIMARY KEY,
    image_name TEXT NOT NULL,
    description TEXT,
    user_id INTEGER NOT NULL,
    created_at INTEGER,
    updated_at INTEGER,
    FOREIGN KEY (image_name)
    REFERENCES images (name)
       ON UPDATE SET NULL
       ON DELETE SET NULL
    FOREIGN KEY (user_id)
    REFERENCES users (id)
       ON UPDATE SET NULL
       ON DELETE SET NULL
);
CREATE INDEX idx_posts_image_name ON posts (image_name);
CREATE INDEX idx_posts_user_id ON posts (user_id);