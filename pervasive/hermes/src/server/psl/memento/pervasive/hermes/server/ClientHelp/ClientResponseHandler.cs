using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.IO;
using System.Xml;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.util.log;
using psl.memento.pervasive.hermes.server.ChatHelper;


namespace psl.memento.pervasive.hermes.server.ClientHelp
{
	/// <summary>
	/// ClientResponseHandler is used for sending all responses to the clients.
	/// 
	/// </summary>
	public class ClientResponseHandler
	{	
		private Socket _client;	
		private StringBuilder _sb;
		private StringWriter _sw;
		private XmlTextWriter _xtw;
		private ClientHandler _ch;

	
		public ClientResponseHandler(Socket client, ClientHandler ch)
		{
			this._client = client;
			this._ch = ch;
			this._sb = new StringBuilder(); 
			this._sw = new StringWriter(_sb);
			this._xtw = new XmlTextWriter(this._sw);
			//
			// TODO: Add constructor logic here
			//
		}

		public void chatInvite(object chatRequest)
		{
			ChatRequest cr = ((ChatRequest)chatRequest);
			try
			{
				lock(this._sb)
				{
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.requestChat(this._xtw, this._ch._server.getServerID(), false, this._ch.getMessageID().ToString(), new ChatBuddy(cr._requestClient._client.getIP(), cr._requestClient._client.getChatName(), cr._requestClient._client._status, cr._requestClient._client.getID()), cr._chatID);
					string cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String to be sent: " + cmd);
					cmd += RuntimeConstants.XML_MESSAGE_END;
					byte[] sender = System.Text.Encoding.ASCII.GetBytes(cmd) ;
					lock(this._client)
					{
						this._client.Send(sender, sender.Length, 0);
					}
				}
			}
			catch(Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending.", eee);
				this._ch._client._status = RuntimeConstants.DISCONNECTED;
			}

		}
		
		public void chatInviteRejected(object reason)
		{
			string theReason = (string)reason;
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "here is the reason:" + theReason);
			try
			{
				lock(this._sb)
				{
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.rejectedInvite(this._xtw, this._ch._server.getServerID().ToString(), false, this._ch.getMessageID().ToString(), theReason);
					string cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String to be sent: " + cmd);
					cmd += RuntimeConstants.XML_MESSAGE_END;
					byte[] sender = System.Text.Encoding.ASCII.GetBytes(cmd) ;
					lock(this._client)
					{
						this._client.Send(sender, sender.Length, 0);
					}
				}
			}
			catch(Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending.", eee);
				this._ch._client._status = RuntimeConstants.DISCONNECTED;
			}


		}

		public void buddyUpdate(object client)
		{
			ChatBuddy cb = (ChatBuddy)client;
			try
			{
				lock(this._sb)
				{
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.chatBuddiesUpdate(this._xtw, this._ch._server.getServerID(), false, this._ch.getMessageID().ToString(), cb);
					string cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String to be sent: " + cmd);
					cmd += RuntimeConstants.XML_MESSAGE_END;
					byte[] sender = System.Text.Encoding.ASCII.GetBytes(cmd) ;
					lock(this._client)
					{
						this._client.Send(sender, sender.Length, 0);
					}
				}
			}
			catch(Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending.", eee);
				this._ch._client._status = RuntimeConstants.DISCONNECTED;
			}
		}

		public void connectConfirm(object state)
		{				
			try
			{
				lock(this._sb)
				{
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.confirmConnect(this._xtw, 
						this._ch._server.getServerID(), 
						false, this._ch.getMessageID().ToString(), 
						this._ch._client.getID(), 
						this._ch._server.getClientList(this._ch), 
						this._ch._server.getServerID());
					string cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String to be sent: " + cmd);
					cmd += RuntimeConstants.XML_MESSAGE_END;
					byte[] sender = System.Text.Encoding.ASCII.GetBytes(cmd) ;
					lock(this._client)
					{
						this._client.Send(sender, sender.Length, 0);
					}
				}
			}
			catch(Exception eee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending Confirm Connection. Closing down client connection.", eee);
				this._ch._client._status = RuntimeConstants.DISCONNECTED;
			}
		}


	}
}
