INSERT INTO COUPONS (id, type, details_json, active, expiry_date, code) VALUES 
(1, 'CART_WISE', '{"threshold":100, "discount":10}', TRUE, '2099-12-31', 'CART10'),
(2, 'PRODUCT_WISE', '{"product_id":1, "discount":20}', TRUE, '2099-12-31', 'PROD20'),
(3, 'BXGY', '{"buy_products":[{"product_id":1,"quantity":3}], "get_products":[{"product_id":3,"quantity":1}], "repetition_limit":2}', TRUE, '2099-12-31', 'B3G1');
