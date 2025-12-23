-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2025. Dec 23. 19:13
-- Kiszolgáló verziója: 5.7.24
-- PHP verzió: 8.3.1

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
CREATE DATABASE IF NOT EXISTS `car_parts_shop_fix` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `car_parts_shop_fix`;

DELIMITER $$
--
-- Eljárások
--
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

DROP PROCEDURE IF EXISTS `createOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createOrders` (IN `p_user_id` INT)   BEGIN
	START TRANSACTION;
    
    INSERT INTO orders(
        user_id,
        created_at,
        is_deleted,
        deleted_at
    ) VALUES (
        p_user_id,
        NOW(),
        0,
        NULL
    );
         SELECT LAST_INSERT_ID() AS new_orders_id;
         
   COMMIT;
END$$

DROP PROCEDURE IF EXISTS `createParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createParts` (IN `p_manufacturer_id` INT, IN `p_sku` VARCHAR(255), IN `p_name` VARCHAR(100), IN `p_category` VARCHAR(100), IN `p_price` DECIMAL(10.2), IN `p_stock` INT, IN `p_status` VARCHAR(50), IN `p_is_active` TINYINT)   BEGIN
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

DROP PROCEDURE IF EXISTS `createReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `createReviews` (IN `p_user_id` INT, IN `p_part_id` INT, IN `p_rating` INT, IN `p_comment` TEXT)   BEGIN

INSERT INTO reviews(
    user_id,
    part_id,
    rating,
    comment,
    created_at,
    is_deleted,
    deleted_at
    )VALUES(
        p_user_id,
        p_part_id,
        p_rating,
        p_comment,
        NOW(),
        0,
        NULL
        );
     SELECT LAST_INSERT_ID() AS new_review_id;
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

DROP PROCEDURE IF EXISTS `getAllOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllOrders` ()   BEGIN
	SELECT
        id,
        user_id,
        status,
        created_at,
        is_deleted,
        deleted_at
     FROM orders
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

DROP PROCEDURE IF EXISTS `getOrdersById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrdersById` (IN `p_id` INT)   BEGIN
	SELECT
        id,
        user_id,
        status,
        created_at,
        is_deleted,
        deleted_at
     FROM orders
     WHERE id = p_id;
END$$

DROP PROCEDURE IF EXISTS `getOrdersByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getOrdersByUserId` (IN `p_user_id` INT)   BEGIN
	SELECT
    	id,
        user_id,
        status,
        created_at,
        is_deleted,
        deleted_at
     FROM orders
     WHERE user_id = p_user_id;
END$$

DROP PROCEDURE IF EXISTS `getPartsByCategory`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPartsByCategory` ()   BEGIN
    SELECT DISTINCT category
    FROM parts
    WHERE is_deleted = 0
    ORDER BY category;
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

DROP PROCEDURE IF EXISTS `getReviewsById`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsById` (IN `p_review_id` INT)   BEGIN
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
     WHERE id = p_review_id;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByPartId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByPartId` (IN `p_part_id` INT)   BEGIN
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
    WHERE part_id = p_part_id
        AND is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByRating`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByRating` (IN `p_rating` INT)   BEGIN
    SELECT 
        id,
        user_id,
        part_id,
        rating,
        comment,
        created_at
    FROM reviews
    WHERE rating = p_rating
        AND is_deleted = 0
    ORDER BY created_at DESC;
END$$

DROP PROCEDURE IF EXISTS `getReviewsByUserId`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getReviewsByUserId` (IN `p_user_id` INT)   BEGIN
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
    WHERE user_id = p_user_id
        AND is_deleted = 0
    ORDER BY created_at DESC;
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

DROP PROCEDURE IF EXISTS `getUsers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUsers` ()   BEGIN
    SELECT id, email, username, first_name, last_name, 
           phone, guid, role,is_active, last_login, created_at, updated_at, is_deleted, is_subscribed
    FROM users
    WHERE is_deleted = 0
    ORDER BY id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteAddress`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteAddress` (IN `p_address_id` INT)   BEGIN
    UPDATE addresses
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_address_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteManufacturers`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteManufacturers` (IN `p_manufacturers_id` INT)   BEGIN
    UPDATE manufacturers
    SET 
        is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_manufacturers_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteOrders`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteOrders` (IN `p_orders_id` INT)   BEGIN
    UPDATE orders
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_orders_id;
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

DROP PROCEDURE IF EXISTS `softDeleteReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteReviews` (IN `p_review_id` INT)   BEGIN
    UPDATE reviews
    SET 
        is_deleted = 1,
        deleted_at = NOW()
    WHERE id = p_review_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteUser`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteUser` (IN `p_user_id` INT)   BEGIN
    UPDATE users
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_user_id;
END$$

DROP PROCEDURE IF EXISTS `softDeleteUserAndAddresses`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `softDeleteUserAndAddresses` (IN `p_user_id` INT)   BEGIN

    UPDATE users
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE id = p_user_id;


    UPDATE addresses
    SET is_deleted = TRUE,
        deleted_at = NOW()
    WHERE user_id = p_user_id;
END$$

DROP PROCEDURE IF EXISTS `updateAddress`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateAddress` (IN `p_address_id` INT, IN `p_first_name` VARCHAR(50), IN `p_last_name` VARCHAR(50), IN `p_company` VARCHAR(50), IN `p_tax_number` VARCHAR(50), IN `p_country` VARCHAR(50), IN `p_city` VARCHAR(50), IN `p_zip_code` VARCHAR(20), IN `p_street` VARCHAR(100), IN `p_is_default` TINYINT)   BEGIN
    DECLARE v_user_id INT;
    
    START TRANSACTION;

    -- User ID lekérdezése
    SELECT user_id INTO v_user_id 
    FROM addresses 
    WHERE id = p_address_id;

    -- Ha ez lesz az alapértelmezett cím akkor a többi címet ne legyen alapértelmezett
    IF p_is_default = 1 THEN
        UPDATE addresses 
        SET is_default = 0 
        WHERE user_id = v_user_id 
            AND id != p_address_id;
    END IF;

    -- Cím frissítése
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
        updated_at = NOW()
    WHERE id = p_address_id;

    COMMIT;
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

DROP PROCEDURE IF EXISTS `updateParts`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateParts` (IN `p_part_id` INT, IN `p_manufacturer_id` INT, IN `p_sku` VARCHAR(100), IN `p_name` VARCHAR(255), IN `p_category` VARCHAR(100), IN `p_price` DECIMAL(10,2), IN `p_stock` INT, IN `p_status` VARCHAR(20), IN `p_is_active` TINYINT)   BEGIN
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
        updated_at = NOW()
    WHERE id = p_part_id;
    
    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `updatePartVariants`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updatePartVariants` (IN `idIN` INT(11), IN `nameIN` VARCHAR(100), IN `value` TEXT, IN `additionalPriceIN` DOUBLE)   BEGIN
    UPDATE part_variants
    SET 
    	id = idIN,
        part_id = partIdIN,
        name = nameIN,
        `value` = valueIN,
        additional_price = additionalPriceIN,
        updated_at = NOW()
    WHERE id = idIN;
END$$

DROP PROCEDURE IF EXISTS `updateReviews`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateReviews` (IN `p_review_id` INT, IN `p_rating` INT, IN `p_comment` TEXT)   BEGIN
    UPDATE reviews
    SET 
        rating = p_rating,
        comment = p_comment
    WHERE id = p_review_id;
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
CREATE TABLE `addresses` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
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
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `addresses`
--

INSERT INTO `addresses` (`id`, `user_id`, `first_name`, `last_name`, `company`, `tax_number`, `country`, `city`, `zip_code`, `street`, `is_default`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 'János', 'Kovács', NULL, NULL, 'Magyarország', 'Budapest', '1053', 'Kossuth Lajos utca 12', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(2, 2, 'Anna', 'Nagy', 'Nagy Kft', '12345678-2-42', 'Magyarország', 'Debrecen', '4024', 'Piac utca 34', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(3, 3, 'Péter', 'Tóth', NULL, NULL, 'Magyarország', 'Szeged', '6720', 'Kárász utca 56', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(4, 4, 'Mária', 'Varga', 'Varga Bt', '87654321-1-41', 'Magyarország', 'Pécs', '7621', 'Széchenyi tér 23', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(5, 5, 'László', 'Horváth', NULL, NULL, 'Magyarország', 'Győr', '9021', 'Baross út 78', 0, '2025-11-20 23:08:08', '2025-11-30 20:54:24', 0, NULL),
(6, 6, 'Éva', 'Kiss', NULL, NULL, 'Magyarország', 'Miskolc', '3525', 'Széchenyi utca 45', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(7, 7, 'István', 'Szabó', 'Szabó és Társa Kft', '23456789-2-43', 'Magyarország', 'Kecskemét', '6000', 'Rákóczi út 67', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(8, 8, 'Katalin', 'Molnár', NULL, NULL, 'Magyarország', 'Székesfehérvár', '8000', 'Fő utca 89', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(9, 9, 'Gábor', 'Németh', NULL, NULL, 'Magyarország', 'Nyíregyháza', '4400', 'Bethlen utca 12', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(10, 10, 'Zsuzsanna', 'Farkas', 'Farkas Szolgáltató Kft', '34567890-1-42', 'Magyarország', 'Szombathely', '9700', 'Kossuth tér 34', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(11, 11, 'András', 'Balogh', NULL, NULL, 'Magyarország', 'Sopron', '9400', 'Várkerület 56', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(12, 12, 'Eszter', 'Papp', NULL, NULL, 'Magyarország', 'Eger', '3300', 'Dobó tér 78', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(13, 13, 'Róbert', 'Takács', 'Takács Autó Kft', '45678901-2-44', 'Magyarország', 'Veszprém', '8200', 'Óváros tér 23', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(14, 14, 'Ilona', 'Juhász', NULL, NULL, 'Magyarország', 'Szolnok', '5000', 'Kossuth út 45', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(15, 15, 'Tamás', 'Lakatos', NULL, NULL, 'Magyarország', 'Kaposvár', '7400', 'Fő utca 67', 1, '2025-11-20 23:08:08', '2025-11-20 23:08:08', 0, NULL),
(16, 1, 'teszt2', 'teszt2', 'fdsfdsa', 'fadsadsf', 'fdsasadf', 'hashash', '7626', 'zekker', 0, '2025-11-30 14:53:43', '2025-11-30 14:53:43', 0, NULL),
(17, 1, 'Péter', 'Nagy', 'Teszt Kft.', '12345678-1-23', 'Magyarország', 'Szeged', '6720', 'Tisza utca 15.', 0, '2025-11-30 16:54:12', '2025-11-30 16:54:12', 0, NULL),
(18, 5, 'Teszt', 'Péter', 'Teszter Kft.', '1234567328-1-23', 'Magyarország', 'Szeged', '6720', 'Tisza utca 15.', 0, '2025-11-30 20:54:24', '2025-12-01 14:29:00', 0, NULL),
(19, 5, 'Teszt', 'Péter', 'Teszter Kft.', '1234567328-1-23', 'Magyarország', 'Szeged', '6720', 'Tisza utca 15.', 0, '2025-12-01 14:29:00', '2025-12-01 14:29:04', 0, NULL),
(20, 5, 'Teszt', 'Péter', 'Teszter Kft.', '1234567328-1-23', 'Magyarország', 'Szeged', '6720', 'Tisza utca 15.', 0, '2025-12-01 14:29:04', '2025-12-06 14:52:23', 1, '2025-12-02 10:11:19'),
(21, 5, 'FŐTeszt', 'FŐTeszt', 'FŐTeszt Kft.', '1234567328-1-23', 'Magyarország', 'Szeged', '6720', 'Tisza utca 15.', 0, '2025-12-06 14:52:23', '2025-12-06 14:52:36', 0, NULL),
(22, 5, 'FŐTeszt', 'FŐTeszt', 'FŐTeszt Kft.', '1234567328-1-23', 'Magyarország', '', '6720', 'Tisza utca 15.', 1, '2025-12-06 14:52:36', '2025-12-08 11:39:19', 1, '2025-12-08 11:39:19');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cars`
--

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
  `id` int(11) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int(11) DEFAULT NULL,
  `year_to` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT '1',
  `added_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `cart_items`
--

INSERT INTO `cart_items` (`id`, `user_id`, `part_id`, `quantity`, `added_at`, `is_deleted`, `deleted_at`) VALUES
(1, 2, 1, 1, '2025-12-09 10:32:51', 0, NULL),
(2, 2, 6, 2, '2025-12-09 10:32:51', 0, NULL),
(3, 3, 3, 4, '2025-12-09 10:32:51', 0, NULL),
(4, 4, 4, 2, '2025-12-09 10:32:51', 0, NULL),
(5, 5, 5, 1, '2025-12-09 10:32:51', 0, NULL),
(6, 6, 7, 1, '2025-12-09 10:32:51', 0, NULL),
(7, 7, 8, 1, '2025-12-09 10:32:51', 0, NULL),
(8, 8, 9, 1, '2025-12-09 10:32:51', 0, NULL),
(9, 9, 10, 1, '2025-12-09 10:32:51', 0, NULL),
(10, 10, 11, 1, '2025-12-09 10:32:51', 0, NULL),
(11, 11, 12, 1, '2025-12-09 10:32:51', 0, NULL),
(12, 12, 13, 1, '2025-12-09 10:32:51', 0, NULL),
(13, 13, 14, 2, '2025-12-09 10:32:51', 0, NULL),
(14, 14, 15, 3, '2025-12-09 10:32:51', 0, NULL),
(15, 15, 1, 1, '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `email_verifications`
--

DROP TABLE IF EXISTS `email_verifications`;
CREATE TABLE `email_verifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(6) NOT NULL,
  `verified` tinyint(1) DEFAULT '0',
  `sent_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `verified_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE `invoices` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `pdf_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `login_logs`
--

DROP TABLE IF EXISTS `login_logs`;
CREATE TABLE `login_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `logged_in_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `login_logs`
--

INSERT INTO `login_logs` (`id`, `user_id`, `user_agent`, `logged_in_at`) VALUES
(1, 1, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0', '2025-12-09 10:32:51'),
(2, 2, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1.15', '2025-12-09 10:32:51'),
(3, 3, 'Mozilla/5.0 (X11; Linux x86_64) Firefox/121.0', '2025-12-09 10:32:51'),
(4, 4, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/120.0.0.0', '2025-12-09 10:32:51'),
(5, 5, 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) Safari/604.1', '2025-12-09 10:32:51'),
(6, 6, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0', '2025-12-09 10:32:51'),
(7, 7, 'Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) Safari/604.1', '2025-12-09 10:32:51'),
(8, 8, 'Mozilla/5.0 (Android 14; Mobile) Chrome/120.0.0.0', '2025-12-09 10:32:51'),
(9, 9, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0', '2025-12-09 10:32:51'),
(10, 10, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Chrome/120.0.0.0', '2025-12-09 10:32:51'),
(11, 11, 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64) Firefox/121.0', '2025-12-09 10:32:51'),
(12, 12, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Opera/105.0.0.0', '2025-12-09 10:32:51'),
(13, 13, 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_1 like Mac OS X) Safari/604.1', '2025-12-09 10:32:51'),
(14, 14, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0', '2025-12-09 10:32:51'),
(15, 15, 'Mozilla/5.0 (Android 14; Mobile) Firefox/121.0', '2025-12-09 10:32:51');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `manufacturers`
--

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `country` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `manufacturers`
--

INSERT INTO `manufacturers` (`id`, `name`, `country`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Bosch', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(2, 'Denso', 'Japán', '2025-11-21 09:58:46', 0, NULL),
(3, 'Continental', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(4, 'Valeo', 'Franciaország', '2025-11-21 09:58:46', 0, NULL),
(5, 'Mahle', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(6, 'Mann-Filter', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(7, 'NGK', 'Japán', '2025-11-21 09:58:46', 0, NULL),
(8, 'Brembo', 'Olaszország', '2025-11-21 09:58:46', 0, NULL),
(9, 'Sachs', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(10, 'SKF', 'Svédország', '2025-11-21 09:58:46', 0, NULL),
(11, 'Gates', 'USA', '2025-11-21 09:58:46', 0, NULL),
(12, 'Hella', 'Németország', '2025-11-21 09:58:46', 0, NULL),
(13, 'Castrol', 'Egyesült Királyság', '2025-11-21 09:58:46', 0, NULL),
(14, 'Bosch GmbH', 'USA', '2025-11-21 09:58:46', 0, NULL),
(15, 'Shell', 'Hollandia', '2025-11-21 09:58:46', 0, NULL),
(16, 'createTeszt', 'Magyarország', '2025-12-16 13:46:43', 1, '2025-12-19 14:45:48');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `motors`
--

DROP TABLE IF EXISTS `motors`;
CREATE TABLE `motors` (
  `id` int(11) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int(11) DEFAULT NULL,
  `year_to` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `motors`
--

INSERT INTO `motors` (`id`, `brand`, `model`, `year_from`, `year_to`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Honda', 'CBR1000RR', 2017, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(2, 'Honda', 'CB500X', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(3, 'Yamaha', 'YZF-R1', 2015, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(4, 'Yamaha', 'MT-07', 2018, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(5, 'Kawasaki', 'Ninja ZX-10R', 2016, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(6, 'Kawasaki', 'Z900', 2020, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(7, 'Suzuki', 'GSX-R1000', 2017, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(8, 'Suzuki', 'V-Strom 650', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(9, 'BMW', 'R1250GS', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(10, 'BMW', 'S1000RR', 2020, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(11, 'Ducati', 'Panigale V4', 2018, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(12, 'Ducati', 'Multistrada 1260', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(13, 'KTM', '1290 Super Duke R', 2020, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(14, 'Triumph', 'Street Triple RS', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(15, 'Harley-Davidson', 'Street Glide', 2021, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `status`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 2, 'completed', '2025-12-09 10:32:51', 1, '2025-12-15 22:12:16'),
(2, 3, 'completed', '2025-12-09 10:32:51', 0, NULL),
(3, 4, 'shipped', '2025-12-09 10:32:51', 0, NULL),
(4, 5, 'processing', '2025-12-09 10:32:51', 0, NULL),
(5, 6, 'pending', '2025-12-09 10:32:51', 1, '2025-12-15 22:17:54'),
(6, 7, 'completed', '2025-12-09 10:32:51', 0, NULL),
(7, 8, 'cancelled', '2025-12-09 10:32:51', 0, NULL),
(8, 9, 'completed', '2025-12-09 10:32:51', 0, NULL),
(9, 10, 'shipped', '2025-12-09 10:32:51', 0, NULL),
(10, 11, 'processing', '2025-12-09 10:32:51', 1, '2025-12-15 20:50:24'),
(11, 12, 'pending', '2025-12-09 10:32:51', 1, '2025-12-15 22:33:42'),
(12, 13, 'completed', '2025-12-09 10:32:51', 0, NULL),
(13, 14, 'shipped', '2025-12-09 10:32:51', 0, NULL),
(14, 15, 'completed', '2025-12-09 10:32:51', 0, NULL),
(15, 2, 'processing', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_items`
--

DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT '1',
  `price` decimal(10,2) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `part_id`, `quantity`, `price`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 1, 1, '15990.00', '2025-12-09 10:32:51', 0, NULL),
(2, 1, 6, 2, '2490.00', '2025-12-09 10:32:51', 0, NULL),
(3, 2, 3, 4, '35990.00', '2025-12-09 10:32:51', 0, NULL),
(4, 3, 4, 2, '18900.00', '2025-12-09 10:32:51', 0, NULL),
(5, 4, 5, 1, '28500.00', '2025-12-09 10:32:51', 0, NULL),
(6, 5, 7, 1, '3990.00', '2025-12-09 10:32:51', 0, NULL),
(7, 6, 8, 1, '8990.00', '2025-12-09 10:32:51', 0, NULL),
(8, 7, 9, 1, '45900.00', '2025-12-09 10:32:51', 0, NULL),
(9, 8, 10, 1, '52900.00', '2025-12-09 10:32:51', 0, NULL),
(10, 9, 11, 1, '38900.00', '2025-12-09 10:32:51', 0, NULL),
(11, 10, 12, 1, '78900.00', '2025-12-09 10:32:51', 0, NULL),
(12, 11, 13, 1, '24900.00', '2025-12-09 10:32:51', 0, NULL),
(13, 12, 14, 2, '12900.00', '2025-12-09 10:32:51', 0, NULL),
(14, 13, 15, 3, '4990.00', '2025-12-09 10:32:51', 0, NULL),
(15, 14, 1, 1, '15990.00', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_logs`
--

DROP TABLE IF EXISTS `order_logs`;
CREATE TABLE `order_logs` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `old_status` varchar(20) DEFAULT NULL,
  `new_status` varchar(20) DEFAULT NULL,
  `changed_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `parts`
--

DROP TABLE IF EXISTS `parts`;
CREATE TABLE `parts` (
  `id` int(11) NOT NULL,
  `manufacturer_id` int(11) NOT NULL,
  `sku` varchar(100) NOT NULL,
  `name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int(11) DEFAULT '0',
  `status` varchar(20) DEFAULT 'available',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `parts`
--

INSERT INTO `parts` (`id`, `manufacturer_id`, `sku`, `name`, `category`, `price`, `stock`, `status`, `is_active`, `created_at`, `updated_at`, `deleted_at`, `is_deleted`) VALUES
(1, 1, 'BOSCH-FB001', 'Fékbetét készlet', 'Fékrendszer', '12500.00', 45, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(2, 2, 'DENSO-GY001', 'Gyújtógyertya szett', 'Gyújtás', '8900.00', 120, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(3, 3, 'CONT-SZ001', 'Szíj készlet', 'Motor', '15600.00', 30, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(4, 4, 'VALEO-KU001', 'Kuplung készlet', 'Erőátvitel', '45000.00', 15, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(5, 5, 'MAHLE-OL001', 'Olajszűrő', 'Szűrők', '3200.00', 200, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(6, 6, 'MANN-LE001', 'Levegőszűrő', 'Szűrők', '4500.00', 150, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(7, 7, 'NGK-GY002', 'Gyújtógyertya platina', 'Gyújtás', '12000.00', 80, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(8, 8, 'BREMBO-FT001', 'Féktárcsa pár', 'Fékrendszer', '28000.00', 25, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(9, 9, 'SACHS-AM001', 'Lengéscsillapító pár', 'Futómű', '65000.00', 10, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(10, 10, 'SKF-CS001', 'Kerékcsapágy', 'Futómű', '18500.00', 60, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(11, 11, 'GATES-FO001', 'Fogasszíj készlet', 'Motor', '22000.00', 35, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(12, 12, 'HELLA-LA001', 'Fényszóró bal', 'Világítás', '35000.00', 20, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(13, 13, 'CAST-OL002', 'Motorolaj 5W-30 4L', 'Kenőanyagok', '8500.00', 100, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(14, 14, 'MOBIL-OL003', 'Motorolaj 10W-40 5L', 'Kenőanyagok', '9200.00', 90, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(15, 15, 'SHELL-OL004', 'Motorolaj 0W-20 4L', 'Kenőanyagok', '11500.00', 75, 'available', 1, '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0),
(16, 2, 'tesztcreate', 'tesztcreate', 'tesztcreate', '10000.00', 1, 'elérhető', 1, '2025-12-06 13:49:41', '2025-12-06 13:49:41', NULL, 0),
(17, 1, 'BRAKE-001', 'Fékbetét', 'Teszt', '15991.00', 10, 'elérhető', 0, '2025-12-06 16:46:51', '2025-12-08 13:18:29', '2025-12-08 13:18:29', 1),
(18, 1, 'BRAKE-PAD-001', 'Fékbetét szett', 'Fékrendszer', '15991.00', 25, 'available', 1, '2025-12-15 11:09:49', '2025-12-15 11:09:49', NULL, 0);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `part_images`
--

DROP TABLE IF EXISTS `part_images`;
CREATE TABLE `part_images` (
  `id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `url` varchar(255) NOT NULL,
  `is_primary` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `part_variants`
--

DROP TABLE IF EXISTS `part_variants`;
CREATE TABLE `part_variants` (
  `id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `additional_price` decimal(10,2) DEFAULT '0.00',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `part_variants`
--

INSERT INTO `part_variants` (`id`, `part_id`, `name`, `value`, `additional_price`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 'Anyag', 'Kerámia', '5000.00', '2025-12-16 10:44:53', 0, NULL),
(2, 1, 'Anyag', 'Fém', '0.00', '2025-12-16 10:44:53', 0, NULL),
(3, 1, 'Anyag', 'Sport', '8000.00', '2025-12-16 10:44:53', 0, NULL),
(4, 3, 'Méret', '205/55 R16', '0.00', '2025-12-16 10:44:53', 0, NULL),
(5, 3, 'Méret', '225/45 R17', '5000.00', '2025-12-16 10:44:53', 0, NULL),
(6, 3, 'Méret', '235/40 R18', '12000.00', '2025-12-16 10:44:53', 0, NULL),
(7, 6, 'Garancia', '1 év', '0.00', '2025-12-16 10:44:53', 0, NULL),
(8, 6, 'Garancia', '2 év', '500.00', '2025-12-16 10:44:53', 0, NULL),
(9, 6, 'Garancia', '3 év', '1200.00', '2025-12-16 10:44:53', 0, NULL),
(10, 11, 'Oldal', 'Bal', '0.00', '2025-12-16 10:44:53', 0, NULL),
(11, 11, 'Oldal', 'Jobb', '0.00', '2025-12-16 10:44:53', 0, NULL),
(12, 11, 'Színhőmérséklet', '4300K (Meleg fehér)', '0.00', '2025-12-16 10:44:53', 0, NULL),
(13, 11, 'Színhőmérséklet', '6000K (Hideg fehér)', '3000.00', '2025-12-16 10:44:53', 0, NULL),
(14, 5, 'Kiszerelés', 'Darab (1 db)', '0.00', '2025-12-16 10:44:53', 0, NULL),
(15, 5, 'Kiszerelés', 'Pár (2 db)', '28500.00', '2025-12-16 10:44:53', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `password_resets`
--

DROP TABLE IF EXISTS `password_resets`;
CREATE TABLE `password_resets` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `method` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `payments`
--

INSERT INTO `payments` (`id`, `order_id`, `amount`, `method`, `status`, `paid_at`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, '20970.00', 'card', 'completed', '2024-11-01 10:15:00', '2025-12-09 10:32:51', 0, NULL),
(2, 2, '143960.00', 'bank_transfer', 'completed', '2024-11-02 14:30:00', '2025-12-09 10:32:51', 0, NULL),
(3, 3, '37800.00', 'card', 'completed', '2024-11-03 09:45:00', '2025-12-09 10:32:51', 0, NULL),
(4, 4, '28500.00', 'card', 'pending', NULL, '2025-12-09 10:32:51', 0, NULL),
(5, 5, '3990.00', 'cash', 'pending', NULL, '2025-12-09 10:32:51', 0, NULL),
(6, 6, '8990.00', 'card', 'completed', '2024-11-06 11:20:00', '2025-12-09 10:32:51', 0, NULL),
(7, 7, '45900.00', 'bank_transfer', 'failed', NULL, '2025-12-09 10:32:51', 0, NULL),
(8, 8, '52900.00', 'card', 'completed', '2024-11-08 16:00:00', '2025-12-09 10:32:51', 0, NULL),
(9, 9, '38900.00', 'card', 'completed', '2024-11-09 13:15:00', '2025-12-09 10:32:51', 0, NULL),
(10, 10, '78900.00', 'bank_transfer', 'pending', NULL, '2025-12-09 10:32:51', 0, NULL),
(11, 11, '24900.00', 'card', 'pending', NULL, '2025-12-09 10:32:51', 0, NULL),
(12, 12, '25800.00', 'cash', 'completed', '2024-11-12 10:30:00', '2025-12-09 10:32:51', 0, NULL),
(13, 13, '14970.00', 'card', 'completed', '2024-11-13 15:45:00', '2025-12-09 10:32:51', 0, NULL),
(14, 14, '15990.00', 'card', 'completed', '2024-11-14 12:00:00', '2025-12-09 10:32:51', 0, NULL),
(15, 15, '20970.00', 'card', 'pending', NULL, '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `refunds`
--

DROP TABLE IF EXISTS `refunds`;
CREATE TABLE `refunds` (
  `id` int(11) NOT NULL,
  `payment_id` int(11) NOT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `refunded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `reviews`
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `rating` int(11) DEFAULT NULL,
  `comment` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `reviews`
--

INSERT INTO `reviews` (`id`, `user_id`, `part_id`, `rating`, `comment`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 2, 1, 5, 'Kiváló minőség, tökéletesen illeszkedik!', '2025-12-09 10:32:51', 0, NULL),
(2, 3, 3, 4, 'Jó gumi, de kicsit drága.', '2025-12-09 10:32:51', 0, NULL),
(3, 4, 4, 5, 'Gyors szállítás, remek minőség!', '2025-12-09 10:32:51', 0, NULL),
(4, 5, 5, 4, 'Nagyon jó lengéscsillapító, ajánlom!', '2025-12-09 10:32:51', 0, NULL),
(5, 6, 6, 5, 'Pontosan passzol, kiváló ár-érték arány!', '2025-12-09 10:32:51', 0, NULL),
(6, 7, 7, 3, 'Rendben van, de lehetne jobb.', '2025-12-09 10:32:51', 0, NULL),
(7, 8, 8, 5, 'Tökéletes, azonnal beszereltem!', '2025-12-09 10:32:51', 0, NULL),
(8, 9, 9, 4, 'Jó generátor, működik ahogy kell.', '2025-12-09 10:32:51', 0, NULL),
(9, 10, 10, 5, 'Hibátlan kuplung, profi minőség!', '2025-12-09 10:32:51', 0, NULL),
(10, 11, 11, 4, 'Szép fényszóró, jól világít.', '2025-12-09 10:32:51', 0, NULL),
(11, 12, 12, 5, 'Kiváló dugattyú, precíz gyártás!', '2025-12-09 10:32:51', 0, NULL),
(12, 13, 13, 5, 'Minőségi fogasszíj, ajánlom mindenkinek!', '2025-12-09 10:32:51', 0, NULL),
(13, 14, 14, 4, 'Jó csapágy, megfelelő áron.', '2025-12-09 10:32:51', 0, NULL),
(14, 15, 15, 5, 'Tökéletes ékszíj, tartós!', '2025-12-09 10:32:51', 0, NULL),
(15, 2, 2, 5, 'Újra rendelek innen!', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `sessions`
--

DROP TABLE IF EXISTS `sessions`;
CREATE TABLE `sessions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `revoked` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `sessions`
--

INSERT INTO `sessions` (`id`, `user_id`, `token`, `expires_at`, `created_at`, `revoked`) VALUES
(1, 1, 'token_admin_12345', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(2, 2, 'token_john_67890', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(3, 3, 'token_jane_11111', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(4, 4, 'token_peter_22222', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(5, 5, 'token_anna_33333', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(6, 6, 'token_laszlo_44444', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(7, 7, 'token_eva_55555', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(8, 8, 'token_gabor_66666', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(9, 9, 'token_zsuzsanna_77777', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(10, 10, 'token_jozsef_88888', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(11, 11, 'token_maria_99999', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(12, 12, 'token_istvan_00000', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(13, 13, 'token_katalin_aaaaa', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(14, 14, 'token_sandor_bbbbb', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0),
(15, 15, 'token_ildiko_ccccc', '2025-12-16 10:32:51', '2025-12-09 10:32:51', 0);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_methods`
--

DROP TABLE IF EXISTS `shipping_methods`;
CREATE TABLE `shipping_methods` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `shipping_methods`
--

INSERT INTO `shipping_methods` (`id`, `name`, `price`, `duration`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Foxpost Automata', '990.00', '1-2 munkanap', '2025-12-09 10:32:51', 0, NULL),
(2, 'GLS Csomagpont', '1190.00', '2-3 munkanap', '2025-12-09 10:32:51', 0, NULL),
(3, 'MPL Házhozszállítás', '1490.00', '2-4 munkanap', '2025-12-09 10:32:51', 0, NULL),
(4, 'DPD Futárszolgálat', '1690.00', '1-2 munkanap', '2025-12-09 10:32:51', 0, NULL),
(5, 'Magyar Posta Csomag', '1290.00', '3-5 munkanap', '2025-12-09 10:32:51', 0, NULL),
(6, 'FedEx Express', '2990.00', '1 munkanap', '2025-12-09 10:32:51', 0, NULL),
(7, 'UPS Standard', '2490.00', '2-3 munkanap', '2025-12-09 10:32:51', 0, NULL),
(8, 'DHL Express', '3490.00', '1 munkanap', '2025-12-09 10:32:51', 0, NULL),
(9, 'TNT Economy', '1890.00', '3-4 munkanap', '2025-12-09 10:32:51', 0, NULL),
(10, 'Sprinter Futár', '1590.00', '1-2 munkanap', '2025-12-09 10:32:51', 0, NULL),
(11, 'Waberer\'s Logistics', '2190.00', '2-3 munkanap', '2025-12-09 10:32:51', 0, NULL),
(12, 'Ziegler Futár', '1790.00', '2-4 munkanap', '2025-12-09 10:32:51', 0, NULL),
(13, 'Trans-Sped Szállítás', '1990.00', '3-5 munkanap', '2025-12-09 10:32:51', 0, NULL),
(14, 'Cargus Csomag', '1390.00', '2-4 munkanap', '2025-12-09 10:32:51', 0, NULL),
(15, 'Személyes Átvétel', '0.00', 'Azonnal', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_status`
--

DROP TABLE IF EXISTS `shipping_status`;
CREATE TABLE `shipping_status` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `tracking_no` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `stock_logs`
--

DROP TABLE IF EXISTS `stock_logs`;
CREATE TABLE `stock_logs` (
  `id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `change_amount` int(11) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `trucks`
--

DROP TABLE IF EXISTS `trucks`;
CREATE TABLE `trucks` (
  `id` int(11) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `year_from` int(11) DEFAULT NULL,
  `year_to` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `trucks`
--

INSERT INTO `trucks` (`id`, `brand`, `model`, `year_from`, `year_to`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Mercedes-Benz', 'Actros', 2011, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(2, 'Mercedes-Benz', 'Atego', 2013, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(3, 'Volvo', 'FH16', 2012, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(4, 'Volvo', 'FM', 2010, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(5, 'Scania', 'R-Series', 2016, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(6, 'Scania', 'S-Series', 2016, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(7, 'MAN', 'TGX', 2018, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(8, 'MAN', 'TGS', 2017, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(9, 'DAF', 'XF', 2017, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(10, 'DAF', 'CF', 2016, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(11, 'Iveco', 'Stralis', 2012, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(12, 'Iveco', 'Eurocargo', 2015, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(13, 'Renault', 'T-Series', 2019, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(14, 'Renault', 'D-Series', 2018, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(15, 'Freightliner', 'Cascadia', 2020, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
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
  `failed_login_attempts` int(11) DEFAULT '0',
  `locked_until` datetime DEFAULT NULL,
  `timezone` varchar(50) DEFAULT NULL,
  `email_verified` tinyint(1) DEFAULT '0',
  `phone_verified` tinyint(1) DEFAULT '0',
  `is_subscribed` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `auth_secret` varchar(255) NOT NULL,
  `guid` char(36) NOT NULL,
  `registration_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `users`
--

INSERT INTO `users` (`id`, `email`, `username`, `password`, `first_name`, `last_name`, `phone`, `is_active`, `role`, `created_at`, `updated_at`, `last_login`, `failed_login_attempts`, `locked_until`, `timezone`, `email_verified`, `phone_verified`, `is_subscribed`, `deleted_at`, `is_deleted`, `auth_secret`, `guid`, `registration_token`) VALUES
(1, 'kovacs.janos@example.com', 'jkovacs', '$2y$10$abcdefghijklmnopqrstuv', 'János', 'Kovács', '+36301234567', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret123', '49f2d816-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(2, 'nagy.anna@example.com', 'annagy', '$2y$10$abcdefghijklmnopqrstuv', 'Anna', 'Nagy', '+36301234568', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret124', '49f3483e-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(3, 'toth.peter@example.com', 'ptoth', '$2y$10$abcdefghijklmnopqrstuv', 'Péter', 'Tóth', '+36301234569', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret125', '49f349e8-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(4, 'varga.maria@example.com', 'mvarga', '$2y$10$abcdefghijklmnopqrstuv', 'Mária', 'Varga', '+36301234570', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret126', '49f34a6e-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(5, 'horvath.laszlo@example.com', 'lhorvath', '$2y$10$abcdefghijklmnopqrstuv', 'László', 'Horváth', '+36301234571', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret127', '49f34b34-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(6, 'kiss.eva@example.com', 'ekiss', '$2y$10$abcdefghijklmnopqrstuv', 'Éva', 'Kiss', '+36301234572', 1, 'admin', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret128', '49f34bbb-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(7, 'szabo.istvan@example.com', 'iszabo', '$2y$10$abcdefghijklmnopqrstuv', 'István', 'Szabó', '+36301234573', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret129', '49f34c2d-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(8, 'molnar.katalin@example.com', 'kmolnar', '$2y$10$abcdefghijklmnopqrstuv', 'Katalin', 'Molnár', '+36301234574', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret130', '49f34ca4-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(9, 'nemeth.gabor@example.com', 'gnemeth', '$2y$10$abcdefghijklmnopqrstuv', 'Gábor', 'Németh', '+36301234575', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret131', '49f34d3d-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(10, 'farkas.zsuzsanna@example.com', 'zfarkas', '$2y$10$abcdefghijklmnopqrstuv', 'Zsuzsanna', 'Farkas', '+36301234576', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret132', '49f34de1-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(11, 'balogh.andras@example.com', 'abalogh', '$2y$10$abcdefghijklmnopqrstuv', 'András', 'Balogh', '+36301234577', 1, 'admin', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret133', '49f34e41-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(12, 'papp.eszter@example.com', 'epapp', '$2y$10$abcdefghijklmnopqrstuv', 'Eszter', 'Papp', '+36301234578', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret134', '49f34e9e-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(13, 'takacs.robert@example.com', 'rtakacs', '$2y$10$abcdefghijklmnopqrstuv', 'Róbert', 'Takács', '+36301234579', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret135', '49f34f01-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(14, 'juhasz.ilona@example.com', 'ijuhasz', '$2y$10$abcdefghijklmnopqrstuv', 'Ilona', 'Juhász', '+36301234580', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret136', '49f34f59-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(15, 'lakatos.tamas@example.com', 'tlakatos', '$2y$10$abcdefghijklmnopqrstuv', 'Tamás', 'Lakatos', '+36301234581', 1, 'user', '2025-11-21 09:58:46', '2025-11-21 09:58:46', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, 'secret137', '49f34fbd-c6b8-11f0-ac09-6bde17d9fc63', NULL),
(16, 'demoUserDifferent@gmail.com', 'demoUserDifferent', 'h7RN+REITtSoJ8eydNYDrw==', 'John', 'Doe', '+36121231234', 0, 'user', '2025-11-21 12:26:31', '2025-11-21 12:26:31', NULL, 0, NULL, NULL, 0, 0, 0, NULL, 0, '450234', 'ee055e1c-c6cc-11f0-8cfd-c6ed54d944ca', '87020737-3735-40e9-8484-00f25aa75cdd');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user_logs`
--

DROP TABLE IF EXISTS `user_logs`;
CREATE TABLE `user_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL,
  `details` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `user_logs`
--

INSERT INTO `user_logs` (`id`, `user_id`, `action`, `details`, `created_at`) VALUES
(1, 1, 'vigike', 'bigike', '2025-12-08 11:46:13'),
(2, 1, 'vigike', 'bigike', '2025-12-08 11:46:13'),
(3, 1, 'vigike', 'bigike', '2025-12-08 11:46:13');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user_twofa`
--

DROP TABLE IF EXISTS `user_twofa`;
CREATE TABLE `user_twofa` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `twofa_enabled` tinyint(1) DEFAULT '0',
  `twofa_secret` varchar(255) DEFAULT NULL,
  `recovery_codes` varchar(1024) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `user_twofa`
--

INSERT INTO `user_twofa` (`id`, `user_id`, `twofa_enabled`, `twofa_secret`, `recovery_codes`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 1, 'JBSWY3DPEHPK3PXP', 'ABC123,DEF456,GHI789', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(2, 2, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(3, 3, 1, 'KRSXG5CTMVRXEZLU', 'XYZ987,UVW654,RST321', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(4, 4, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(5, 5, 1, 'IFBEGRCFIZDUQSKKJ', 'QWE111,ASD222,ZXC333', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(6, 6, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(7, 7, 1, 'MJSWC2LNMVZXI4TF', 'POI444,LKJ555,MNB666', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(8, 8, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(9, 9, 1, 'KRSXG5CTMVRXEZLU', 'QAZ777,WSX888,EDC999', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(10, 10, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(11, 11, 1, 'IFBEGRCFIZDUQSKKJ', 'RFV000,TGB111,YHN222', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(12, 12, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(13, 13, 1, 'MJSWC2LNMVZXI4TF', 'UJM333,IKO444,OLP555', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(14, 14, 0, NULL, NULL, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(15, 15, 1, 'KRSXG5CTMVRXEZLU', 'ZAQ666,XSW777,CDE888', '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
CREATE TABLE `warehouses` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `warehouses`
--

INSERT INTO `warehouses` (`id`, `name`, `location`, `created_at`, `is_deleted`, `deleted_at`) VALUES
(1, 'Budapest Központi Raktár', 'Budapest, Váci út 123', '2025-11-21 09:58:46', 0, NULL),
(2, 'Debrecen Raktár', 'Debrecen, Piac utca 45', '2025-11-21 09:58:46', 0, NULL),
(3, 'Szeged Raktár', 'Szeged, Tisza Lajos krt. 67', '2025-11-21 09:58:46', 0, NULL),
(4, 'Pécs Raktár', 'Pécs, Rákóczi út 89', '2025-11-21 09:58:46', 0, NULL),
(5, 'Győr Raktár', 'Győr, Baross Gábor út 12', '2025-11-21 09:58:46', 0, NULL),
(6, 'Miskolc Raktár', 'Miskolc, Szinva utca 34', '2025-11-21 09:58:46', 0, NULL),
(7, 'Kecskemét Raktár', 'Kecskemét, Kossuth tér 56', '2025-11-21 09:58:46', 0, NULL),
(8, 'Székesfehérvár Raktár', 'Székesfehérvár, Ady Endre utca 78', '2025-11-21 09:58:46', 0, NULL),
(9, 'Nyíregyháza Raktár', 'Nyíregyháza, Bethlen Gábor utca 90', '2025-11-21 09:58:46', 0, NULL),
(10, 'Szombathely Raktár', 'Szombathely, Savaria út 23', '2025-11-21 09:58:46', 0, NULL),
(11, 'Sopron Raktár', 'Sopron, Várkerület 45', '2025-11-21 09:58:46', 0, NULL),
(12, 'Eger Raktár', 'Eger, Dobó István tér 67', '2025-11-21 09:58:46', 0, NULL),
(13, 'Veszprém Raktár', 'Veszprém, Óváros tér 12', '2025-11-21 09:58:46', 0, NULL),
(14, 'Szolnok Raktár', 'Szolnok, Kossuth Lajos út 34', '2025-11-21 09:58:46', 0, NULL),
(15, 'Kaposvár Raktár', 'Kaposvár, Fő utca 56', '2025-11-21 09:58:46', 0, NULL),
(16, 'Központi Raktár', 'Budapest, 1111 Fő utca 1.', '2025-12-09 10:32:51', 0, NULL),
(17, 'Debreceni Raktár', 'Debrecen, 4000 Kossuth utca 10.', '2025-12-09 10:32:51', 0, NULL),
(18, 'Szegedi Raktár', 'Szeged, 6720 Tisza utca 5.', '2025-12-09 10:32:51', 0, NULL),
(19, 'Pécsi Raktár', 'Pécs, 7600 Király utca 8.', '2025-12-09 10:32:51', 0, NULL),
(20, 'Győri Raktár', 'Győr, 9000 Rákóczi utca 3.', '2025-12-09 10:32:51', 0, NULL),
(21, 'Miskolci Raktár', 'Miskolc, 3525 Petőfi utca 12.', '2025-12-09 10:32:51', 0, NULL),
(22, 'Kecskeméti Raktár', 'Kecskemét, 6000 Arany János utca 7.', '2025-12-09 10:32:51', 0, NULL),
(23, 'Nyíregyházi Raktár', 'Nyíregyháza, 4400 Ady Endre utca 4.', '2025-12-09 10:32:51', 0, NULL),
(24, 'Székesfehérvári Raktár', 'Székesfehérvár, 8000 Vörösmarty utca 6.', '2025-12-09 10:32:51', 0, NULL),
(25, 'Szombathelyi Raktár', 'Szombathely, 9700 Rózsa utca 2.', '2025-12-09 10:32:51', 0, NULL),
(26, 'Egri Raktár', 'Eger, 3300 Dobó István utca 9.', '2025-12-09 10:32:51', 0, NULL),
(27, 'Zalaegerszegi Raktár', 'Zalaegerszeg, 8900 Bajcsy-Zsilinszky utca 11.', '2025-12-09 10:32:51', 0, NULL),
(28, 'Soproni Raktár', 'Sopron, 9400 Széchenyi utca 5.', '2025-12-09 10:32:51', 0, NULL),
(29, 'Veszprémi Raktár', 'Veszprém, 8200 Óvári utca 13.', '2025-12-09 10:32:51', 0, NULL),
(30, 'Tartalék Raktár', 'Budapest, 1052 Petőfi Sándor utca 20.', '2025-12-09 10:32:51', 0, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `warehouse_stock`
--

DROP TABLE IF EXISTS `warehouse_stock`;
CREATE TABLE `warehouse_stock` (
  `id` int(11) NOT NULL,
  `warehouse_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `warehouse_stock`
--

INSERT INTO `warehouse_stock` (`id`, `warehouse_id`, `part_id`, `quantity`, `created_at`, `updated_at`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 1, 10, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(2, 1, 2, 8, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(3, 2, 3, 15, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(4, 2, 4, 7, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(5, 3, 5, 5, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(6, 3, 6, 50, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(7, 4, 7, 30, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(8, 4, 8, 25, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(9, 5, 9, 4, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(10, 5, 10, 5, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(11, 6, 11, 3, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(12, 6, 12, 2, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(13, 7, 13, 10, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(14, 7, 14, 15, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL),
(15, 8, 15, 20, '2025-12-09 10:32:51', '2025-12-09 10:32:51', 0, NULL);

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `cars`
--
ALTER TABLE `cars`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `email_verifications`
--
ALTER TABLE `email_verifications`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- A tábla indexei `login_logs`
--
ALTER TABLE `login_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `logged_in_at` (`logged_in_at`);

--
-- A tábla indexei `manufacturers`
--
ALTER TABLE `manufacturers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- A tábla indexei `motors`
--
ALTER TABLE `motors`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `order_logs`
--
ALTER TABLE `order_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- A tábla indexei `parts`
--
ALTER TABLE `parts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `sku` (`sku`),
  ADD KEY `manufacturer_id` (`manufacturer_id`),
  ADD KEY `category` (`category`);

--
-- A tábla indexei `part_images`
--
ALTER TABLE `part_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `part_variants`
--
ALTER TABLE `part_variants`
  ADD PRIMARY KEY (`id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `token_2` (`token`);

--
-- A tábla indexei `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- A tábla indexei `refunds`
--
ALTER TABLE `refunds`
  ADD PRIMARY KEY (`id`),
  ADD KEY `payment_id` (`payment_id`);

--
-- A tábla indexei `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `token_2` (`token`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `shipping_methods`
--
ALTER TABLE `shipping_methods`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `shipping_status`
--
ALTER TABLE `shipping_status`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- A tábla indexei `stock_logs`
--
ALTER TABLE `stock_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `part_id` (`part_id`);

--
-- A tábla indexei `trucks`
--
ALTER TABLE `trucks`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `guid` (`guid`);

--
-- A tábla indexei `user_logs`
--
ALTER TABLE `user_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `user_twofa`
--
ALTER TABLE `user_twofa`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `warehouses`
--
ALTER TABLE `warehouses`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `warehouse_stock`
--
ALTER TABLE `warehouse_stock`
  ADD PRIMARY KEY (`id`),
  ADD KEY `warehouse_id` (`warehouse_id`),
  ADD KEY `part_id` (`part_id`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `addresses`
--
ALTER TABLE `addresses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT a táblához `cars`
--
ALTER TABLE `cars`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `email_verifications`
--
ALTER TABLE `email_verifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `login_logs`
--
ALTER TABLE `login_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `manufacturers`
--
ALTER TABLE `manufacturers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT a táblához `motors`
--
ALTER TABLE `motors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `order_logs`
--
ALTER TABLE `order_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `parts`
--
ALTER TABLE `parts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT a táblához `part_images`
--
ALTER TABLE `part_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `part_variants`
--
ALTER TABLE `part_variants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `password_resets`
--
ALTER TABLE `password_resets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `refunds`
--
ALTER TABLE `refunds`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `shipping_methods`
--
ALTER TABLE `shipping_methods`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `shipping_status`
--
ALTER TABLE `shipping_status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `stock_logs`
--
ALTER TABLE `stock_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `trucks`
--
ALTER TABLE `trucks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT a táblához `user_logs`
--
ALTER TABLE `user_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT a táblához `user_twofa`
--
ALTER TABLE `user_twofa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `warehouses`
--
ALTER TABLE `warehouses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT a táblához `warehouse_stock`
--
ALTER TABLE `warehouse_stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

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
