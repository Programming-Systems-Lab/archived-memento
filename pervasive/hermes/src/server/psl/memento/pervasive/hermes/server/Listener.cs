using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.util.constants;
using psl.memento.pervasive.hermes.util.exceptions;
using psl.memento.pervasive.hermes.server.ClientHelp;


namespace psl.memento.pervasive.hermes.server
{
	/// <summary>
	/// Listener is where we listen for incoming clients. 
	/// we then spawn them off with there own thread.
	/// </summary>
	public class Listener
	{
		//we are going to keep track of all of our clients in here
		//we put client in this hashtable
		private PVCServer _server;
		private Socket _serverSocket;
		private int _port;
		private bool _open;

		public Listener(PVCServer server)
		{
			this._server = server;
		}

		public void start()
		{
			this._open = true;
			//get our port
			try
			{
				this._port = Constants.getIntConst("ServerPort");
			}
			catch(ConstantNotFoundException cnf)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "ServerPort constant not found.  Reverting to port 5500", cnf);
				this._port = 5500;
			}
			
			//open our listener
			try
			{
				this._serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
				this._serverSocket.Blocking = true ;

				IPHostEntry IPHost = Dns.Resolve(Dns.GetHostName()); 
				string[] aliases = IPHost.Aliases; 
				IPAddress[] addr = IPHost.AddressList; 
				IPEndPoint ipepServer = new IPEndPoint(addr[0], 5500);
				this._serverSocket.Bind(ipepServer);
				this._serverSocket.Listen(-1);
			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.FATAL_PRIORITY, "Server socket cannot be opened.", e);
			}

			//as a connection comes in create a new client and add it to the server hashtable
			try
			{
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "We are about to start listening." );
				while(this._open)
				{
					Socket client = this._serverSocket.Accept();
					//client.Blocking = 0;
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Incoming Client" );
					
					if(client.Connected)
					{
						//as a client comes in we grab it and send it to a new thread for handling
						ClientHandler incomingClient = new ClientHandler(client, this._server);
						new Thread(new ThreadStart(incomingClient.start)).Start();
						Logger.getLogger().log(Logger.INFO_PRIORITY, "New client has been recieved and spanwed off to thread.");
					}
					else 
					{
						Logger.getLogger().log(Logger.INFO_PRIORITY, "Client Lost.");
					}

					Logger.getLogger().log(Logger.INFO_PRIORITY, "Return to listening for clients.");

				}
			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "No Longer Listening for incoming clients.", e);
			}
				
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Listener is exiting. Restart is possible.");
		}

		//stop the listener
		public void stop()
		{
			this._serverSocket.Close();
			this._open = false;
		}

			
	}
}
