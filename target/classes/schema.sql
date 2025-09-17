CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    details_json TEXT,
    active BOOLEAN,
    expiry_date DATE,
    code VARCHAR(255)
);
