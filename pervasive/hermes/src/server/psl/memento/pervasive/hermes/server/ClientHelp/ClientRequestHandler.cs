using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading ;
using System.Collections;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.server.ChatHelper;
using System.Xml;
using System.IO;
using System;


namespace psl.memento.pervasive.hermes.server.ClientHelp
{
	/// <summary>
	/// This class will be responsible for reading in xml from the client, parsing it to xml and figuring out what method to call on the client handler.
	/// </summary>
	public class ClientRequestHandler
	{
		private XmlTextReader _xmlReader;
		//private NetworkStream _ns;
		//private NetworkStream _clientStream;
		private ClientHandler _handler;
		private Socket _clientsock;
		private XMLSocketParser _xsp;

		public ClientRequestHandler(ClientHandler handler, Socket clientsock)
		{
			//this._ns = clientStream;
			//this._xmlReader.
			this._handler = handler;
			this._clientsock = clientsock;
			this._xsp = new XMLSocketParser(clientsock);
			this._xmlReader = null;
		}

		public void start()
		{
			//begin decoding
			try
			{
				
				string clientmessage = null;
				
				bool rcount = false;

				//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Check for client stats and socket info: " + this._handler._client._status + 
				//	" and " + this._clientsock.Connected);

				#region Decoding and Action Loop
				while(this._clientsock.Connected && (this._handler._client._status == RuntimeConstants.PENDING || this._handler._client._status == RuntimeConstants.CONNECTED))
				{

				//	Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the status of our connection: " + this._clientsock.Connected);

					clientmessage = null;
					rcount = this._xsp.getXMLMessage(ref clientmessage);
					//rcount = this._clientsock.Receive(recs,recs.Length,0);

					if(rcount)
					{
						try
						{
							//clientmessage = System.Text.Encoding.ASCII.GetString(recs) ;
							//clientmessage = clientmessage.Substring(0,rcount);
					
					
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is what they said: " + clientmessage + " and length : " + rcount);
					
							this._xmlReader = new XmlTextReader(new StringReader(clientmessage));
							this._xmlReader.ReadStartElement();
							this._xmlReader.ReadStartElement();
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "next node: " + this._xmlReader.Name);
							/*
					
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "loaded xml reader: " + this._xmlReader.Name);
					
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "next node: " + this._xmlReader.Name);
							*/
							//this._xmlReader.Name
				
							//the current xmlReader.Name is the message name we care about so now we do our if else statements

							//the client is requesting a connect here
							if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CONNECT)
							{
								//first we check if the client is in the pending connect state
								//if not pending then this request should not be honored
								if(!this._handler._client._status.Equals(RuntimeConstants.PENDING))
								{
									Logger.getLogger().log(Logger.INFO_PRIORITY, "Client - " + 
										this._handler._client.getChatName() + "@" + 
										this._handler._client.getIP() + 
										" send connect request with client status set to: " + 
										this._handler._client._status);
								}
									//we handle all the connect stuff
								else
								{
									//we need to pull all the info off the message
									//ip address
									this._xmlReader.ReadStartElement();
									this._handler._client.setIP(this._xmlReader.ReadElementString());
									this._handler._client.setChatName(this._xmlReader.ReadElementString());

									//now we need to assign the client a unique ID, add client to the hashtable
									//of connected clients and return a connection element.

									this._handler._client.setID(this._handler._server.getClientID());
									this._handler._client._status = RuntimeConstants.CONNECTED;
									this._handler._server.addClient(this._handler);

									Logger.getLogger().log(Logger.DEBUG_PRIORITY, this._handler._client.ToString());
									ThreadPool.QueueUserWorkItem(new WaitCallback(this._handler._responseHandler.connectConfirm));

								}

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CONFIRM_CONNECT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DISCONNECT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHAT_INVITE)
							{
								//the client wants to chat with someone
								if(this._handler._client._status.Equals(RuntimeConstants.PENDING))
								{	
									Logger.getLogger().log(Logger.INFO_PRIORITY, "Client must send connection first.");
									// SHOULD SEND ERROR MESSAGE BACK TO CLIENT
								}
								else
								{
									string ip;
									string chatName;
									string status;
									string id;

									//no we go ahead and parse and make the request
									this._xmlReader.ReadStartElement();
									this._xmlReader.ReadStartElement();
									ip = this._xmlReader.ReadElementString();
									chatName = this._xmlReader.ReadElementString();
									status = this._xmlReader.ReadElementString();
									id = this._xmlReader.ReadElementString();

									ChatBuddy cb = new ChatBuddy(ip, chatName, status, id);
									ChatRequest cr = new ChatRequest(this._handler, this._handler._server.lookupClient(cb), this._handler._server.getChatID());
									//now that we have the buddy they want to chat with let's make the right call
									ThreadPool.QueueUserWorkItem(new WaitCallback(this._handler._server._chatRequestHandler.chatRequest), cr);


								}
								
							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CONFIRM_CHAT_INVITE)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_REQUEST_CHAT)
							{
					
							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_ACCEPT_CHAT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DECLINE_CHAT)
							{
								//decline chat we now need to tell the sorry sucker who wanted to
								//chat with this buddy and remove the ChatRequest from the pending chat requests
								//hashtable in the ChatRequestHandler
								//we let the ChatRequestHandler take care of all of this
								string chatID;
								//decode the chatID
								//this._xmlReader.ReadStartElement();
								chatID = this._xmlReader.ReadElementString();


								ThreadPool.QueueUserWorkItem(new WaitCallback(this._handler._server._chatRequestHandler.chatRequestReject), chatID);

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_EXIT_CHAT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHAT_UPDATE)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CLOSE_CHAT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHATTER)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DATA)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DECODED_CHATTER)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_ERROR)
							{

							}
								//xml message is not recognized
							else
							{

							}
						}
						catch(System.Xml.XmlException xmlE)
						{
							Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Could not parse last message", xmlE);
							//send out error xml here
						}
					}
					else
					{
						break;
					}

				}//while
				#endregion
			
			}
			catch(System.Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Unexpected exception: " + eee.Message, eee);
				
			}
			finally
			{
				this.cleanup();
				
			}
			

		}

		private void cleanup()
		{
			this._clientsock.Close();
			if(this._xmlReader != null)
			{
				this._xmlReader.Close();
			}
			
			if(this._handler._client._status != RuntimeConstants.PENDING)
			{
				this._handler._server.removeClient(this._handler);
			}
			else
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Client connection could not be made.");
			}

			this._handler.cleanup();
		}

	}
}
