-- Xóa dữ liệu cũ nếu có
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_detail;
TRUNCATE TABLE product_supplier;
TRUNCATE TABLE product;
TRUNCATE TABLE category;
TRUNCATE TABLE supplier;
TRUNCATE TABLE account;
TRUNCATE TABLE role;
SET FOREIGN_KEY_CHECKS = 1;

-- Thêm vai trò (Role)
INSERT INTO role (role_id, role_name) VALUES 
('R01', 'ADMIN'),
('R02', 'STAFF'),
('R03', 'CUSTOMER');

-- Thêm tài khoản (Account) - Mật khẩu mặc định là '123456' đã được Bcrypt (nếu cần)
-- Ở đây giả định bạn dùng mật khẩu text hoặc sẽ update sau bằng code
INSERT INTO account (username, password, role_id) VALUES 
('admin', '$2a$10$Xm8vH6p/yvXInK5v4mCReO.q9N0z5aA0x/UvB/X.v6iE5tC8O.S8q', 'R01'),
('customer1', '$2a$10$Xm8vH6p/yvXInK5v4mCReO.q9N0z5aA0x/UvB/X.v6iE5tC8O.S8q', 'R03');

-- Thêm danh mục (Category)
INSERT INTO category (category_id, category_name) VALUES 
('CAT01', 'Lifestyle'),
('CAT02', 'Running'),
('CAT03', 'Basketball'),
('CAT04', 'Training');

-- Thêm nhà cung cấp (Supplier)
INSERT INTO supplier (supplier_id, supplier_name, phone_number, email, address) VALUES 
('SUP01', 'Nike Vietnam', '0123456789', 'contact@nike.vn', 'District 1, HCM City'),
('SUP02', 'Adidas Vietnam', '0987654321', 'info@adidas.vn', 'Tan Binh District, HCM City'),
('SUP03', 'Puma Global', '0555666777', 'sales@puma.com', 'Binh Duong Province');

-- Thêm sản phẩm (Product)
INSERT INTO product (product_id, product_name, brand, origin, description, material, price, tax, gender, category_id, supplier_id, image) VALUES 
('PRD01', 'Air Jordan 1 Retro High', 'Nike', 'Vietnam', 'The classic sneaker that started it all.', 'Leather', 189.00, 0.1, 'MALE', 'CAT03', 'SUP01', 'jordan1.jpg'),
('PRD02', 'Nike Air Max 270', 'Nike', 'Vietnam', 'Big Air for big comfort.', 'Mesh', 150.00, 0.1, 'UNISEX', 'CAT02', 'SUP01', 'airmax270.jpg'),
('PRD03', 'Adidas Ultraboost Light', 'Adidas', 'Germany', 'Lightest Ultraboost ever made.', 'Primeknit', 180.00, 0.1, 'UNISEX', 'CAT02', 'SUP02', 'ultraboost.jpg'),
('PRD04', 'Puma Suede Classic', 'Puma', 'Indonesia', 'The icon from 1968.', 'Suede', 85.00, 0.1, 'UNISEX', 'CAT01', 'SUP03', 'pumasuede.jpg'),
('PRD05', 'Nike Blazer Mid 77', 'Nike', 'Vietnam', 'Vintage basketball style.', 'Leather', 105.00, 0.1, 'MALE', 'CAT01', 'SUP01', 'blazer77.jpg'),
('PRD06', 'New Balance 550', 'New Balance', 'USA', 'Retro hoop vibes.', 'Leather', 110.00, 0.1, 'UNISEX', 'CAT01', 'SUP02', 'nb550.jpg');

-- Thêm chi tiết sản phẩm (ProductDetail) - Size và màu sắc
INSERT INTO product_detail (product_detail_id, product_id, color, size, stock_quantity) VALUES 
('PD01', 'PRD01', 'Chicago Red', 40, 10),
('PD02', 'PRD01', 'Chicago Red', 41, 15),
('PD03', 'PRD01', 'Chicago Red', 42, 5),
('PD04', 'PRD02', 'White/Black', 39, 20),
('PD05', 'PRD02', 'White/Black', 40, 25),
('PD06', 'PRD03', 'Core Black', 41, 12),
('PD07', 'PRD03', 'Core Black', 42, 18),
('PD08', 'PRD04', 'Navy Blue', 38, 30),
('PD09', 'PRD05', 'White', 40, 15),
('PD10', 'PRD06', 'White Green', 41, 22);

-- Liên kết Product và Supplier (nếu bảng Product_Supplier tồn tại độc lập)
INSERT INTO product_supplier (product_id, supplier_id) VALUES 
('PRD01', 'SUP01'),
('PRD02', 'SUP01'),
('PRD03', 'SUP02'),
('PRD04', 'SUP03'),
('PRD05', 'SUP01'),
('PRD06', 'SUP02');
