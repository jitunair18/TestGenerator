#!/bin/sh

# pass group as variable to master script
java -jar swaggerParserUtility-0.01-combined-with-tests.jar $1

# use exit status as exit code for program
STATUS=$?

pathname=$1
echo $pathname
dirName=$(basename $pathname)
echo $dirName
echo $STATUS
echo mylogReport=./logfile.log  >>  "%qm_AttachmentsFile%" 
exit $STATUS
