#!/bin/bash

PATH_TO_CODE_BASE=`pwd`


MAIN_CLASS="ar.edu.itba.cripto.commands.StegoBMP"

java -cp 'lib/jars/*' $MAIN_CLASS "$@"