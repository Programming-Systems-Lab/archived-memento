using System;
using psl.memento.pervasive.hermes.server;

namespace Test
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class Class1
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			//System.Console.WriteLine("got this far.");
			PVCServer pvcs = new PVCServer();
			//System.Console.WriteLine("now this far");
			pvcs.start();
			//System.Console.WriteLine("problem is here");
			
			System.Threading.Thread.Sleep(System.TimeSpan.MaxValue);
		}
	}
}
