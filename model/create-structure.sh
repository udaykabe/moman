#!/bin/bash

echo "create database moman CHARACTER SET = 'utf8';" | mysql -u $1 -p 
mvn hibernate3:hbm2ddl

