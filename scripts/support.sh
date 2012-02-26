t=`dirname ${rfile}`
fqfiledir=`(cd $t && pwd)`
fqjardir=${fqfiledir}/../jars/
fqsrcdir=${fqfiledir}/../src/
function startRMIRegistry ()
{
    rmiregrunningP=`ps aux | grep rmiregistry | grep -v grep`
    if ! [[ -n ${rmiregrunningP} ]];
    then 
	echo "Starting rmiregistry"
	rmiregistry &
    fi
}
