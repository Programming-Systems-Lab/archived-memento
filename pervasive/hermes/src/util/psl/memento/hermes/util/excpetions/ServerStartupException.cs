using System;

namespace Utilities.exceptions
{
	/// <summary>
	/// Simply give us a special type of ServerStartupException where we can list what is wrong.
	/// </summary>
	public class ServerStartupException : Exception
	{
		public ServerStartupException(String message)
			: base(message) 
		{
			//
			// TODO: Add constructor logic here
			//
		}
	}
}
