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
		private string _id;

		public ChatBuddy(string ip, string chatName, string status, string id)
		{
			this._ip = ip;
			this._chatName = chatName;
			this._status = status;
			this._id = id;
		}
		
		public string getID()
		{
			return this._id;
		}
			
		public string getIP()
		{
			return this._ip;
		}

		public string getChatName()
		{
			return this._chatName;
		}

		public void setStatus(string status)
		{
			this._status = status;
		}

		public string getStatus()
		{
			return this._status;
		}

		public override string ToString()
		{
			string temp = this._chatName + " - ";
			if(this._status.ToLower().Equals("1") || this._status.ToLower().Equals("3"))
			{
				temp += "NOT CHATTING";
			
			}
			else
			{
				temp += "CHATTING";
			}
			
			return temp;

		}

	}
}
