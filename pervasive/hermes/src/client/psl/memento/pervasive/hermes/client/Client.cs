using System;

namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// Client is the representation of the client as it is seen by user
	/// that is it stores information about itself.
	/// </summary>
	public class Client
	{
		private string _clientID;
		private string _chatName;
		private string _clientIP;
		public bool _chatPending;
		public bool _isChatting;

		public Client()
		{
			this._isChatting = false;
			this._chatPending = false;
			try
			{
				this._clientIP = (System.Net.Dns.GetHostByName(System.Net.Dns.GetHostName()).AddressList[0].ToString());
			}
			catch(Exception e)
			{
				this._clientIP = "ERROR" + e.ToString();
			}
		}

		public void setClientID(string clientID)
		{
			this._clientID = clientID;
		}
		
		public string getClientID()
		{
			return this._clientID;
		}

		public void setChatName(string chatName)
		{
			this._chatName = chatName;
		}

		public string getChatName()
		{
			return this._chatName;
		}

		public string getIP()
		{
			return this._clientIP;
		}
	}
}
