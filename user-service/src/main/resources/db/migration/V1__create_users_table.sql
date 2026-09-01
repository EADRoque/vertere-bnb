   CREATE TABLE users (
       id UUID PRIMARY KEY,
       email VARCHAR(255) NOT NULL UNIQUE,
       password_hash VARCHAR(255) NOT NULL,
       full_name VARCHAR(255) NOT NULL,
       phone VARCHAR(255),
       avatar_url VARCHAR(255),
       is_admin BOOLEAN NOT NULL DEFAULT FALSE,
       created_at TIMESTAMPTZ NOT NULL
   );