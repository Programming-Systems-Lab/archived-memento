using System;
using psl.memento.pervasive.hermes.server;
using psl.memento.pervasive.hermes.server.ClientHelp;

namespace psl.memento.pervasive.hermes.server.ChatHelper
{
	/// <summary>
	/// Summary description for ChatRequestHandler.
	/// </summary>
	public class ChatRequest
	{
		public ClientHandler _requestClient;
		public ClientHandler _inviteClient;
		public string _chatID;

		public ChatRequest(ClientHandler requestClient, ClientHandler inviteClient, string id)
		{
			this._requestClient = requestClient;
			this._inviteClient = inviteClient;
			this._chatID = id;
		}

	}
}
