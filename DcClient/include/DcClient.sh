#!/bin/sh

AppServer=https://dc7-dev1.anu.edu.au:8443
AppServer=https://datacommons.anu.edu.au:8443
BagsDir=~/Bags
DcClientAppDir=`dirname $0`

echo Server: $AppServer
echo Bags Dir: $BagsDir
echo App Dir: $DcClientAppDir

java -Xmx1024m\
	-Djavax.net.ssl.trustStore=$DcClientAppDir/cacerts\
	-Dapp.server=$AppServer\
	-Dlocal.bagsDir=$BagsDir\
	-jar DcClient-0.0.1-SNAPSHOT.jar $*
