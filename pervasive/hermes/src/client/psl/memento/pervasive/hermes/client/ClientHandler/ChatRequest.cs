using System;
using psl.memento.pervasive.hermes.xml.objects;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	/// <summary>
	/// Summary description for ChatRequest.
	/// </summary>
	public class ChatRequest
	{
		public ChatBuddy _chatBuddy;
		public string _chatID;

		public ChatRequest(ChatBuddy chatBuddy, string chatID)
		{
			this._chatBuddy = chatBuddy;
			this._chatID = chatID;
		}
	}
}
