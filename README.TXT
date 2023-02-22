Copyright (c) 2001-2003 Afilias Limited  All rights reserved.


TABLE OF CONTENTS
==================
1. Afilias RTK Add-on Overview
2. Quick Start
3. Environment
4. Building The Source
5. Documentation

1. Afilias RTK Add-on Overview
==============================
The Afilias RTK Add-on provides Afilias .info specific extension to EPP.

To use this package, you will require at least:
    + EPP RTK (download from sourceforge.net)
    + Java JRE 1.6

To make changes and compile this package, you will additionally require:
    + Java SDK 1.6
    + knowledge of the workings of the EPP RTK
    + knowledge of EPP, the EPP IDLs and the epp_Unspec and
      epp_Extension interfaces
    + knowledge of XML

The Afilias Add-on requires the EPP RTK in order to run and to build.

Ant and Xerces-J are required in order to build the Afilias Add-on.  The Ant
and Xerces jars are included in the EPP RTK.  As long as the environment
variable RTK_HOME points to the installation of the RTK, Ant and Xerces will
be found.

Javadocs can be viewed in the doc/api-doc  
directory.

The bin directory contains some script to make running the 
samples a little easier.  See the README in there for
more info.

2. Quick Start
===============

Define the following environment variables:
export RTK_HOME=<path to epp-rtk>
export ADDON_HOME=<path to afilias-rtk-addon>

To run without modification, use script run_example.[sh|bat] to simplify
the running of the examples provided in this package.

As long as you include the appropriate Afilias RTK Add-on jar file 
in the CLASSPATH when using the EPP RTK, you'll have access to 
Afilias's Add-on classes.  If you are using the 0.7.x or greater
series of EPP Java RTKs (at least 0.7.4), then you used the 
afilias-rtk-addon.jar file (and version JDK 1.6). 

You can view the sample source code in the following subdirectories:
    src/com/liberty/rtk/addon/example (for .info)
    src/com/liberty/rtk/extension/epprtk/example (for .org)

In each of the files, usage of the particular add-on is demonstrated.  
Running of the sample can be accomplished using run_example.[sh|bat].
For Bash users, the bin directory contains easy-to-use scripts to
run the RGP and IDN samples.  Eventually, all samples will be runnable
from there (and with Windows batch files too).

To see the usage of run_example, run it without parameters.  It will
list the samples which are runnables.  
Using plain "IDNGUIExample" will
use the newer jar file and expect RTK to be available in $RTK_HOME.

You will see output which displays the responses from the server.

Note for .info samples: The protocol of choice in the "add-on" samples is TCP 
with TLS (SSL).  
To use plain TCP, you must edit the samples and rebuild the samples.  
The "extension" samples (for .org) use the transport as indicated in the 
rtk.properties file.


3. Environment
===============
As stated earlier, the Afilias RTK Add-on requires the EPP RTK.
To meet this requirement, include the EPP RTK jar files in the CLASSPATH.


4. Building The Source
=======================
Development was done using Java JDK 1.6.

The Afilias RTK Add-on comes ready-to-use with two bundled jar files (see
Quickstart above for an explanation of this).  If you wish to experiment 
with the samples or change the source code, you will need to rebuild a 
jar file.

The cross-platform build tool Ant is used to build the Afilias Add-on.  Please
see http://jakarta.apache.org/ant for more information on Ant.

You do not need to download Ant in order to build the Afilias Add-on, as the
necessary Ant jar file is included in the main RTK distribution. The Ant 
build files are included in the etc directory.  Copy the build.* files 
from etc to the main java directory (i.e. "cp etc/build.* .").

You can run Ant for the following targets:

    ./build.[sh|bat]               - Build classes from source
    ./build.[sh|bat] targets       - Show the list of targets
    ./build.[sh|bat] all           - Clean and build dist
    ./build.[sh|bat] library       - Build classes from source (in build dir)
    ./build.[sh|bat] javadoc       - Build the Javadocs (in build dir)
                                     for the code found in the src directory
    ./build.[sh|bat] dist          - Build everything and create a distribution
    ./build.[sh|bat] jar           - Build the jar file portion of the distribution
                                     for the code found in the src directory
    ./build.[sh|bat] clean         - Remove generated files and directories

build.[sh|bat] requires that the environment variable RTK_HOME be set.
This is required to find the Ant, Xerces-J and the EPP RTK jar files.

Everything is best compiled with JDK 1.6.

A Makefile is included for cheating and convenience.

5. Documentation
=================
Javadocs are included in the doc/api-doc directory.
Please also take a look at the sample code provided.  See above
for the location of the samples.

