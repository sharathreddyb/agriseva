CREATE TABLE product_orders (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,

    unit_price NUMERIC(12, 2) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    buyer_note VARCHAR(500),
    seller_response_note VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_orders_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT fk_product_orders_buyer
        FOREIGN KEY (buyer_id)
        REFERENCES users (id),

    CONSTRAINT chk_product_order_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_product_order_unit_price
        CHECK (unit_price > 0),

    CONSTRAINT chk_product_order_total_amount
        CHECK (total_amount > 0),

    CONSTRAINT chk_product_order_status
        CHECK (
            status IN (
                'PENDING',
                'ACCEPTED',
                'REJECTED',
                'CANCELLED',
                'COMPLETED'
            )
        )
);

CREATE INDEX idx_product_orders_product
    ON product_orders (product_id);

CREATE INDEX idx_product_orders_buyer
    ON product_orders (buyer_id);

CREATE INDEX idx_product_orders_status
    ON product_orders (status);

CREATE INDEX idx_product_orders_created_at
    ON product_orders (created_at);