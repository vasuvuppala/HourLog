--   -------------------------------------------------- 
--   Generated by Enterprise Architect Version 9.2.921
--   Created On : Wednesday, 04 June, 2014 
--   DBMS       : MySql 
--   -------------------------------------------------- 


SET FOREIGN_KEY_CHECKS=0;


--  Drop Tables, Stored Procedures and Views 

DROP TABLE IF EXISTS author CASCADE;
DROP TABLE IF EXISTS breakdownt_status CASCADE;
DROP TABLE IF EXISTS cached_experiment CASCADE;
DROP TABLE IF EXISTS cached_person CASCADE;
DROP TABLE IF EXISTS component CASCADE;
DROP TABLE IF EXISTS facility CASCADE;
DROP TABLE IF EXISTS facility_status CASCADE;
DROP TABLE IF EXISTS log_entry CASCADE;
DROP TABLE IF EXISTS log_entry_mod CASCADE;
DROP TABLE IF EXISTS logbook CASCADE;
DROP TABLE IF EXISTS priv CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS status CASCADE;
DROP TABLE IF EXISTS user CASCADE;
DROP TABLE IF EXISTS user_role CASCADE;

--  Create Tables 
CREATE TABLE author
(
	author_id INTEGER UNSIGNED NOT NULL,
	first_name VARCHAR(64) NOT NULL,
	last_name VARCHAR(64) NOT NULL,
	person_id INTEGER UNSIGNED,
	version INTEGER UNSIGNED NOT NULL DEFAULT 0,
	PRIMARY KEY (author_id)

) ENGINE=InnoDB COMMENT='Each row represents a log author';


CREATE TABLE breakdownt_status
(
	bd_status_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	status SMALLINT NOT NULL DEFAULT '0' COMMENT '0 means system is down. 1 means up.',
	occurred_at DATETIME NOT NULL,
	facility INTEGER UNSIGNED NOT NULL,
	component INTEGER UNSIGNED NOT NULL,
	log_entry INTEGER UNSIGNED NOT NULL,
	experiment_number INTEGER NOT NULL,
	modified_at DATETIME NOT NULL,
	modified_by VARCHAR(64) NOT NULL,
	version INTEGER NOT NULL DEFAULT 0 COMMENT 'For concurrency control',
	PRIMARY KEY (bd_status_id),
	KEY (component),
	KEY (facility),
	KEY (log_entry)

) ENGINE=InnoDB;


CREATE TABLE cached_experiment
(
	number INTEGER NOT NULL,
	spokesperson VARCHAR(128),
	title VARCHAR(128),
	hours_approved INTEGER,
	hours_requested INTEGER,
	experiment_completed VARCHAR(3),
	description TEXT,
	a1900_contact VARCHAR(64),
	approval_date DATE,
	PAC SMALLINT,
	completion_date DATE,
	on_target_hours INTEGER,
	PRIMARY KEY (number)

) ENGINE=InnoDB;


CREATE TABLE cached_person
(
	person_id INTEGER UNSIGNED NOT NULL,
	first_name VARCHAR(64) NOT NULL,
	last_name VARCHAR(64) NOT NULL,
	email VARCHAR(128),
	login_id VARCHAR(32) NOT NULL,
	department VARCHAR(64),
	PRIMARY KEY (person_id)

) ENGINE=InnoDB COMMENT='a person who is a current employee';


CREATE TABLE component
(
	component_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Each row represents a system such as RF, Vaccuum, Cryo, Detector etc',
	name VARCHAR(64) NOT NULL,
	description VARCHAR(255) NOT NULL,
	parent INTEGER UNSIGNED,
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (component_id),
	UNIQUE UQ_component_name(name)

) ENGINE=InnoDB;


CREATE TABLE facility
(
	facility_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	facility_name VARCHAR(32) NOT NULL,
	description VARCHAR(255) NOT NULL,
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (facility_id),
	UNIQUE UQ_facility_facility_name(facility_name)

) ENGINE=InnoDB;


CREATE TABLE facility_status
(
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	occurred_at DATETIME NOT NULL COMMENT 'time at which the status change occurred',
	modified_by VARCHAR(64) NOT NULL,
	modified_at DATETIME NOT NULL COMMENT 'time at which the record was modified',
	facility VARCHAR(16) NOT NULL,
	status VARCHAR(8) NOT NULL,
	experiment_number INTEGER NOT NULL,
	version INTEGER NOT NULL DEFAULT 0 COMMENT 'for concurrency control',
	PRIMARY KEY (id),
	KEY (status)

) ENGINE=InnoDB;


CREATE TABLE log_entry
(
	log_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	logbook VARCHAR(32) NOT NULL,
	created_at DATETIME NOT NULL,
	notes TEXT NOT NULL,
	author INTEGER UNSIGNED NOT NULL,
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (log_id),
	UNIQUE UQ_log_entry_log_id(log_id),
	KEY (author)

) ENGINE=InnoDB COMMENT='Each row is a log entry';


CREATE TABLE log_entry_mod
(
	log_mod_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	orig_log_entry INTEGER UNSIGNED NOT NULL,
	created_at DATETIME NOT NULL,
	notes TEXT NOT NULL,
	author INTEGER UNSIGNED NOT NULL,
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (log_mod_id),
	KEY (author),
	KEY (orig_log_entry)

) ENGINE=InnoDB COMMENT='each row is a modification to a log entry';


CREATE TABLE logbook
(
	logbook_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	logbook_name VARCHAR(32) NOT NULL,
	description VARCHAR(255) NOT NULL,
	version INTEGER NOT NULL DEFAULT 0 COMMENT 'for concurrency control',
	PRIMARY KEY (logbook_id),
	UNIQUE UQ_logbook_logbook_name(logbook_name)

) ENGINE=InnoDB;


CREATE TABLE priv
(
	privilege_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	role INTEGER UNSIGNED NOT NULL,
	resource VARCHAR(128) NOT NULL,
	oper CHAR(1) NOT NULL,
	PRIMARY KEY (privilege_id),
	KEY (role)

) ENGINE=InnoDB;


CREATE TABLE role
(
	role_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	role_name VARCHAR(64) NOT NULL,
	description VARCHAR(255) NOT NULL,
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (role_id),
	UNIQUE UQ_role_role_name(role_name)

) ENGINE=InnoDB;


CREATE TABLE status
(
	status_id VARCHAR(8) NOT NULL,
	description VARCHAR(255) NOT NULL,
	parent VARCHAR(8) COMMENT 'statuses  have hierarchy. id of parent.',
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (status_id),
	KEY (parent)

) ENGINE=InnoDB;


CREATE TABLE user
(
	user_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	login_id VARCHAR(32) NOT NULL,
	first_name VARCHAR(64) NOT NULL,
	last_name VARCHAR(64) NOT NULL,
	email VARCHAR(64) NOT NULL,
	comment VARCHAR(255),
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (user_id),
	UNIQUE UQ_user_login_id(login_id)

) ENGINE=InnoDB;


CREATE TABLE user_role
(
	user_role_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	user INTEGER UNSIGNED NOT NULL,
	role INTEGER UNSIGNED NOT NULL,
	can_delegate BOOL NOT NULL DEFAULT false,
	is_role_manager BIGINT NOT NULL DEFAULT false,
	start_time TIMESTAMP,
	end_time TIMESTAMP,
	comment VARCHAR(255),
	version INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (user_role_id)

) ENGINE=InnoDB;



SET FOREIGN_KEY_CHECKS=1;


--  Create Foreign Key Constraints 
ALTER TABLE breakdownt_status ADD CONSTRAINT FK_breakdownt_status_component 
	FOREIGN KEY (component) REFERENCES component (component_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE breakdownt_status ADD CONSTRAINT FK_breakdownt_status_facility 
	FOREIGN KEY (facility) REFERENCES facility (facility_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE breakdownt_status ADD CONSTRAINT FK_breakdownt_status_log_entry 
	FOREIGN KEY (log_entry) REFERENCES log_entry (log_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE facility_status ADD CONSTRAINT FK_facility_status_status 
	FOREIGN KEY (status) REFERENCES status (status_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE log_entry ADD CONSTRAINT FK_log_entry_author 
	FOREIGN KEY (author) REFERENCES author (author_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE log_entry_mod ADD CONSTRAINT FK_log_entry_mod_author 
	FOREIGN KEY (author) REFERENCES author (author_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE log_entry_mod ADD CONSTRAINT FK_log_entry_mod_log_entry 
	FOREIGN KEY (orig_log_entry) REFERENCES log_entry (log_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE priv ADD CONSTRAINT FK_priv_role 
	FOREIGN KEY (role) REFERENCES role (role_id)
	ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE status ADD CONSTRAINT fk_status_status 
	FOREIGN KEY (parent) REFERENCES status (status_id);
