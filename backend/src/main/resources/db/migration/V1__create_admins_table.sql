-- admins  table creation
CREATE TABLE IF NOT EXISTS admins (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(50) UNIQUE NOT NULL,
                                      email VARCHAR(100) UNIQUE NOT NULL,
                                      password_hash TEXT NOT NULL,
                                      created_at TIMESTAMP DEFAULT NOW(),
                                      updated_at TIMESTAMP DEFAULT NOW()
);