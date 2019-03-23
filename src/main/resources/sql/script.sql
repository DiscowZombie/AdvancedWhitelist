-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema advancedwhitelist
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema advancedwhitelist
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `advancedwhitelist` DEFAULT CHARACTER SET utf8;
USE `advancedwhitelist`;

-- -----------------------------------------------------
-- Table `advancedwhitelist`.`group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `advancedwhitelist`.`group`(`name` VARCHAR(22)  NOT NULL, `description` VARCHAR(255) NOT NULL DEFAULT ' ', PRIMARY KEY (`name`), UNIQUE INDEX `name_UNIQUE` (`name` ASC))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `advancedwhitelist`.`group_has_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `advancedwhitelist`.`group_has_user`(`group_name` VARCHAR(22) NOT NULL, `user_uuid`  VARCHAR(255) NOT NULL, PRIMARY KEY (`user_uuid`, `group_name`), INDEX `fk_group_has_user_group_idx` (`group_name` ASC), CONSTRAINT `fk_group_has_user_group` FOREIGN KEY (`group_name`) REFERENCES `advancedwhitelist`.`group` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION)
  ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
