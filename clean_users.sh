#!/bin/bash

set -a # automatically export all variables
source .env
set +a

# check that a file is passed as argument
if [ $# -eq 0 ]
  then
    java -jar jenkins-cli.jar -s $URL -auth $USER:$PWD groovy = < clear_users.groovy '{"users":[]}'  
    exit 0
else
  java -jar jenkins-cli.jar -s $URL -auth $USER:$PWD groovy = < clear_users.groovy `python csv2json.py $1 | tr -d "\r\n" | tr --d '[:space:]'`
  exit 0
fi



