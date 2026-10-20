-- Схема базы данных Pawinder для ЛОКАЛЬНОЙ MySQL 8 (получена из дампа облачной TiDB, без данных).
-- Отличия от облачной базы:
--   * убраны расширения TiDB и счётчики AUTO_INCREMENT;
--   * у внешнего ключа fk_user_avatar убрано ON DELETE SET DEFAULT (InnoDB его не поддерживает),
--     действует поведение по умолчанию (запрет удаления используемого аватара).
-- Скрипт ничего не удаляет: его можно выполнять только в пустой базе pawinder.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `avatar` (
  `id_avatar` int NOT NULL AUTO_INCREMENT,
  `image_name` varchar(50) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_avatar`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `city` (
  `id_city` int NOT NULL AUTO_INCREMENT,
  `city` varchar(45) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_country` int NOT NULL,
  PRIMARY KEY (`id_city`),
  KEY `fk_city_country` (`id_country`),
  CONSTRAINT `fk_city_country` FOREIGN KEY (`id_country`) REFERENCES `country` (`id_country`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `country` (
  `id_country` int NOT NULL AUTO_INCREMENT,
  `country` varchar(45) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_country`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `favorites` (
  `id_favorite` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_pet` int NOT NULL,
  `added_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_favorite`),
  UNIQUE KEY `uk_user_pet_fav` (`id_user`,`id_pet`),
  KEY `fk_fav_pet` (`id_pet`),
  CONSTRAINT `fk_fav_pet` FOREIGN KEY (`id_pet`) REFERENCES `pet` (`id_pet`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pet` (
  `id_pet` int NOT NULL AUTO_INCREMENT,
  `api_pet_id` varchar(50) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_type` int NOT NULL,
  `id_shelter` int DEFAULT NULL,
  `id_sex` int DEFAULT NULL,
  `id_size` int DEFAULT NULL,
  `name` varchar(45) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `photo_url` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_pet`),
  UNIQUE KEY `uk_api_type` (`api_pet_id`,`id_type`),
  KEY `fk_pet_type` (`id_type`),
  KEY `fk_pet_shelter` (`id_shelter`),
  KEY `fk_pet_sex` (`id_sex`),
  KEY `fk_pet_size` (`id_size`),
  CONSTRAINT `fk_pet_sex` FOREIGN KEY (`id_sex`) REFERENCES `sex` (`id_sex`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pet_shelter` FOREIGN KEY (`id_shelter`) REFERENCES `shelter` (`id_shelter`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pet_size` FOREIGN KEY (`id_size`) REFERENCES `size` (`id_size`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pet_type` FOREIGN KEY (`id_type`) REFERENCES `type` (`id_type`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sex` (
  `id_sex` int NOT NULL AUTO_INCREMENT,
  `sex_name` varchar(20) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_sex`),
  UNIQUE KEY `sex_name` (`sex_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `shelter` (
  `id_shelter` int NOT NULL AUTO_INCREMENT,
  `name_shelter` varchar(100) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` int DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `phone` varchar(45) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_shelter`),
  KEY `fk_shelter_city` (`city`),
  CONSTRAINT `fk_shelter_city` FOREIGN KEY (`city`) REFERENCES `city` (`id_city`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `size` (
  `id_size` int NOT NULL AUTO_INCREMENT,
  `size_name` varchar(20) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_size`),
  UNIQUE KEY `size_name` (`size_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `swipe` (
  `id_swipe` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_pet` int NOT NULL,
  `is_like` tinyint(1) NOT NULL,
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_swipe`),
  UNIQUE KEY `uk_user_pet_swipe` (`id_user`,`id_pet`),
  KEY `fk_swipe_pet` (`id_pet`),
  CONSTRAINT `fk_swipe_pet` FOREIGN KEY (`id_pet`) REFERENCES `pet` (`id_pet`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_swipe_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `type` (
  `id_type` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(20) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_type`),
  UNIQUE KEY `type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(45) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(45) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `surname` varchar(45) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(45) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pass_hash` varchar(255) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `salt` varchar(45) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` int DEFAULT NULL,
  `birth_year` date DEFAULT NULL,
  `id_avatar` int DEFAULT '1',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `nickname` (`nickname`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_user_city` (`city`),
  KEY `fk_user_avatar` (`id_avatar`),
  CONSTRAINT `fk_user_avatar` FOREIGN KEY (`id_avatar`) REFERENCES `avatar` (`id_avatar`) ON UPDATE CASCADE,
  CONSTRAINT `fk_user_city` FOREIGN KEY (`city`) REFERENCES `city` (`id_city`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_preference` (
  `id_preference` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_type` int DEFAULT NULL,
  `id_sex` int DEFAULT NULL,
  `id_size` int DEFAULT NULL,
  PRIMARY KEY (`id_preference`),
  UNIQUE KEY `id_user` (`id_user`),
  KEY `fk_pref_type` (`id_type`),
  KEY `fk_pref_sex` (`id_sex`),
  KEY `fk_pref_size` (`id_size`),
  CONSTRAINT `fk_pref_sex` FOREIGN KEY (`id_sex`) REFERENCES `sex` (`id_sex`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pref_size` FOREIGN KEY (`id_size`) REFERENCES `size` (`id_size`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pref_type` FOREIGN KEY (`id_type`) REFERENCES `type` (`id_type`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_pref_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
