using System;
using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using psl.memento.pervasive.hermes.util.log;
using System.Xml;

namespace psl.memento.pervasive.hermes.server.ClientHandler
{
	/// <summary>
	/// Summary description for ClientHandler.
	/// </summary>
	public class ClientHandler
	{
		private PVCServer _server;
		private ClientRequestHandler _crh;
		
		public ClientHandler(NetworkStream client, PVCServer server)
		{
			this._client = client;
			this._server = server;
			this._xmlReader = new XmlTextReader(this._clientStream);
			this._crh = new ClientRequestHandler(this, client);
		}

	}
}
