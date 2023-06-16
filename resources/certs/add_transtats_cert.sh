#!/usr/bin/env sh
# File: add_transtats_cert.sh
# Author: jxandercrawford@gmail.com
# Date 2023-06-15
# Purpose: Add the transtats.bts.gov certificate to java keystore.

JAVA_VERSION="temurin-11.jdk"
CACERTS="/Library/Java/JavaVirtualMachines/$JAVA_VERSION/Contents/Home/lib/security/cacerts"

# Ref: https://stackoverflow.com/questions/4325263/how-to-import-a-cer-certificate-into-a-java-keystore
sudo keytool -importcert -file transtats.bts.gov.cer -keystore $CACERTS -alias "transtats"
