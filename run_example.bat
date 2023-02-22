@echo off
rem ---------------------------------------------------------------------------
rem Modified from the original (by dankirkd) in the epp-rtk
rem ---------------------------------------------------------------------------
rem Supported Environment Variables (default values in square brackets):
rem
rem   JAVA_HOME             Java Development Kit directory. [REQUIRED]
rem
rem   RTK_HOME              Distribution directory for Registrar Toolkit. [REQUIRED]
rem
rem   ADDON_HOME            Distribution directory for the Afilias RTK Addon [REQUIRED]
rem
rem ---------------------------------------------------------------------------


rem ----- Verify and Set Required Environment Variables -----------------------

set USAGE="usage: %0 OxrsTransferExample|DomainProtocolExample|IDNExample|IDNGUIExample|oldIDNGUIExample|TrademarkExample|TestNumberExample epp_host_name epp_host_port epp_client_id epp_password ..."

if not "%JAVA_HOME%" == "" goto gotJavaHome
  echo You must set JAVA_HOME to point at your Java Development Kit directory
  goto cleanUp
:gotJavaHome

if not "%RTK_HOME%" == "" goto gotRTKHome
  echo You must set RTK_HOME to point at your Registrar Toolkit directory
  goto cleanUp
:gotRTKHome

if not "%ADDON_HOME%" == "" goto gotADDONHome
  echo You must set ADDON_HOME to point at your Afilias Addon installation directory
  goto cleanUp
:gotADDONHome

set EXAMPLE_CLASS=%1
set EPP_HOST=%2
set EPP_PORT=%3
set CLIENT_ID=%4
set PASSWORD=%5

if not "%EXAMPLE_CLASS%" == "" goto gotExampleClass
  echo %USAGE%
  goto cleanUp
:gotExampleClass

set EXAMPLE_PACKAGE=addon.example

if not "%EXAMPLE_CLASS%" == "OxrsTransferExample" goto gotExamplePackage
  set EXAMPLE_PACKAGE=extension.epp0705.example
if not "%EXAMPLE_CLASS%" == "DomainProtocolExample" goto gotExamplePackage
  set EXAMPLE_PACKAGE=extension.epp0705.example
:gotExamplePackage

set ADDON_JAR=%ADDON_HOME%\java\lib\afilias-rtk-addon.jar

rem ----- Set Up The Runtime Classpath ----------------------------------------

if "%CLASSPATH%" == "" goto noClasspath
set CP=%RTK_HOME%\java\lib\xerces.jar;%RTK_HOME%\java\lib\regexp.jar;%RTK_HOME%\java\lib\log4j.jar;%RTK_HOME%\java\lib\bcprov-jdk14-115.jar;%RTK_HOME%\java\lib\epp-rtk-java.jar;%ADDON_JAR%;%ADDON_HOME%\java\lib\IDNSDK.jar;%CLASSPATH%
goto gotClasspath
:noClasspath
set CP=%RTK_HOME%\java\lib\xerces.jar;%RTK_HOME%\java\lib\regexp.jar;%RTK_HOME%\java\lib\log4j.jar;%RTK_HOME%\java\lib\bcprov-jdk14-115.jar;%RTK_HOME%\java\lib\epp-rtk-java.jar;%ADDON_JAR%;%ADDON_HOME%\java\lib\IDNSDK.jar
:gotClasspath

rem ----- Run The Example -----------------------------------------------------

%JAVA_HOME%\bin\java -Dssl.props.location="%RTK_HOME%\java\ssl" -Drtk.props.file=%RTK_HOME%\java\etc\rtk.properties -classpath "%CP%" com.liberty.rtk.addon.example.%EXAMPLE_CLASS% %EPP_HOST% %EPP_PORT% %CLIENT_ID% %PASSWORD% %6

rem ----- Clean Up Environment Variables --------------------------------------

:cleanup


