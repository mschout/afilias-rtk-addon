#!/bin/bash

##############################
#          GLOBALS           #
##############################

APP_NAME="$(basename $0)"

##############################
#         FUNCTIONS          #
##############################

function usage {
  printf "NAME\n"
  printf "    ${APP_NAME} - run an example provided in this release\n\n"
  printf "SYNOPSIS\n"
  printf "    ${APP_NAME} <version> <example> <epp host> <epp host port> <epp client id> <epp password> ...\n\n"
  printf "DESCRIPTION\n"
  printf "    Runs one of the many example Java programs provided in this release.\n\n"
  printf "    <version>\n"
  printf "        RTK version. \n"
  printf "        Can be one of the following: \"02\", \"0705\", \"rtk\"\n"
  printf "        Recommended: \"rtk\"\n\n"
  printf "    <example>\n"
  printf "        Example program name.\n"
  printf "        Can be one of the following:\n\n"
  printf "            AeroSessionExample\n"
  printf "            DomainProtocolExample\n"
  printf "            IDNExample\n"
  printf "            IDNGUIExample\n"
  printf "            IprSessionExample\n"
  printf "            MobiSessionExample\n"
  printf "            OxrsSessionExample\n"
  printf "            OxrsTransferExample\n"
  printf "            RgpSessionExample\n"
  printf "            SecDNSExample\n"
  printf "            SessionExample\n"
  printf "            TestNumberExample\n"
  printf "            TrademarkExample\n"
  printf "            TrademarkSessionExample\n"
  printf "            AssociationContactExample\n"
  printf "            SupplementalDataExample\n"
  printf "            LaunchSunriseExample\n"
  printf "            LaunchClaimsExample\n\n"
  printf "            IDValidationDomainUpdateExample\n\n"
  printf "            FeeSessionExample\n\n"
  printf "            DomainSyncSessionExample\n\n"
  printf "            AuExtensionSessionExample\n\n"
  printf "            OrganizationSessionExample\n\n"
  printf "            PolicyDeleteSessionExample\n\n"
  printf "    <epp host>\n"
  printf "        EPP server host name.\n\n"
  printf "    <epp host port>\n"
  printf "        EPP server port number.\n\n"
  printf "    <epp client id>\n"
  printf "        EPP user name.\n\n"
  printf "    <epp password>\n"
  printf "        EPP client's password.\n\n"
}

##############################
#            MAIN            #
##############################

if [[ -z "$RTK_HOME" ]]; then
  printf "You must set RTK_HOME to point to the RTK installation directory.\n"
  exit 1
fi

if [[ -z "$ADDON_HOME" ]]; then
  printf "You must set ADDON_HOME to point to the Afilias Addon installation directory.\n"
  exit 1
fi

if [[ ${#} -lt 2 ]]; then
    printf "Expected 1 or more parameters.\n\n"
    usage
    exit 1
fi

VERSION=$1
EXAMPLE_CLASS=$2
EPP_HOST=$3
EPP_PORT=$4
CLIENT_ID=$5
PASSWORD=$6

shift 6

case $VERSION in 
    "02")
    EXAMPLE_PACKAGE="addon.example";;
    "0705")
    EXAMPLE_PACKAGE="extension.epp0705.example";;
    "rtk")
    EXAMPLE_PACKAGE="extension.epprtk.example";;
    *)
    echo version must be 02, 0705 or rtk
    exit 1
esac

ADDON_JAR=$ADDON_HOME/java/lib/afilias-rtk-addon.jar
java -Dssl.props.location=$RTK_HOME/java/ssl \
     -Drtk.props.file=$RTK_HOME/java/etc/rtk.properties \
     -Djavax.net.ssl.trustStore=$RTK_HOME/java/ssl/trustStore.jks \
     -cp $RTK_HOME/java/lib/xerces.jar:$RTK_HOME/java/lib/regexp.jar:$RTK_HOME/java/lib/log4j.jar:$RTK_HOME/java/lib/bcprov-jdk14-115.jar:$RTK_HOME/java/lib/epp-rtk-java.jar:$ADDON_JAR:$ADDON_HOME/java/lib/IDNSDK.jar \
     com.liberty.rtk.$EXAMPLE_PACKAGE.$EXAMPLE_CLASS \
         $EPP_HOST $EPP_PORT $CLIENT_ID $PASSWORD $*
