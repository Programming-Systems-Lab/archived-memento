using System;

namespace psl.memento.pervasive.hermes.server
{
	/// <summary>
	/// This is the wrapper of the our client. Contains info like ip address and unique name of client.
	/// </summary>
	public class Client
	{
		private string _ip;
		private string _chatName;
		private string _id;
		public string _status;
		public bool _chatPending;
		public bool _canChat = true;

		public Client(String ip, String chatName, String id) : this()
		{
			this._chatPending = false;
			this._ip = ip;
			this._chatName = chatName;
			this._id = id;
			this._canChat = true;
			
		}
		public Client()
		{
			this._status = RuntimeConstants.PENDING;
			this._canChat = true;
		}
	
		//get methods
		public string getIP()
		{
			return this._ip;
		}
		public string getChatName()
		{
			return this._chatName;
		}
		public string getID()
		{
			return this._id;
		}

		public void setIP(string ip)
		{
			this._ip = ip;
		}

		public void setChatName(string chatName)
		{
			this._chatName = chatName;
		}

		public void setID(string id)
		{
			this._id = id;
		}

		override public string ToString()
		{
			string temp;
			temp = "Client Info:\r\n";
			temp += "Client Name: " + this._chatName;
			temp += "\r\nClient IP: " + this._ip;
			temp += "\r\nClient ID: " + this._id;
			temp += "\r\nClient Status: " + this._status;
			temp += "\r\n";
			return temp;
		}

	}
}
