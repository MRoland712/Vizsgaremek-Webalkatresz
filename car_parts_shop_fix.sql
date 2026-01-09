-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2026. Jan 09. 17:05
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateParts` (IN `p_part_id` INT, IN `p_manufacturer_id` INT, IN `p_sku` VARCHAR(100), IN `p_name` VARCHAR(255), IN `p_category` VARCHAR(100), IN `p_price` DECIMAL(10,2), IN `p_stock` INT, IN `p_status` VARCHAR(20), IN `p_is_active` TINYINT, IN `p_is_deleted` TINYINT)   BEGIN
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `cars`
--
ALTER TABLE `cars`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `manufacturers`
--
ALTER TABLE `manufacturers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `motors`
--
ALTER TABLE `motors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `order_logs`
--
ALTER TABLE `order_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `parts`
--
ALTER TABLE `parts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `part_images`
--
ALTER TABLE `part_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `part_variants`
--
ALTER TABLE `part_variants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `password_resets`
--
ALTER TABLE `password_resets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `refunds`
--
ALTER TABLE `refunds`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `shipping_methods`
--
ALTER TABLE `shipping_methods`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `user_logs`
--
ALTER TABLE `user_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `user_twofa`
--
ALTER TABLE `user_twofa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `warehouses`
--
ALTER TABLE `warehouses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `warehouse_stock`
--
ALTER TABLE `warehouse_stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

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
