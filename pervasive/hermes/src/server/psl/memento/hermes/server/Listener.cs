using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using Utilities.log;
using Utilities.constants;


namespace PVCServer
{
	/// <summary>
	/// Summary description for Listener.
	/// </summary>
	public class Listener
	{
		//we are going to keep track of all of our clients in here
		//we put client in this hashtable
		private PVCServer _server;
		private TcpListener _clientListener;
		private int _port;

		public Listener(PVCServer server)
		{
			this._server = server;
		}

		public void start()
		{
			//get our port
			try
			{
				this._port = Constants.getIntConst("ServerPort");
			}
			catch(ConstantNotFoundException cnf)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "ServerPort constant not found.  Reverting to port 1754", cnf);
				this._port = 1754;
			}
			
			//open our listener
			try
			{
				this._clientListener = new TcpListener(this._port);
			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.FATAL_PRIORITY, "Server socket cannot be opened.", e);
			}

			//as a connection comes in create a new client and add it to the server hashtable
			try
			{
				while(true)
				{
					Socket client = this._clientListener.AcceptSocket();
					
					if(client.Connected)
					{
						this._server.addClient(new ClientHandler(client, this._server));
					}

				}
			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "No Longer Listening for incoming clients.", e);
			}
				
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Listener is exiting. Restart is possible.");
		}

			
	}
}
