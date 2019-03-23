CREATE SCHEMA IF NOT EXISTS `advancedwhitelist`;
SET SCHEMA `advancedwhitelist`;

CREATE TABLE IF NOT EXISTS `advancedwhitelist`.`group`(`name` VARCHAR(22) NOT NULL, `description` VARCHAR(255) NOT NULL DEFAULT ' ', PRIMARY KEY (`name`), UNIQUE INDEX `name_UNIQUE` (`name` ASC));