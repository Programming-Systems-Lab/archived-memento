Rob Bruce
rmb51
Memento/Pervasive/Hermes


The client can be delpoyed to a pocketPC device or to the emulator. when you hit the start
button on VS it will prompt you for the device to deploy.  You can also build cabs for smartdevices.


Client:
The action of this application happens in the ClientHandler folder and
the MainForm.cs file.

	ClientHandler:
	ClientHandler is the inbetween class for user interface and communication with
	server.  Through it the MainForm (user interface) access the ClientRequestHandler
	which actually send messages to the server.  The ServerResponseHandler is responsible
	for handling incoming xml messages from the server.
	
	MainForm:
	MainForm is the main interface with with the user. The user has some options like
	connecting to a chatBuddy, disconnecting from a server, talking.  This form
	is for the most part done.  The ClientHandler is where most work is left to be
	finished.
	
XML Data:
All XML data is sent with a '|||' appended to the end. This is used for decoding
messages by both server and client. 
