using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using psl.memento.pervasive.hermes.util.log;
using System.Xml;


namespace psl.memento.pervasive.hermes.server.ClientHelp
{
	/// <summary>
	/// This class will be responsible for reading in xml from the client, parsing it to xml and figuring out what method to call on the client handler.
	/// </summary>
	public class ClientRequestHandler
	{
		private XmlTextReader _xmlReader;
		private NetworkStream _ns;
		//private NetworkStream _clientStream;
		private ClientHandler _handler;

		public ClientRequestHandler(ClientHandler handler, NetworkStream clientStream)
		{
			this._ns = clientStream;
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

				char[] incomingStuff = new char[10000];
				System.IO.StreamReader bs = new System.IO.StreamReader(this._ns, System.Text.ASCIIEncoding.ASCII);
				bs.ReadBlock(incomingStuff, 0, 10);
				//bs.Length;

				//this._ns.BeginRead(incomingStuff, 0, 100, null, new object());
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is length of the bytes coming in: " + new string(incomingStuff));
			
				/*
				
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "We are trying to decode. "  + this._ns.DataAvailable);
				//we will decode the xml off the stream from this point
				this._xmlReader.MoveToElement();
				string moreXml = this._xmlReader.ReadOuterXml();
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "More XML: " + moreXml);
				this._xmlReader.MoveToContent();
				string innerXml = this._xmlReader.ReadInnerXml();
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "XML: " + innerXml);
				//this._ns.DataAvailable;
			*/
			
			}
			catch(System.Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Decoding problems.", eee);
			}
		}

	}
}
