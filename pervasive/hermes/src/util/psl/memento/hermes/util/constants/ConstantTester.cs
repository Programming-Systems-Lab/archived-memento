using System;

namespace Server.util
{
	/// <summary>
	/// Summary description for ConstantTester.
	/// </summary>
	public class ConstantTester
	{
		
		public static void Main()
		{

			Logger.log(Logger.DEBUG_PRIORITY, "What the fuck!!!");

			Console.WriteLine(Constants.getSConst("Test"));
			Console.WriteLine(Constants.getIntConst("Money"));
			Console.WriteLine(Constants.getIntConst("Test"));
			Console.WriteLine(Constants.getSConst("Mosdfney"));

		}
	}
}

