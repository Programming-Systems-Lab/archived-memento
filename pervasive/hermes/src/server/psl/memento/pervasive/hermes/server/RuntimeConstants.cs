using System;

namespace psl.memento.pervasive.hermes.server
{
	/// <summary>
	/// Summary description for Constants.
	/// </summary>
	public class RuntimeConstants
	{
		#region Client States
		public static string	PENDING = "pending";
		public static string	CONNECTED = "connected";
		public static string	DISCONNECTED = "disconnected";
		#endregion

		#region ChatBuddy States

		public static string CAN_CHAT = "1";
		public static string CANNOT_CHAT = "2";
		public static string NEW_BUDDY = "3";
		public static string BUDDY_DISCONNECTING = "4";

		#endregion
		
		#region XMLMessages
		
		public static int		XML_DECODING_ATTEMPTS = 10;
		public static string	XML_MESSAGE_END = "|||";
		public static string	XML_MESSAGE_CONNECT = "connect";
		public static string	XML_MESSAGE_CONFIRM_CONNECT = "confirmConnect";
		public static string	XML_MESSAGE_DISCONNECT = "disconnect";
		public static string	XML_MESSAGE_CHAT_INVITE = "chatInvite";
		public static string	XML_MESSAGE_CONFIRM_CHAT_INVITE = "confirmChatInvite";
		public static string	XML_MESSAGE_REQUEST_CHAT = "requestChat";
		public static string	XML_MESSAGE_ACCEPT_CHAT = "acceptChat";
		public static string	XML_MESSAGE_DECLINE_CHAT = "declineChat";
		public static string	XML_MESSAGE_EXIT_CHAT = "exitChat";
		public static string	XML_MESSAGE_CHAT_UPDATE = "chatUpdate";
		public static string	XML_MESSAGE_CLOSE_CHAT = "closeChat";
		public static string	XML_MESSAGE_CHATTER = "chatter";
		public static string	XML_MESSAGE_DATA = "data";
		public static string	XML_MESSAGE_DECODED_CHATTER = "decodedChatter";
		public static string	XML_MESSAGE_ERROR = "error";
		public static string	XML_MESSAGE_CHAT_BUDDIES_UPDATE = "chatBuddiesUpdate";
		
		#endregion

	}
}
