
select f.facility_name, c.name as category, b.failed, e.occurred_at, e.event_entered_at 
  from breakdown_event b 
     inner join event e on e.event_id = b.event 
     inner join breakdown_category c on c.brkcat_id = b.category
     inner join facility f on f.facility_id = e.facility      
  where f.facility_name = 'CCF' and 
        e.obsoleted_by is null and 
        e.occurred_at between '2011-01-01' and '2015-09-30'
  order by e.occurred_at
  INTO OUTFILE '/tmp/hl-breakdowns.csv'
  FIELDS TERMINATED BY ',' 
  LINES TERMINATED BY '\n';
  
  select f.facility_name, c.name as summary, e.occurred_at, e.event_entered_at 
  from summary_event b 
     inner join event e on e.event_id = b.event 
     inner join summary c on c.summary_id = b.summary
     inner join facility f on f.facility_id = e.facility      
  where f.facility_name = 'CCF' and 
        e.obsoleted_by is null and 
        e.occurred_at between '2011-01-01' and '2015-09-30'
  order by e.occurred_at
  INTO OUTFILE '/tmp/hl-summary.csv'
  FIELDS TERMINATED BY ',' 
  LINES TERMINATED BY '\n';
  