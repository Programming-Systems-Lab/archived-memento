============================
README FOR CONTENT EXTRACTOR
============================

To use the content extractor through the console, the following format 
must be used:

	java ContentExtractor [input file] [output file] {settings file}

The input file and the output file are necessary but the settings file is 
optional. The default settings file is settings.txt. In order to change 
the settings without the settings GUI provided in the Proxy, the file must be 
directly edited. The file is saved using a Java Properties file. See the 
Java APIs for the proper format.



To use the content extractor as a proxy, run the batch file. It will start
the proxy up on port 4000. Point your web browser to listen to port 4000 on
the localhost if you run it on your own machine, or on the name of the 
particular server that you run it on.

In Internet Explorer, this can be done by going to Tools -> Internet Options ->
Connections -> LAN Settings. Check the proxy server box and set the server name to
localhost (if on local machine) or to the server that you are running the proxy on.
Default port is 4000 unless otherwise specified.