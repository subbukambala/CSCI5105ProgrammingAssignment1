#!/bin/bash
rfile=`which $0`
rfiledir=`dirname ${rfile}`
. ${rfiledir}/support.sh
startRMIRegistry
(cd ${fqsrcdir} && java -cp .:${fqsrcdir}/:${fqjardir}commons-cli-1.2.jar SuperPeer $*)
