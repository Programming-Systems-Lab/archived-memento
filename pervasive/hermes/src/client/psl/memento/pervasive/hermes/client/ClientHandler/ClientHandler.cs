using System;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Xml;
using System.Configuration;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.client;
using psl.memento.pervasive.hermes.client.util;
using System.Threading;

namespace psl.memento.pervasive.hermes.client.ClientHandler
{
	

	/// <summary>
	/// Client Handler is used to handle the client.
	/// IE request are sent from the main form to the client request
	/// handler through this class.
	/// it is a point of contact between the user interface and the other
	/// classes that handle communication.
	/// </summary>
	///


	public class ClientHandler
	{
		public Client _client;
		public MainForm _clientInterface;
		private int _messageID;

		public Socket _serverSocket;

		//private XmlTextWriter _xtw;

		private string _serverIP;
		private ServerResponseHandler _srh;
		public ClientRequestHandler _crh;

		public ClientHandler(MainForm clientInterface)
		{
			this._clientInterface = clientInterface;
			this._client = new Client();
			this._messageID = 1;
			this._crh = new ClientRequestHandler(this);
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

		public void requestChat(ChatBuddy cb)
		{
			//set the client status is chatting so other invites will be rejected out right
			this._client._chatPending = true;
			//we now put a thread out there to send out the request
			ThreadPool.QueueUserWorkItem(new WaitCallback(this._crh.chatInvite), cb );
			
		}

		public void disconnect()
		{
			try
			{
				this.updateStatusBar("Disconnecting and closing.");
				this._crh.disconnect();
				this.cleanup();
				this._clientInterface.Close();

			}
			catch(Exception wow)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem on disconnect. All things will be closed.", wow);
			}
		}

		public void updateStatusBar(string update)
		{
			this._clientInterface.statusBar1.Text = update;
		}

		public void populateChatBuddies(ChatBuddy[] buddies)
		{
			//we need to add all the chat buddies
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here we update the list of buddies. Count: " + buddies.Length);
			if(buddies.Length > 0)
			{
				for(int i = 0; i < (buddies.Length); i++)
				{
					Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Length Buddies:" + buddies.Length + " and: " + i);
					this._clientInterface.listBox1.Items.Add(buddies[i]);
				}
			}
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Done");
		}

		public void updateChatBuddy(ChatBuddy cb)
		{
			string status = cb.getStatus();

			if(status.Equals(RuntimeConstants.CAN_CHAT) || status.Equals(RuntimeConstants.CANNOT_CHAT ))
			{
				System.Collections.IEnumerator ie = this._clientInterface.listBox1.Items.GetEnumerator();
				ie.MoveNext();
				while(ie.Current != null)
				{
					if(((ChatBuddy)ie.Current).getID().Equals(cb.getID()))
					{
						((ChatBuddy)ie.Current).setStatus(status);
						break;
					}
					ie.MoveNext();
				}				
			}
			else if(status.Equals(RuntimeConstants.NEW_BUDDY))
			{
				this._clientInterface.listBox1.Items.Add(cb);
			}
			else if(status.Equals(RuntimeConstants.BUDDY_DISCONNECTING))
			{
				System.Collections.IEnumerator ie = this._clientInterface.listBox1.Items.GetEnumerator();
				ie.MoveNext();
				while(ie.Current != null)
				{
					if(((ChatBuddy)ie.Current).getID().Equals(cb.getID()))
					{
						this._clientInterface.listBox1.Items.Remove(ie.Current);
						break;
					}
					ie.MoveNext();
				}
			}

		}

		public void connect()
		{
			try 
			{
				//this is where we set up the new socket with the server
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "And right before we create our socket.");
				this._serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
				_serverSocket.Blocking = true;
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Here is this damn thing." + this._serverIP + "  - ");
				IPHostEntry IPHost = Dns.Resolve(this._serverIP); 
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "DNS Resolve is ginve probklems");
				string[] aliases = IPHost.Aliases; 
				IPAddress[] addr = IPHost.AddressList;
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "here is this addr:" + addr[0]);
				IPEndPoint ipepServer = new IPEndPoint(addr[0], 5500);
				this._serverSocket.Connect(ipepServer);
				this._crh.setSocket(this._serverSocket);
				//now we are connected

				//we set up the response
				this._srh = new ServerResponseHandler(this._serverSocket, this);
				new System.Threading.Thread(new System.Threading.ThreadStart(this._srh.start)).Start();
			
				//now we are ready to send out our connection request
				this._crh.connect();


				
				
				
				
				//this._serverSocket.Send(sender, sender.Length, 0);
				//this._serverSocket.Send(sender, sender.Length, 0);
				
			





				
				/*
				this._tcp = new TcpClient();
				this._tcp.Connect(this._serverIP, 5500);

				
				this._tcp.NoDelay = true;
				byte[] howdy = System.Text.ASCIIEncoding.ASCII.GetBytes("Howdy World!!!");
				this._tcp.GetStream().Write(howdy, 0, howdy.Length); 
				this._tcp.GetStream().Flush();

				
				
				byte[] bb=new byte[100];
				int k=this._tcp.GetStream().Read(bb,0,100);
				*/
				//for (int i=0;i<k;i++)
					//System.Console.Write(Convert.ToChar(bb[i]));
/*
				//this._tcp.Connect(
				NetworkStream ns = new NetworkStream(this._clientSocket);
				this._xtw = new XmlTextWriter(ns, System.Text.ASCIIEncoding.ASCII);
				//we write off the message here.
				PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());
				ns.Flush();

*/				
				
				//this._xtw.BaseStream.EndWrite(null);
				//this._xtw.Close();
				//this._tcp.Close();


				//this._xtw = new XmlTextWriter(this._clientSocket, System.Text.ASCIIEncoding.ASCII);

				//this._xtw.Close();

				//StreamWriter sw = new StreamWriter(this._tcp.GetStream(), System.Text.ASCIIEncoding.ASCII);
				//sw.Write("Test for good");
				//sw.WriteLine("Test AGain");
				//sw.WriteLine("Test AGain");
				//sw.WriteLine("Test AGain");
				//sw.WriteLine("Test AGain");
				//sw.Flush();
				//sw.Close();
				
				//sw = new StreamWriter(this._tcp.GetStream(), System.Text.ASCIIEncoding.ASCII);
				//at this point we need to go to the wait stage of getting messages back.
				//PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());
				//PVCTDMessage.connect(this._xtw, "", true, this.getMessageID().ToString(), this._client.getIP(), this._client.getChatName());

			}
			catch(Exception ee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Could not send connection XML. Fatal Error." + ee.Message, ee);
			}
		}

		public void cleanup()
		{
			try
			{
				//this._xtw.Close();
				
				this._serverSocket.Close();
				
				this._srh.cleanup();
			}
			catch(Exception oops)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Closing Socket.", oops);
			}
		}

	}
}
