using System;
using System.Data;
using System.Xml;
using System.IO;
using System.Collections;
using psl.memento.pervasive.hermes.xml.objects;

namespace psl.memento.pervasive.hermes.xml.messages
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class PVCTDMessage
	{
		public static void connect(XmlTextWriter xtw, string id, bool client, string messageID, string ip, string chatName)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("connect");
			//ip attribute
			xtw.WriteStartElement("ip");
			xtw.WriteString(ip);
			xtw.WriteEndElement();
			//chatName element
			xtw.WriteStartElement("chatName");
			xtw.WriteString(chatName);
			xtw.WriteEndElement();
			//close connect
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void confirmConnect(XmlTextWriter xtw, string id, bool client, string messageID, string clientID, ArrayList chatBuddies, string serverID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("confirmConnect");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("clientID");
			xtw.WriteString(clientID);
			xtw.WriteEndElement();

			xtw.WriteStartElement("chatBuddies");
			
			//we add a chat buddy for each one in the list of chat buddies

			object[] buddies = chatBuddies.ToArray();
			xtw.WriteAttributeString("chatBuddiesCount", buddies.Length.ToString());
			//xtw.WriteAttributeString("chatBuddies", "buddyCount", "columbia-psl-memento-pervasive-hermes", buddies.Length.ToString());
			//xtw.WriteEndAttribute();
				
			if(buddies.Length > 0)
			{

				for(int i = 0; i < buddies.Length; i++)
				{					
					ChatBuddy chatBuddy = (ChatBuddy)buddies[i];
					xtw.WriteStartElement("chatBuddy");
					xtw.WriteStartElement("ip");
					xtw.WriteString(chatBuddy.getIP());
					xtw.WriteEndElement();
					xtw.WriteStartElement("chatName");
					xtw.WriteString(chatBuddy.getChatName());
					xtw.WriteEndElement();
					xtw.WriteStartElement("status");
					xtw.WriteString(chatBuddy.getStatus());
					xtw.WriteEndElement();
					xtw.WriteStartElement("clientID");
					xtw.WriteString(chatBuddy.getID());
					xtw.WriteEndElement();
					xtw.WriteEndElement();
				}
			}
				
			xtw.WriteEndElement();	
			//serverID element
			xtw.WriteStartElement("serverID");
			xtw.WriteString(serverID);
			xtw.WriteEndElement();
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}


		public static void disconnect(XmlTextWriter xtw, string id, bool client, string messageID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("disconnect");
				
			//open and close all your internal elements here


			
			//**********

			//close disconnecdt
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void rejectedInvite(XmlTextWriter xtw, string id, bool client, string messageID, string reason)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("rejectedInvite");
				
			xtw.WriteStartElement("reason");
			xtw.WriteString(reason);
			xtw.WriteEndElement();
			
			//xtw.WriteEndElement();
			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();



		}

		public static void chatInvite(XmlTextWriter xtw, string id, bool client, string messageID, ChatBuddy chatBuddy)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("chatInvite");
				
			
			xtw.WriteStartElement("chatBuddy");
					
			xtw.WriteStartElement("ip");
			xtw.WriteString(chatBuddy.getIP());
			xtw.WriteEndElement();
			xtw.WriteStartElement("chatName");
			xtw.WriteString(chatBuddy.getChatName());
			xtw.WriteEndElement();
			xtw.WriteStartElement("status");
			xtw.WriteString(chatBuddy.getStatus());
			xtw.WriteEndElement();
			xtw.WriteStartElement("clientID");
			xtw.WriteString(chatBuddy.getID());
			xtw.WriteEndElement();
			
			xtw.WriteEndElement();
			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void confirmOpenChat(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("confirmOpenChat");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void requestChat(XmlTextWriter xtw, string id, bool client, string messageID, ChatBuddy chatBuddy, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("requestChat");

			xtw.WriteStartElement("chatBuddy");
					
			xtw.WriteStartElement("ip");
			xtw.WriteString(chatBuddy.getIP());
			xtw.WriteEndElement();
			xtw.WriteStartElement("chatName");
			xtw.WriteString(chatBuddy.getChatName());
			xtw.WriteEndElement();
			xtw.WriteStartElement("status");
			xtw.WriteString(chatBuddy.getStatus());
			xtw.WriteEndElement();
			xtw.WriteStartElement("clientID");
			xtw.WriteString(chatBuddy.getID());
			xtw.WriteEndElement();
			
			xtw.WriteEndElement();
		
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();
			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void acceptChat(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("acceptChat");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void declineChat(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("declineChat");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void exitChat(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("exitChat");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void chatUpdate(XmlTextWriter xtw, string id, bool client, string messageID, string chatID, ArrayList chatBuddies)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("chatUpdate");
				
			//open and close all your internal elements here


			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			//open and close all your internal elements here
			object[] buddies = chatBuddies.ToArray();
			//xtw.WriteAttributeString("chatBuddies", "buddyCount", "columbia-psl-memento-pervasive-hermes", buddies.Length.ToString());
			//xtw.WriteEndAttribute();
				
			if(buddies.Length > 0)
			{

				for(int i = 0; i < buddies.Length; i++)
				{					
					ChatBuddy chatBuddy = (ChatBuddy)buddies[i];
			
					xtw.WriteStartElement("chatBuddy");
					
					xtw.WriteStartElement("ip");
					xtw.WriteString(chatBuddy.getIP());
					xtw.WriteEndElement();
					xtw.WriteStartElement("chatName");
					xtw.WriteString(chatBuddy.getChatName());
					xtw.WriteEndElement();
					xtw.WriteStartElement("status");
					xtw.WriteString(chatBuddy.getStatus());
					xtw.WriteEndElement();
					xtw.WriteStartElement("clientID");
					xtw.WriteString(chatBuddy.getID());
					xtw.WriteEndElement();
					xtw.WriteEndElement();
				}
			}

			
			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void closeChat(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("closeChat");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void chatter(XmlTextWriter xtw, string id, bool client, string messageID, byte[] voice, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("chatter");
				

			xtw.WriteStartElement("voice");
			xtw.WriteBinHex(voice, 0, voice.Length);
			xtw.WriteEndElement();
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();

			//open and close all your internal elements here


			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		public static void data(XmlTextWriter xtw, string id, bool client, string messageID, string chatID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("data");
				
			//open and close all your internal elements here
			xtw.WriteStartElement("chatID");
			xtw.WriteString(chatID);
			xtw.WriteEndElement();


			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}

		/*
		 * 
		 * 
		 * for the decodedChatter element that may not be used
		 * 
		 * 
public static void decodedChatter(XmlTextWriter xtw, string id, bool client, string messageID, )
{
xtw.WriteStartDocument();
			//open pvctd tag
xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
xtw.WriteStartElement("message");
			//open connect element of message
xtw.WriteStartElement("");
				
				//open and close all your internal elements here


			
				//**********

			//close 
xtw.WriteEndElement();
			//close message
xtw.WriteEndElement();

			//open time element
xtw.WriteStartElement("time");
xtw.WriteString(System.DateTime.Now.ToString());
xtw.WriteEndElement();
			//open id
xtw.WriteStartElement("id");
xtw.WriteString(id);
xtw.WriteEndElement();
			
			//clientID or serverID
if(client)
{
xtw.WriteStartElement("clientID");
}
else 
{
xtw.WriteStartElement("serverID");
}
xtw.WriteString(id);
xtw.WriteEndElement();
			
			//close it all up
xtw.WriteEndElement();
xtw.WriteEndDocument();
xtw.Flush();

}

		*/
		

		public static void error(XmlTextWriter xtw, string id, bool client, string messageID, string messageType, string messageContent, string errorID)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("error");
				
			//open and close all your internal elements here
			xtw.WriteStartElement(messageType);
			xtw.WriteString(messageContent);
			xtw.WriteEndElement();

			xtw.WriteStartElement("id");
			xtw.WriteString(errorID);
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}
	

		public static void chatBuddiesUpdate(XmlTextWriter xtw, string id, bool client, string messageID, ChatBuddy chatBuddy)
		{
			xtw.WriteStartDocument();
			//open pvctd tag
			xtw.WriteStartElement("pvctd", "columbia-psl-memento-pervasive-hermes");
			//open message element
			xtw.WriteStartElement("message");
			//open connect element of message
			xtw.WriteStartElement("chatBuddiesUpdate");
				
				
			
			xtw.WriteStartElement("chatBuddy");
					
			xtw.WriteStartElement("ip");
			xtw.WriteString(chatBuddy.getIP());
			xtw.WriteEndElement();
			xtw.WriteStartElement("chatName");
			xtw.WriteString(chatBuddy.getChatName());
			xtw.WriteEndElement();
			xtw.WriteStartElement("status");
			xtw.WriteString(chatBuddy.getStatus());
			xtw.WriteEndElement();
			xtw.WriteStartElement("clientID");
			xtw.WriteString(chatBuddy.getID());
			xtw.WriteEndElement();
			xtw.WriteEndElement();

			
			//**********

			//close 
			xtw.WriteEndElement();
			//close message
			xtw.WriteEndElement();

			//open time element
			xtw.WriteStartElement("time");
			xtw.WriteString(System.DateTime.Now.ToString());
			xtw.WriteEndElement();
			//open id
			xtw.WriteStartElement("id");
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//clientID or serverID
			if(client)
			{
				xtw.WriteStartElement("clientID");
			}
			else 
			{
				xtw.WriteStartElement("serverID");
			}
			xtw.WriteString(id);
			xtw.WriteEndElement();
			
			//close it all up
			xtw.WriteEndElement();
			xtw.WriteEndDocument();
			xtw.Flush();

		}
	}		
}

