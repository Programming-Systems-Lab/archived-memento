using System;

namespace Utilities.exceptions
{
	/// <summary>
	/// Simply give us a special type of LoggerCannotWriteException where we can list what is wrong.
	/// </summary>
	public class LoggerCannotWriteException : Exception
	{
		public LoggerCannotWriteException() 
		{
			//nothing in here
		}

		public LoggerCannotWriteException(String message, Exception e) 
			: base(message, e)
		{
			//nothing in here
		}

		public LoggerCannotWriteException(String message)
			: base(message) 
		{
			//
			// TODO: Add constructor logic here
			//
		}
	}
}