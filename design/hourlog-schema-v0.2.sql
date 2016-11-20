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
-- Table `HourLog2`.`experiment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`experiment` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`experiment` (
  `id` MEDIUMINT(9) NOT NULL AUTO_INCREMENT ,
  `exp_number` INT(5) NULL DEFAULT NULL ,
  `spokesperson` VARCHAR(128) NULL DEFAULT NULL ,
  `backupspokesperson` VARCHAR(128) NULL DEFAULT NULL ,
  `title` VARCHAR(128) NULL DEFAULT NULL ,
  `hoursApproved` INT(4) NULL DEFAULT NULL ,
  `hoursBeamOnTarget` INT(4) NULL DEFAULT '0' ,
  `hoursCompleted` INT(4) NULL DEFAULT NULL ,
  `hoursRequested` INT(4) NULL DEFAULT NULL ,
  `hoursReserved` INT(4) NULL DEFAULT '0' ,
  `PACNumber` INT(4) NULL DEFAULT NULL ,
  `safety_rep` VARCHAR(128) NULL DEFAULT NULL ,
  `completed` ENUM('yes','no') NULL DEFAULT NULL ,
  `description` TEXT NULL DEFAULT NULL ,
  `A1900Contact` VARCHAR(64) NULL ,
  `ReadyBy` DATE NULL DEFAULT NULL ,
  `SupportLevel` CHAR(1) NULL DEFAULT NULL ,
  `SafetyRev` TEXT NULL DEFAULT NULL ,
  `withdrawn` TINYINT(1) NULL DEFAULT NULL ,
  `spokespersonaffiliation` ENUM('MSU','non-MSU') NULL DEFAULT NULL ,
  `backupspokespersonaffiliation` ENUM('MSU','non-MSU') NULL DEFAULT NULL ,
  `approvedDate` DATE NULL DEFAULT NULL ,
  `firstOfferedDate` DATE NULL DEFAULT NULL ,
  `firstRunDate` DATE NULL DEFAULT NULL ,
  `PAC` SMALLINT(5) UNSIGNED NULL DEFAULT NULL ,
  `line_manager` INT(11) NOT NULL DEFAULT '0' ,
  `discHours` INT(11) NOT NULL DEFAULT '0' ,
  `discID` INT(11) NULL DEFAULT NULL ,
  `discDate` DATE NULL DEFAULT NULL ,
  `type` VARCHAR(45) NULL DEFAULT NULL ,
  `completion_date` DATE NULL DEFAULT NULL ,
  `experimenter_description` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ExpNumber` (`exp_number` ASC) )
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
  `experiment_id` MEDIUMINT(9) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 COMMENT 'for concurrency control' ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_facility_status_facility1_idx` (`facility_id` ASC) ,
  INDEX `fk_facility_status_status1_idx` (`status_id` ASC) ,
  INDEX `fk_facility_status_experiment1_idx` (`experiment_id` ASC) ,
  CONSTRAINT `fk_facility_status_facility1`
    FOREIGN KEY (`facility_id` )
    REFERENCES `HourLog2`.`facility` (`facility_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_facility_status_status1`
    FOREIGN KEY (`status_id` )
    REFERENCES `HourLog2`.`status` (`status_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_facility_status_experiment1`
    FOREIGN KEY (`experiment_id` )
    REFERENCES `HourLog2`.`experiment` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`system`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`system` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`system` (
  `system_id` VARCHAR(16) NOT NULL COMMENT 'Each row represents a system such as RF, Vaccuum, Cryo, Detector etc' ,
  `description` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`system_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `HourLog2`.`system_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `HourLog2`.`system_status` ;

CREATE  TABLE IF NOT EXISTS `HourLog2`.`system_status` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `status` SMALLINT NOT NULL DEFAULT 0 COMMENT '0 means system is down. 1 means up.' ,
  `occurred_at` DATETIME NOT NULL ,
  `modified_at` DATETIME NOT NULL ,
  `modified_by` VARCHAR(64) NOT NULL ,
  `facility_id` VARCHAR(16) NOT NULL ,
  `system_id` VARCHAR(16) NOT NULL ,
  `activity_id` INT UNSIGNED NOT NULL ,
  `experiment_id` MEDIUMINT(9) NOT NULL ,
  `version` INT NOT NULL DEFAULT 0 COMMENT 'For concurrency control' ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_system_status_facility1_idx` (`facility_id` ASC) ,
  INDEX `fk_system_status_system1_idx` (`system_id` ASC) ,
  INDEX `fk_system_status_activity1_idx` (`activity_id` ASC) ,
  INDEX `fk_system_status_experiment1_idx` (`experiment_id` ASC) ,
  CONSTRAINT `fk_system_status_facility1`
    FOREIGN KEY (`facility_id` )
    REFERENCES `HourLog2`.`facility` (`facility_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_system_status_system1`
    FOREIGN KEY (`system_id` )
    REFERENCES `HourLog2`.`system` (`system_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_system_status_activity1`
    FOREIGN KEY (`activity_id` )
    REFERENCES `HourLog2`.`activity` (`activity_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_system_status_experiment1`
    FOREIGN KEY (`experiment_id` )
    REFERENCES `HourLog2`.`experiment` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `HourLog2` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
