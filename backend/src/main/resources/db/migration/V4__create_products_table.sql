CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    seller_id BIGINT NOT NULL,

    name VARCHAR(150) NOT NULL,
    category VARCHAR(30) NOT NULL,
    description VARCHAR(1000),

    price NUMERIC(12, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    unit VARCHAR(20) NOT NULL,

    image_url VARCHAR(500),

    service_address VARCHAR(255) NOT NULL,
    village VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_seller
        FOREIGN KEY (seller_id)
        REFERENCES users (id),

    CONSTRAINT chk_products_category
        CHECK (
            category IN (
                'FERTILIZER',
                'SEED',
                'PESTICIDE',
                'FARMING_TOOL',
                'OTHER'
            )
        ),

    CONSTRAINT chk_products_price
        CHECK (price > 0),

    CONSTRAINT chk_products_stock
        CHECK (stock_quantity >= 0),

    CONSTRAINT chk_products_unit
        CHECK (
            unit IN (
                'KG',
                'LITRE',
                'PACK',
                'PIECE'
            )
        )
);

CREATE INDEX idx_products_seller
    ON products (seller_id);

CREATE INDEX idx_products_category
    ON products (category);

CREATE INDEX idx_products_location
    ON products (district, village);

CREATE INDEX idx_products_active
    ON products (active);