using System;
using psl.memento.pervasive.hermes.server;
using psl.memento.pervasive.hermes.server.ClientHelp;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.util.log;
using System.Threading;

namespace psl.memento.pervasive.hermes.server.ChatHelper
{
	/// <summary>
	/// Summary description for ChatRequestHandler.
	/// </summary>
	public class ChatRequestHandler
	{
		private PVCServer _server;
		private System.Collections.Hashtable _pendingChats;

		public ChatRequestHandler(PVCServer server)
		{
			this._server = server;
			this._pendingChats = new System.Collections.Hashtable();
		}


		//this method takes care of the chatRequest
		public void chatRequest(object chatRequest)
		{
			ChatRequest cr = (ChatRequest)chatRequest;
			lock(this._pendingChats)
			{
				this._pendingChats.Add(cr._chatID, cr);
			}
			ClientHandler invited = cr._inviteClient;
			ClientHandler request = cr._requestClient;
			if(!invited._client._canChat || invited._client._chatPending || !invited._client._status.Equals(RuntimeConstants.CONNECTED))
			{
				ThreadPool.QueueUserWorkItem(new WaitCallback(cr._requestClient._responseHandler.chatInviteRejected), "Cannot chat with this guy.");
				request._client._chatPending = false;
			}
			else
			{
				// we send the request to our buddy
				ThreadPool.QueueUserWorkItem(new WaitCallback(invited._responseHandler.chatInvite), cr);
			}


		}

		public void chatRequestReject(object chatID)
		{		

			string chatIDReject = (string)chatID;
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here we are go go go: " + chatIDReject);
			ChatRequest cr = (ChatRequest)this._pendingChats[chatID];
			lock(this._pendingChats)
			{
				this._pendingChats.Remove(chatIDReject);
			}
			ThreadPool.QueueUserWorkItem(new WaitCallback(cr._requestClient._responseHandler.chatInviteRejected), "Client declined chat");

		}

		public void chatRequestAccept(object chatID)
		{		
			string chatIDAccept = (string)chatID;

		}

	}
}
