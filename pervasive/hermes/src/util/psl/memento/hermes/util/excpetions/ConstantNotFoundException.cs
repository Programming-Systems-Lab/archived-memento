using System;

namespace Utilities.exceptions
{
	/// <summary>
	/// Summary description for ConstantNotFoundException.
	/// </summary>
	public class ConstantNotFoundException : Exception
	{
		public ConstantNotFoundException()
		{
			//
			// TODO: Add constructor logic here
			//
		}
		public ConstantNotFoundException(String message) : base(message) 
		{
			//nothing
		}

		public ConstantNotFoundException(String message, Exception e) : base(message, e) 
		{
			//nothing
		}
	}
}
