#!/bin/bash

export CLASSPATH=target/moman-1.0.0-SNAPSHOT.jar:lib/commons-logging-1.1.jar:lib/commons-validator-1.3.1.jar:lib/dom4j-1.6.1.jar:lib/jaxen-1.1.1.jar:lib/moman.jar:lib/nanoxml-2.2.3.jar:lib/ofx4j-1.2.jar:lib/org.eclipse.osgi_3.5.1.R35x_v20090827.jar:lib/spring-beans-2.5.6.jar:lib/spring-context-2.5.6.jar:lib/spring-core-2.5.6.jar

> urls.csv
for url in $(cat fi-urls); do
    wget -q -O out $url
    name=$(cat out | head -2 | tail -1 | awk -F'>' '{print $5}' | awk -F'<' '{print $1}' | sed -e 's/ *$//g' -e 's/,/%COMMA%/g')
    fiid=$(cat out | head -2 | tail -1 | awk -F'>' '{print $14}' | awk -F'<' '{print $1}'| sed -e 's/,/%COMMA%/g')
    org=$(cat out | head -2 | tail -1 | awk -F'>' '{print $20}' | awk -F'<' '{print $1}' | sed -e 's/,/%COMMA%/g')
    furl=$(cat out | head -2 | tail -1 | awk -F'>' '{print $27}' | awk -F'<' '{print $1}'| sed  -e 's/,/%COMMA%/g')
    if [ "$fiid" != "" ]; then
        echo "$name,$furl,$fiid,$org" >> urls.csv
    fi
done

java net.deuce.moman.fi.service.BuildFinancialInstitutionsFile urls.csv

#rm out urls.csv
