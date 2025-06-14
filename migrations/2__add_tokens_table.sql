CREATE TABLE tokens (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    rt TEXT UNIQUE,
    created_at INTEGER,
    updated_at INTEGER
);
CREATE UNIQUE INDEX idx_tokens_username ON tokens (username);
CREATE UNIQUE INDEX idx_tokens_rt ON tokens (rt);