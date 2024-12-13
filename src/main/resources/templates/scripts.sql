-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    name VARCHAR(255),
    avatar_id BIGINT, -- Связь с таблицей Image через avatar
    active BOOLEAN NOT NULL DEFAULT TRUE,
    activation_code VARCHAR(255),
    password VARCHAR(1000) NOT NULL
);

-- Таблица ролей
CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20),
    PRIMARY KEY (user_id, role)
);

-- Таблица продуктов
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price INTEGER,
    city VARCHAR(255),
    preview_image_id BIGINT, -- Идентификатор главного изображения
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    date_of_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица изображений
CREATE TABLE images (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255),
original_file_name VARCHAR(255),
size BIGINT,
content_type VARCHAR(100),
is_preview_image BOOLEAN NOT NULL,
bytes BYTEA,
product_id BIGINT REFERENCES products(id) ON DELETE CASCADE
);

-- Таблица паролей
CREATE TABLE passwords (
user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
password VARCHAR(1000) NOT NULL
);

-- Таблица email-адресов
CREATE TABLE emails (
user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
email VARCHAR(255) NOT NULL
);

-- Таблица продавцов
CREATE TABLE sellers (
user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
title VARCHAR(255) NOT NULL,
product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE
);

-- Функция для копирования пароля
CREATE OR REPLACE FUNCTION copy_password_to_passwords()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO passwords (user_id, password)
VALUES (NEW.id, NEW.password);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для копирования пароля
CREATE TRIGGER trigger_copy_password_to_passwords
    AFTER INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION copy_password_to_passwords();

-- Функция для копирования email
CREATE OR REPLACE FUNCTION copy_email_to_emails()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO emails (user_id, email)
VALUES (NEW.id, NEW.email);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для копирования email
CREATE TRIGGER trigger_copy_email_to_emails
    AFTER INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION copy_email_to_emails();

-- Функция для добавления продавцов
CREATE OR REPLACE FUNCTION add_to_sellers()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sellers WHERE user_id = NEW.user_id AND product_id = NEW.id) THEN
        INSERT INTO sellers (user_id, product_id, title)
        VALUES (NEW.user_id, NEW.id, NEW.title);
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для добавления продавцов
CREATE TRIGGER trigger_add_to_sellers
    AFTER INSERT ON products
    FOR EACH ROW
    EXECUTE FUNCTION add_to_sellers();