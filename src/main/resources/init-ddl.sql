DROP TABLE IF EXISTS order_detail_products CASCADE;
DROP TABLE IF EXISTS order_approvals CASCADE;
DROP TABLE IF EXISTS order_details CASCADE;
DROP TABLE IF EXISTS order_status CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;

CREATE TABLE IF NOT EXISTS product_categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    available BOOLEAN NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE IF NOT EXISTS order_status(
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_details (
    id SERIAL PRIMARY KEY,
    order_status_id INT,
    total_amount DECIMAL (10, 2) NOT NULL,
    FOREIGN KEY (order_status_id) REFERENCES order_status(id)
);

CREATE TABLE IF NOT EXISTS order_approvals (
    id SERIAL PRIMARY KEY,
    order_detail_id INT,
    FOREIGN KEY (order_detail_id) REFERENCES order_details(id)
);

CREATE TABLE IF NOT EXISTS order_detail_products(
    order_detail_id INT,
    product_id INT,
    PRIMARY KEY (order_detail_id, product_id),
    FOREIGN KEY (order_detail_id) REFERENCES order_details(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
