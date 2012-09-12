@echo off
setlocal
set AppServer=https://datacommons.anu.edu.au:8443
rem set AppServer=https://l3a5006.uds.anu.edu.au:9443
rem set AppServer=https://dc7-dev1.anu.edu.au:8443
rem set AppServer=https://dc7-dev2.anu.edu.au:8443

rem Set environment variables
set BagsDir=C:\Rahul\Bags_dev1
set DcAppPath=%~dp0

rem Display environment variables being used.
echo.
echo App Server: %AppServer%
echo Local Bags Location: %BagsDir%

rem Execute DcClient.
java -Xmx1024m -Djavax.net.ssl.trustStore=%DcAppPath%cacerts -Dapp.server=%AppServer% -Dlocal.bagsDir=%BagsDir% -jar %DcAppPath%DcClient-0.0.1-SNAPSHOT.jar %*
endlocal
echo.