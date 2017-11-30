#!/bin/sh
# pass group as variable to master script
BASEPATH=/local/rqm/services/swaggerParserUtility
LOGFILE="$BASEPATH/logfile.log"
CUCUMBERPATH="$BASEPATH/target/cucumber-reports/cucumber-pretty"
RQMPATH=/rqm/services/swaggerParserUtility
>$LOGFILE
java -jar swaggerParserUtility-0.01-combined-with-tests.jar $1
status="${?}"
mkdir -p results
cp  $LOGFILE results/.
cp -r $CUCUMBERPATH results/.
tar -cvf results.tar results
scp -i ~/.ssh/id_rsa.rqmuser-rqm-adapter-1 $BASEPATH/results.tar rqmuser@rqm-adapter-1:$RQMPATH/.
rm -rf results.tar results
echo $status
exit $status
