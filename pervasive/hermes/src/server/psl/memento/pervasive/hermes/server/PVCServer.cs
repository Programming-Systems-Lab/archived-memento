using System;
using System.Collections;
using System.Threading;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.server.ClientHelp;
using psl.memento.pervasive.hermes.server.ChatHelper;
using psl.memento.pervasive.hermes.xml.objects;

namespace psl.memento.pervasive.hermes.server
{


	/// <summary>
	/// This is the main class that handles all the high level sever commands.
	/// startup and shutdown for instance.
	/// </summary>
	public class PVCServer
	{
		// clients hashtable stores reference to all connected clients
		private Hashtable _clients;
		// chats hashtable stores reference to all chats in progress
		private Hashtable _chats;
		private Listener _clientListener;
		private bool _isrunning;
		private int _clientID;
		private int _chatID;
		private object _locker;
		private string _serverID;
		public ChatRequestHandler _chatRequestHandler;

		public PVCServer()
		{
			this._serverID = "BIATCH";
			this._isrunning = false;
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Server is ready to start up.");
			this._clients = new Hashtable();
			

			//fake chat buddies for the moment


			this._chats = new Hashtable();
			this._clientListener = new Listener(this);
			this._clientID = 0;
			this._chatID = 0;
			this._locker = new object();
			this._chatRequestHandler = new ChatRequestHandler(this);
			
		
		}

		public ArrayList getClientList(ClientHandler client)
		{	
			lock(this._clients)
			{
				ArrayList al = new ArrayList();


				//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "What is the Count of hashtable?

				if(this._clients.Count > 0)
				{
					string clientStatus;
					System.Collections.IDictionaryEnumerator ide = this._clients.GetEnumerator();
					ide.MoveNext();
					while(ide.Current != null)
					{
						Logger.getLogger().log(Logger.DEBUG_PRIORITY, "What type of object is this: " + ide.Current.GetType());
						ClientHandler ch = (ClientHandler)((DictionaryEntry)ide.Current).Value;
						if(ch._client._canChat)
						{
							clientStatus = RuntimeConstants.CAN_CHAT;
						}
						else
						{
							clientStatus = RuntimeConstants.CANNOT_CHAT;
						}
						if(!ch.getClientID().Equals(client.getClientID()))
							al.Add(new ChatBuddy(ch._client.getIP(), ch._client.getChatName(), clientStatus, ch._client.getID()));
						
						if(!ide.MoveNext())
							break;

					}
				}
				return al;

			}
		}

		public string getServerID()
		{
			return this._serverID;
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
				lock(this._clients)
				{
					//we tell all the clients that a new client has logged on to the server
					ChatBuddy newChatBuddy = new ChatBuddy(ch._client.getIP(), ch._client.getChatName(), RuntimeConstants.NEW_BUDDY, ch._client.getID());
					if(this._clients.Count > 0)
					{
						System.Collections.IDictionaryEnumerator ide = this._clients.GetEnumerator();
						ide.MoveNext();
						while(ide.Current != null)
						{
							//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "What type of object is this: " + ide.Current.GetType());
							ClientHandler tempCH = (ClientHandler)((DictionaryEntry)ide.Current).Value;
							ThreadPool.QueueUserWorkItem(new WaitCallback(tempCH._responseHandler.buddyUpdate), newChatBuddy);
							if(!ide.MoveNext())
								break;

						}
					}
					//  we add the client
					this._clients.Add(ch.getClientID(), ch);

				}
			}
			else
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Server is not running");
			}	
		}

		public void removeClient(ClientHandler ch)
		{
			lock(this._clients)
			{
				//we tell all the clients that a new client has logged on to the server
				this._clients.Remove(ch.getClientID());
				ChatBuddy newChatBuddy = new ChatBuddy(ch._client.getIP(), ch._client.getChatName(), RuntimeConstants.BUDDY_DISCONNECTING, ch._client.getID());
				if(this._clients.Count > 0)
				{
					System.Collections.IDictionaryEnumerator ide = this._clients.GetEnumerator();
					ide.MoveNext();
					while(ide.Current != null)
					{
						//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "What type of object is this: " + ide.Current.GetType());
						ClientHandler tempCH = (ClientHandler)((DictionaryEntry)ide.Current).Value;
						ThreadPool.QueueUserWorkItem(new WaitCallback(tempCH._responseHandler.buddyUpdate), newChatBuddy);
						if(!ide.MoveNext())
							break;

					}
				}
				//  we add the client
				
				//we need to tell all the other clients that this person has left
			}
			ch._client._status =  RuntimeConstants.DISCONNECTED;
			Logger.getLogger().log(Logger.INFO_PRIORITY, "The following client has left the chat: " + ch._client.ToString());
			//System.GC.Collect();
		}

		public string getClientID()
		{
			lock(this._locker)
			{
				int tempID = this._clientID;
				this._clientID++;
				return tempID.ToString();
			}
		}

		public string getChatID()
		{
			lock(this._locker)
			{
				int tempID = this._chatID;
				this._chatID++;
				return tempID.ToString();
			}
		}

		public ClientHandler lookupClient(ChatBuddy client)
		{
			lock(this._clients)
			{
				return (ClientHandler)this._clients[client.getID()];
			}
		}

	}
}
