using System;
using System.IO;
using System.Net.Sockets;
using psl.memento.pervasive.hermes.util.log;


namespace psl.memento.pervasive.hermes.server.ClientHelp
{
	/// <summary>
	/// Summary description for XMLSocketParser.
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
				
				/*
				if(this._client.Available == 0 && j != 0)
				{
					return false;
				}
				*/
				rcount = this._client.Receive(_recs,_recs.Length,0);
				if(rcount <= 0)
				{
					return false;
				}
				tempmessage = System.Text.Encoding.ASCII.GetString(_recs) ;
				clientmessage += tempmessage.Substring(0,rcount);

				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is what we first pulled off: " + clientmessage);

				for(int i = 0; i < (clientmessage.Length - 2); i++)
				{
					//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "We will no compare the following string --" + clientmessage.Substring(i, 3) + "--");

					if(clientmessage.Substring(i, 3).Equals(RuntimeConstants.XML_MESSAGE_END))
					{
						if(clientmessage.Length > (i+3))
						{
							//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "God damn: " + i + " and length " + clientmessage.Length);
							this._leftover = clientmessage.Substring((i + 3), (clientmessage.Length - i - 3));
							//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Left over: " + this._leftover);

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
