using System;
using System.IO;
using System.Net.Sockets;
using psl.memento.pervasive.hermes.client.util;
using psl.memento.pervasive.hermes.client;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	/// <summary>
	/// XMLSocketParser this class is used to pull messages off the socket.
	/// it is necessary since all XML message are sent with a '|||' at the end to signal
	/// the end of a message.  so when getXmlMessage is called a loop begins pulling
	/// off blocks of text from the stream. when it gets a full buffer it checks for the 
	/// end of xml message '|||' and returns the xml before this signal.
	/// </summary>
	public class XMLSocketParser
	{
		private Socket _client;
		private string _leftover;
		private byte[] _recs;

		public XMLSocketParser(Socket client)
		{
			this._client = client;
			this._leftover = "";
			this._recs = new byte[65536];
		}

		public bool getXMLMessage(ref string clientmessage)
		{
			string tempmessage = "";
			clientmessage = this._leftover + clientmessage;
			int rcount = 0;

			for(int j = 0; j < RuntimeConstants.XML_DECODING_ATTEMPTS; j++)
			{
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "This is the current pull attempt: " + j);

				rcount = this._client.Receive(_recs,_recs.Length,0);
				if(rcount <= 0)
				{
					return false;
				}
				tempmessage = System.Text.Encoding.ASCII.GetString(this._recs, 0, this._recs.Length);
				clientmessage += tempmessage.Substring(0,rcount);

				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is what we first pulled off: " + clientmessage);

				for(int i = 0; i < (clientmessage.Length - 2); i++)
				{
					//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "We will no compare the following string --" + clientmessage.Substring(i, 3) + "--");

					if(clientmessage.Substring(i, 3).Equals(RuntimeConstants.XML_MESSAGE_END))
					{
						if(clientmessage.Length > (i+3))
						{
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "God damn: " + i + " and length " + clientmessage.Length);
							this._leftover = clientmessage.Substring((i + 3), (clientmessage.Length - i - 3));
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Left over: " + this._leftover);

						}
						else
						{	
							this._leftover = "";
						}
						clientmessage = clientmessage.Substring(0, i);
						return true;
					}
				}

			}
			Logger.getLogger().log(Logger.INFO_PRIORITY, "No xml message could be parsed after 10 consecutive pulls from the stream" );
			return false;

		}

		public void cleanup()
		{
			try
			{
				this._client.Close();
			}
			catch(Exception poop)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "On clean up socket close problem: ", poop); 
			}
		}
	}
}
