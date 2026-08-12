CREATE TABLE equipment_rentals (
    id BIGSERIAL PRIMARY KEY,

    equipment_id BIGINT NOT NULL,
    farmer_id BIGINT NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days INTEGER NOT NULL,

    rental_price_per_day NUMERIC(12, 2) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    farmer_note VARCHAR(500),
    owner_response_note VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipment_rentals_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment (id),

    CONSTRAINT fk_equipment_rentals_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES users (id),

    CONSTRAINT chk_equipment_rental_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_equipment_rental_days
        CHECK (total_days > 0),

    CONSTRAINT chk_equipment_rental_daily_price
        CHECK (rental_price_per_day > 0),

    CONSTRAINT chk_equipment_rental_total_amount
        CHECK (total_amount > 0),

    CONSTRAINT chk_equipment_rental_status
        CHECK (
            status IN (
                'PENDING',
                'APPROVED',
                'REJECTED',
                'CANCELLED',
                'COMPLETED'
            )
        )
);

CREATE INDEX idx_equipment_rentals_equipment
    ON equipment_rentals (equipment_id);

CREATE INDEX idx_equipment_rentals_farmer
    ON equipment_rentals (farmer_id);

CREATE INDEX idx_equipment_rentals_status
    ON equipment_rentals (status);

CREATE INDEX idx_equipment_rentals_dates
    ON equipment_rentals (
        equipment_id,
        start_date,
        end_date
    );