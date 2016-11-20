--
-- migrate data from Hour Log Schema v1.0 to v1.1
--

-- dump produciton database (v1.0)
-- mysqldump  -h controlsdb.frib.msu.edu -u fdbdba -p --ssl  HourLogProd > hourlog_prod.sql;
drop schema if exists HourLogProd;
create schema HourLogProd;
use HourLogProd;
source hourlog_prod.sql;

-- fix obsoleted_by entries in v1.0
select 'fixing hour log obsoleted logs ...';
update HourLogProd.log_entry 
    set obsoleted_by = null
    where obsoleted_by = log_entry_id;
    
-- create v1.1 schema
drop schema if exists hour_log;
create schema hour_log;
use hour_log;
source hourlog-schema-v1.1.sql;

-- migrate
-- select concat('insert into hour_log.', TABLE_NAME, ' select * from HourLogProd.', TABLE_NAME, ';') 
--   from information_schema.tables where table_schema = (select database());

-- SET FOREIGN_KEY_CHECKS=0;
    
-- base
insert into hour_log.logbook select * from HourLogProd.logbook;
insert into hour_log.facility select * from HourLogProd.facility;

-- insert into hour_log.facility(facility_id,facility_name,description,ops_logbook) values
--   (1,'CCF', 'Coupled Cyclotron Facility', 'CCF Operations'), 
--   (2,'ReA', 'ReAccelerator Facility','ReA Operations');   
insert into hour_log.api_client(id,name,description,contact_name,contact_email,comments,api_key) 
        select id,name,description,contact_name,contact_email,comment,'TBD' from HourLogProd.api_client;       
insert into hour_log.beam_system(beam_system_id,name,facility,description) 
   select beam_system_id,name,facility,description from HourLogProd.beam_system;
insert into hour_log.element select * from HourLogProd.element;
insert into hour_log.beam select * from HourLogProd.beam;
insert into hour_log.breakdown_category(brkcat_id,name,description,parent) 
   select brkcat_id,name,description,parent from HourLogProd.breakdown_category;
insert into hour_log.mode(mode_id,name,description) 
   select mode_id,name,description from HourLogProd.mode;
insert into hour_log.source(source_id,facility,name,description)
   select source_id,facility,name,description from HourLogProd.source;   
insert into hour_log.vault(vault_id,name,description,facility)
  select vault_id,name,description,1 from HourLogProd.vault;
insert into hour_log.summary(summary_id,name,description,parent) 
  select summary_id,name,description,parent from HourLogProd.summary;

  -- cache etc
insert into hour_log.sysuser select * from HourLogProd.sysuser;
insert into hour_log.control_signal select * from HourLogProd.control_signal;
insert into hour_log.experiment select * from HourLogProd.experiment;
insert into hour_log.external_service select * from HourLogProd.external_service;

insert into hour_log.system_property select * from HourLogProd.system_property;
insert into hour_log.training_record select * from HourLogProd.training_record;
insert into hour_log.trouble_report select * from HourLogProd.trouble_report;
insert into hour_log.user_preference select * from HourLogProd.user_preference;
insert into hour_log.log_cache select * from HourLogProd.log_cache; 

-- auth
insert into hour_log.auth_operation select * from HourLogProd.auth_operation;
insert into hour_log.auth_resource select * from HourLogProd.auth_resource;
insert into hour_log.auth_role select * from HourLogProd.auth_role;
insert into hour_log.auth_permission select * from HourLogProd.auth_permission;
insert into hour_log.auth_user_role select * from HourLogProd.auth_user_role;

-- events
insert into hour_log.event select * from HourLogProd.event where obsoleted_by is null;
insert into hour_log.event select * from HourLogProd.event where obsoleted_by is not null;
insert into hour_log.expr_event select * from HourLogProd.expr_event;
insert into hour_log.source_event select * from HourLogProd.source_event;
insert into hour_log.summary_event select * from HourLogProd.summary_event;
insert into hour_log.vault_event select * from HourLogProd.vault_event;
insert into hour_log.breakdown_event select * from HourLogProd.breakdown_event;
insert into hour_log.mode_event select * from HourLogProd.mode_event;
insert into hour_log.beam_event select * from HourLogProd.beam_event;

-- logs
insert into hour_log.log_entry select * from HourLogProd.log_entry where obsoleted_by is null;
insert into hour_log.log_entry select * from HourLogProd.log_entry where obsoleted_by is not null;
insert into hour_log.artifact select * from HourLogProd.artifact;

-- shift
insert into hour_log.shift_status select * from HourLogProd.shift_status;
insert into hour_log.shift select * from HourLogProd.shift;
insert into hour_log.operations_role(op_role_id,name,description) 
   select op_role_id,name,description from HourLogProd.operations_role;
insert into hour_log.staff_role select * from HourLogProd.staff_role;
insert into hour_log.shift_staff_member select * from HourLogProd.shift_staff_member;

-- unused tables

-- insert into hour_log.breakdown_status select * from HourLogProd.breakdown_status;
-- insert into hour_log.snapshot select * from HourLogProd.snapshot;
-- insert into hour_log.snapshot_event select * from HourLogProd.snapshot_event;

-- SET FOREIGN_KEY_CHECKS=1;
-- 
--
-- 
  