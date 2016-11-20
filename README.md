# hourlog
Hour Log

Hour Log is a web-based system to manage operational data of a particle accelerator facility. 
It is being used in production at National Superconducting Cyclotron Laboratary, East Lansing, Michigan, USA.

It is a system with a macro-service architecture. It requires the following services for its oepration:
  1. Olog Service: for logbook entries
  2. Trouble Report Service: for Trouble Reports
  3. Experiments Service: for a list of Experiments at NSCL
  4. Training Service: to check the training level of operators  

## Artifacts

It contains the following directories

1. core:           Entities, Web GUI, RESTful service
2. data-migration: Scripts for data migration
3. design:         Schema, DDL scripts
4. docs:           Documentation
5. pm:             Project Management 
6. qa:             Quality Assurance