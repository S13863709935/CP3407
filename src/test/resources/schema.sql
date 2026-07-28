DROP TABLE IF EXISTS goods;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id INT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    name VARCHAR(255),
    avatar VARCHAR(255),
    role VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    info VARCHAR(255)
);

CREATE TABLE goods (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10, 2),
    content CLOB,
    address VARCHAR(255),
    img VARCHAR(255),
    date VARCHAR(255),
    status VARCHAR(255),
    category VARCHAR(255),
    user_id INT,
    sale_status VARCHAR(255),
    read_count INT DEFAULT 0
);
