#!/usr/bin/env bash

clear
[[ ! -e "resources.txt" ]] && echo "resources.txt found! make sure your links are there" && exit

java -jar anima.jar
