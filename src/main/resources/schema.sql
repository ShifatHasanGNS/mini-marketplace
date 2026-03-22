CREATE TABLE IF NOT EXISTS login (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE PRECISION,
    origin VARCHAR(255),
    pic VARCHAR(255),
    seller_name VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS coupon (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255),
    discount_percentage INTEGER,
    valid_until DATE
);
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_name VARCHAR(255),
    delivery_address VARCHAR(255),
    mobile_number VARCHAR(255),
    product_name VARCHAR(255),
    origin VARCHAR(255),
    price DOUBLE PRECISION,
    seller_name VARCHAR(255),
    discount_percentage DOUBLE PRECISION,
    coupon_code VARCHAR(255),
    order_date DATE
);