CREATE TABLE images (
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    base64 TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    created_at INTEGER,
    updated_at INTEGER,
    FOREIGN KEY (user_id)
    REFERENCES users (id)
       ON UPDATE SET NULL
       ON DELETE SET NULL
);
CREATE UNIQUE INDEX idx_images_name ON images (name);
CREATE INDEX idx_images_user_id ON images (user_id);