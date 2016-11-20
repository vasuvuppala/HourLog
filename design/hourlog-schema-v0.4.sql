SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `HourLog2` DEFAULT CHARACTER SET latin1 ;
USE `HourLog2` ;

-- -----------------------------------------------------
-- Table `HourLog2`.`logbook`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`logbook` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`logbook` (
  `logbook_id` VARCHAR(32) NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 COMMENT 'for concurrency control' ,
  PRIMARY KEY (`logbook_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`activity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`activity` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`activity` (
  `activity_id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `logbook_id` VARCHAR(32) NOT NULL ,
  `at` DATETIME NOT NULL ,
  `notes` TEXT NOT NULL ,
  `author` VARCHAR(32) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`activity_id`) ,
  INDEX `fk_activity_logbook1_idx` (`logbook_id` ASC) )
ENGINE = MyISAM
AUTO_INCREMENT = 43087
DEFAULT CHARACTER SET = latin1
COMMENT = 'Each row is a log entry';


-- -----------------------------------------------------
-- Table `HourLog2`.`cached_experiment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`cached_experiment` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`cached_experiment` (
  `number` INT NOT NULL ,
  `spokesperson` VARCHAR(128) NULL DEFAULT NULL ,
  `title` VARCHAR(128) NULL DEFAULT NULL ,
  `hours_approved` INT NULL DEFAULT NULL ,
  `hours_requested` INT NULL DEFAULT NULL ,
  `experiment_completed` VARCHAR(3) NULL DEFAULT NULL ,
  `description` TEXT NULL DEFAULT NULL ,
  `a1900_contact` VARCHAR(64) NULL ,
  `approval_date` DATE NULL DEFAULT NULL ,
  `PAC` SMALLINT(5) NULL DEFAULT NULL ,
  `completion_date` DATE NULL DEFAULT NULL ,
  `on_target_hours` INT NULL ,
  INDEX `ExpNumber` (`number` ASC) ,
  PRIMARY KEY (`number`) )
ENGINE = InnoDB
AUTO_INCREMENT = 694
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `HourLog2`.`modified_activity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`modified_activity` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`modified_activity` (
  `mod_activity_id` INT NOT NULL AUTO_INCREMENT ,
  `activity_id` INT NOT NULL ,
  `at` DATETIME NOT NULL ,
  `notes` TEXT NOT NULL ,
  `author` VARCHAR(32) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`mod_activity_id`) ,
  INDEX `fk_modifiedActivity_activity_idx` (`activity_id` ASC) ,
  CONSTRAINT `fk_modifiedActivity_activity`
    FOREIGN KEY (`activity_id` )
    REFERENCES `HourLog2`.`activity` (`activity_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`facility`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`facility` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`facility` (
  `facility_id` VARCHAR(16) NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`facility_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`status` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`status` (
  `status_id` VARCHAR(8) NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  `parent_id` VARCHAR(8) NULL DEFAULT NULL COMMENT 'statuses  have hierarchy. id of parent.' ,
  `version` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`status_id`) ,
  INDEX `fk_status_status1_idx` (`parent_id` ASC) ,
  CONSTRAINT `fk_status_status1`
    FOREIGN KEY (`parent_id` )
    REFERENCES `HourLog2`.`status` (`status_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`facility_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`facility_status` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`facility_status` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `occurred_at` DATETIME NOT NULL COMMENT 'time at which the status change occurred' ,
  `modified_by` VARCHAR(64) NOT NULL ,
  `modified_at` DATETIME NOT NULL COMMENT 'time at which the record was modified' ,
  `facility_id` VARCHAR(16) NOT NULL ,
  `status_id` VARCHAR(8) NOT NULL ,
  `experiment_number` INT NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 COMMENT 'for concurrency control' ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_facility_status_facility1_idx` (`facility_id` ASC) ,
  INDEX `fk_facility_status_status1_idx` (`status_id` ASC) ,
  CONSTRAINT `fk_facility_status_facility1`
    FOREIGN KEY (`facility_id` )
    REFERENCES `HourLog2`.`facility` (`facility_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_facility_status_status1`
    FOREIGN KEY (`status_id` )
    REFERENCES `HourLog2`.`status` (`status_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`component` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`component` (
  `component_id` VARCHAR(16) NOT NULL COMMENT 'Each row represents a system such as RF, Vaccuum, Cryo, Detector etc' ,
  `description` VARCHAR(255) NOT NULL ,
  `parent_id` VARCHAR(16) NULL ,
  `version` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`component_id`) ,
  INDEX `fk_system_system1_idx` (`parent_id` ASC) ,
  CONSTRAINT `fk_system_system1`
    FOREIGN KEY (`parent_id` )
    REFERENCES `HourLog2`.`component` (`component_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`component_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`component_status` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`component_status` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `status` SMALLINT NOT NULL DEFAULT 0 COMMENT '0 means system is down. 1 means up.' ,
  `occurred_at` DATETIME NOT NULL ,
  `modified_at` DATETIME NOT NULL ,
  `modified_by` VARCHAR(64) NOT NULL ,
  `facility_id` VARCHAR(16) NOT NULL ,
  `component_id` VARCHAR(16) NOT NULL ,
  `activity_id` INT UNSIGNED NOT NULL ,
  `experiment_number` INT NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 COMMENT 'For concurrency control' ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_system_status_facility1_idx` (`facility_id` ASC) ,
  INDEX `fk_system_status_system1_idx` (`component_id` ASC) ,
  INDEX `fk_system_status_activity1_idx` (`activity_id` ASC) ,
  CONSTRAINT `fk_system_status_facility1`
    FOREIGN KEY (`facility_id` )
    REFERENCES `HourLog2`.`facility` (`facility_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_system_status_system1`
    FOREIGN KEY (`component_id` )
    REFERENCES `HourLog2`.`component` (`component_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_system_status_activity1`
    FOREIGN KEY (`activity_id` )
    REFERENCES `HourLog2`.`activity` (`activity_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `HourLog2` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
