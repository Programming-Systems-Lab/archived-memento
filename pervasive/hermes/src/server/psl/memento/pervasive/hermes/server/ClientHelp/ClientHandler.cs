using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.server;
using System.Xml;

namespace psl.memento.pervasive.hermes.server.ClientHelp
{
	/// <summary>
	/// Summary description for ClientHandler.
	/// </summary>
	public class ClientHandler
	{
		private PVCServer _server;
		private Socket _stream;
		private Client _client;
		private ClientRequestHandler _crh;

		public ClientHandler(Socket stream, PVCServer server)
		{
			this._client = new Client();
			this._server = server;
			this._stream = stream;
			this._crh = new ClientRequestHandler(this, new NetworkStream(this._stream));
		}

		//this is where the magic starts to happen.
		//we get our first xml message using our xml decoding class and set up our client object
		//we then wait for the client to send us messages
		public void start()
		{
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "About to send out the client request handler thread.");
			//we start off our client request handler and it calls things on us
			new Thread(new ThreadStart(this._crh.start)).Start();
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Thread for client request handler has been started.");
			
		}


		public void connect()
		{
			this._server.addClient(this);
		}

		public string getClientID()
		{
			return this._client.getID();
		}
		
	}
}
