@echo off
cd ..
javaw -cp lib/commons-beanutils.jar;lib/commons-collections.jar;lib/commons-digester.jar;lib/commons-httpclient.jar;lib/commons-logging.jar;lib/commons-logging-api.jar;lib/icu4j.jar;lib/jakarta-poi-1.5.1.jar;lib/JConfig.zip;lib/jena.jar;lib/psl-frax.jar;lib/xerces.jar psl.chime4.server.frax.gui.FraxGUI
cd bin