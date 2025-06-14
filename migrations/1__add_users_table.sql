CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    real_name TEXT
);
CREATE UNIQUE INDEX idx_users_username ON users (username);