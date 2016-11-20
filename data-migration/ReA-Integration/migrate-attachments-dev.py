import MySQLdb
import requests
import getpass
import sys
from requests.auth import HTTPBasicAuth
import logging
import warnings

def postAttachment(entry, name, filename, user, passwd):
    OLOG_URL= "https://dev0.hlc.nscl.msu.edu:8181/Olog/resources/attachments/"
    BLOB_HOME="/tmp/hourlog/blobstore/"
    pathname = BLOB_HOME + filename 
   
    # remove special chars and UTF3 chars from the name otherwise olog gives error
    newname = '' . join(s for s in name if ord(s) < 128 and s not in '\/:*?<>|"')
    logging.info('Posting attachment %s for entry %s', newname, entry)
    
    files = {'file': (newname, open(pathname, 'rb'), 'application/octet-stream')}
    url = OLOG_URL + str(entry) + "/" + newname
    # url = OLOG_URL + str(entry)
    headers = {'accept':'application/xml'}
    # r = requests.post(url, files=files, verify=False, auth=(user, passwd))
    r = requests.put(url, files=files, verify=False, auth=(user, passwd))
    
    if (r.status_code == requests.codes.ok):
       logging.info('Posted attachment %s to entry %s ', newname, entry)
    elif (r.status_code == 409):
       logging.warning('Attachment already exists for entry %s. ', entry)
    else:
       logging.error('Error in posting attachment %s to %s code: %s', newname, entry, r.status_code)       
       logging.error(r.headers)
       logging.error(r.raw)
       raise SystemError('Error in posting attachment. HTTP error: ' + str(r.status_code))

#
#
#
def migrateAttachment(user, passwd):    
    cnx = MySQLdb.connect(user='hourlog', 
                            passwd='gp4hourlog',
                            host='localhost',
                            db='hour_log')
    cur = cnx.cursor()
    cur.execute("SELECT * from temp_attachment")
    numrec = 0
    
    for row in cur.fetchall():
        name = row[2]
        entry = row[7]
        filename = row[4]
        numrec += 1
        logging.info('Attachment #: %s. Name: %s. Entry: %s', numrec, name, entry)
        res = postAttachment(entry, name, filename, user, passwd)        
    cnx.close()

    logging.info('Total number of attachments processed %s', numrec)
#
#
#
# reload(sys)  
# sys.setdefaultencoding('UTF8')

logging.basicConfig(filename='migrate.log',level=logging.INFO, filemode='w')
# logging.captureWarnings(True)

username = raw_input('Olog user name: ')
passwd = getpass.getpass('Olog password: ')
# postAttachment(73979,'test.txt', 'test.txt', username, passwd)
migrateAttachment(username, passwd)
#
#
#
