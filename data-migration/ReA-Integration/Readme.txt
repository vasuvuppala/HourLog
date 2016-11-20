Instructions to integrate olog and migrate to ReA
Dev server: svcsdev0

1. Disable HourLog App
2. Dump HourLog into hourlog_prod.sql
3. On dev server (svcsdev0), source hourlog1.1.sql script. This will create v1.1 hour_log database and copy data from current hourlog.

4. Shutdown olog test instance (on server dev0)
5. Disable/undeploy Olog on production server
6. Dump Olog and JCR databases
7. On dev server, create empty olog database and source olog dump

cd to dev/hourlog/data-migration/ReA-migration

8.  Note down row counts of all tables in olog and hour_log (mysql -u root -p -e 'source  hourlog-rowcount.sql')
9.  Use hour_log, and source main.msql
10. call m2o_migrate()
11. Note down row counts of all tables in olog and hour_log again
12. copy hour_log and olog from dev server to production server.
    Drop temp tables from hour_log (temp_attachment, temp_entries, temp_logs, temp_version)

# migrate attachments
13. Copy blobstore from production (controlsapp1:/srv/hourlog/blobstore) onto dev server (svcsdev0:/tmp/hourlog/blobstore)
14. Change Olog URL to production in migrate-attachments.py script, and run it
15. check log file for errors (migrate.log)

#  configuration
16. Set JNDI properties for external services: experiments, TRs, Olog etc
17. Deploy new Hour Log on Production
18. Restart Glassfish



