-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.6.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for shoeshop
CREATE DATABASE IF NOT EXISTS `shoeshop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `shoeshop`;

-- Dumping structure for table shoeshop.account
CREATE TABLE IF NOT EXISTS `account` (
  `account_id` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UKq0uja26qgu1atulenwup9rxyr` (`email`),
  UNIQUE KEY `UKgex1lmaqpg0ir5g1f5eftyaa1` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.account: ~2 rows (approximately)
DELETE FROM `account`;
INSERT INTO `account` (`account_id`, `email`, `password`, `username`) VALUES
	('54985cb2-249b-4a6f-aeb8-17c65bc97b43', 'liemhuynh0789@gmail.com', '$2a$10$NY7Q8FBsG.7XCyahGi/Eduo2Gfgv8ohZjJxRDnN5Vu0Rz36kuom3C', 'huynhliem'),
	('TK001', 'admin@example.com', '$2a$10$uxY1MLSWZeNUP9o25oiHdubVOFBuovp39FXIAYdDXVSefERAV/Xdq', 'admin');

-- Dumping structure for table shoeshop.account_role
CREATE TABLE IF NOT EXISTS `account_role` (
  `account_id` varchar(255) NOT NULL,
  `role` enum('ROLE_ADMIN','ROLE_USER') DEFAULT NULL,
  KEY `FK1f8y4iy71kb1arff79s71j0dh` (`account_id`),
  CONSTRAINT `FK1f8y4iy71kb1arff79s71j0dh` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.account_role: ~3 rows (approximately)
DELETE FROM `account_role`;
INSERT INTO `account_role` (`account_id`, `role`) VALUES
	('54985cb2-249b-4a6f-aeb8-17c65bc97b43', 'ROLE_ADMIN'),
	('TK001', 'ROLE_USER'),
	('TK001', 'ROLE_ADMIN');

-- Dumping structure for table shoeshop.category
CREATE TABLE IF NOT EXISTS `category` (
  `category_id` varchar(255) NOT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.category: ~4 rows (approximately)
DELETE FROM `category`;
INSERT INTO `category` (`category_id`, `category_name`) VALUES
	('CAT01', 'Lifestyle'),
	('CAT02', 'Running'),
	('CAT03', 'Basketball'),
	('CAT04', 'Training');

-- Dumping structure for table shoeshop.comments
CREATE TABLE IF NOT EXISTS `comments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int(11) NOT NULL,
  `customer_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiu2ob8pkb818nwfiiwvm4f0xn` (`customer_id`),
  KEY `FKj9to9e3tjoimlgn3w4vjm4xe3` (`product_id`),
  CONSTRAINT `FKiu2ob8pkb818nwfiiwvm4f0xn` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKj9to9e3tjoimlgn3w4vjm4xe3` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.comments: ~0 rows (approximately)
DELETE FROM `comments`;

-- Dumping structure for table shoeshop.customer
CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `join_date` datetime(6) DEFAULT NULL,
  `loyalty_points` int(11) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `total_spending` double NOT NULL,
  `account_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `UKjwt2qo9oj3wd7ribjkymryp8s` (`account_id`),
  CONSTRAINT `FKn9x2k8svpxj3r328iy1rpur83` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.customer: ~1 rows (approximately)
DELETE FROM `customer`;
INSERT INTO `customer` (`customer_id`, `address`, `email`, `full_name`, `join_date`, `loyalty_points`, `phone_number`, `total_spending`, `account_id`) VALUES
	('c3db1ca8-28ce-468c-8332-dd65e65a7554', 'đa, da, da, ad', 'liemhuynh0789@gmail.com', 'Liem Huynh', '2026-05-05 18:34:30.160972', 0, '0789354898', 0, '54985cb2-249b-4a6f-aeb8-17c65bc97b43');

-- Dumping structure for table shoeshop.import_order
CREATE TABLE IF NOT EXISTS `import_order` (
  `import_order_id` varchar(255) NOT NULL,
  `import_date` datetime(6) DEFAULT NULL,
  `staff_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`import_order_id`),
  KEY `FKafhitm4hhra6gfw79rfmuqxjc` (`staff_id`),
  CONSTRAINT `FKafhitm4hhra6gfw79rfmuqxjc` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.import_order: ~0 rows (approximately)
DELETE FROM `import_order`;

-- Dumping structure for table shoeshop.import_order_detail
CREATE TABLE IF NOT EXISTS `import_order_detail` (
  `import_order_detail_id` varchar(255) NOT NULL,
  `import_price` double NOT NULL,
  `quantity` int(11) NOT NULL,
  `import_order_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`import_order_detail_id`),
  KEY `FKkoeuvat1kdj2mx53pi5hijawe` (`import_order_id`),
  KEY `FKlbchbwjggqyesb7f806eii740` (`product_id`),
  CONSTRAINT `FKkoeuvat1kdj2mx53pi5hijawe` FOREIGN KEY (`import_order_id`) REFERENCES `import_order` (`import_order_id`),
  CONSTRAINT `FKlbchbwjggqyesb7f806eii740` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.import_order_detail: ~0 rows (approximately)
DELETE FROM `import_order_detail`;

-- Dumping structure for table shoeshop.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` varchar(255) NOT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `order_status` enum('AWAITING_CANCELLATION','CANCELLED','DELIVERED','PENDING','RETURNED','SHIPPING') DEFAULT NULL,
  `payment_method` enum('CARD','COD','EWALLET','SEPAY') DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `used_points` int(11) NOT NULL,
  `customer_id` varchar(255) DEFAULT NULL,
  `promotion_id` varchar(255) DEFAULT NULL,
  `staff_id` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FK624gtjin3po807j3vix093tlf` (`customer_id`),
  KEY `FKkl19lst67x545047o4n1d0jpv` (`promotion_id`),
  KEY `FK4ery255787xl56k025fyxrqe9` (`staff_id`),
  CONSTRAINT `FK4ery255787xl56k025fyxrqe9` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`),
  CONSTRAINT `FK624gtjin3po807j3vix093tlf` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKkl19lst67x545047o4n1d0jpv` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`promotion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.orders: ~1 rows (approximately)
DELETE FROM `orders`;
INSERT INTO `orders` (`order_id`, `order_date`, `order_status`, `payment_method`, `total_amount`, `used_points`, `customer_id`, `promotion_id`, `staff_id`, `full_name`, `note`, `phone_number`, `shipping_address`) VALUES
	('ORD-20260512-4564', '2026-05-12 18:33:24.565487', 'CANCELLED', 'COD', 217.9, 0, 'c3db1ca8-28ce-468c-8332-dd65e65a7554', NULL, NULL, NULL, NULL, NULL, NULL);

-- Dumping structure for table shoeshop.order_detail
CREATE TABLE IF NOT EXISTS `order_detail` (
  `order_detail_id` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `product_detail_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`),
  KEY `FKrws2q0si6oyd6il8gqe2aennc` (`order_id`),
  KEY `FKb8bg2bkty0oksa3wiq5mp5qnc` (`product_id`),
  KEY `FK4onmghajt9jh9quh6ed3lipdn` (`product_detail_id`),
  CONSTRAINT `FK4onmghajt9jh9quh6ed3lipdn` FOREIGN KEY (`product_detail_id`) REFERENCES `product_detail` (`product_detail_id`),
  CONSTRAINT `FKb8bg2bkty0oksa3wiq5mp5qnc` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FKrws2q0si6oyd6il8gqe2aennc` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.order_detail: ~1 rows (approximately)
DELETE FROM `order_detail`;
INSERT INTO `order_detail` (`order_detail_id`, `quantity`, `total_price`, `order_id`, `product_id`, `product_detail_id`) VALUES
	('ORDD-285100', 1, 189, 'ORD-20260512-4564', 'PRD01', 'PD01');

-- Dumping structure for table shoeshop.product
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` varchar(255) NOT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `gender` enum('Female','Male') DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `material` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `tax` double NOT NULL,
  `category_id` varchar(255) DEFAULT NULL,
  `supplier_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),
  KEY `FK2kxvbr72tmtscjvyp9yqb12by` (`supplier_id`),
  CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
  CONSTRAINT `FK2kxvbr72tmtscjvyp9yqb12by` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.product: ~6 rows (approximately)
DELETE FROM `product`;
INSERT INTO `product` (`product_id`, `brand`, `description`, `gender`, `image`, `material`, `origin`, `price`, `product_name`, `tax`, `category_id`, `supplier_id`) VALUES
	('PRD01', 'Nike', 'The classic sneaker that started it all.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/AirJordan1RetroHigh.png', 'Leather', 'Vietnam', 2000, 'Air Jordan 1 Retro High', 0.1, 'CAT03', 'SUP01'),
	('PRD02', 'Nike', 'Big Air for big comfort.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/NikeAirMax270.jpg', 'Mesh', 'Vietnam', 15000, 'Nike Air Max 270', 0.1, 'CAT02', 'SUP01'),
	('PRD03', 'Adidas', 'Lightest Ultraboost ever made.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/AdidasUltraboostLight.jpg', 'Primeknit', 'Germany', 18000, 'Adidas Ultraboost Light', 0.1, 'CAT02', 'SUP02'),
	('PRD04', 'Puma', 'The icon from 1968.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/Puma+Suede+Classic.jpg', 'Suede', 'Indonesia', 8500, 'Puma Suede Classic', 0.1, 'CAT01', 'SUP03'),
	('PRD05', 'Nike', 'Vintage basketball style.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/Nike+Blazer+Mid+77.jpg', 'Leather', 'Vietnam', 10500, 'Nike Blazer Mid 77', 0.1, 'CAT01', 'SUP01'),
	('PRD06', 'Nike', 'Premium basketball shoes.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/G.T.%2BCUT%2B4%2BVW%2BEP.png', 'Synthetic', 'Vietnam', 12500, 'G.T. CUT 4 VW EP', 0.1, 'CAT01', 'SUP01'),
	('PRD07', 'Adidas', 'Comfortable running shoes.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-adidas-pureboost-5-nam-xam-xanh-01.jpg', 'Mesh', 'Indonesia', 11000, 'Adidas Pureboost 5', 0.1, 'CAT02', 'SUP02'),
	('PRD08', 'Adidas', 'Daily wear running shoes.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-adidas-response-2-nu-trang-01.png', 'Mesh', 'Vietnam', 9500, 'Adidas Response 2', 0.1, 'CAT02', 'SUP02'),
	('PRD09', 'Adidas', 'Lightweight running shoes.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-adidas-runfalcon-5-nam-xam-xanh-01.jpg', 'Mesh', 'Indonesia', 8000, 'Adidas Runfalcon 5', 0.1, 'CAT02', 'SUP02'),
	('PRD10', 'Adidas', 'Ultimate running experience.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-adidas-ultraboost-5x-nu-trang-den-01.jpg', 'Primeknit', 'Vietnam', 19000, 'Adidas Ultraboost 5X', 0.1, 'CAT02', 'SUP02'),
	('PRD11', 'Puma', 'Classic court style.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-puma-caracal-nu-trang-den-01.jpg', 'Leather', 'China', 7000, 'Puma Caracal', 0.1, 'CAT01', 'SUP03'),
	('PRD12', 'Puma', 'Performance running.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-puma-scend-pro-nam-den-cam-01.jpg', 'Mesh', 'Vietnam', 8500, 'Puma Scend Pro', 0.1, 'CAT02', 'SUP03'),
	('PRD13', 'Puma', 'Slip-on comfort.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-puma-softride-astro-slip-on-nam-den-01.jpg', 'Textile', 'Indonesia', 7500, 'Puma Softride Astro', 0.1, 'CAT02', 'SUP03'),
	('PRD14', 'Puma', 'Stylish everyday wear.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/giay-puma-tori-nu-trang-xanh-ngoc-01.jpg', 'Leather', 'Vietnam', 8000, 'Puma Tori', 0.1, 'CAT01', 'SUP03'),
	('PRD15', 'Nike', 'Signature Lebron shoes.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/LEBRON%2BXXIII%2BLUX%2BEP.png', 'Synthetic', 'China', 20000, 'Lebron XXIII LUX EP', 0.1, 'CAT01', 'SUP01'),
	('PRD16', 'New Balance', 'Retro basketball design.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/New+Balance+550.jpg', 'Leather', 'Vietnam', 11500, 'New Balance 550', 0.1, 'CAT01', 'SUP04'),
	('PRD17', 'Nike', 'Training essentials.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/W%2BNIKE%2BFREE%2BMETCON%2B7.png', 'Mesh', 'Vietnam', 12000, 'Nike Free Metcon 7', 0.1, 'CAT01', 'SUP01'),
	('PRD18', 'Nike', 'Advanced training.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/W%2BNIKE%2BMETCON%2B10.png', 'Mesh', 'Vietnam', 13500, 'Nike Metcon 10', 0.1, 'CAT01', 'SUP01'),
	('PRD19', 'Nike', 'Iconic low-top.', 'Female', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/WMNS%2BAIR%2BJORDAN%2B1%2BLOW.png', 'Leather', 'Vietnam', 15500, 'Air Jordan 1 Low', 0.1, 'CAT03', 'SUP01'),
	('PRD20', 'Nike', 'Elite soccer cleats.', 'Male', 'https://liembucket2004.s3.ap-southeast-2.amazonaws.com/products/ZM%2BSUPERFLY%2B11%2BELITE%2BFG%2BT.png', 'Synthetic', 'China', 22000, 'Zoom Superfly 11 Elite', 0.1, 'CAT01', 'SUP01');

-- Dumping structure for table shoeshop.product_detail
CREATE TABLE IF NOT EXISTS `product_detail` (
  `product_detail_id` varchar(255) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `size` int(11) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_detail_id`),
  KEY `FKilxoi77ctyin6jn9robktb16c` (`product_id`),
  CONSTRAINT `FKilxoi77ctyin6jn9robktb16c` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.product_detail: ~10 rows (approximately)
DELETE FROM `product_detail`;
INSERT INTO `product_detail` (`product_detail_id`, `color`, `size`, `stock_quantity`, `product_id`) VALUES
	('PD01', 'Chicago Red', 40, 9, 'PRD01'),
	('PD02', 'Chicago Red', 41, 15, 'PRD01'),
	('PD03', 'Chicago Red', 42, 5, 'PRD01'),
	('PD04', 'White/Black', 39, 20, 'PRD02'),
	('PD05', 'White/Black', 40, 25, 'PRD02'),
	('PD06', 'Core Black', 41, 12, 'PRD03'),
	('PD07', 'Core Black', 42, 18, 'PRD03'),
	('PD08', 'Navy Blue', 38, 30, 'PRD04'),
	('PD09', 'White', 40, 15, 'PRD05'),
	('PD10', 'Black/Red', 41, 20, 'PRD06'),
	('PD11', 'Grey/Blue', 42, 10, 'PRD07'),
	('PD12', 'White', 38, 25, 'PRD08'),
	('PD13', 'Grey/Green', 40, 15, 'PRD09'),
	('PD14', 'White/Black', 39, 30, 'PRD10'),
	('PD15', 'White/Black', 38, 12, 'PRD11'),
	('PD16', 'Black/Orange', 42, 18, 'PRD12'),
	('PD17', 'Black', 41, 22, 'PRD13'),
	('PD18', 'White/Teal', 39, 14, 'PRD14'),
	('PD19', 'Multi', 43, 8, 'PRD15'),
	('PD20', 'White', 41, 35, 'PRD16'),
	('PD21', 'White', 38, 20, 'PRD17'),
	('PD22', 'White', 39, 15, 'PRD18'),
	('PD23', 'Black/White', 38, 25, 'PRD19'),
	('PD24', 'Volt/Black', 42, 5, 'PRD20');

-- Dumping structure for table shoeshop.product_supplier
CREATE TABLE IF NOT EXISTS `product_supplier` (
  `product_id` varchar(255) NOT NULL,
  `supplier_id` varchar(255) NOT NULL,
  KEY `FKojmkj7n4g02l3vj0lf10j7rer` (`supplier_id`),
  KEY `FK9ycab4fchfe9g9uxleti557pv` (`product_id`),
  CONSTRAINT `FK9ycab4fchfe9g9uxleti557pv` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FKojmkj7n4g02l3vj0lf10j7rer` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.product_supplier: ~4 rows (approximately)
DELETE FROM `product_supplier`;
INSERT INTO `product_supplier` (`product_id`, `supplier_id`) VALUES
	('PRD01', 'SUP01'),
	('PRD02', 'SUP01'),
	('PRD03', 'SUP02'),
	('PRD04', 'SUP03');

-- Dumping structure for table shoeshop.promotion
CREATE TABLE IF NOT EXISTS `promotion` (
  `promotion_id` varchar(255) NOT NULL,
  `promo_condition` varchar(255) DEFAULT NULL,
  `discount` double NOT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `staff_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`promotion_id`),
  KEY `FK21of89p7bx0tdg2qskfkdf1ln` (`staff_id`),
  CONSTRAINT `FK21of89p7bx0tdg2qskfkdf1ln` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.promotion: ~0 rows (approximately)
DELETE FROM `promotion`;

-- Dumping structure for table shoeshop.return_order
CREATE TABLE IF NOT EXISTS `return_order` (
  `return_order_id` varchar(255) NOT NULL,
  `refund_amount` double NOT NULL,
  `return_date` datetime(6) DEFAULT NULL,
  `customer_id` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`return_order_id`),
  UNIQUE KEY `UK3091gcfgjipb62rf9x7xw4fws` (`order_id`),
  KEY `FKhgovjsbgbfea8uvjpnrfsteqk` (`customer_id`),
  CONSTRAINT `FKd2m1bv0p2swr9vqsmicjyou4o` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKhgovjsbgbfea8uvjpnrfsteqk` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.return_order: ~0 rows (approximately)
DELETE FROM `return_order`;

-- Dumping structure for table shoeshop.return_order_detail
CREATE TABLE IF NOT EXISTS `return_order_detail` (
  `return_order_detail_id` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `product_detail_id` varchar(255) DEFAULT NULL,
  `return_order_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`return_order_detail_id`),
  KEY `FK17b49pty9dq4yb3nww1clkr1c` (`product_id`),
  KEY `FKnkn2bimx7hbvrl3pjs8r6bidm` (`product_detail_id`),
  KEY `FK1yv8wnq6592pgmwnms00kiicq` (`return_order_id`),
  CONSTRAINT `FK17b49pty9dq4yb3nww1clkr1c` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FK1yv8wnq6592pgmwnms00kiicq` FOREIGN KEY (`return_order_id`) REFERENCES `return_order` (`return_order_id`),
  CONSTRAINT `FKnkn2bimx7hbvrl3pjs8r6bidm` FOREIGN KEY (`product_detail_id`) REFERENCES `product_detail` (`product_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.return_order_detail: ~0 rows (approximately)
DELETE FROM `return_order_detail`;

-- Dumping structure for table shoeshop.staff
CREATE TABLE IF NOT EXISTS `staff` (
  `staff_id` varchar(255) NOT NULL,
  `birth_date` datetime(6) DEFAULT NULL,
  `citizen_id` varchar(255) DEFAULT NULL,
  `department` enum('Administrative','FinanceAccounting','HumanResources','Marketing','Sales','Technical','Warehouse') DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` enum('Female','Male') DEFAULT NULL,
  `img` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `position` enum('CEO','DepartmentManager','DeputyManager','Director','Specialist','Staff') DEFAULT NULL,
  `work_status` enum('Active','Resigned') DEFAULT NULL,
  `account_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `UK4uqyb8awsv3mfncjj737o7oo9` (`account_id`),
  CONSTRAINT `FKs9jl798sgmtrl79dm4svocvaw` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.staff: ~0 rows (approximately)
DELETE FROM `staff`;

-- Dumping structure for table shoeshop.supplier
CREATE TABLE IF NOT EXISTS `supplier` (
  `supplier_id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `supplier_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table shoeshop.supplier: ~3 rows (approximately)
DELETE FROM `supplier`;
INSERT INTO `supplier` (`supplier_id`, `address`, `email`, `phone_number`, `supplier_name`) VALUES
	('SUP01', 'District 1, HCM City', 'contact@nike.vn', '0123456789', 'Nike Vietnam'),
	('SUP02', 'Tan Binh District, HCM City', 'info@adidas.vn', '0987654321', 'Adidas Vietnam'),
	('SUP03', 'Binh Duong Province', 'sales@puma.com', '0555666777', 'Puma Global');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
