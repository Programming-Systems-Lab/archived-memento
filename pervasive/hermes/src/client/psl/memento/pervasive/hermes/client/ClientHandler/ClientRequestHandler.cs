using System;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Xml;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.client;
using psl.memento.pervasive.hermes.client.util;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	/// <summary>
	/// ClientRequestHandler is used to send out client requests.
	/// there should be methods for each request the client may have.
	/// 
	/// you can see how the base methods for connection and disconnecting have been added.
	/// </summary>
	public class ClientRequestHandler
	{

		private Socket _serverSocket;
		private ClientHandler _ch;
		private XmlTextWriter _xtw;
		private System.Text.StringBuilder _sb;
		private System.IO.StringWriter _sw;
		private string _cmd;
		private byte[] _sender;


		public ClientRequestHandler(ClientHandler ch)
		{
			this._ch = ch;
			this._serverSocket = null;
			this._sb = new System.Text.StringBuilder();
			this._sw = new StringWriter(this._sb);
			this._xtw = new XmlTextWriter(this._sw);
			this._cmd = null;
			this._sender = new byte[100];

			//
			// TODO: Add constructor logic here
			//
		}

		public void chatReject(object chatID)
		{
			string chatIDReject = (string)chatID;
		
			try
			{
				lock(this._sb)
				{
		
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.declineChat(this._xtw, this._ch._client.getClientID(), true, this._ch.getMessageID().ToString(), chatIDReject);
					_cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String: " + _cmd);
					_cmd += RuntimeConstants.XML_MESSAGE_END;
					_sender = System.Text.Encoding.ASCII.GetBytes(_cmd);
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the value of love: " + _cmd);
					lock(this._serverSocket)
					{
						//we use the isChatting value as a way of knowing if the user still wants this chat to connect

							this._serverSocket.Send(_sender, _sender.Length, 0);
					}
				}
			}
			catch(Exception connectProblem)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending Connect.", connectProblem);
				//we need to handle the error here probably a error call function
			}

		}

		public void chatInvite(object chatBuddy)
		{
			try
			{
				lock(this._sb)
				{
		
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.chatInvite(this._xtw, this._ch._client.getClientID().ToString(), true, this._ch.getMessageID().ToString(), (ChatBuddy)chatBuddy);
					_cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String: " + _cmd);
					_cmd += RuntimeConstants.XML_MESSAGE_END;
					_sender = System.Text.Encoding.ASCII.GetBytes(_cmd);
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the value of love: " + _cmd);
					lock(this._serverSocket)
					{
						//we use the isChatting value as a way of knowing if the user still wants this chat to connect
						if(this._ch._client._chatPending)
							this._serverSocket.Send(_sender, _sender.Length, 0);
					}
				}
			}
			catch(Exception connectProblem)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending Connect.", connectProblem);
				//we need to handle the error here probably a error call function
			}


		}

		public void disconnect()
		{
			try
			{
				lock(this._sb)
				{
		
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.disconnect(this._xtw, this._ch._client.getClientID(), true, this._ch.getMessageID().ToString());
					_cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String: " + _cmd);
					_cmd += RuntimeConstants.XML_MESSAGE_END;
					_sender = System.Text.Encoding.ASCII.GetBytes(_cmd);
					//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is the value of love: " + this._serverSocket);
					lock(this._serverSocket)
					{
						this._serverSocket.Send(_sender, _sender.Length, 0);
					}
				}
			}
			catch(Exception connectProblem)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending Connect.");
				throw connectProblem;
			}

		}

		public void connect()
		{
			try
			{
				lock(this._sb)
				{
		
					this._sb.Remove(0, this._sb.Length);
					PVCTDMessage.connect(this._xtw, "", true, this._ch.getMessageID().ToString(), this._ch._client.getIP(), this._ch._client.getChatName());
					_cmd = _sb.ToString();
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "The String: " + _cmd);
					_cmd += RuntimeConstants.XML_MESSAGE_END;
					_sender = System.Text.Encoding.ASCII.GetBytes(_cmd);
					lock(this._serverSocket)
					{
						this._serverSocket.Send(_sender, _sender.Length, 0);
					}
				}
			}
			catch(Exception connectProblem)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem sending Connect.");
				throw connectProblem;
			}
		}

		public void setSocket(Socket s)
		{
			this._serverSocket = s;
		}

	}
}
