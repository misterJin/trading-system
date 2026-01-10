-- 数据库表结构定义

-- 用户账户表
CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT uk_username UNIQUE (username)
);

-- 商家账户表
CREATE TABLE IF NOT EXISTS merchant_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT uk_merchant_name UNIQUE (name)
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    merchant_id BIGINT NOT NULL,
    stock_quantity BIGINT NOT NULL,
    sold_quantity BIGINT NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT uk_sku UNIQUE (sku),
    CONSTRAINT fk_product_merchant FOREIGN KEY (merchant_id) REFERENCES merchant_accounts(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user_accounts(id),
    CONSTRAINT fk_order_merchant FOREIGN KEY (merchant_id) REFERENCES merchant_accounts(id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES products(id)
);

