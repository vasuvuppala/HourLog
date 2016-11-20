--
-- Hour Log View Definitions
-- Version 0.9
--
-- 
-- Snapshot view
--
 drop view if exists temp_snap;
/*
 create view temp_snap as select e.*, m1.id as mode, ee.id as expr, se.id as source, ve.id as vault, fe.id as summary  
   from event e, mode_event m1, expr_event ee, source_event se, vault_event ve, facility_status_event fe 
   where m1.id = (select id from mode_event se left join event e1 on e1.event_id = se.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at desc limit 1) 
   and ee.id = (select id from expr_event se left join event e1 on e1.event_id = se.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1) 
   and se.id = (select id from source_event se left join event e1 on e1.event_id = se.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and ve.id = (select id from vault_event se left join event e1 on e1.event_id = se.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and fe.id = (select id from facility_status_event se left join event e1 on e1.event_id = se.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1);
   */
   
   create view temp_snap as 
     select e.*, m1.id as mode, ee.id as expr, se.id as source, ve.id as vault, fe.id as summary 
   from event e, mode_event m1, expr_event ee, source_event se, vault_event ve, facility_status_event fe
   where m1.id = (select id from mode_event me left join event e1 on e1.event_id = me.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at desc limit 1) 
   and ee.id = (select id from expr_event xe left join event e1 on e1.event_id = xe.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at desc limit 1)
   and se.id = (select id from source_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and ve.id = (select id from vault_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and fe.id = (select id from facility_status_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   ;
    -- select id from mode_event s left join event e on e.event_id = s.event order by e.occurred_at desc;
   drop view if exists mode_event_view;
   create view mode_event_view as
      select s.*, e.* from mode_event s 
      inner join event e on e.event_id = s.event and e.obsoleted_by is null  
      order by e.occurred_at desc;
   drop view if exists summary_event_view;
   create view summary_event_view as
      select s.*, e.* from summary_event s 
      inner join event e on e.event_id = s.event and e.obsoleted_by is null 
      order by e.occurred_at desc;   
     
   drop view if exists snapshot_view;
   create view snapshot_view as 
     select e.event_id, e.occurred_at, 'mode' as name, s.id
     from event e, mode_event_view s 
     where  s.id = (select m.id from mode_event_view m where m.occurred_at <= e.occurred_at limit 1)
   UNION ALL
     select e.event_id, e.occurred_at, 'summary' as name, s.id
     from event e, summary_event_view s 
     where s.id = (select m.id from summary_event_view m where m.occurred_at <= e.occurred_at limit 1)   
   ; 
/*   
   and ee.id = (select id from expr_event xe left join event e1 on e1.event_id = xe.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at desc limit 1)
   and se.id = (select id from source_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and ve.id = (select id from vault_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   and fe.id = (select id from facility_status_event s1 left join event e1 on e1.event_id = s1.event where e1.occurred_at <= e.occurred_at order by e1.occurred_at limit 1)
   */