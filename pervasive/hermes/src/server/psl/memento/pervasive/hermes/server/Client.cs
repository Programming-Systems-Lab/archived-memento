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
		private string _status;

		public Client(String ip, String chatName, String id)
		{
			this._ip = ip;
			this._chatName = chatName;
			this._id = id;
			this._status = "1";
		}
		public Client()
		{
			
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
			return this._ip;
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

	}
}
