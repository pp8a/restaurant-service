INSERT INTO product_categories (name, type) VALUES 
('Appetizers', 'Starter'),
('Main Courses', 'Main'),
('Desserts', 'Dessert'),
('Beverages', 'Drink'),
('Salads', 'Starter')
ON CONFLICT DO NOTHING;

INSERT INTO products (name, price, quantity, available, category_id) VALUES 
('Spring Rolls', 5.99, 50, TRUE, 1),
('Grilled Chicken', 12.99, 30, TRUE, 2),
('Chocolate Cake', 6.99, 20, TRUE, 3),
('Lemonade', 2.99, 100, TRUE, 4),
('Vodka', 10.99, 45, TRUE, 4),
('Caesar Salad', 7.99, 40, TRUE, 5)
ON CONFLICT DO NOTHING;

INSERT INTO order_status (status_name) VALUES 
('ACCEPTED'),
('APPROVED'),
('CANCELLED'),
('PAID')
ON CONFLICT DO NOTHING;

INSERT INTO order_details (order_status_id, total_amount) VALUES 
(1, 25.97),
(2, 10.98),
(3, 23.98)
ON CONFLICT DO NOTHING;

INSERT INTO order_approvals (order_detail_id) VALUES 
(2)
ON CONFLICT DO NOTHING;

INSERT INTO order_detail_products (order_detail_id, product_id) VALUES 
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 6),
(3, 5),
(3, 2)
ON CONFLICT DO NOTHING;
