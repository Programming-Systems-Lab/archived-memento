using System;
using System.Collections;
using System.Threading;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.server.ClientHelp;

namespace psl.memento.pervasive.hermes.server
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class PVCServer
	{
		// clients hashtable stores reference to all connected clients
		private Hashtable _clients;
		// chats hashtable stores reference to all chats in progress
		private Hashtable _chats;
		private Listener _clientListener;
		private bool _isrunning;

		public PVCServer()
		{
			this._isrunning = false;
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Server is ready to start up.");
			this._clients = new Hashtable();
			this._chats = new Hashtable();
			this._clientListener = new Listener(this);
			
		}
	//start server
		public void start()
		{
			this._isrunning = true;
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Server is starting up now.");
			this._clientListener.start();
		}
/// <summary>
/// Called by the the listener.
/// </summary>
/// <param name="ch"></param>

		public void addClient(ClientHandler ch)
		{
			if(this._isrunning)
			{
				this._clients.Add(ch.getClientID(), ch);
			}
			else
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Server is not running");
			}	
		}

		public void removeClient(ClientHandler ch)
		{
			this._clients.Remove(ch.getClientID());
		}

	}
}
