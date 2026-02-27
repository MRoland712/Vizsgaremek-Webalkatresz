-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:8889
-- Létrehozás ideje: 2026. Feb 27. 17:05
-- Kiszolgáló verziója: 8.0.44
-- PHP verzió: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `car_parts_shop_fix`
--
CREATE DATABASE IF NOT EXISTS `car_parts_shop_fix` DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci;
USE `car_parts_shop_fix`;

DELIMITER $$
--
-- Eljárások
--
DROP PROCEDURE IF EXISTS `admin_login`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `admin_login` (IN `emailIN` VARCHAR(255))   BEGIN

    -- Update last login
    UPDATE users 
    SET last_login = NOW(),
        updated_at = NOW()
    WHERE email = emailIN
        AND role = 'admin'
        AND is_deleted = 0;
    
    SELECT 
        id, 
        email, 
        username, 
        first_name, 
        last_name, 
        phone, 
        guid, 
        role, 
        is_active, 
        password, 
        registration_token,
        auth_secret
    FROM users
    WHERE email = emailIN
        AND role = 'admin'
        AND is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `checkoutCart`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkoutCart` (IN `userIdIN` INT)   BEGIN
    DECLARE newOrderId INT;
    DECLARE cartItemCount INT;
    DECLARE calculatedTotal DECIMAL(10,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Checkout failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    -- Kosár elemek számának ellenőrzése
    SELECT COUNT(*) INTO cartItemCount
    FROM cart_items
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    IF cartItemCount = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cart is empty';
    END IF;
    
    -- Stock ellenőrzés ZÁROLÁSSAL
    -- ci = cart_items, p = parts (table aliases)
    IF EXISTS (
        SELECT 1 
        FROM cart_items ci
        INNER JOIN parts p ON ci.part_id = p.id
        WHERE ci.user_id = userIdIN 
            AND ci.is_deleted = 0
            AND p.stock < ci.quantity
        FOR UPDATE
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock for some items';
    END IF;
    
    -- Végösszeg számítása
    SELECT SUM(p.price * ci.quantity) INTO calculatedTotal
    FROM cart_items ci
    INNER JOIN parts p ON ci.part_id = p.id
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- 1. Order létrehozása
    INSERT INTO orders (user_id, status, created_at, is_deleted, deleted_at)
    VALUES (userIdIN, 'pending', NOW(), 0, NULL);
    SET newOrderId = LAST_INSERT_ID();
    
    -- 2. Cart items → Order items
    INSERT INTO order_items (order_id, part_id, quantity, price, created_at, is_deleted, deleted_at)
    SELECT newOrderId, ci.part_id, ci.quantity, p.price, NOW(), 0, NULL
    FROM cart_items ci
    INNER JOIN parts p ON ci.part_id = p.id
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- 3. Stock csökkentése
    UPDATE parts p
    INNER JOIN cart_items ci ON p.id = ci.part_id
    SET p.stock = p.stock - ci.quantity,
        p.updated_at = NOW()
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- 4. Stock log rögzítése
    INSERT INTO stock_logs (part_id, change_amount, reason, created_at)
    SELECT ci.part_id, -ci.quantity, CONCAT('Order #', newOrderId), NOW()
    FROM cart_items ci
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- 5. Cart ürítése (soft delete)
    UPDATE cart_items 
    SET is_deleted = 1, deleted_at = NOW()
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    COMMIT;
    
    SELECT newOrderId AS new_order_id, calculatedTotal AS total_amount;
END$$

DROP PROCEDURE IF EXISTS `checkUserTwoFaEnabled`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkUserTwoFaEnabled` (IN `user_IdIN` INT(11))   BEGIN
    SELECT 
        twofa_enabled,
        twofa_secret
    FROM user_twofa
    WHERE user_id = user_IdIN
        AND is_deleted = 0
        AND twofa_enabled = 1;
END$$

DROP PROCEDURE IF EXISTS `createAddress`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createAddress` (IN `p_user_id` INT, IN `p_first_name` VARCHAR(50), IN `p_last_name` VARCHAR(50), IN `p_company` VARCHAR(50), IN `p_tax_number` VARCHAR(50), IN `p_country` VARCHAR(50), IN `p_city` VARCHAR(50), IN `p_zip_code` VARCHAR(20), IN `p_street` VARCHAR(100), IN `p_is_default` TINYINT)   BEGIN
    START TRANSACTION;

    -- Ha ez lesz az alapértelmezett cím akkor a többi címet ne legyen alapértelmezett azt hogy ezt menniyre így kell majd kideritjük
    IF p_is_default = 1 THEN
        UPDATE addresses 
        SET is_default = 0 
        WHERE user_id = p_user_id;
    END IF;


    INSERT INTO addresses (
        user_id,
        first_name,
        last_name,
        company,
        tax_number,
        country,
        city,
        zip_code,
        street,
        is_default,
        created_at,
        updated_at
    ) VALUES (
        p_user_id,
        p_first_name,
        p_last_name,
        p_company,
        p_tax_number,
        p_country,
        p_city,
        p_zip_code,
        p_street,
        IF(p_is_default IS NULL, 0, p_is_default),
        NOW(),
        NOW()
    );

    SELECT LAST_INSERT_ID() AS new_address_id;

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createCars`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createCars` (IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11))   BEGIN
    INSERT INTO cars (
        brand,
        model,
        year_from,
        year_to,
        created_at
    )
    VALUES (
        brandIN,
        modelIN,
        yearFromIN,
        yearToIN,
        NOW()
    );
    
    SELECT LAST_INSERT_ID()AS new_cars_id;
END$$

DROP PROCEDURE IF EXISTS `createCartItems`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createCartItems` (IN `userIdIN` INT(11), IN `partIdIN` INT(11), IN `quantityIN` INT(11))   BEGIN
    INSERT INTO cart_items 
    (
        user_id, 
        part_id, 
        quantity, 
        added_at, 
        is_deleted, 
        deleted_at
    )
    VALUES (
        userIdIN, 
        partIdIN, 
        COALESCE(quantityIN, 1),
        NOW(),
        0,
        NULL
    );
    
    SELECT LAST_INSERT_ID() AS new_cart_item_id;
END$$

DROP PROCEDURE IF EXISTS `createInvoice`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createInvoice` (IN `orderIdIN` INT, IN `pdfUrlIN` VARCHAR(255))   BEGIN
    INSERT INTO invoices (order_id, pdf_url, created_at, is_deleted, deleted_at)
    VALUES (orderIdIN, pdfUrlIN, NOW(), 0, NULL);
    
    SELECT LAST_INSERT_ID() AS new_invoice_id;
END$$

DROP PROCEDURE IF EXISTS `createManufacturers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createManufacturers` (IN `p_name` VARCHAR(100), IN `p_country` VARCHAR(50))   BEGIN
	INSERT INTO manufacturers(
        name,
        country,
        created_at
        )VALUES(
            p_name,
            p_country,
            NOW()
        );
END$$

DROP PROCEDURE IF EXISTS `createMotors`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createMotors` (IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11))   BEGIN
    INSERT INTO motors (
        brand,
        model,
        year_from,
        year_to,
        created_at
    )
    VALUES (
        brandIN,
        modelIN,
        yearFromIN,
        yearToIN,
        NOW()
    );
    
    SELECT LAST_INSERT_ID()AS new_motors_id;
END$$

DROP PROCEDURE IF EXISTS `createOrderFromCart`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrderFromCart` (IN `userIdIN` INT(11))   BEGIN
    DECLARE newOrderId INT;
    DECLARE cartCount INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Order creation failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    -- Van termék a kosárban vagy nincs
    SELECT COUNT(*) INTO cartCount
    FROM cart_items
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    IF cartCount = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cart is empty';
    END IF;
    
    -- Stock ellenőrzés minden termékre
    IF EXISTS (
        SELECT 1 FROM cart_items ci
        INNER JOIN parts p ON p.id = ci.part_id
        WHERE ci.user_id = userIdIN 
          AND ci.is_deleted = 0
          AND p.stock < ci.quantity
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock for one or more items';
    END IF;
    
    -- Order létrehozása
    INSERT INTO orders (user_id, status, created_at, is_deleted, deleted_at)
    VALUES (userIdIN, 'pending', NOW(), 0, NULL);
    SET newOrderId = LAST_INSERT_ID();
    
    -- Cart items → Order items
    INSERT INTO order_items (order_id, part_id, quantity, price, created_at, is_deleted, deleted_at)
    SELECT newOrderId, ci.part_id, ci.quantity, p.price, NOW(), 0, NULL
    FROM cart_items ci
    INNER JOIN parts p ON p.id = ci.part_id
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- Stock csökkentése
    UPDATE parts p
    INNER JOIN cart_items ci ON p.id = ci.part_id
    SET p.stock = p.stock - ci.quantity,
        p.updated_at = NOW()
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- Stock log
    INSERT INTO stock_logs (part_id, change_amount, reason, created_at)
    SELECT ci.part_id, -ci.quantity, CONCAT('Order #', newOrderId), NOW()
    FROM cart_items ci
    WHERE ci.user_id = userIdIN AND ci.is_deleted = 0;
    
    -- Kosár kiürítése
    UPDATE cart_items
    SET is_deleted = 1, deleted_at = NOW()
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    COMMIT;
    
    SELECT newOrderId AS new_order_id;
END$$

DROP PROCEDURE IF EXISTS `createOrderItems`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrderItems` (IN `orderIdIN` INT(11), IN `partIdIN` INT(11), IN `quantityIN` INT(11), IN `priceIN` DECIMAL(10,2))   BEGIN
    
    INSERT INTO order_items(
        order_id,
        part_id,
        quantity,
        price,
        created_at,
        is_deleted,
        deleted_at
    ) VALUES (
        orderIdIN,
        partIdIN,
        quantityIN,
        priceIN,
        NOW(),
        0,
        NULL
    );
       SELECT LAST_INSERT_ID() AS new_orders_id;
END$$

DROP PROCEDURE IF EXISTS `createOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrders` (IN `userIdIN` INT(11), IN `statusIN` VARCHAR(20))   BEGIN
    START TRANSACTION;

    INSERT INTO orders(
        user_id,
        status,
        created_at,
        is_deleted,
        deleted_at
    ) VALUES (
        userIdIN,
        statusIN,
        NOW(),
        0,
        NULL
    );
         SELECT LAST_INSERT_ID() AS new_orders_id;

   COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createPartCompatibility`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createPartCompatibility` (IN `partIdIN` INT(11), IN `vehicleTypeIN` VARCHAR(255), IN `vehicleIdIN` INT(11))   BEGIN

    INSERT INTO part_compatibility(
        part_id,
        vehicle_type,
        vehicle_id,
        created_at
        )VALUES(
            partIdIN,
            vehicleTypeIN,
            vehicleIdIN,    
            NOW()
           );
           
         SELECT LAST_INSERT_ID()AS new_part_compatibility;
         
END$$

DROP PROCEDURE IF EXISTS `createPartImages`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createPartImages` (IN `partIdIN` INT(11), IN `urlIN` VARCHAR(255), IN `isPrimaryIN` TINYINT)   BEGIN
	START TRANSACTION;
    INSERT INTO part_images(
        part_id,
        url,
        is_primary,
        created_at
        )VALUES(
            partIdIN,
            urlIN,
            isPrimaryIN,
            NOW()
           );
           
         SELECT LAST_INSERT_ID()AS new_partImages_id;
         COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createParts` (IN `p_manufacturer_id` INT, IN `p_sku` VARCHAR(255), IN `p_name` VARCHAR(255), IN `p_category` VARCHAR(100), IN `p_price` DECIMAL(10.2), IN `p_stock` INT, IN `p_status` VARCHAR(50), IN `p_is_active` TINYINT)   BEGIN
	START TRANSACTION;
    INSERT INTO parts(
        manufacturer_id,
        sku,
        name,
        category,
        price,
        stock,
        status,
        is_active,
        created_at,
        updated_at
        )VALUES(
            p_manufacturer_id,
            p_sku,
            p_name,
            p_category,
            p_price,
            p_stock,
            p_status,
            p_is_active,
            NOW(),
            NOW()
           );
           
         SELECT LAST_INSERT_ID()AS new_parts_id;
         COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createPartVariants`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createPartVariants` (IN `partIdIN` INT(11), IN `nameIN` VARCHAR(100), IN `valueIN` TEXT, IN `additionalPriceIN` DOUBLE)   BEGIN
	START TRANSACTION;
    INSERT INTO part_variants(
        part_id,
        name,
        value,
        additional_price,
        created_at
        )VALUES(
            partIdIN,
            nameIN,
            valueIN,
            additionalPriceIN,     
            NOW()
           );
           
         SELECT LAST_INSERT_ID()AS new_part_variants;
         COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createPayments`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createPayments` (IN `orderIdIN` INT(11), IN `amountIN` DECIMAL(10,2), IN `methodIN` VARCHAR(50), IN `statusIN` VARCHAR(20), IN `paidAtIN` DATETIME)   BEGIN
    INSERT INTO payments (
        order_id,
        amount,
        method,
        status,
        paid_at,
        created_at,
        is_deleted,
        deleted_at
    )
    VALUES (
        orderIdIN,
        amountIN,
        methodIN,
        COALESCE(statusIN, 'pending'),
        paidAtIN,
        NOW(),
        0,
        NULL
    );
    
    SELECT LAST_INSERT_ID() AS new_payment_id;
END$$

DROP PROCEDURE IF EXISTS `createReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createReviews` (IN `useridIN` INT(11), IN `partidIN` INT(11), IN `ratingIN` INT(11), IN `commentIN` TEXT)   BEGIN

INSERT INTO reviews(
    user_id,
    part_id,
    rating,
    comment,
    created_at,
    is_deleted,
    deleted_at
    )VALUES(
        userIdIN,
        partIdIN,
        ratingIN,
        commentIN,
        NOW(),
        0,
        NULL
        );
     SELECT LAST_INSERT_ID() AS new_review_id;
END$$

DROP PROCEDURE IF EXISTS `createTrucks`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createTrucks` (IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11))   BEGIN
    INSERT INTO trucks (
        brand,
        model,
        year_from,
        year_to,
        created_at
    )
    VALUES (
        brandIN,
        modelIN,
        yearFromIN,
        yearToIN,
        NOW()
    );
    
    SELECT LAST_INSERT_ID()AS new_Trucks_id;
END$$

DROP PROCEDURE IF EXISTS `createUser`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createUser` (IN `p_email` VARCHAR(255), IN `p_username` VARCHAR(255), IN `p_password` VARCHAR(255), IN `p_first_name` VARCHAR(255), IN `p_last_name` VARCHAR(255), IN `p_phone` VARCHAR(50), IN `p_role` VARCHAR(50), IN `p_auth_secret` VARCHAR(255), IN `p_registration_token` VARCHAR(255))   BEGIN
    START TRANSACTION;

    INSERT INTO users (
        guid,
        email,
        username,
        password,
        first_name,
        last_name,
        phone,
        role,
        is_active,
        created_at,
        updated_at,
        auth_secret,
        registration_token
    ) VALUES (
        UUID(),
        p_email,
        p_username,
        p_password,
        p_first_name,
        p_last_name,
        p_phone,
        IF(p_role IS NULL OR p_role ='', 'user', p_role),
        0, -- inaktív, amíg nem erősít
        NOW(),
        NOW(),
        p_auth_secret,
        p_registration_token
    );

    SELECT LAST_INSERT_ID() AS new_user_id;

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createUserLogs`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createUserLogs` (IN `p_user_id` INT, IN `p_action` VARCHAR(255), IN `p_details` TEXT)   BEGIN
    INSERT INTO user_logs (
        user_id,
        action,
        details,
        created_at
    )
    VALUES (
        p_user_id,
        p_action,
        p_details,
        NOW()
    );
END$$

DROP PROCEDURE IF EXISTS `createUserTwoFa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createUserTwoFa` (IN `user_idIN` INT(11), IN `twofa_enabledIN` TINYINT, IN `twofa_secretIN` VARCHAR(255), IN `recovery_codesIN` VARCHAR(1024))   BEGIN
    START TRANSACTION;
    
    INSERT INTO user_twofa (
        user_id,
        twofa_enabled,
        twofa_secret,
        recovery_codes,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    ) VALUES (
        user_idIN,
        COALESCE(twofa_enabledIN, 0),
        twofa_secretIN,
        recovery_codesIN,
        NOW(),
        NOW(),
        0,
        NULL
    );
    
    SELECT LAST_INSERT_ID() AS new_twofa_id;
    
    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createWarehouses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createWarehouses` (IN `nameIN` VARCHAR(50), IN `locationIN` VARCHAR(255))   BEGIN
    INSERT INTO warehouses (
        name,
        location,
        created_at
    ) VALUES (
        nameIN,
        locationIN,
        NOW()
    );
    
    SELECT LAST_INSERT_ID() AS new_warehouse_id;
END$$

DROP PROCEDURE IF EXISTS `getAddressById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAddressById` (IN `p_address_id` INT)   BEGIN
    SELECT 
        id,
        user_id,
        first_name,
        last_name,
        company,
        tax_number,
        country,
        city,
        zip_code,
        street,
        is_default,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM addresses
    WHERE id = p_address_id;
END$$

DROP PROCEDURE IF EXISTS `getAddressesByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAddressesByUserId` (IN `p_user_id` INT)   BEGIN
    SELECT 
        a.id,
        a.user_id,
        a.first_name,
        a.last_name,
        a.company,
        a.tax_number,
        a.country,
        a.city,
        a.zip_code,
        a.street,
        a.is_default,
        a.created_at,
        a.updated_at
    FROM addresses a
    WHERE a.user_id = p_user_id
        AND a.is_deleted = 0
    ORDER BY a.is_default DESC, a.id DESC;
END$$

DROP PROCEDURE IF EXISTS `getAdminByEmail`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAdminByEmail` (IN `emailIN` VARCHAR(255))   BEGIN
    SELECT 
        id, 
        email, 
        username, 
        first_name, 
        last_name, 
        phone, 
        guid, 
        role, 
        is_active, 
        is_subscribed, 
        last_login, 
        created_at, 
        updated_at, 
        password, 
        is_deleted, 
        auth_secret, 
        registration_token
    FROM users 
    WHERE email = emailIN 
        AND role = 'admin'
        AND is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `getAllAddresses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllAddresses` ()   BEGIN
    SELECT 
        a.id,
        a.user_id,
        a.first_name,
        a.last_name,
        a.company,
        a.tax_number,
        a.country,
        a.city,
        a.zip_code,
        a.street,
        a.is_default,
        a.created_at,
        a.updated_at
    FROM addresses a
    WHERE a.is_deleted = 0
    ORDER BY a.id DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllCars`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCars` ()   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
        FROM cars
        WHERE is_deleted = 0
        ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllCartItems`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCartItems` ()   BEGIN
    SELECT
        id,
        user_id,
        part_id,
        quantity,
        added_at,
        is_deleted,
        deleted_at
    FROM cart_items
    WHERE is_deleted = 0
    ORDER BY added_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllInvoices`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllInvoices` ()   BEGIN
    SELECT
        id,
        order_id,
        pdf_url,
        created_at,
        is_deleted,
        deleted_at
    FROM invoices
    WHERE is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllManufacturers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllManufacturers` ()   BEGIN
	SELECT
    id,
    name,
    country,
    created_at,
    is_deleted,
    deleted_at
    FROM manufacturers
    WHERE is_deleted = 0
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllMotors`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllMotors` ()   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
        FROM motors
        WHERE is_deleted = 0
        ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllOrderItems`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllOrderItems` ()   BEGIN
    SELECT
        id,
        order_id,
        part_id,
        quantity,
        price,
        created_at,
        is_deleted,
        deleted_at
    FROM order_items
    WHERE is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllOrderItemsAdmin`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllOrderItemsAdmin` ()   SELECT 
id,
quantity,
price,
created_at,
is_deleted,
deleted_at,
order_id,
part_id
FROM `order_items`$$

DROP PROCEDURE IF EXISTS `getAllOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllOrders` ()   BEGIN
    SELECT
        id,
        user_id,
        status,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
     FROM orders
     WHERE is_deleted = 0
     ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllPartCompatibility`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllPartCompatibility` ()   SELECT
    id,
    part_id,
    vehicle_type,
    vehicle_id,
    created_at,
    updated_at,
    deleted_at,
    is_deleted
FROM part_compatibility
WHERE is_deleted = 0$$

DROP PROCEDURE IF EXISTS `getAllPartImages`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllPartImages` ()   BEGIN
	SELECT
    	id,
        part_id,
        url,
        is_primary,
        created_at,
        is_deleted,
        deleted_at
     FROM part_images
     WHERE is_deleted = 0
     ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllParts` ()   BEGIN
	SELECT
    	id,
        manufacturer_id,
        sku,
        name,
        category,
        price,
        stock,
        status,
        is_active,
        created_at,
        updated_at,
        deleted_at,
        is_deleted
	FROM parts
    WHERE is_deleted = 0
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllPartVariants`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllPartVariants` ()   SELECT id, part_id, name, value, additional_price, created_at, is_deleted, deleted_at
FROM part_variants
WHERE is_deleted = 0$$

DROP PROCEDURE IF EXISTS `getAllPayments`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllPayments` ()   BEGIN
    SELECT
        id,
        order_id,
        amount,
        method,
        status,
        paid_at,
        created_at,
        is_deleted,
        deleted_at
    FROM payments
    WHERE is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllReviews` ()   BEGIN
	SELECT
    	id,
        user_id,
        part_id,
        rating,
        comment,
        created_at,
        is_deleted,
        deleted_at
     FROM reviews
     WHERE is_deleted = 0
     ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllTrucks`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllTrucks` ()   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
        FROM trucks
        WHERE is_deleted = 0
        ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getAllUserTwoFa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllUserTwoFa` ()   BEGIN
    SELECT 
        id,
        user_id,
        twofa_enabled,
        twofa_secret,
        recovery_codes,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM user_twofa
    WHERE is_deleted = 0
    ORDER BY id DESC;
END$$

DROP PROCEDURE IF EXISTS `getAllWarehouses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllWarehouses` ()   Begin
	SELECT 
        id,
        name,
        location,
        created_at,
        is_deleted,
        deleted_at
    FROM warehouses
    WHERE is_deleted = 0
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getCarsByBrand`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCarsByBrand` (IN `brandIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM cars
    WHERE brand = brandIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getCarsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCarsById` (IN `idIN` INT(11))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM cars
    WHERE id = idIN
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getCarsByModel`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCarsByModel` (IN `modelIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM cars
    WHERE model = modelIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getCartItemById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCartItemById` (IN `idIN` INT)   BEGIN
    SELECT
        id,
        user_id,
        part_id,
        quantity,
        added_at,
        is_deleted,
        deleted_at
    FROM cart_items
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getCartItemsByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCartItemsByUserId` (IN `userId` INT(11))   BEGIN
    SELECT
        ci.id,
        ci.user_id,
        ci.part_id,
        ci.quantity,
        ci.added_at,
        ci.is_deleted,
        ci.deleted_at,
        p.name AS part_name,
        p.price AS part_price
    FROM cart_items ci
    LEFT JOIN parts p ON p.id = ci.part_id
    WHERE ci.user_id = userId
      AND ci.is_deleted = 0
    ORDER BY ci.added_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getInvoiceById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getInvoiceById` (IN `idIN` INT)   BEGIN
    SELECT
        id,
        order_id,
        pdf_url,
        created_at,
        is_deleted,
        deleted_at
    FROM invoices
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getManufacturersById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getManufacturersById` (IN `p_manufacturers_id` INT)   BEGIN
    SELECT 
    	id,
        name,
        country,
        created_at,
        is_deleted,
        deleted_at
    FROM manufacturers
    WHERE id = p_manufacturers_id;
END$$

DROP PROCEDURE IF EXISTS `getMotorsByBrand`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getMotorsByBrand` (IN `brandIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM motors
    WHERE brand = brandIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getMotorsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getMotorsById` (IN `idIN` INT(11))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM motors
    WHERE id = idIN
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getMotorsByModel`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getMotorsByModel` (IN `modelIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM motors
    WHERE model = modelIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getOrderItemById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrderItemById` (IN `idIN` INT(11))   BEGIN
    SELECT
        id,
        order_id,
        part_id,
        quantity,
        price,
        created_at,
        is_deleted,
        deleted_at
     FROM order_items
     WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getOrderItemsByOrderId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrderItemsByOrderId` (IN `orderId` INT(11))   BEGIN
    SELECT 
        oi.id,           
        oi.order_id,     
        oi.part_id,      
        oi.quantity,     
        oi.price,        
        oi.created_at,   
        oi.is_deleted,   
        oi.deleted_at,  
        p.name           
    FROM order_items oi
    LEFT JOIN parts p ON p.id = oi.part_id
    WHERE oi.order_id = orderId
      AND oi.is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `getOrderItemsByPartId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrderItemsByPartId` (IN `partId` INT(11))   BEGIN
    SELECT
        id,
        order_id,
        part_id,
        quantity,
        price,
        created_at,
        is_deleted,
        deleted_at
     FROM order_items
     WHERE part_id = partId;
END$$

DROP PROCEDURE IF EXISTS `getOrdersById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrdersById` (IN `idIN` INT(11))   BEGIN
    SELECT
        id,
        user_id,
        status,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
     FROM orders
     WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getOrdersByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrdersByUserId` (IN `idIN` INT(11))   BEGIN
    SELECT
        id,
        user_id,
        status,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
     FROM orders
     WHERE user_id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getPartCompatibilityById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartCompatibilityById` (IN `idIN` INT(11))   SELECT
    id,
    part_id,
    vehicle_type,
    vehicle_id,
    created_at,
    updated_at,
    deleted_at,
    is_deleted
FROM part_compatibility
WHERE id = idIN
ORDER BY id$$

DROP PROCEDURE IF EXISTS `getPartCompatibilityByVehicleType`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartCompatibilityByVehicleType` (IN `vehicleTypeIN` VARCHAR(255))   SELECT 
        id,
        part_id,
        vehicle_type,
        vehicle_id,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM part_compatibility
    WHERE vehicle_type = vehicleTypeIN
        AND is_deleted = 0
    ORDER BY id DESC$$

DROP PROCEDURE IF EXISTS `getPartCompatibiltyByVehicleId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartCompatibiltyByVehicleId` (IN `vehicleidIN` INT(11))   SELECT 
        id,
        part_id,
        vehicle_type,
        vehicle_id,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM part_compatibility
    WHERE vehicle_id = vehicleidIN
        AND is_deleted = 0
    ORDER BY id DESC$$

DROP PROCEDURE IF EXISTS `getPartImagesById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartImagesById` (IN `partImages_id` INT(11))   BEGIN
	SELECT
    	id,
        part_id,
        url,
        is_primary,
        created_at,
        is_deleted,
        deleted_at
     FROM part_images
     WHERE id = partImages_id;
END$$

DROP PROCEDURE IF EXISTS `getPartImagesByPartId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartImagesByPartId` (IN `partIdIN` INT(11))   BEGIN
	SELECT
    	id,
        part_id,
        url,
        is_primary,
        created_at,
        is_deleted,
        deleted_at
     FROM part_images
     WHERE part_id = partIdIN;
END$$

DROP PROCEDURE IF EXISTS `getPartImagesByUrl`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartImagesByUrl` (IN `urlIN` VARCHAR(255))   BEGIN
	SELECT
    	id,
        part_id,
        url,
        is_primary,
        created_at,
        is_deleted,
        deleted_at
     FROM part_images
     WHERE url = urlIN;
END$$

DROP PROCEDURE IF EXISTS `getPartsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsById` (IN `p_parts_id` INT)   BEGIN
    SELECT 
        id,
        manufacturer_id,
        sku,
        name,
        category,
        price,
        stock,
        status,
        is_active,
        created_at,
        updated_at,
        deleted_at
    FROM parts
    WHERE id = p_parts_id;
END$$

DROP PROCEDURE IF EXISTS `getPartsByManufacturerId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsByManufacturerId` (IN `p_manufacturer_id` INT)   BEGIN
    SELECT 
        id,
        manufacturer_id,
        sku,
        name,
        category,
        price,
        stock,
        status,
        is_active,
        created_at,
        updated_at,
        deleted_at
    FROM parts
    WHERE manufacturer_id = p_manufacturer_id
    ORDER BY id DESC;
END$$

DROP PROCEDURE IF EXISTS `getPartsBySku`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsBySku` (IN `p_sku` VARCHAR(100))   BEGIN
    SELECT 
        id,
        manufacturer_id,
        sku,
        name,
        category,
        price,
        stock,
        status,
        is_active,
        created_at,
        updated_at,
        deleted_at
    FROM parts
    WHERE sku = p_sku
    ORDER BY id DESC;
END$$

DROP PROCEDURE IF EXISTS `getPartsByVehicleId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsByVehicleId` (IN `vehicleIdIN` INT(11), IN `vehicleTypeIN` VARCHAR(255))   BEGIN
    SELECT 
        p.id,
        p.manufacturer_id,
        p.sku,
        p.name,
        p.category,
        p.price,
        p.stock,
        p.status,
        p.is_active,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM parts p
    INNER JOIN part_compatibility pc ON pc.part_id = p.id
    WHERE pc.vehicle_id = vehicleIdIN
      AND pc.vehicle_type = vehicleTypeIN
      AND pc.is_deleted = 0
      AND p.is_deleted = 0
    ORDER BY p.id;
END$$

DROP PROCEDURE IF EXISTS `getPartsCategory`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsCategory` ()   BEGIN
    SELECT DISTINCT category
    FROM parts
    WHERE is_deleted = 0
    ORDER BY category;
END$$

DROP PROCEDURE IF EXISTS `getPartVariantsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartVariantsById` (IN `partVariantsId` INT(11))   BEGIN
    SELECT 
        id,
        part_id,
        name,
        value,
        additional_price,
        created_at,
        is_deleted,
        deleted_at
    FROM part_variants
    WHERE id = partVariantsId;
END$$

DROP PROCEDURE IF EXISTS `getPartVariantsByName`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartVariantsByName` (IN `partVariantsName` VARCHAR(100))   BEGIN
    SELECT 
        id,
        part_id,
        name,
        value,
        additional_price,
        created_at,
        is_deleted,
        deleted_at
    FROM part_variants
    WHERE name = partVariantsName;
END$$

DROP PROCEDURE IF EXISTS `getPartVariantsByValue`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartVariantsByValue` (IN `partVariantsValue` VARCHAR(50))   BEGIN
    SELECT 
        id,
        part_id,
        name,
        `value`,
        additional_price,
        created_at,
        is_deleted,
        deleted_at
    FROM part_variants
    WHERE value = partVariantsValue;
END$$

DROP PROCEDURE IF EXISTS `getPaymentById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPaymentById` (IN `idIN` INT)   BEGIN
    SELECT
        id,
        order_id,
        amount,
        method,
        status,
        paid_at,
        created_at,
        is_deleted,
        deleted_at
    FROM payments
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getReviewsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsById` (IN `review_id` INT)   BEGIN
	SELECT
    	id,
        user_id,
        part_id,
        rating,
        comment,
        created_at,
        is_deleted,
        deleted_at
     FROM reviews
     WHERE id = review_id;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByPartId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByPartId` (IN `part_IdIN` INT)   BEGIN
    SELECT 
        id,
        user_id,
        part_id,
        rating,
        comment,
        created_at,
        is_deleted,
        deleted_at
    FROM reviews
    WHERE part_id = part_IdIN
        AND is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByRating`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByRating` (IN `ratingIN` INT)   BEGIN
    SELECT 
        id,
        user_id,
        part_id,
        rating,
        comment,
        created_at
    FROM reviews
    WHERE rating = ratingIN
        AND is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByUserId` (IN `userIdIN` INT)   BEGIN
    SELECT 
        id,
        user_id,
        part_id,
        rating,
        comment,
        created_at,
        is_deleted,
        deleted_at
    FROM reviews
    WHERE user_id = userIdIN
        AND is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getTrucksByBrand`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTrucksByBrand` (IN `brandIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM trucks
    WHERE brand = brandIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getTrucksById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTrucksById` (IN `idIN` INT(11))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM trucks
    WHERE id = idIN
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getTrucksByModel`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTrucksByModel` (IN `modelIN` VARCHAR(100))   BEGIN
	SELECT
        id,
        brand,
        model,
        year_from,
        year_to,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
	FROM trucks
    WHERE model = modelIN
    ORDER BY brand;
END$$

DROP PROCEDURE IF EXISTS `getUserByEmail`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserByEmail` (IN `p_email` VARCHAR(255))   BEGIN
  SELECT id, email, username, first_name, last_name, phone, guid, role, is_active, is_subscribed, last_login, created_at, updated_at, password, is_deleted, auth_secret, registration_token
  FROM users 
  WHERE email = p_email;
END$$

DROP PROCEDURE IF EXISTS `getUserById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `p_user_id` INT)   BEGIN
 SELECT id, email, username, first_name, last_name, phone, guid, role, is_active, is_subscribed, 
 last_login, created_at, updated_at, password, is_deleted, auth_secret, registration_token
  FROM users 
  WHERE id = p_user_id;
END$$

DROP PROCEDURE IF EXISTS `getUserByRegistrationToken`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserByRegistrationToken` (IN `regTokenIN` VARCHAR(255))   BEGIN
  SELECT id, email, username, first_name, last_name, phone, guid, role, is_active, is_subscribed, last_login, created_at, updated_at, password, is_deleted, auth_secret, registration_token
  FROM users 
  WHERE registration_token = regTokenIN
  AND is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `getUsers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUsers` ()   BEGIN
    SELECT id, email, username, first_name, last_name, 
           phone, guid, role,is_active, last_login, created_at, updated_at, is_deleted, is_subscribed
    FROM users
    WHERE is_deleted = 0
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `getUserTwoFaById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserTwoFaById` (IN `idIN` INT(11))   BEGIN
    SELECT 
        id,
        user_id,
        twofa_enabled,
        twofa_secret,
        recovery_codes,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM user_twofa
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `getUserTwoFaByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserTwoFaByUserId` (IN `user_idIN` INT(11))   BEGIN
    SELECT 
        id,
        user_id,
        twofa_enabled,
        twofa_secret,
        recovery_codes,
        created_at,
        updated_at,
        is_deleted,
        deleted_at
    FROM user_twofa
    WHERE user_id = user_idIN
        AND is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `getWarehousesById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getWarehousesById` (IN `idIN` INT(11))   BEGIN
    SELECT 
        id,
        name,
        location,
        created_at,
        is_deleted,
        deleted_at
    FROM warehouses
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `increasePageViewers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `increasePageViewers` (IN `pageNameIN` VARCHAR(255))   BEGIN
    DECLARE pageExists INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Failed to increase page viewers' AS error_message;
    END;
    
    START TRANSACTION;
    
    SELECT COUNT(*) INTO pageExists
    FROM page_statistics
    WHERE pageName = pageNameIN AND is_deleted = 0
    FOR UPDATE;
    
    IF pageExists > 0 THEN
        UPDATE page_statistics
        SET viewersCount = viewersCount + 1,
            updated_at = NOW()
        WHERE pageName = pageNameIN AND is_deleted = 0;
    ELSE
        INSERT INTO page_statistics (pageName, viewersCount, created_at, updated_at, is_deleted, deleted_at)
        VALUES (pageNameIN, 1, NOW(), NOW(), 0, NULL);
    END IF;
    
    COMMIT;
    
    SELECT viewersCount 
    FROM page_statistics 
    WHERE pageName = pageNameIN AND is_deleted = 0;
END$$

DROP PROCEDURE IF EXISTS `processPayment`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `processPayment` (IN `orderIdIN` INT, IN `methodIN` VARCHAR(50))   BEGIN
    DECLARE newPaymentId INT;
    DECLARE newInvoiceId INT;
    DECLARE currentOrderStatus VARCHAR(20);
    DECLARE calculatedAmount DECIMAL(10,2); 
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Payment processing failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    -- Order státusz lekérése ZÁROLÁSSAL
    SELECT status INTO currentOrderStatus 
    FROM orders 
    WHERE id = orderIdIN AND is_deleted = 0
    FOR UPDATE;
    
    -- Validációk
    IF currentOrderStatus IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order not found';
    END IF;
    
    IF currentOrderStatus != 'pending' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order already processed';
    END IF;
    
    -- Összeg kiszámítása az order_items alapján  
    SELECT SUM(oi.price * oi.quantity) INTO calculatedAmount
    FROM order_items oi
    WHERE oi.order_id = orderIdIN AND oi.is_deleted = 0;
    
    IF calculatedAmount IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No order items found';
    END IF;
    
    -- Payment rögzítése
    INSERT INTO payments (order_id, amount, method, status, paid_at, created_at, is_deleted, deleted_at)
    VALUES (orderIdIN, calculatedAmount, methodIN, 'completed', NOW(), NOW(), 0, NULL);  -- calculatedAmount
    SET newPaymentId = LAST_INSERT_ID();
    
    -- Order státusz frissítése
    UPDATE orders 
    SET status = 'paid'
    WHERE id = orderIdIN;
    
    -- Invoice létrehozása
    INSERT INTO invoices (order_id, created_at, is_deleted, deleted_at)
    VALUES (orderIdIN, NOW(), 0, NULL);
    SET newInvoiceId = LAST_INSERT_ID();
    
    COMMIT;
    
    SELECT newPaymentId AS new_payment_id, newInvoiceId AS new_invoice_id;
END$$

DROP PROCEDURE IF EXISTS `processRefund`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `processRefund` (IN `paymentIdIN` INT, IN `amountIN` DECIMAL(10,2), IN `reasonIN` VARCHAR(255))   BEGIN
    DECLARE relatedOrderId INT;
    DECLARE newRefundId INT;
    DECLARE currentPaymentStatus VARCHAR(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Refund processing failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    -- Payment és Order ID lekérése ZÁROLÁSSAL
    SELECT order_id, status INTO relatedOrderId, currentPaymentStatus
    FROM payments 
    WHERE id = paymentIdIN AND is_deleted = 0
    FOR UPDATE;
    
    -- Validációk
    IF relatedOrderId IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payment not found';
    END IF;
    
    IF currentPaymentStatus = 'refunded' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payment already refunded';
    END IF;
    
    -- 1. Refund rögzítése
    INSERT INTO refunds (payment_id, amount, reason, refunded_at, is_deleted, deleted_at)
    VALUES (paymentIdIN, amountIN, reasonIN, NOW(), 0, NULL);
    SET newRefundId = LAST_INSERT_ID();
    
    -- 2. Payment státusz frissítése
    UPDATE payments 
    SET status = 'refunded'
    WHERE id = paymentIdIN;
    
    -- 3. Order státusz frissítése
    UPDATE orders 
    SET status = 'refunded'
    WHERE id = relatedOrderId;
    
    -- 4. Stock visszaállítása
    UPDATE parts p
    INNER JOIN order_items oi ON p.id = oi.part_id
    SET p.stock = p.stock + oi.quantity,
        p.updated_at = NOW()
    WHERE oi.order_id = relatedOrderId AND oi.is_deleted = 0;
    
    -- 5. Stock log rögzítése
    INSERT INTO stock_logs (part_id, change_amount, reason, created_at)
    SELECT oi.part_id, oi.quantity, CONCAT('Refund #', newRefundId), NOW()
    FROM order_items oi
    WHERE oi.order_id = relatedOrderId AND oi.is_deleted = 0;
    
    COMMIT;
    
    SELECT newRefundId AS new_refund_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteAddress`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteAddress` (IN `p_address_id` INT)   BEGIN
    UPDATE addresses
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_address_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteCars`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteCars` (IN `idIN` INT(11))   BEGIN
    UPDATE cars
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteCartItem`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteCartItem` (IN `idIN` INT)   BEGIN
    UPDATE cart_items
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteInvoice`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteInvoice` (IN `idIN` INT)   BEGIN
    UPDATE invoices
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteManufacturers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteManufacturers` (IN `p_manufacturers_id` INT)   BEGIN
    UPDATE manufacturers
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_manufacturers_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteMotors`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteMotors` (IN `idIN` INT(11))   BEGIN
    UPDATE motors
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteOrderItem`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteOrderItem` (IN `idIN` INT(11))   BEGIN
    UPDATE order_items
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteOrders` (IN `idIN` INT(11))   BEGIN
    UPDATE orders
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeletePartCompatibility`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeletePartCompatibility` (IN `idIN` INT(11))   BEGIN
    UPDATE part_compatibility
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeletePartComplete`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeletePartComplete` (IN `partIdIN` INT)   BEGIN
    DECLARE partExists INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Part deletion failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    -- Part létezésének ellenőrzése
    SELECT COUNT(*) INTO partExists
    FROM parts
    WHERE id = partIdIN AND is_deleted = 0;
    
    IF partExists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Part not found or already deleted';
    END IF;
    
    -- 1. Part törlése
    UPDATE parts 
    SET is_deleted = 1, deleted_at = NOW()
    WHERE id = partIdIN;
    
    -- 2. Part Images törlése
    UPDATE part_images 
    SET is_deleted = 1, deleted_at = NOW()
    WHERE part_id = partIdIN AND is_deleted = 0;
    
    -- 3. Part Variants törlése
    UPDATE part_variants 
    SET is_deleted = 1, deleted_at = NOW()
    WHERE part_id = partIdIN AND is_deleted = 0;
    
    -- 4. Reviews törlése
    UPDATE reviews
    SET is_deleted = 1, deleted_at = NOW()
    WHERE part_id = partIdIN AND is_deleted = 0;
    
    COMMIT;
    
    SELECT partIdIN AS deleted_part_id;
END$$

DROP PROCEDURE IF EXISTS `softDeletePartImages`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeletePartImages` (IN `partImages_IdIN` INT(11))   BEGIN
    UPDATE part_images
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = partImages_IdIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteParts` (IN `p_parts_id` INT)   BEGIN
    UPDATE parts
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_parts_id;
END$$

DROP PROCEDURE IF EXISTS `softDeletePartVariants`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeletePartVariants` (IN `partVaraintsId` INT(11))   BEGIN
    UPDATE part_variants
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = partVaraintsId;
END$$

DROP PROCEDURE IF EXISTS `softDeletePayment`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeletePayment` (IN `idIN` INT)   BEGIN
    UPDATE payments
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteReviews` (IN `review_IdIN` INT(11))   BEGIN
    UPDATE reviews
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = review_IdIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteTrucks`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteTrucks` (IN `idIN` INT(11))   BEGIN
    UPDATE trucks
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteUser`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteUser` (IN `p_user_id` INT)   BEGIN
    UPDATE users
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_user_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteUserAndAddresses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteUserAndAddresses` (IN `userIdIN` INT)   BEGIN
    DECLARE userExists INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: User deletion failed' AS error_message;
    END;
    
    START TRANSACTION;
    
    SELECT COUNT(*) INTO userExists
    FROM users
    WHERE id = userIdIN AND is_deleted = 0;
    
    IF userExists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User not found or already deleted';
    END IF;
    
    UPDATE users
    SET is_deleted = 1, deleted_at = NOW()
    WHERE id = userIdIN;
    
    UPDATE addresses
    SET is_deleted = 1, deleted_at = NOW()
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    UPDATE cart_items
    SET is_deleted = 1, deleted_at = NOW()
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    UPDATE user_twofa
    SET is_deleted = 1, deleted_at = NOW()
    WHERE user_id = userIdIN AND is_deleted = 0;
    
    COMMIT;
    
    SELECT userIdIN AS deleted_user_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteUserTwoFa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteUserTwoFa` (IN `twofa_idIN` INT(11))   BEGIN
    UPDATE user_twofa
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = twofa_idIN;
END$$

DROP PROCEDURE IF EXISTS `softDeleteWarehouses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteWarehouses` (IN `idIN` INT(11))   BEGIN
    UPDATE warehouses
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateAddress`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateAddress` (IN `p_address_id` INT(11), IN `p_user_id` INT(11), IN `p_first_name` VARCHAR(50), IN `p_last_name` VARCHAR(50), IN `p_company` VARCHAR(50), IN `p_tax_number` VARCHAR(50), IN `p_country` VARCHAR(50), IN `p_city` VARCHAR(50), IN `p_zip_code` VARCHAR(20), IN `p_street` VARCHAR(100), IN `p_is_default` TINYINT, IN `p_is_deleted` TINYINT)   BEGIN
    DECLARE v_user_id INT;
    
    START TRANSACTION;

    SELECT user_id INTO v_user_id 
    FROM addresses 
    WHERE id = p_address_id;

    IF p_is_default = 1 THEN
        UPDATE addresses 
        SET is_default = 0 
        WHERE user_id = v_user_id 
            AND id != p_address_id;
    END IF;

    UPDATE addresses
    SET 
        first_name = p_first_name,
        last_name = p_last_name,
        company = p_company,
        tax_number = p_tax_number,
        country = p_country,
        city = p_city,
        zip_code = p_zip_code,
        street = p_street,
        is_default = p_is_default,
        is_deleted = p_is_deleted,
        updated_at = NOW()
    WHERE id = p_address_id;

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updateCars`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCars` (IN `idIN` INT(11), IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11), IN `isDeleted` TINYINT)   BEGIN

    UPDATE cars
    SET 
        brand = brandIN,
        model = modelIN,
        year_from = yearFromIN,
        year_to = yearToIN,
        updated_at = NOW(),
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `updateCartItem`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCartItem` (IN `idIN` INT, IN `userIdIN` INT, IN `partIdIN` INT, IN `quantityIN` INT, IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE cart_items
    SET 
        user_id = userIdIN,
        part_id = partIdIN,
        quantity = quantityIN,
        is_deleted = isDeletedIN,
        deleted_at = IF(isDeletedIN = 1, NOW(), NULL)
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateInvoice`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateInvoice` (IN `idIN` INT, IN `orderIdIN` INT, IN `pdfUrlIN` VARCHAR(255), IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE invoices
    SET 
        order_id = orderIdIN,
        pdf_url = pdfUrlIN,
        is_deleted = isDeletedIN,
        deleted_at = IF(isDeletedIN = 1, NOW(), NULL)
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateInvoicePdfUrl`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateInvoicePdfUrl` (IN `orderIdIN` INT, IN `pdfUrlIN` VARCHAR(500))   BEGIN
    UPDATE invoices
    SET pdf_url = pdfUrlIN
    WHERE order_id = orderIdIN;
END$$

DROP PROCEDURE IF EXISTS `updateManufacturers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateManufacturers` (IN `p_manufacturers_id` INT, IN `p_name` VARCHAR(255), IN `p_country` VARCHAR(100))   BEGIN
  START TRANSACTION;
    UPDATE manufacturers
    SET name = p_name,
        country = p_country
    WHERE id = p_manufacturers_id;
  COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updateMotors`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateMotors` (IN `idIN` INT(11), IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11), IN `isDeleted` TINYINT)   BEGIN

    UPDATE motors
    SET 
        brand = brandIN,
        model = modelIN,
        year_from = yearFromIN,
        year_to = yearToIN,
        updated_at = NOW(),
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `updateOrderItem`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateOrderItem` (IN `idIN` INT(11), IN `orderIdIN` INT(11), IN `partIdIN` INT(11), IN `quantityIN` INT(11), IN `priceIN` DECIMAL(10,2), IN `isDeleted` TINYINT)   BEGIN
    UPDATE order_items
    SET 
        id = idIN,
        order_id = orderIdIN,
        part_id = partIdIN,
        quantity = quantityIN,
        price = priceIN,
        is_deleted = isDeleted,
        deleted_at = IF(isDeleted = 1, NOW(), NULL)
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateOrders` (IN `idIN` INT(11), IN `statusIN` VARCHAR(20), IN `isDeleted` TINYINT)   BEGIN

    UPDATE orders
    SET 
        status = statusIN,
        updated_at = NOW(),
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `updatePartCompatibility`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updatePartCompatibility` (IN `idIN` INT(11), IN `partIdIN` INT(11), IN `vehicleTypeIN` VARCHAR(255), IN `vehicleIdIN` INT(11), IN `isDeleted` TINYINT)   BEGIN

    UPDATE part_compatibility
    SET 
        part_id = partIdIN,
        vehicle_type = vehicleTypeIN,
        vehicle_id = vehicleIdIN,
        updated_at = NOW(),
        deleted_at = IF(isDeleted = 1, NOW(), NULL),
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `updatePartImages`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updatePartImages` (IN `IdIN` INT(11), IN `partIdIN` INT(11), IN `urlIN` VARCHAR(255), IN `isPrimaryIN` TINYINT, IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE part_images
    SET 
    	part_id = partIdIN,
        url = urlIN,
        is_primary = isPrimaryIN,
        is_deleted = isDeletedIN,
        deleted_at = IF(isDeletedIN = 1, NOW(), NULL)
    WHERE id = IdIN;
END$$

DROP PROCEDURE IF EXISTS `updateParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateParts` (IN `p_part_id` INT(11), IN `p_manufacturer_id` INT(11), IN `p_sku` VARCHAR(100), IN `p_name` VARCHAR(255), IN `p_category` VARCHAR(100), IN `p_price` DECIMAL(10,2), IN `p_stock` INT(11), IN `p_status` VARCHAR(20), IN `p_is_active` TINYINT, IN `p_is_deleted` TINYINT)   BEGIN
    START TRANSACTION;
    
    UPDATE parts
    SET 
        manufacturer_id = p_manufacturer_id,
        sku = p_sku,
        name = p_name,
        category = p_category,
        price = p_price,
        stock = p_stock,
        status = p_status,
        is_active = p_is_active,
        is_deleted = p_is_deleted,
        updated_at = NOW()
    WHERE id = p_part_id;
    
    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updatePartVariants`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updatePartVariants` (IN `idIN` INT(11), IN `partIdIN` INT(11), IN `nameIN` VARCHAR(100), IN `valueIN` VARCHAR(100), IN `additionalPriceIN` DOUBLE, IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE part_variants
    SET 
    	id = idIN,
        part_id = partIdIN,
        name = nameIN,
        `value` = valueIN,
        additional_price = additionalPriceIN,
        is_deleted = isDeletedIN
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updatePayment`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updatePayment` (IN `idIN` INT, IN `orderIdIN` INT, IN `amountIN` DECIMAL(10,2), IN `methodIN` VARCHAR(50), IN `statusIN` VARCHAR(20), IN `paidAtIN` DATETIME, IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE payments
    SET 
        order_id = orderIdIN,
        amount = amountIN,
        method = methodIN,
        status = statusIN,
        paid_at = paidAtIN,
        is_deleted = isDeletedIN,
        deleted_at = IF(isDeletedIN = 1, NOW(), NULL)
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateReviews` (IN `review_id` INT, IN `ratingIN` INT, IN `commentIN` TEXT, IN `isDeletedIN` TINYINT)   BEGIN
    UPDATE reviews
    SET 
        rating = ratingIN,
        comment = commentIN,
        is_deleted = isDeletedIN
    WHERE id = review_id;
END$$

DROP PROCEDURE IF EXISTS `updateTrucks`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateTrucks` (IN `idIN` INT(11), IN `brandIN` VARCHAR(100), IN `modelIN` VARCHAR(100), IN `yearFromIN` INT(11), IN `yearToIN` INT(11), IN `isDeleted` TINYINT)   BEGIN

    UPDATE trucks
    SET 
        brand = brandIN,
        model = modelIN,
        year_from = yearFromIN,
        year_to = yearToIN,
        updated_at = NOW(),
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `updateUser`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUser` (IN `p_user_id` INT, IN `p_email` VARCHAR(255), IN `p_username` VARCHAR(100), IN `p_first_name` VARCHAR(100), IN `p_last_name` VARCHAR(100), IN `p_phone` VARCHAR(50), IN `p_role` VARCHAR(50), IN `p_is_active` TINYINT, IN `p_password` VARCHAR(255), IN `p_registration_token` VARCHAR(255), IN `p_auth_secret` INT(255), IN `P_is_subscibed` INT)   BEGIN
  START TRANSACTION;
    UPDATE users
    SET email = p_email,
        username = p_username,
        first_name = p_first_name,
        last_name = p_last_name,
        phone = p_phone,
        role = COALESCE(p_role, role),
        is_active = p_is_active,
        is_subscribed = p_is_subscibed,
        updated_at = NOW(),
        password = p_password,
        registration_token = p_registration_token,  
        auth_secret = p_auth_secret
    WHERE id = p_user_id OR email = p_email;
  COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updateUserLogs`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUserLogs` (IN `p_user_id` INT, IN `p_action` VARCHAR(255), IN `p_details` TEXT)   BEGIN
    UPDATE user_logs
    SET
        action = p_action,
        details = p_details,
        created_at = NOW()
    WHERE user_id = p_user_id
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `updateUserTwoFa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUserTwoFa` (IN `idIN` INT(11), IN `user_IdIN` INT(11), IN `twofa_enabledIN` TINYINT, IN `twofa_secretIN` VARCHAR(255), IN `recovery_codesIN` VARCHAR(1024))   BEGIN
    START TRANSACTION;
    
    UPDATE user_twofa
    SET 
        user_id = user_IdIN,
        twofa_enabled = twofa_enabledIN,
        twofa_secret = twofa_secretIN,
        recovery_codes = recovery_codesIN,
        updated_at = NOW()
    WHERE id = idIN;
    
    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updateWarehouses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateWarehouses` (IN `idIN` INT(11))   BEGIN

    UPDATE warehouses
    SET 
    	name = nameIN,
        location = locationIN,
        is_deleted = isDeleted
    WHERE id = idIN;

END$$

DROP PROCEDURE IF EXISTS `user_login`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `user_login` (IN `p_email` VARCHAR(50))   BEGIN
    -- Update last login
    UPDATE users 
    SET last_login = NOW(),
        updated_at = NOW()
    WHERE email = p_email;
    
    -- Return user data
    SELECT id, email, username, first_name, last_name, 
           phone, guid, role, is_active, password, registration_token
    FROM users
    WHERE email = p_email;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `addresses`
--

DROP TABLE IF EXISTS `addresses`;
CREATE TABLE IF NOT EXISTS `addresses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `company` varchar(50) DEFAULT NULL,
  `tax_number` varchar(50) DEFAULT NULL,
  `country` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `zip_code` varchar(20) NOT NULL,
  `street` varchar(100) NOT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cars`
--

DROP TABLE IF EXISTS `cars`;
CREATE TABLE IF NOT EXISTS `cars` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int DEFAULT NULL,
  `year_to` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `part_id` int NOT NULL,
  `quantity` int DEFAULT '1',
  `added_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `cart_items`
--

INSERT INTO `cart_items` (`id`, `user_id`, `part_id`, `quantity`, `added_at`, `is_deleted`, `deleted_at`) VALUES
(1, 6, 6, 10, '2026-02-27 10:16:32', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `email_verifications`
--

DROP TABLE IF EXISTS `email_verifications`;
CREATE TABLE IF NOT EXISTS `email_verifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `verified` tinyint(1) DEFAULT '0',
  `sent_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `verified_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `pdf_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `invoices_ibfk_1` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `invoices`
--

INSERT INTO `invoices` (`id`, `order_id`, `pdf_url`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 3, NULL, '2026-02-23 13:53:41', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `login_logs`
--

DROP TABLE IF EXISTS `login_logs`;
CREATE TABLE IF NOT EXISTS `login_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `logged_in_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `logged_in_at` (`logged_in_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `manufacturers`
--

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE IF NOT EXISTS `manufacturers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `country` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `manufacturers`
--

INSERT INTO `manufacturers` (`id`, `name`, `country`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Bosch', 'Magyarország', '2026-01-30 10:27:49', 0, NULL),
(2, 'Abakus', 'Lengyelország', '2026-02-02 13:01:48', 0, NULL),
(3, 'Alkar', 'Spanyolország', '2026-02-02 13:03:00', 0, NULL),
(4, 'Alutec', 'Anglia', '2026-02-02 13:03:56', 0, NULL),
(5, 'ASPL', 'Lengyelország', '2026-02-02 13:04:18', 0, NULL),
(6, 'ATS', 'Kanada', '2026-02-02 13:04:41', 0, NULL),
(7, 'Bilstein', 'Németország', '2026-02-02 13:05:06', 0, NULL),
(8, 'BlueResponse', 'Németország', '2026-02-02 13:05:34', 0, NULL),
(9, 'BMK', 'Németország', '2026-02-02 13:05:51', 0, NULL),
(10, 'BorgWarner', 'Amerika', '2026-02-02 13:06:11', 0, NULL),
(11, 'Brembo', 'Olaszország', '2026-02-02 13:06:44', 0, NULL),
(12, 'BridgeStone', 'Japán', '2026-02-02 13:10:58', 0, NULL),
(13, 'BTS', 'Törökország', '2026-02-02 13:12:03', 0, NULL),
(14, 'Castrol', 'Anglia', '2026-02-02 13:12:35', 0, NULL),
(15, 'ContiTech', 'Németország', '2026-02-02 13:13:45', 0, NULL),
(16, 'Daco', 'Németország', '2026-02-02 13:14:04', 0, NULL),
(17, 'Febi', 'Németország', '2026-02-02 13:14:14', 0, NULL),
(18, 'Elf', 'Franciaország', '2026-02-02 13:14:58', 0, NULL),
(19, 'Fast', 'Olaszország', '2026-02-02 13:15:15', 0, NULL),
(20, 'Filtron', 'Lengyelország', '2026-02-02 13:15:40', 0, NULL),
(21, 'Garrett', 'Svájc', '2026-02-02 13:16:07', 0, NULL),
(22, 'GtBergmann', 'Németország', '2026-02-02 13:16:28', 0, NULL),
(23, 'Hella', 'Németország', '2026-02-02 13:16:57', 0, NULL),
(24, 'Hengst', 'Németország', '2026-02-02 13:17:06', 0, NULL),
(25, 'Hepu', 'Németország', '2026-02-02 13:17:17', 0, NULL),
(26, 'Imperial', 'Amerika', '2026-02-02 13:17:51', 0, NULL),
(27, 'INA', 'Németország', '2026-02-02 13:18:05', 0, NULL),
(28, 'Izawit', 'Lengyelország', '2026-02-02 13:18:20', 0, NULL),
(29, 'JapanParts', 'Japán', '2026-02-02 13:18:48', 0, NULL),
(30, 'JMJ', 'Franciaország', '2026-02-02 13:18:59', 0, NULL),
(31, 'KESKIN', 'Németország', '2026-02-02 13:19:09', 0, NULL),
(32, 'KN', 'Anglia', '2026-02-02 13:19:17', 0, NULL),
(33, 'KYB', 'Japán', '2026-02-02 13:19:25', 0, NULL),
(34, 'Lpr', 'Wales', '2026-02-02 13:20:22', 0, NULL),
(35, 'Lemförder', 'Németország', '2026-02-02 13:20:38', 0, NULL),
(36, 'Lucas', 'Amerika', '2026-02-02 13:21:54', 0, NULL),
(37, 'LUK', 'Németország', '2026-02-02 13:22:21', 0, NULL),
(38, 'Mann', 'Amerika', '2026-02-02 13:23:20', 0, NULL),
(39, 'Mannol', 'Németország', '2026-02-02 13:23:35', 0, NULL),
(40, 'Mapco', 'Németország', '2026-02-02 13:23:47', 0, NULL),
(41, 'MasterSport', 'Németország', '2026-02-02 13:24:13', 0, NULL),
(42, 'Maxxis', 'Amerika', '2026-02-02 13:25:34', 0, NULL),
(43, 'Minerva', 'Belgium', '2026-02-02 13:25:53', 0, NULL),
(44, 'Monroe', 'Amerika', '2026-02-02 13:26:03', 0, NULL),
(45, 'Nankang', 'Taiwan', '2026-02-02 13:26:16', 0, NULL),
(46, 'NTY', 'Németország', '2026-02-02 13:26:25', 0, NULL),
(47, 'SACHS', 'Németország', '2026-02-02 13:26:48', 0, NULL),
(48, 'Shell', 'Anglia', '2026-02-02 13:27:01', 0, NULL),
(49, 'SKF', 'Svédország', '2026-02-02 13:27:21', 0, NULL),
(50, 'STARDAX', 'Lengyelország', '2026-02-02 13:27:41', 0, NULL),
(51, 'STAR', 'Amerika', '2026-02-02 13:28:25', 0, NULL),
(52, 'Superia', 'Kína', '2026-02-02 13:29:06', 0, NULL),
(53, 'TBB', 'Amerika', '2026-02-02 13:29:19', 0, NULL),
(54, 'Textar', 'Németország', '2026-02-02 13:29:36', 0, NULL),
(55, 'Topran', 'Németország', '2026-02-02 13:30:21', 0, NULL),
(56, 'Tristar', 'Németország', '2026-02-02 13:30:55', 0, NULL),
(57, 'TRW', 'Amerika', '2026-02-02 13:31:14', 0, NULL),
(58, 'TYC', 'Taiwan', '2026-02-02 13:31:24', 0, NULL),
(59, 'VAG', 'Németország', '2026-02-02 13:31:43', 0, NULL),
(60, 'Vaico', 'Németország', '2026-02-02 13:31:55', 0, NULL),
(61, 'VARTA', 'Németország', '2026-02-02 13:32:02', 0, NULL),
(62, 'Yuasa', 'Japán', '2026-02-02 13:32:12', 0, NULL),
(63, 'Zimmermann', 'Németország', '2026-02-02 13:32:21', 0, NULL),
(64, 'BROCK', 'Amerika', '2026-02-03 10:28:17', 0, NULL),
(65, 'DOTZ', 'Amerika', '2026-02-03 10:33:33', 0, NULL),
(66, 'SNR', 'Németország', '2026-02-06 15:10:45', 0, NULL),
(67, 'Meyle', 'Amerika', '2026-02-06 15:32:37', 0, NULL),
(68, 'Automega', 'Amerika', '2026-02-06 15:52:34', 0, NULL),
(69, 'Comma Oil', 'Anglia', '2026-02-09 11:54:47', 0, NULL),
(70, 'LIQUI MOLY', 'Németország', '2026-02-09 11:55:15', 0, NULL),
(71, 'Denso', 'Japán', '2026-02-09 12:49:26', 0, NULL),
(72, 'BM CATALYSTS', 'Anglia', '2026-02-09 12:55:40', 0, NULL),
(73, 'DR MOTOR', 'Lengyelország', '2026-02-09 13:00:24', 0, NULL),
(74, 'AutoLog', 'Kanada', '2026-02-09 13:03:34', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `motors`
--

DROP TABLE IF EXISTS `motors`;
CREATE TABLE IF NOT EXISTS `motors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int DEFAULT NULL,
  `year_to` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `status` enum('pending','paid','shipped','delivered','cancelled','refunded') DEFAULT 'pending',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `status`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 3, 'pending', '2026-02-13 13:49:39', '2026-02-16 17:42:47', 0, NULL),
(2, 4, 'delivered', '2026-02-14 10:14:33', '2026-02-14 10:14:33', 0, NULL),
(3, 3, 'paid', '2026-02-17 09:52:29', '2026-02-23 13:53:41', 0, NULL),
(4, 4, 'pending', '2026-02-17 13:57:21', '2026-02-17 13:57:21', 0, NULL),
(5, 10, 'pending', '2026-02-17 13:57:30', '2026-02-17 13:57:30', 0, NULL),
(6, 6, 'pending', '2026-02-17 13:57:38', '2026-02-17 13:57:38', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_items`
--

DROP TABLE IF EXISTS `order_items`;
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `part_id` int NOT NULL,
  `quantity` int DEFAULT '1',
  `price` decimal(10,2) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `part_id`, `quantity`, `price`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 3, 2, 2, 22679.00, '2026-02-17 09:52:29', 1, NULL),
(2, 4, 5, 10, 12100.00, '2026-02-17 13:57:21', 0, NULL),
(3, 5, 8, 7, 49957.00, '2026-02-17 13:57:30', 0, NULL),
(4, 6, 16, 3, 14300.00, '2026-02-17 13:57:38', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_logs`
--

DROP TABLE IF EXISTS `order_logs`;
CREATE TABLE IF NOT EXISTS `order_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `old_status` varchar(20) DEFAULT NULL,
  `new_status` varchar(20) DEFAULT NULL,
  `changed_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `page_statistics`
--

DROP TABLE IF EXISTS `page_statistics`;
CREATE TABLE IF NOT EXISTS `page_statistics` (
  `id` int NOT NULL,
  `pageName` varchar(255) NOT NULL,
  `viewersCount` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `parts`
--

DROP TABLE IF EXISTS `parts`;
CREATE TABLE IF NOT EXISTS `parts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manufacturer_id` int NOT NULL,
  `sku` varchar(100) NOT NULL,
  `name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int DEFAULT '0',
  `status` varchar(20) DEFAULT 'available',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku` (`sku`),
  KEY `manufacturer_id` (`manufacturer_id`),
  KEY `category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `parts`
--

INSERT INTO `parts` (`id`, `manufacturer_id`, `sku`, `name`, `category`, `price`, `stock`, `status`, `is_active`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`) VALUES
(1, 42, 'ALLSEASON-TIRES-001', 'Maxxis Premitra All Season 185/65 R15 92H XL M+S négyévszakos gumi', 'Gumik és felnik', 15930.00, 24, 'available', 1, '2026-02-02 14:04:32', '2026-02-17 11:24:00', NULL, 0),
(2, 56, 'ALLSEASON-TIRES-002', 'Tristar All Season Power 215/45 R17 91W M+S négyévszakos gumi', 'Gumik és felnik', 22679.00, 18, 'available', 1, '2026-02-02 14:06:18', '2026-02-17 09:52:29', NULL, 0),
(3, 8, 'ALLSEASON-TIRES-003', 'Dunlop Sport Bluresponse 225/50 R17 98W XL', 'Gumik és felnik', 46120.00, 42, 'available', 1, '2026-02-02 14:10:27', '2026-02-05 12:47:42', NULL, 0),
(4, 12, 'ALLSEASON-TIRES-004', 'Bridgestone Blizzak LM005 185/65 R15 88T M+S ', 'Gumik és felnik', 36700.00, 52, 'available', 1, '2026-02-02 14:11:38', '2026-02-05 12:49:16', NULL, 0),
(5, 4, 'RIM-TIRES-001', 'ALUTEC Tormenta 19 col ezüst alufelni', 'Gumik és felnik', 12100.00, 18, 'available', 1, '2026-02-02 14:13:30', '2026-02-17 13:57:21', NULL, 0),
(6, 64, 'RIM-TIRES-002', 'BROCK B41 20 col fekete alufeln', 'Gumik és felnik', 159332.00, 16, 'available', 1, '2026-02-03 10:29:17', '2026-02-03 10:29:17', NULL, 0),
(7, 31, 'RIM-TIRES-003', 'KESKIN WHEELS KT17 Hurricane 18 col matt fekete alufeln', 'Gumik és felnik', 115133.00, 8, 'available', 1, '2026-02-03 10:30:08', '2026-02-03 10:30:08', NULL, 0),
(8, 65, 'RIM-TIRES-004', 'DOTZ Mugello 15 col matt fekete front polírozott alufeln', 'Gumik és felnik', 49957.00, 25, 'available', 1, '2026-02-03 10:33:54', '2026-02-17 13:57:30', NULL, 0),
(9, 6, 'RIM-TIRES-005', 'ATS Mizar 19 col fekete alufeln', 'Gumik és felnik', 147566.00, 22, 'available', 1, '2026-02-03 10:34:57', '2026-02-03 10:34:57', NULL, 0),
(10, 53, 'SUMMER-TIRES-001', 'TBB Tires Fortezza 175/70 R14 84T nyárigumi', 'Gumik és felnik', 12782.00, 67, 'available', 1, '2026-02-03 10:40:46', '2026-02-03 10:40:46', NULL, 0),
(11, 56, 'SUMMER-TIRES-002', 'Tristar Ecopower 3 145/70 R12 69T nyárigumi', 'Gumik és felnik', 13808.00, 12, 'available', 1, '2026-02-03 11:10:13', '2026-02-03 11:10:13', NULL, 0),
(12, 56, 'SUMMER-TIRES-003', 'Tristar Ecopower 3 145/70 R13 71T nyárigumi', 'Gumik és felnik', 13808.00, 12, 'available', 1, '2026-02-03 11:10:35', '2026-02-03 11:10:35', NULL, 0),
(13, 26, 'SUMMER-TIRES-004', 'Imperial Ecodriver 4 145/70 R12 69T nyárigumi', 'Gumik és felnik', 13808.00, 12, 'available', 1, '2026-02-03 11:12:06', '2026-02-03 11:12:06', NULL, 0),
(14, 51, 'WINTER-TIRES-001', 'Star Performer Stratos HP 155/80 R13 79T M+S téligumi', 'Gumik és felnik', 13589.00, 20, 'available', 1, '2026-02-03 11:13:45', '2026-02-03 11:13:45', NULL, 0),
(15, 56, 'WINTER-TIRES-002', 'Tristar Snowpower HP 145/70 R13 71T M+S téligumi', 'Gumik és felnik', 15144.00, 24, 'available', 1, '2026-02-03 11:14:16', '2026-02-03 11:14:16', NULL, 0),
(16, 43, 'WINTER-TIRES-003', 'Minerva FROSTRACK HP M+S 3 155/65 R13 73T M+S téligumi', 'Gumik és felnik', 14300.00, 25, 'available', 1, '2026-02-03 11:14:41', '2026-02-17 13:57:38', NULL, 0),
(17, 52, 'WINTER-TIRES-004', 'Superia Bluewin HP 155/70 R13 75T M+S téligumi', 'Gumik és felnik', 14570.00, 32, 'available', 1, '2026-02-03 11:15:14', '2026-02-03 11:15:14', NULL, 0),
(18, 45, 'WINTER-TIRES-005', 'Nankang SV-3 Winter 165/70 R14 81T M+S téligumi', 'Gumik és felnik', 14799.00, 20, 'available', 1, '2026-02-03 11:16:04', '2026-02-03 11:16:04', NULL, 0),
(19, 11, 'DISC-BRAKE-001', 'BREMBO Prime 09.B436.51 Féktárcsa', 'Fékrendszer', 20677.00, 8, 'available', 1, '2026-02-03 11:18:46', '2026-02-03 11:18:46', NULL, 0),
(20, 11, 'DISC-BRAKE-002', 'BREMBO Prime 09.9313.33 Féktárcsa', 'Fékrendszer', 16000.00, 12, 'available', 1, '2026-02-03 11:19:37', '2026-02-09 10:31:19', NULL, 0),
(21, 63, 'DISC-BRAKE-003', 'ZIMMERMANN SPORT COAT Z 600.3250.52 Féktárcsa', 'Fékrendszer', 3200.00, 10, 'available', 1, '2026-02-03 11:20:12', '2026-02-09 10:31:03', NULL, 0),
(22, 54, 'DISC-BRAKE-004', 'TEXTAR PRO 92082503 Féktárcsa', 'Fékrendszer', 8719.00, 10, 'available', 1, '2026-02-03 11:21:02', '2026-02-03 11:21:02', NULL, 0),
(23, 1, 'DISC-BRAKE-005', 'BOSCH 0 986 479 A01 Féktárcsa', 'Fékrendszer', 17442.00, 22, 'available', 1, '2026-02-03 11:21:35', '2026-02-03 11:21:35', NULL, 0),
(24, 1, 'PAD-BRAKE-001', 'BOSCH 0 986 424 098 Fékbetét készlet, tárcsafékű', 'Fékrendszer', 5668.00, 36, 'available', 1, '2026-02-03 11:23:25', '2026-02-03 11:23:25', NULL, 0),
(25, 11, 'PAD-BRAKE-002', 'BREMBO Prime P 50 051 Fékbetét készlet, tárcsafék MERCEDES-BENZ VIANO, VITO', 'Fékrendszer', 11031.00, 40, 'available', 1, '2026-02-03 11:24:02', '2026-02-03 11:24:02', NULL, 0),
(26, 54, 'PAD-BRAKE-003', 'TEXTAR 2166404 Fékbetét készlet, tárcsafék', 'Fékrendszer', 11607.00, 22, 'available', 1, '2026-02-03 11:24:50', '2026-02-03 11:24:50', NULL, 0),
(27, 57, 'PAD-BRAKE-004', 'TRW GDB1956 Fékbetét készlet, tárcsafék', 'Fékrendszer', 19070.00, 32, 'available', 1, '2026-02-03 11:26:01', '2026-02-03 11:26:01', NULL, 0),
(28, 17, 'DRUM-BRAKE-001', 'FEBI BILSTEIN 37562 Fék készlet, dobfék', 'Fékrendszer', 22795.00, 4, 'available', 1, '2026-02-03 11:27:32', '2026-02-03 11:27:32', NULL, 0),
(29, 1, 'DRUM-BRAKE-002', 'BOSCH 0 204 114 554 Fék készlet, dobfék', 'Fékrendszer', 24157.00, 8, 'available', 1, '2026-02-03 11:28:10', '2026-02-03 11:28:10', NULL, 0),
(30, 34, 'DRUM-BRAKE-003', 'LPR EASY KIT OEK614 Fék készlet, dobfék', 'Fékrendszer', 39666.00, 12, 'available', 1, '2026-02-03 11:29:17', '2026-02-03 11:29:17', NULL, 0),
(31, 17, 'DRUM-BRAKE-004', 'FEBI BILSTEIN 38746 Fék készlet, dobfék', 'Fékrendszer', 23015.00, 16, 'available', 1, '2026-02-03 11:29:48', '2026-02-03 11:29:48', NULL, 0),
(32, 58, 'HEAD-LAMP-001', 'TYC 20-5488-08-2 Fényszóró OPEL ASTRA', 'Világitás', 18207.00, 6, 'available', 1, '2026-02-03 11:31:51', '2026-02-03 11:31:51', NULL, 0),
(33, 58, 'HEAD-LAMP-002', 'TYC 20-0008-05-2 Fényszóró AUDI A4', 'Világitás', 36200.00, 6, 'available', 1, '2026-02-03 11:32:23', '2026-02-03 11:32:23', NULL, 0),
(34, 58, 'HEAD-LAMP-003', 'TYC 20-12033-05-2 Fényszóró Polo 6r', 'Világitás', 29266.00, 8, 'available', 1, '2026-02-03 11:33:43', '2026-02-03 11:33:43', NULL, 0),
(35, 58, 'HEAD-LAMP-004', 'TYC 20-0166-05-2 Fényszóró PEUGEOT 307', 'Világitás', 29160.00, 10, 'available', 1, '2026-02-03 11:34:14', '2026-02-03 11:34:14', NULL, 0),
(36, 23, 'HEAD-LAMP-005', 'HELLA 1EL 011 937-311 Fényszóró VW Touareg 7p', 'Világitás', 225177.00, 4, 'available', 1, '2026-02-03 11:34:52', '2026-02-03 11:34:52', NULL, 0),
(37, 2, 'TAIL-LAMP-001', 'ABAKUS 231-1940L-A Hátsólámpa Ford Ranger EQ', 'Világitás', 7197.00, 4, 'available', 1, '2026-02-03 11:37:13', '2026-02-03 11:37:13', NULL, 0),
(38, 2, 'TAIL-LAMP-002', 'ABAKUS 665-1912L-UE Hátsólámpa Skoda Octavia 2 Kombi', 'Világitás', 13478.00, 4, 'available', 1, '2026-02-03 11:37:42', '2026-02-03 11:37:42', NULL, 0),
(39, 2, 'TAIL-LAMP-003', 'ABAKUS 214-1952L-A Hátsólámpa Mitsubishi L200 K60T', 'Világitás', 7500.00, 8, 'available', 1, '2026-02-03 11:38:16', '2026-02-03 11:38:16', NULL, 0),
(40, 3, 'SIDE-MIRROR-001', 'ALKAR 6126127 Külső visszapillantó VOLKSWAGEN GOLF, BORA', 'Karosszéria', 12056.00, 10, 'available', 1, '2026-02-03 12:50:05', '2026-02-03 12:50:05', NULL, 0),
(41, 3, 'SIDE-MIRROR-COVER-001', 'ALKAR 6343438 Borítás, külső visszapillantó', 'Karosszéria', 2895.00, 40, 'available', 1, '2026-02-03 12:51:47', '2026-02-03 12:51:47', NULL, 0),
(42, 3, 'SIDE-MIRROR-GLASS-001', 'ALKAR 6432438 Tükör üveg, külső visszapillantó pár OPEL ASTRA', 'Karosszéria', 8450.00, 22, 'available', 1, '2026-02-03 12:53:14', '2026-02-03 12:53:14', NULL, 0),
(43, 7, 'SUSPENSION-001', 'BILSTEIN B4 OE Replacement 22-105813 Lengéscsillapító', 'Lengéscsillapító', 22726.00, 44, 'available', 1, '2026-02-03 12:59:56', '2026-02-03 12:59:56', NULL, 0),
(44, 57, 'SUSPENSION-002', 'TRW TWIN JGM5967T Lengéscsillapító Fiat Panda 169', 'Lengéscsillapító', 15053.00, 38, 'available', 1, '2026-02-03 13:00:32', '2026-02-03 13:00:32', NULL, 0),
(46, 16, 'SUSPENSION-004', 'DACO Germany 533701 Lengéscsillapító', 'Lengéscsillapító', 7814.00, 20, 'available', 1, '2026-02-03 13:02:44', '2026-02-03 13:02:44', NULL, 0),
(47, 47, 'SPRING-SUSPENSION-001', 'SACHS 996 072 Futómű rugó MERCEDES-BENZ 124-es széria, E-osztály', 'Futómű', 10201.00, 34, 'available', 1, '2026-02-03 13:09:40', '2026-02-03 13:09:40', NULL, 0),
(48, 33, 'SPRING-SUSPENSION-002', 'KYB K-Flex RA6028 Futómű rugó Subaru Forester SG', 'Futómű', 19577.00, 30, 'available', 1, '2026-02-03 13:10:27', '2026-02-03 13:10:27', NULL, 0),
(49, 44, 'SPRING-SUSPENSION-003', 'MONROE SP3675 Futómű rugó SMART ROADSTER', 'Futómű', 5890.00, 10, 'available', 1, '2026-02-03 13:11:34', '2026-02-03 13:11:34', NULL, 0),
(50, 40, 'SPRING-SUSPENSION-004', 'MAPCO 72876 Futómű rugó', 'Futómű', 3100.00, 8, 'available', 1, '2026-02-03 13:12:10', '2026-02-03 13:12:10', NULL, 0),
(51, 17, 'STRUTBEARING-SUSPENSION-001', 'FEBI BILSTEIN 37884 Javítókészlet, gólyaláb támasztó csapágy', 'Futómű', 15000.00, 63, 'available', 1, '2026-02-06 15:07:57', '2026-02-06 15:07:57', NULL, 0),
(52, 66, 'STRUTBEARING-SUSPENSION-002', 'SNR KB674.03 Javítókészlet, gólyaláb támasztó csapágy HONDA CIVIC', 'Futómű', 22640.00, 28, 'available', 1, '2026-02-06 15:13:06', '2026-02-06 15:13:06', NULL, 0),
(53, 17, 'STRUTBEARING-SUSPENSION-003', 'FEBI BILSTEIN 14116 Javítókészlet, gólyaláb támasztó csapágy', 'Futómű', 4900.00, 65, 'available', 1, '2026-02-06 15:14:27', '2026-02-06 15:14:27', NULL, 0),
(54, 44, 'STRUTBEARING-SUSPENSION-004', 'MONROE MK199 Javítókészlet, gólyaláb támasztó csapágy RENAULT LAGUNA, VEL SATIS, ESPACE', 'Futómű', 18000.00, 54, 'available', 1, '2026-02-06 15:19:50', '2026-02-06 15:20:17', NULL, 0),
(55, 35, 'STRUTBEARING-SUSPENSION-005', 'LEMFÖRDER 31770 01 Javítókészlet, gólyaláb támasztó csapágy', 'Futómű', 10790.00, 34, 'available', 1, '2026-02-06 15:20:51', '2026-02-06 15:20:51', NULL, 0),
(56, 55, 'CONTROLARM-SUSPENSION-001', 'TOPRAN 115 800 Lengőkar szett', 'Futómű', 46453.00, 23, 'available', 1, '2026-02-06 15:24:29', '2026-02-06 15:24:29', NULL, 0),
(57, 35, 'CONTROLARM-SUSPENSION-002', 'LEMFÖRDER 31913 01 Lengőkar szett AUDI A4', 'Futómű', 291999.00, 35, 'available', 1, '2026-02-06 15:26:51', '2026-02-06 15:27:03', NULL, 0),
(58, 40, 'CONTROLARM-SUSPENSION-003', 'MAPCO 53742/1 Lengőkar szett', 'Futómű', 132900.00, 13, 'available', 1, '2026-02-06 15:28:29', '2026-02-06 15:28:29', NULL, 0),
(59, 22, 'CONTROLARM-SUSPENSION-004', 'GT-BERGMANN GT20-087 Lengőkar, kerékfelfüggesztés FORD FOCUS', 'Futómű', 37238.00, 21, 'available', 1, '2026-02-06 15:30:30', '2026-02-06 15:30:30', NULL, 0),
(60, 67, 'CONTROLARM-SUSPENSION-005', 'MEYLE 116 050 0223/HD Lengőkar szett AUDI A5, A4, Q5', 'Futómű', 260891.00, 4, 'available', 1, '2026-02-06 15:33:25', '2026-02-06 15:33:25', NULL, 0),
(61, 55, 'HUBCOVER-SUSPENSION-001', 'TOPRAN 206 440 Védőfedél, kerékagy', 'Futómű', 732.00, 4, 'available', 1, '2026-02-06 15:47:34', '2026-02-06 15:47:34', NULL, 0),
(62, 68, 'HUBCOVER-SUSPENSION-002', 'AUTOMEGA 110150110 Védőfedél, kerékagy', 'Futómű', 732.00, 123, 'available', 1, '2026-02-06 15:52:51', '2026-02-06 15:52:51', NULL, 0),
(63, 55, 'HUBCOVER-SUSPENSION-003', 'TOPRAN 104 189 Védőfedél, kerékagy', 'Futómű', 482.00, 82, 'available', 1, '2026-02-06 15:53:59', '2026-02-06 15:53:59', NULL, 0),
(64, 55, 'WHEELBEARING-SUSPENSION-001', 'TOPRAN 200 398 Kerékagy', 'Futómű', 482.00, 36, 'available', 1, '2026-02-06 15:55:09', '2026-02-06 15:55:09', NULL, 0),
(65, 66, 'WHEELBEARING-SUSPENSION-002', 'SNR R154.61 Kerékcsapágy készlet', 'Futómű', 31676.00, 32, 'available', 1, '2026-02-06 15:55:42', '2026-02-06 15:55:42', NULL, 0),
(66, 38, 'AIR-FILTER-001', 'MANN-FILTER C 934 x Légszűrő', 'Szűrők', 6382.00, 11, 'available', 1, '2026-02-09 10:09:32', '2026-02-09 10:09:32', NULL, 0),
(67, 32, 'AIR-FILTER-002', 'K&N Filters 33-2964 Légszűrő', 'Szűrők', 30245.00, 9, 'available', 1, '2026-02-09 10:10:42', '2026-02-09 10:10:42', NULL, 0),
(68, 29, 'AIR-FILTER-003', 'JAPANPARTS FA-108S Légszűrő', 'Szűrők', 3370.00, 5, 'available', 1, '2026-02-09 10:12:17', '2026-02-09 10:12:17', NULL, 0),
(69, 1, 'AIR-FILTER-004', 'BOSCH F 026 400 492 Légszűrő', 'Szűrők', 5405.00, 7, 'available', 1, '2026-02-09 10:15:03', '2026-02-09 10:15:03', NULL, 0),
(70, 20, 'OIL-FILTER-001', 'FILTRON OP 629/1 Olajszűrő', 'Szűrők', 2857.00, 27, 'available', 1, '2026-02-09 10:17:03', '2026-02-09 10:17:03', NULL, 0),
(71, 1, 'OIL-FILTER-002', 'BOSCH 0 986 452 028 Olajszűrő', 'Szűrők', 1987.00, 10, 'available', 1, '2026-02-09 10:17:42', '2026-02-09 10:17:42', NULL, 0),
(72, 32, 'OIL-FILTER-003', 'Olajszűrő K&N Filters KN-204-1', 'Szűrők', 5135.00, 9, 'available', 1, '2026-02-09 10:18:27', '2026-02-09 10:18:27', NULL, 0),
(74, 1, 'OIL-FILTER-004', 'BOSCH 0 451 103 261 Olajszűrő', 'Szűrők', 2184.00, 20, 'available', 1, '2026-02-09 10:19:12', '2026-02-09 10:19:12', NULL, 0),
(75, 24, 'OIL-FILTER-005', 'HENGST FILTER H13W01 Olajszűrő', 'Szűrők', 2469.00, 17, 'available', 1, '2026-02-09 10:20:12', '2026-02-09 10:20:12', NULL, 0),
(76, 38, 'AIR-FILTER-005', 'MANN-FILTER FP 25 007 Szűrő, utastér levegő', 'Szűrők', 7521.00, 15, 'available', 1, '2026-02-09 10:21:41', '2026-02-09 10:21:41', NULL, 0),
(77, 1, 'AIR-FILTER-006', 'BOSCH 0 986 628 583 Szűrő, utastér levegő TESLA MODEL X, MODEL 3, MODEL Y', 'Szűrők', 6129.00, 30, 'available', 1, '2026-02-09 10:22:19', '2026-02-09 10:22:19', NULL, 0),
(78, 20, 'AIR-FILTER-007', 'FILTRON K 1311A Szűrő, utastér levegő', 'Szűrők', 5100.00, 18, 'available', 1, '2026-02-09 10:23:32', '2026-02-09 10:23:32', NULL, 0),
(79, 1, 'AIR-FILTER-008', 'BOSCH 1 987 432 397 Szűrő, utastér levegő', 'Szűrők', 3878.00, 14, 'available', 1, '2026-02-09 10:24:50', '2026-02-09 10:24:50', NULL, 0),
(82, 20, 'OIL-FILTER-006', 'FILTRON OP 629/1 Olajszűrő', 'Szűrők', 2857.00, 13, 'available', 1, '2026-02-09 10:28:47', '2026-02-09 10:28:47', NULL, 0),
(83, 1, 'OIL-FILTER-007', 'BOSCH 0 986 452 028 Olajszűrő', 'Szűrők', 1987.00, 10, 'available', 1, '2026-02-09 10:29:43', '2026-02-09 10:29:43', NULL, 0),
(84, 1, 'WATER-PUMP-KIT-001', 'BOSCH 1 987 948 738 Vezérműszíj készlet vízpumpával', 'Motoralkatrész', 36039.00, 10, 'available', 1, '2026-02-09 10:40:18', '2026-02-09 10:40:18', NULL, 0),
(85, 15, 'WATER-PUMP-KIT-002', 'CONTITECH CT1140WP1 Vezérműszíj készlet vízpumpával', 'Motoralkatrész', 41062.00, 9, 'available', 1, '2026-02-09 10:41:37', '2026-02-09 10:41:37', NULL, 0),
(86, 27, 'WATER-PUMP-KIT-003', 'INA 530 0550 32 Vezérműszíj készlet vízpumpával', 'Motoralkatrész', 53831.00, 6, 'available', 1, '2026-02-09 10:42:36', '2026-02-09 10:42:36', NULL, 0),
(87, 17, 'WATER-PUMP-KIT-004', 'FEBI BILSTEIN 172599 Vezérműszíj készlet vízpumpával', 'Motoralkatrész', 12649.00, 20, 'available', 1, '2026-02-09 10:45:10', '2026-02-09 10:45:10', NULL, 0),
(88, 15, 'WATER-PUMP-KIT-005', 'CONTITECH CT504WP1 Vezérműszíj készlet vízpumpával', 'Motoralkatrész', 19551.00, 10, 'available', 1, '2026-02-09 10:45:45', '2026-02-09 10:45:45', NULL, 0),
(89, 13, 'TURBO-BASE-001', 'BTS TURBO ORIGINAL T911332 Turbó VOLKSWAGEN POLO, CADDY', 'Motoralkatrész', 418532.00, 3, 'available', 1, '2026-02-09 10:50:06', '2026-02-09 10:51:35', NULL, 0),
(90, 10, 'TURBO-BASE-002', 'BorgWarner 5303 988 0207 Turbó', 'Motoralkatrész', 339251.00, 5, 'available', 1, '2026-02-09 10:51:25', '2026-02-09 10:51:25', NULL, 0),
(91, 21, 'TURBO-BASE-003', 'GARRETT 753420-5006S Turbó', 'Motoralkatrész', 186710.00, 10, 'available', 1, '2026-02-09 10:52:30', '2026-02-09 10:52:30', NULL, 0),
(92, 21, 'TURBO-BASE-004', 'GARRETT 753420-9006S Turbó', 'Motoralkatrész', 165809.00, 5, 'available', 1, '2026-02-09 10:53:05', '2026-02-09 10:53:05', NULL, 0),
(93, 62, 'STARTER-BATTERY-001', 'YUASA HJ-S46B24L(S) Indító akkumulátor', 'Generátor', 89803.00, 11, 'available', 1, '2026-02-09 10:57:36', '2026-02-09 10:57:36', NULL, 0),
(94, 62, 'STARTER-BATTERY-002', 'YUASA 570901076 Indító akkumulátor', 'Generátor', 68371.00, 17, 'available', 1, '2026-02-09 10:58:13', '2026-02-09 10:58:13', NULL, 0),
(95, 62, 'STARTER-BATTERY-003', 'YUASA YBX7053 Indító akkumulátor', 'Generátor', 44713.00, 10, 'available', 1, '2026-02-09 10:58:50', '2026-02-09 10:58:50', NULL, 0),
(96, 1, 'STARTER-BATTERY-004', 'BOSCH S3 008 Indító akkumulátor', 'Generátor', 35789.00, 12, 'available', 1, '2026-02-09 10:59:34', '2026-02-09 10:59:34', NULL, 0),
(97, 61, 'STARTER-BATTERY-005', 'VARTA N70 Indító akkumulátor', 'Generátor', 63931.00, 12, 'available', 1, '2026-02-09 11:00:44', '2026-02-09 11:00:44', NULL, 0),
(98, 36, 'GENERATOR-BASIC-001', 'LUCAS LRB00183 Generátor', 'Generátor', 26677.00, 20, 'available', 1, '2026-02-09 11:01:36', '2026-02-09 11:01:36', NULL, 0),
(99, 5, 'GENERATOR-BASIC-002', 'AS-PL A9011 Generátor', 'Generátor', 42420.00, 11, 'available', 1, '2026-02-09 11:02:24', '2026-02-09 11:02:24', NULL, 0),
(100, 50, 'GENERATOR-BASIC-003', 'STARDAX STX102224 Generátor RENAULT MEGANE', 'Generátor', 33345.00, 7, 'available', 1, '2026-02-09 11:03:02', '2026-02-09 11:03:02', NULL, 0),
(101, 36, 'GENERATOR-BASIC-004', 'LUCAS LRB00502 Generátor', 'Generátor', 33129.00, 7, 'available', 1, '2026-02-09 11:03:41', '2026-02-09 11:03:41', NULL, 0),
(102, 5, 'GENERATOR-BASIC-005', 'AS-PL A9011PR Generátor', 'Generátor', 38100.00, 20, 'available', 1, '2026-02-09 11:04:23', '2026-02-09 11:04:23', NULL, 0),
(103, 41, 'STARTER-BASIC-001', 'MASTER-SPORT 2101-3708300-PCS-MS Önindító', 'Generátor', 3705.00, 32, 'available', 1, '2026-02-09 11:14:22', '2026-02-09 11:14:22', NULL, 0),
(104, 1, 'STARTER-BASIC-002', 'BOSCH 0 986 025 940 Önindító', 'Generátor', 51000.00, 10, 'available', 1, '2026-02-09 11:14:55', '2026-02-09 11:14:55', NULL, 0),
(106, 1, 'STARTER-BASIC-003', 'BOSCH 0 986 018 310 Önindító', 'Generátor', 36317.00, 14, 'available', 1, '2026-02-09 11:15:31', '2026-02-09 11:15:31', NULL, 0),
(108, 5, 'STARTER-BASIC-004', 'AS-PL S0128 Önindító', 'Generátor', 38813.00, 10, 'available', 1, '2026-02-09 11:16:05', '2026-02-09 11:16:05', NULL, 0),
(109, 5, 'STARTER-BASIC-005', 'AS-PL S0005 Önindító', 'Generátor', 43500.00, 7, 'available', 1, '2026-02-09 11:16:26', '2026-02-09 11:16:26', NULL, 0),
(110, 18, 'ENGINE-OIL-001', '2217610 ELF Evolution R-Tech ELITE 1L', 'Folyadékok', 23176.00, 12, 'available', 1, '2026-02-09 11:28:12', '2026-02-09 11:28:12', NULL, 0),
(111, 48, 'ENGINE-OIL-002', '550042279 SHELL Helix Ultra Prof AF', 'Folyadékok', 19738.00, 20, 'available', 1, '2026-02-09 11:28:55', '2026-02-09 11:28:55', NULL, 0),
(112, 17, 'ENGINE-OIL-003', '32943 FEBI BILSTEIN Longlife', 'Folyadékok', 14389.00, 5, 'available', 1, '2026-02-09 11:44:26', '2026-02-09 11:44:26', NULL, 0),
(113, 59, 'ENGINE-OIL-004', 'GS55502M4 VAG Special G', 'Folyadékok', 19423.00, 11, 'available', 1, '2026-02-09 11:47:45', '2026-02-09 11:47:45', NULL, 0),
(115, 59, 'ENGINE-OIL-005', 'GS60107M4 VAG Special e', 'Folyadékok', 15404.00, 11, 'available', 1, '2026-02-09 11:50:36', '2026-02-09 11:50:36', NULL, 0),
(117, 14, 'ENGINE-OIL-006', '151A95 CASTROL Magnatec Professional Ford E', 'Folyadékok', 26235.00, 20, 'available', 1, '2026-02-09 11:51:43', '2026-02-09 11:51:43', NULL, 0),
(118, 39, 'ENGINE-OIL-007', 'MN7715-5 MANNOL Longlife 504/507', 'Folyadékok', 11107.00, 30, 'available', 1, '2026-02-09 11:52:40', '2026-02-09 11:52:40', NULL, 0),
(123, 1, 'OXYGEN-SENSOR-001', 'BOSCH 0 258 003 957 Lambdaszonda', 'Motoralkatrész', 23789.00, 40, 'available', 1, '2026-02-09 12:47:03', '2026-02-09 12:47:03', NULL, 0),
(124, 1, 'OXYGEN-SENSOR-002', 'BOSCH 0 258 017 617 Lambdaszonda', 'Motoralkatrész', 27762.00, 40, 'available', 1, '2026-02-09 12:47:31', '2026-02-09 12:47:31', NULL, 0),
(125, 71, 'OXYGEN-SENSOR-003', 'DENSO Direct Fit DOX-0351 Lambdaszonda', 'Motoralkatrész', 29695.00, 40, 'available', 1, '2026-02-09 12:49:51', '2026-02-09 12:49:51', NULL, 0),
(126, 46, 'OXYGEN-SENSOR-004', 'NTY ESL-SK-000 Lambdaszonda', 'Motoralkatrész', 13488.00, 22, 'available', 1, '2026-02-09 12:51:55', '2026-02-09 12:51:55', NULL, 0),
(127, 1, 'OXYGEN-SENSOR-005', 'BOSCH 0 258 006 206 Lambdaszonda', 'Motoralkatrész', 17168.00, 30, 'available', 1, '2026-02-09 12:52:17', '2026-02-09 12:52:17', NULL, 0),
(128, 30, 'EXHAUST-BASE-001', 'JMJ 02-50ST Kipufogócső', 'Kipufogó', 15029.00, 45, 'available', 1, '2026-02-09 12:54:34', '2026-02-09 12:54:34', NULL, 0),
(129, 72, 'EXHAUST-BASE-002', 'BM CATALYSTS BM90854H Katalizátor', 'Kipufogó', 42028.00, 10, 'available', 1, '2026-02-09 12:55:59', '2026-02-09 12:55:59', NULL, 0),
(130, 28, 'EXHAUST-BASE-003', 'IZAWIT 22.053 Katalizátor DAEWOO MATIZ', 'Kipufogó', 32830.00, 12, 'available', 1, '2026-02-09 12:56:34', '2026-02-09 12:56:34', NULL, 0),
(131, 30, 'EXHAUST-BASE-004', 'JMJ 1090138 Katalizátor', 'Kipufogó', 36419.00, 6, 'available', 1, '2026-02-09 12:57:11', '2026-02-09 12:57:11', NULL, 0),
(132, 30, 'EXHAUST-BASE-005', 'JMJ 1091391 Katalizátor', 'Kipufogó', 120249.00, 11, 'available', 1, '2026-02-09 12:57:47', '2026-02-09 12:57:47', NULL, 0),
(134, 73, 'EXHAUST-BASE-006', 'DR.MOTOR AUTOMOTIVE DRM0562 Cső, kipuf.gáz visszavezető szelep', 'Kipufogó', 16750.00, 23, 'available', 1, '2026-02-09 13:00:42', '2026-02-09 13:00:42', NULL, 0),
(136, 74, 'EXHAUST-BASE-007', 'AUTLOG AV6126 Vákuumvezérlő szelep, kipufogógáz-visszavezetés', 'Kipufogó', 9178.00, 33, 'available', 1, '2026-02-09 13:03:57', '2026-02-09 13:03:57', NULL, 0),
(138, 2, 'EXHAUST-BASE-008', 'ABAKUS 121-01-017 AGR-szelep', 'Kipufogó', 9286.00, 22, 'available', 1, '2026-02-09 13:04:58', '2026-02-09 13:04:58', NULL, 0),
(140, 46, 'EXHAUST-BASE-009', 'NTY EGR-VW-046 Cső, kipuf.gáz visszavezető szelep', 'Kipufogó', 20366.00, 17, 'available', 1, '2026-02-09 13:05:40', '2026-02-09 13:05:40', NULL, 0),
(141, 25, 'TIMING-SYSTEM-001', 'HEPU 21-0421 Vezérműlánc készlet', 'Motoralkatrész', 87470.00, 13, 'available', 1, '2026-02-09 13:10:54', '2026-02-09 13:10:54', NULL, 0),
(142, 49, 'TIMING-SYSTEM-002', 'SKF VKML 84005 Vezérműlánc készlet', 'Motoralkatrész', 75345.00, 7, 'available', 1, '2026-02-09 13:11:39', '2026-02-09 13:11:39', NULL, 0),
(143, 27, 'TIMING-SYSTEM-003', 'INA 560 0002 10 Vezérműlánc készlet', 'Motoralkatrész', 206088.00, 10, 'available', 1, '2026-02-09 13:12:32', '2026-02-09 13:12:32', NULL, 0),
(145, 60, 'TIMING-SYSTEM-004', 'VAICO V30-10007 Vezérműlánc készlet', 'Motoralkatrész', 937079.00, 3, 'available', 1, '2026-02-09 13:13:34', '2026-02-09 13:13:34', NULL, 0),
(147, 25, 'TIMING-SYSTEM-005', 'HEPU 21-0548 Vezérműlánc készlet AUDI A4, A8, A6', 'Motoralkatrész', 250271.00, 6, 'available', 1, '2026-02-09 13:14:17', '2026-02-09 13:14:17', NULL, 0),
(148, 37, 'CLUTCH-SYSTEM-001', 'LuK RepSet 618 0740 00 Kuplung, szett', 'Egyéb', 25118.00, 25, 'available', 1, '2026-02-09 13:20:28', '2026-02-09 13:20:28', NULL, 0),
(149, 47, 'CLUTCH-SYSTEM-002', 'SACHS 3000 951 427 Kuplung, szett', 'Egyéb', 23790.00, 20, 'available', 1, '2026-02-09 13:20:58', '2026-02-09 13:20:58', NULL, 0),
(150, 37, 'CLUTCH-SYSTEM-003', 'LuK RepSet DMF 600 0144 00 Kuplung, szett AUDI A5, A4, Q5', 'Egyéb', 212782.00, 13, 'available', 1, '2026-02-09 13:21:41', '2026-02-09 13:21:41', NULL, 0),
(151, 37, 'CLUTCH-SYSTEM-004', 'LuK 624 3226 33 Kuplung, szett', 'Egyéb', 97123.00, 9, 'available', 1, '2026-02-09 13:22:17', '2026-02-09 13:22:17', NULL, 0),
(153, 37, 'CLUTCH-SYSTEM-005', 'LuK 600 0371 00 Kuplung, szett', 'Egyéb', 172793.00, 7, 'available', 1, '2026-02-09 13:22:45', '2026-02-09 13:22:45', NULL, 0),
(154, 9, 'BRAKE-DISC-010', 'TESZT', 'Féktárcsa', 19.00, 20, 'available', 1, '2026-02-22 18:40:06', '2026-02-22 18:40:06', NULL, 0),
(155, 9, 'BRAKE-DISC-011', 'TESZT2', 'Féktárcsa', 19.00, 20, 'available', 1, '2026-02-22 19:13:54', '2026-02-22 19:13:54', NULL, 0),
(156, 1, 'TESZT-TERMEK12345', 'teszttermekfrontend', 'Fékrendszer', 12000.00, 24, 'available', 1, '2026-02-22 20:15:09', '2026-02-22 20:15:09', NULL, 0),
(157, 1, 'TESZT-FRONTEND123', 'anyád', 'Gumik és felnik', 20.00, 2, 'available', 1, '2026-02-22 20:19:01', '2026-02-22 20:19:01', NULL, 0),
(158, 4, 'TESZT3', 'tesztfrontednecske', 'Gumik és felnik', 30.00, 28, 'available', 1, '2026-02-22 20:36:59', '2026-02-22 20:36:59', NULL, 0),
(159, 1, 'TESZT5', 'teszt5', 'Gumik és felnik', 45.00, 2, 'available', 1, '2026-02-22 20:38:16', '2026-02-22 20:38:16', NULL, 0);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `part_compatibility`
--

DROP TABLE IF EXISTS `part_compatibility`;
CREATE TABLE IF NOT EXISTS `part_compatibility` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL,
  `vehicle_type` enum('car','motor','truck') NOT NULL,
  `vehicle_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_compatibility` (`part_id`,`vehicle_type`,`vehicle_id`),
  KEY `idx_part_id` (`part_id`),
  KEY `idx_vehicle_type` (`vehicle_type`),
  KEY `idx_vehicle_id` (`vehicle_id`),
  KEY `idx_part_vehicle` (`part_id`,`vehicle_type`,`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `part_images`
--

DROP TABLE IF EXISTS `part_images`;
CREATE TABLE IF NOT EXISTS `part_images` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL,
  `url` varchar(255) NOT NULL,
  `is_primary` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `part_images`
--

INSERT INTO `part_images` (`id`, `part_id`, `url`, `is_primary`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/maxxisgumi.png', 1, '2026-02-05 11:30:02', 0, NULL),
(2, 2, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/tristargumi.png', 1, '2026-02-05 12:45:28', 0, NULL),
(5, 51, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/febicsapagy.png', 1, '2026-02-06 15:08:41', 0, NULL),
(6, 53, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/febicsapagy2.png', 1, '2026-02-06 15:17:26', 0, NULL),
(7, 52, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/snrcsapagy.png', 1, '2026-02-06 15:17:37', 0, NULL),
(8, 54, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/monroecsapagy.png', 1, '2026-02-06 15:21:30', 0, NULL),
(9, 55, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/lemfÃ¶rdercsapagy.png', 1, '2026-02-06 15:21:39', 0, NULL),
(10, 56, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TopranLengokarSzett.png', 1, '2026-02-06 15:25:36', 0, NULL),
(11, 57, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/LemforderLengokarSzett.png', 1, '2026-02-06 15:27:39', 0, NULL),
(12, 58, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/mapcolengokarszett.png', 1, '2026-02-06 15:29:38', 0, NULL),
(13, 59, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/GtBergmannLenogkarSzett.png', 1, '2026-02-06 15:30:54', 0, NULL),
(14, 60, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/meylelengokarszett.png', 1, '2026-02-06 15:35:29', 0, NULL),
(15, 61, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/toprankerekagy.png', 1, '2026-02-06 16:00:09', 0, NULL),
(16, 63, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/toprankerekagy2.png', 1, '2026-02-06 16:00:40', 0, NULL),
(17, 62, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/automegakerekagy.png', 1, '2026-02-06 16:01:00', 0, NULL),
(18, 64, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/toprankerekcsapagy.png', 1, '2026-02-06 16:01:24', 0, NULL),
(19, 65, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/snrkerekcsapagy.png', 1, '2026-02-06 16:01:42', 0, NULL),
(20, 3, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/dunlopgumi.png', 1, '2026-02-09 10:10:25', 0, NULL),
(21, 4, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/bridgestonegumi.png', 1, '2026-02-09 10:10:52', 0, NULL),
(22, 5, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AlutecFelni.png', 1, '2026-02-09 10:11:32', 0, NULL),
(23, 6, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BrockFelni.png', 1, '2026-02-09 10:15:22', 0, NULL),
(24, 7, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/keskinfelni.png', 1, '2026-02-09 10:15:43', 0, NULL),
(25, 9, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/atsfelni.png', 1, '2026-02-09 10:16:08', 0, NULL),
(26, 8, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/DOTZfelni.png', 1, '2026-02-09 10:16:54', 0, NULL),
(27, 10, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TbbTiresNyari.png', 1, '2026-02-09 10:17:39', 0, NULL),
(28, 11, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TristarNyariGumi.png', 1, '2026-02-09 10:17:54', 0, NULL),
(29, 12, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TristarNyariGumi.png', 1, '2026-02-09 10:18:22', 0, NULL),
(30, 13, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ImperialNyariGumi.png', 1, '2026-02-09 10:19:05', 0, NULL),
(31, 14, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/StarTeliGumi.png', 1, '2026-02-09 10:19:29', 0, NULL),
(32, 15, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TristarTeliGumi.png', 1, '2026-02-09 10:19:41', 0, NULL),
(33, 17, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/SuperiaTeli.png', 1, '2026-02-09 10:19:57', 0, NULL),
(34, 16, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MinervaTeli.png', 1, '2026-02-09 10:20:12', 0, NULL),
(35, 18, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/NankangTeli.png', 1, '2026-02-09 10:20:40', 0, NULL),
(36, 19, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BremboFektarcsa.png', 1, '2026-02-09 10:21:03', 0, NULL),
(37, 20, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BremboFektarcsa.png', 1, '2026-02-09 10:21:07', 0, NULL),
(38, 21, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ZimmermannFektarcsa.png', 1, '2026-02-09 10:21:23', 0, NULL),
(39, 22, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TexTarFektarcsa.png', 1, '2026-02-09 10:21:37', 0, NULL),
(40, 23, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschFektarcsa.png', 1, '2026-02-09 10:21:58', 0, NULL),
(41, 24, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschFekbetetKeszlet.png', 1, '2026-02-09 10:22:15', 0, NULL),
(42, 25, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BremboFekbetetKeszlet.png', 1, '2026-02-09 10:22:31', 0, NULL),
(43, 26, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TextarFekbetetKeszlet.png', 1, '2026-02-09 10:22:47', 0, NULL),
(44, 27, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TrwFekbetetkeszlet.png', 1, '2026-02-09 10:23:07', 0, NULL),
(45, 28, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/FebiDobfekKeszlet.png', 1, '2026-02-09 10:23:43', 0, NULL),
(46, 29, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschDobfek.png', 1, '2026-02-09 10:24:28', 0, NULL),
(47, 30, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/lprDobfek.png', 1, '2026-02-09 10:26:52', 0, NULL),
(48, 31, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/FebiDobfekKeszlet.png', 1, '2026-02-09 10:27:09', 0, NULL),
(49, 32, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TycFenyszoro.png', 1, '2026-02-09 10:27:38', 0, NULL),
(50, 33, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TycFenyszoro2.png', 1, '2026-02-09 10:27:46', 0, NULL),
(51, 34, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TycFenyszoro3.png', 1, '2026-02-09 10:28:20', 0, NULL),
(52, 35, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TycFenyszoro4.png', 1, '2026-02-09 10:28:34', 0, NULL),
(53, 36, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/HellaFenyszoro.png', 1, '2026-02-09 10:41:38', 0, NULL),
(54, 37, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AbakusHatsoLampa.png', 1, '2026-02-09 10:42:25', 0, NULL),
(55, 39, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AbakusHatsoLampa3.png', 1, '2026-02-09 10:43:08', 0, NULL),
(56, 38, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AbakusHatsoLampa2.png', 1, '2026-02-09 10:43:15', 0, NULL),
(57, 40, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AlkarVisszapillanto.png', 1, '2026-02-09 10:45:05', 0, NULL),
(58, 41, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AlkarVisszapillantoBoritas.png', 1, '2026-02-09 10:45:16', 0, NULL),
(59, 42, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AlkarKulsoVisszapillantoTukorUveg.png', 1, '2026-02-09 10:45:22', 0, NULL),
(60, 43, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BilsteinLengescsillapito.png', 1, '2026-02-09 10:45:43', 0, NULL),
(61, 44, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/TrwLengescsillapito.png', 1, '2026-02-09 10:47:09', 0, NULL),
(62, 46, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/DacoLengescsillapito.png', 1, '2026-02-09 10:47:37', 0, NULL),
(63, 47, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/SachsFutomuRugo.png', 1, '2026-02-09 10:48:09', 0, NULL),
(64, 48, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/KybFutomuRugo.png', 1, '2026-02-09 10:48:23', 0, NULL),
(65, 49, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MonroeFutomuRugo.png', 1, '2026-02-09 10:50:21', 0, NULL),
(66, 50, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MapcoFutmoRugo.png', 1, '2026-02-09 10:50:41', 0, NULL),
(67, 66, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MannfilterLevegoszuro.png', 1, '2026-02-09 10:55:03', 0, NULL),
(68, 67, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/KnLevegoszuro.png', 1, '2026-02-09 10:55:19', 0, NULL),
(69, 68, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/JapanPartsLevegoszuro.png', 1, '2026-02-09 10:55:30', 0, NULL),
(70, 69, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschLegszuro.png', 1, '2026-02-09 10:55:46', 0, NULL),
(71, 69, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschLegszuro.png', 1, '2026-02-09 10:55:53', 0, NULL),
(72, 70, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/FiltronOlajszuro.png', 1, '2026-02-09 10:56:11', 0, NULL),
(73, 71, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/KnOlajszuro.png', 1, '2026-02-09 10:56:22', 0, NULL),
(74, 72, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/KnOlajszuro.png', 1, '2026-02-09 10:58:01', 0, NULL),
(75, 74, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschOlajszuro.png', 1, '2026-02-09 10:58:42', 0, NULL),
(76, 75, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/HengstOlajszuro.png', 1, '2026-02-09 10:59:03', 0, NULL),
(77, 76, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MannUtasterSzuro.png', 1, '2026-02-09 10:59:24', 0, NULL),
(78, 77, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BOSCHLegszuro(2).png', 1, '2026-02-09 11:02:20', 0, NULL),
(79, 78, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/FiltronUtasterSzuro.png', 1, '2026-02-09 11:02:31', 0, NULL),
(80, 79, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BOSCHLegszuro(2).png', 1, '2026-02-09 11:02:47', 0, NULL),
(81, 82, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/FiltronOlajszuro.png', 1, '2026-02-09 11:03:07', 0, NULL),
(82, 83, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschOlajszuro.png', 1, '2026-02-09 11:03:17', 0, NULL),
(83, 84, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschVizpumpaKeszlet.png', 1, '2026-02-09 11:04:08', 0, NULL),
(84, 85, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ContitechVizpumpaKeszlet.png', 1, '2026-02-09 11:04:54', 0, NULL),
(85, 86, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/InaVizpumpaKeszlet.png', 1, '2026-02-09 11:05:16', 0, NULL),
(86, 87, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/febivizpumpa.png', 1, '2026-02-09 11:10:30', 0, NULL),
(87, 88, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ContitechVizpumpaKeszlet.png', 1, '2026-02-09 11:11:06', 0, NULL),
(88, 89, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BtsTurbo.png', 1, '2026-02-09 11:11:33', 0, NULL),
(89, 90, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BorgWarnerTurbo.png', 1, '2026-02-09 11:11:40', 0, NULL),
(90, 91, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/GarrettTurbo.png', 1, '2026-02-09 11:11:58', 0, NULL),
(91, 92, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/GarrettTurbo.png', 1, '2026-02-09 11:14:18', 0, NULL),
(92, 93, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/YuasaAkkumulator.png', 1, '2026-02-09 11:14:34', 0, NULL),
(93, 94, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/YuasaAkkumulator.png', 1, '2026-02-09 11:14:54', 0, NULL),
(94, 95, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/YuasaAkkumulator.png', 1, '2026-02-09 11:14:56', 0, NULL),
(95, 96, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschAkkumulator.png', 1, '2026-02-09 11:15:13', 0, NULL),
(96, 97, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/VartaAkkumulator.png', 1, '2026-02-09 11:15:25', 0, NULL),
(97, 98, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/LucasGenerator.png', 1, '2026-02-09 11:15:42', 0, NULL),
(98, 99, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AsPLGenerator.png', 1, '2026-02-09 11:16:04', 0, NULL),
(99, 100, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/StardaxGenerator.png', 1, '2026-02-09 11:16:21', 0, NULL),
(100, 101, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/LucasGenerator.png', 1, '2026-02-09 11:16:36', 0, NULL),
(101, 102, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/AsPLGenerator.png', 1, '2026-02-09 11:16:57', 0, NULL),
(102, 103, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MasterSportOnindito.png', 1, '2026-02-09 11:20:21', 0, NULL),
(103, 104, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschOnindito.png', 1, '2026-02-09 11:20:32', 0, NULL),
(104, 106, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/BoschOnindito.png', 1, '2026-02-09 11:20:40', 0, NULL),
(105, 108, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ASPLOnindito.png', 1, '2026-02-09 11:20:52', 0, NULL),
(106, 109, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ASPLOnindito.png', 1, '2026-02-09 11:20:57', 0, NULL),
(107, 110, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ElfEvolutionMotorolaj.png', 1, '2026-02-09 12:38:40', 0, NULL),
(108, 111, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ShellMotorolaj.png', 1, '2026-02-09 12:38:52', 0, NULL),
(109, 113, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/VagMotorolaj.png', 1, '2026-02-09 12:39:13', 0, NULL),
(110, 115, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/VagMotorolaj.png', 1, '2026-02-09 12:39:24', 0, NULL),
(111, 117, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/CastrolMotorolaj.png', 1, '2026-02-09 12:39:37', 0, NULL),
(112, 118, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/MannolMotorolaj.png', 1, '2026-02-09 12:40:25', 0, NULL),
(113, 1, 'http://api.Carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/parts/ZimmermannFektarcsa.jpg', 1, '2026-02-22 19:27:59', 1, '2026-02-22 19:31:56');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `part_variants`
--

DROP TABLE IF EXISTS `part_variants`;
CREATE TABLE IF NOT EXISTS `part_variants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `additional_price` decimal(10,2) DEFAULT '0.00',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `password_resets`
--

DROP TABLE IF EXISTS `password_resets`;
CREATE TABLE IF NOT EXISTS `password_resets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `user_id` (`user_id`),
  KEY `token_2` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `method` varchar(50) DEFAULT NULL,
  `status` enum('pending','completed','failed','refunded','cancelled') DEFAULT 'pending',
  `paid_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `refunds`
--

DROP TABLE IF EXISTS `refunds`;
CREATE TABLE IF NOT EXISTS `refunds` (
  `id` int NOT NULL AUTO_INCREMENT,
  `payment_id` int NOT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `refunded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `payment_id` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `reviews`
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `part_id` int NOT NULL,
  `rating` int DEFAULT NULL,
  `comment` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

--
-- A tábla adatainak kiíratása `reviews`
--

INSERT INTO `reviews` (`id`, `user_id`, `part_id`, `rating`, `comment`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 4, 3, 5, 'Kiváló minőség! Tökéletes ajánlom mindenkinek.', '2026-02-18 19:04:58', 0, NULL),
(4, 3, 3, 2, 'Azért nem tökéletes! Átgondolnám mások helyében.', '2026-02-18 19:06:14', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `sessions`
--

DROP TABLE IF EXISTS `sessions`;
CREATE TABLE IF NOT EXISTS `sessions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `revoked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `token_2` (`token`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_methods`
--

DROP TABLE IF EXISTS `shipping_methods`;
CREATE TABLE IF NOT EXISTS `shipping_methods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_status`
--

DROP TABLE IF EXISTS `shipping_status`;
CREATE TABLE IF NOT EXISTS `shipping_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `tracking_no` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `stock_logs`
--

DROP TABLE IF EXISTS `stock_logs`;
CREATE TABLE IF NOT EXISTS `stock_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL,
  `change_amount` int DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `trucks`
--

DROP TABLE IF EXISTS `trucks`;
CREATE TABLE IF NOT EXISTS `trucks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int DEFAULT NULL,
  `year_to` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `role` varchar(20) DEFAULT 'user',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  `failed_login_attempts` int DEFAULT '0',
  `locked_until` datetime DEFAULT NULL,
  `timezone` varchar(50) DEFAULT NULL,
  `email_verified` tinyint(1) DEFAULT '0',
  `phone_verified` tinyint(1) DEFAULT '0',
  `is_subscribed` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `auth_secret` varchar(255) NOT NULL,
  `guid` char(36) NOT NULL,
  `registration_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user_logs`
--

DROP TABLE IF EXISTS `user_logs`;
CREATE TABLE IF NOT EXISTS `user_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `action` varchar(255) NOT NULL,
  `details` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user_twofa`
--

DROP TABLE IF EXISTS `user_twofa`;
CREATE TABLE IF NOT EXISTS `user_twofa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `twofa_enabled` tinyint(1) DEFAULT '0',
  `twofa_secret` varchar(255) DEFAULT NULL,
  `recovery_codes` varchar(1024) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
CREATE TABLE IF NOT EXISTS `warehouses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `warehouse_stock`
--

DROP TABLE IF EXISTS `warehouse_stock`;
CREATE TABLE IF NOT EXISTS `warehouse_stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `warehouse_id` int NOT NULL,
  `part_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `part_id` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`);

--
-- Megkötések a táblához `email_verifications`
--
ALTER TABLE `email_verifications`
  ADD CONSTRAINT `email_verifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `login_logs`
--
ALTER TABLE `login_logs`
  ADD CONSTRAINT `login_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`);

--
-- Megkötések a táblához `order_logs`
--
ALTER TABLE `order_logs`
  ADD CONSTRAINT `order_logs_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `parts`
--
ALTER TABLE `parts`
  ADD CONSTRAINT `parts_ibfk_1` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`);

--
-- Megkötések a táblához `part_compatibility`
--
ALTER TABLE `part_compatibility`
  ADD CONSTRAINT `part_compatibility_ibfk_1` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `part_images`
--
ALTER TABLE `part_images`
  ADD CONSTRAINT `part_images_ibfk_1` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `part_variants`
--
ALTER TABLE `part_variants`
  ADD CONSTRAINT `part_variants_ibfk_1` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `password_resets`
--
ALTER TABLE `password_resets`
  ADD CONSTRAINT `password_resets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `refunds`
--
ALTER TABLE `refunds`
  ADD CONSTRAINT `refunds_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`);

--
-- Megkötések a táblához `sessions`
--
ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `shipping_status`
--
ALTER TABLE `shipping_status`
  ADD CONSTRAINT `shipping_status_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `stock_logs`
--
ALTER TABLE `stock_logs`
  ADD CONSTRAINT `stock_logs_ibfk_1` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `user_logs`
--
ALTER TABLE `user_logs`
  ADD CONSTRAINT `user_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `user_twofa`
--
ALTER TABLE `user_twofa`
  ADD CONSTRAINT `user_twofa_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `warehouse_stock`
--
ALTER TABLE `warehouse_stock`
  ADD CONSTRAINT `warehouse_stock_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `warehouse_stock_ibfk_2` FOREIGN KEY (`part_id`) REFERENCES `parts` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
