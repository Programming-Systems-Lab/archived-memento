using System;
using System.Net;
using System.Net.Sockets;
using psl.memento.pervasive.hermes.client;
using psl.memento.pervasive.hermes.client.util;
using psl.memento.pervasive.hermes.xml.objects;
using System.Threading;
using System.Xml;
using System.IO;
using System.Text;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	/// <summary>
	/// This Class is used to handle the incoming messages from the server.
	/// </summary>
	public class ServerResponseHandler
	{

		private System.Net.Sockets.Socket _server;		
		private System.Xml.XmlTextReader _xmlReader;
		private XMLSocketParser _xsp;
		private ClientHandler _ch;

		public ServerResponseHandler(Socket server, ClientHandler ch)
		{
			this._ch = ch;
			this._server = server;
			this._xmlReader = null;
			this._xsp = new XMLSocketParser(server);
			//
			// TODO: Add constructor logic here
			//
		}

		//Start listening to incoming messages
		public void start()
		{
			try
			{
				string clientmessage = null;
				
				bool rcount = false;

				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "We have started this thread up and are ready to listen for things coming in.");

				while(this._server.Connected)
				{
					clientmessage = null;
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Getting a message.");
					rcount = this._xsp.getXMLMessage(ref clientmessage);

					if(rcount)
					{
						try
						{
					
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is what they said: " + clientmessage + " and length : " + rcount);
					
							this._xmlReader = new XmlTextReader(new StringReader(clientmessage));
							this._xmlReader.ReadStartElement();
							this._xmlReader.ReadStartElement();
							
							Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current node:" + this._xmlReader.Name);

							if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CONFIRM_CONNECT)
							{
								//here we need to get the list of chat buddies and send update the server list as well as

								//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Yipee we all gonna be famous.");
								this._ch.updateStatusBar("Connected.");
								this._ch._client.setClientID(this._xmlReader.ReadElementString());
								//this._xmlReader.ReadStartElement();
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element with attribute:" + this._xmlReader.Name);
								//this._ch._client.setClientID(this._xmlReader.ReadElementString());
								//move to chatBuddies
								//this._xmlReader.ReadStartElement();
								//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element with attribute:" + this._xmlReader.Name);
								int chatBuddiesCount = System.Int16.Parse(this._xmlReader.GetAttribute(0));
								//we get the number of chat buddies
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is what we got for our chatBuddiesCount:" + chatBuddiesCount);
								
								ChatBuddy[] buddies = new ChatBuddy[chatBuddiesCount];

								if(chatBuddiesCount > 0)
								{
									string ip = null;
									string chatName = null;
									string status = null;
									string clientID = null;

									this._xmlReader.ReadStartElement();
									//this._xmlReader.ReadStartElement();

									for(int i = 0; i < chatBuddiesCount; i++)
									{
										
										//read the chatBuddy
										//this._xmlReader.ReadStartElement();
										
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of " + i + " value buddy "+ this._xmlReader.Name);
										
										
										//read the ip
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of " + i + " value ip"+ this._xmlReader.Name);
										ip = this._xmlReader.ReadElementString();
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of " + i + " value chatName "+ this._xmlReader.Name);
										chatName = this._xmlReader.ReadElementString();
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of " + i + " value  status "+ this._xmlReader.Name);
										status = this._xmlReader.ReadElementString();
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of " + i + " value clientID "+ this._xmlReader.Name);
										clientID = this._xmlReader.ReadElementString();
										
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is my problem." + i);
										buddies[i] = new ChatBuddy(ip, chatName, status, clientID);
										Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is my problem." + i);
										
										this._xmlReader.ReadEndElement();

									}
								}
								
								this._ch.populateChatBuddies(buddies);
							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DISCONNECT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHAT_INVITE)
							{
								

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CONFIRM_CHAT_INVITE)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_REQUEST_CHAT)
							{
								//if a chat is pending or the client is in a chat
								string ip;
								string chatName;
								string status;
								string id;
								string chatID;

								//no we go ahead and parse and make the request
								this._xmlReader.ReadStartElement();
								this._xmlReader.ReadStartElement();
								ip = this._xmlReader.ReadElementString();
								chatName = this._xmlReader.ReadElementString();
								status = this._xmlReader.ReadElementString();
								id = this._xmlReader.ReadElementString();
								this._xmlReader.ReadEndElement();

								chatID = this._xmlReader.ReadElementString();

								ChatRequest chatRequest = new ChatRequest(new ChatBuddy(ip, chatName, status, id), chatID);


								if(this._ch._client._chatPending || this._ch._client._isChatting)
								{
									ThreadPool.QueueUserWorkItem(new WaitCallback(this._ch._crh.chatReject), chatRequest);
								}
								else
								{
									ThreadPool.QueueUserWorkItem(new WaitCallback(this._ch._clientInterface.incomingChatInvite), chatRequest);
								}

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHAT_INVITE_REJECT)
							{
								string reason;
								this._xmlReader.ReadStartElement();
								this._xmlReader.ReadStartElement();
								reason = this._xmlReader.ReadElementString();

								ThreadPool.QueueUserWorkItem(new WaitCallback(this._ch._clientInterface.chatInviteReject), reason);

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_ACCEPT_CHAT)
							{

							}
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_DECLINE_CHAT)
							{
								

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
							else if(this._xmlReader.Name == RuntimeConstants.XML_MESSAGE_CHAT_BUDDIES_UPDATE)
							{
								string ip = null;
								string chatName = null;
								string status = null;
								string clientID = null;

								this._xmlReader.ReadStartElement();
								//this._xmlReader.ReadStartElement();
									
								//read the chatBuddy
								//this._xmlReader.ReadStartElement();
									
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of  value buddy "+ this._xmlReader.Name);
									
									
								//read the ip
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of  value ip"+ this._xmlReader.Name);
								ip = this._xmlReader.ReadElementString();
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of  value chatName "+ this._xmlReader.Name);
								chatName = this._xmlReader.ReadElementString();
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of  value  status "+ this._xmlReader.Name);
								status = this._xmlReader.ReadElementString();
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the current element of  value clientID "+ this._xmlReader.Name);
								clientID = this._xmlReader.ReadElementString();

								ChatBuddy cb = new ChatBuddy(ip, chatName, status, clientID);
								//check what we should do with our list of peeps
								
								this._ch.updateChatBuddy(cb);
								Logger.getLogger().log(Logger.DEBUG_PRIORITY, "And we keep going.");
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



				}
			}
			catch(Exception eeee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Stream is now closed.", eeee);

			}


		}

		public void cleanup()
		{
			this._xmlReader.Close();
		}

	}
}
