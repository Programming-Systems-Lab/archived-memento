using System;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Xml;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.client;
using psl.memento.pervasive.hermes.client.util;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	

	/// <summary>
	/// Summary description for ClientHandler.
	/// </summary>
	///


	public class ClientHandler
	{
		private Client _client;

		private int _messageID;

		private System.Net.Sockets.TcpClient _tcp;

		private XmlTextWriter _xtw;

		private string _serverIP;

		public ClientHandler()
		{
			this._client = new Client();
			this._messageID = 1;
		}

		public void setIP(string ip)
		{
			this._serverIP = ip;
		}

		public void setChatName(string chatName)
		{
			this._client.setChatName(chatName);
		}

		public string getIP()
		{
			return this._serverIP;
		}

		public int getMessageID()
		{
			lock(this)
			{
				int currentID = this._messageID;
				this._messageID++;
				return currentID;
			}
		}

		public void connect()
		{
			try 
			{
				this._tcp = new TcpClient(this._serverIP, 5500);
				this._xtw = new XmlTextWriter(this._tcp.GetStream(), System.Text.ASCIIEncoding.ASCII);
				//we write off the message here.
				PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());
				//this._tcp.GetStream().BeginWrite(System.Text.ASCIIEncoding.ASCII.GetBytes("\r\n"), 0, System.Text.ASCIIEncoding.ASCII.GetBytes("\r\n").Length, null, null);
				//this._tcp.GetStream().Flush();
				//at this point we need to go to the wait stage of getting messages back.
				//PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());
				//PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());

			}
			catch(Exception ee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Could not send connection XML. Fatal Error.", ee);
			}
		}

	}
}
