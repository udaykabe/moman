#!/bin/bash

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006 -classpath $(dirname $0)/server/target/moman-server-1.0.0-SNAPSHOT.jar -javaagent:$HOME/.m2/repository/org/springframework/spring-agent/2.5.6/spring-agent-2.5.6.jar net.deuce.moman.Main
