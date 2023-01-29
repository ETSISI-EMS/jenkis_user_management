#!/bin/bash

# check that a file is passed as argument
if [ $# -eq 0 ]
  then
    echo "CSV file is missing."
    exit 1
fi

set -a # automatically export all variables
source .env
set +a

java -jar jenkins-cli.jar -s $URL -auth $USER:$PWD groovy = < load_credentials.groovy `python csv2json.py $1 | tr -d "\r\n" | tr --d '[:space:]'`