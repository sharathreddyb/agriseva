CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,

    name VARCHAR(150) NOT NULL,
    category VARCHAR(40) NOT NULL,
    description VARCHAR(1000),

    rental_price_per_day NUMERIC(12, 2) NOT NULL,
    security_deposit NUMERIC(12, 2) NOT NULL DEFAULT 0,

    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    service_address VARCHAR(255) NOT NULL,
    village VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,

    image_url VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipment_owner
        FOREIGN KEY (owner_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_equipment_category
        CHECK (
            category IN (
                'TRACTOR',
                'HARVESTER',
                'CULTIVATOR',
                'PLOUGH',
                'ROTAVATOR',
                'SEEDER',
                'SPRAYER',
                'IRRIGATION_EQUIPMENT',
                'JCB',
                'OTHER'
            )
        ),

    CONSTRAINT chk_equipment_status
        CHECK (
            status IN (
                'AVAILABLE',
                'UNAVAILABLE',
                'MAINTENANCE'
            )
        ),

    CONSTRAINT chk_equipment_rental_price
        CHECK (rental_price_per_day > 0),

    CONSTRAINT chk_equipment_security_deposit
        CHECK (security_deposit >= 0)
);

CREATE INDEX idx_equipment_owner
    ON equipment (owner_id);

CREATE INDEX idx_equipment_category
    ON equipment (category);

CREATE INDEX idx_equipment_location
    ON equipment (district, village);

CREATE INDEX idx_equipment_active_status
    ON equipment (active, status);