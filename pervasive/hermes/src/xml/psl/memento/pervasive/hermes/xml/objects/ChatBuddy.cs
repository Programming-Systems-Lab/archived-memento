using System;
using System.Data;

namespace psl.memento.pervasive.hermes.xml.objects
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class ChatBuddy
	{
		private string _ip; 
		private string _chatName;
		private string _status;

		public ChatBuddy(string ip, string chatName, string status)
		{
			this._ip = ip;
			this._chatName = chatName;
			this._status = status;
		}
		
		public string getIP()
		{
			return this._ip;
		}

		public string getChatName()
		{
			return this._chatName;
		}

		public string getStatus()
		{
			return this._status;
		}

	}
}
