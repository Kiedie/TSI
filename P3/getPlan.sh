#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "getPlan - A quick ff wrapper"
    echo "----------------------------"
    echo "This program needs 1 parameter."
    echo "Usage: ./getPlan <name>"
    echo "<name>      Name of problem, the program loads the .ppdl files as required"
    echo "            The problem and domains name must be <name>Dom.pddl and <name>Prob.pdll"
    exit
fi

prefix=$1
domain=$prefix"Dom.pddl"
problem=$prefix"Prob.pddl"


if [ -f "$domain" -a -f "$problem" ]; then

./ff -o $domain -f $problem -O -g 1 -h 1

else

    if [ ! -f "$domain" ]; then
        echo ">ERROR: File '$domain' does not exist."
    fi

    if [ ! -f "$problem" ]; then
        echo ">ERROR: File '$problem' does not exist."
    fi

fi
