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
	/// This class will be responsible for reading in xml from the client, parsing it to xml and figuring out what method to call on the client handler.
	/// </summary>
	public class ClientRequestHandler
	{
		private XmlTextReader _xmlReader;
		private NetworkStream _clientStream;
		private ClientHandler _handler;

		public ClientRequestHandler(ClientHandler handler, NetworkStream clientStream)
		{
			this._xmlReader = new XmlTextReader(clientStream);
			this._handler = handler;
			//
			// TODO: Add constructor logic here
			//
		}

		public void start()
		{
			//begin decoding
			try
			{

				//we will decode the xml off the stream from this point


			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Decoding problems.", e);
			}

	}
}
