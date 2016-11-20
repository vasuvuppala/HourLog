use hour_log;
SELECT "api_client" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`api_client` UNION
SELECT "artifact" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`artifact` UNION
SELECT "auth_operation" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`auth_operation` UNION
SELECT "auth_permission" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`auth_permission` UNION
SELECT "auth_resource" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`auth_resource` UNION
SELECT "auth_role" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`auth_role` UNION
SELECT "auth_user_role" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`auth_user_role` UNION
SELECT "beam" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`beam` UNION
SELECT "beam_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`beam_event` UNION
SELECT "beam_system" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`beam_system` UNION
SELECT "breakdown_category" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`breakdown_category` UNION
SELECT "breakdown_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`breakdown_event` UNION
SELECT "breakdown_status" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`breakdown_status` UNION
SELECT "control_signal" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`control_signal` UNION
SELECT "element" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`element` UNION
SELECT "event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`event` UNION
SELECT "experiment" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`experiment` UNION
SELECT "expr_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`expr_event` UNION
SELECT "external_service" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`external_service` UNION
SELECT "facility" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`facility` UNION
SELECT "log_cache" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`log_cache` UNION
SELECT "log_entry" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`log_entry` UNION
SELECT "logbook" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`logbook` UNION
SELECT "mode" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`mode` UNION
SELECT "mode_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`mode_event` UNION
SELECT "operations_role" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`operations_role` UNION
SELECT "shift" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`shift` UNION
SELECT "shift_staff_member" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`shift_staff_member` UNION
SELECT "shift_status" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`shift_status` UNION
SELECT "snapshot" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`snapshot` UNION
SELECT "snapshot_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`snapshot_event` UNION
SELECT "source" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`source` UNION
SELECT "source_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`source_event` UNION
SELECT "staff_role" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`staff_role` UNION
SELECT "summary" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`summary` UNION
SELECT "summary_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`summary_event` UNION
SELECT "system_property" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`system_property` UNION
SELECT "sysuser" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`sysuser` UNION
SELECT "training_record" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`training_record` UNION
SELECT "trouble_report" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`trouble_report` UNION
SELECT "user_preference" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`user_preference` UNION
SELECT "vault" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`vault` UNION
SELECT "vault_event" AS table_name, COUNT(*) AS row_count FROM `hour_log`.`vault_event`;

use olog;
SELECT "attributes" AS table_name, COUNT(*) AS row_count FROM `olog`.`attributes` UNION
SELECT "bitemporal_log" AS table_name, COUNT(*) AS row_count FROM `olog`.`bitemporal_log` UNION
SELECT "entries" AS table_name, COUNT(*) AS row_count FROM `olog`.`entries` UNION
SELECT "logbooks" AS table_name, COUNT(*) AS row_count FROM `olog`.`logbooks` UNION
SELECT "logs" AS table_name, COUNT(*) AS row_count FROM `olog`.`logs` UNION
SELECT "logs_attributes" AS table_name, COUNT(*) AS row_count FROM `olog`.`logs_attributes` UNION
SELECT "logs_logbooks" AS table_name, COUNT(*) AS row_count FROM `olog`.`logs_logbooks` UNION
SELECT "properties" AS table_name, COUNT(*) AS row_count FROM `olog`.`properties` UNION
SELECT "schema_version" AS table_name, COUNT(*) AS row_count FROM `olog`.`schema_version` UNION
SELECT "subscriptions" AS table_name, COUNT(*) AS row_count FROM `olog`.`subscriptions`;
