using System;
using System.IO;
using System.Threading;

namespace psl.memento.pervasive.hermes.client.util
{
	/// <summary>
	/// Summary description for Logger.
	/// </summary>
	public class Logger : Dispatcher
	{

		public static object sync = new object();
		//constants
		public static int INFO_PRIORITY = 0;
		public static int DEBUG_PRIORITY = 1;
		public static int EXCEPTION_PRIORITY = 2;
		public static int FATAL_PRIORITY = 3;

		public static String[] PRIORITY = {"<<INFO>>", "<<DEBUG>>", "<<EXCEPTION>>", "<<FATAL>>"};

		//static private logger object is used for writing the log
		protected static Logger _logger;
		protected Runloop _rl;

		//private instance variables
		protected System.IO.StreamWriter _sw;
		protected String _outputFileName;
		protected bool[] _prioritySettings;


		public Logger()
		{
			this._outputFileName = "Client" + "." + System.DateTime.Now.Hour + "." + 
				System.DateTime.Now.Minute + "." + System.DateTime.Now.Month + 
				"." + System.DateTime.Now.Day + "." + System.DateTime.Now.Year + ".txt";
			this._sw = new StreamWriter(this._outputFileName);
			this._prioritySettings = new bool[FATAL_PRIORITY + 1];
			this._rl = new Runloop(this);
			this._rl.start();
			//Console.WriteLine();

			//for each of the priority settings provided we set the print out to true
			for(int i = 0; i < this._prioritySettings.Length; i++)
			{
				this._prioritySettings[i] = true;
			}
			//set the runloop going
		}

		public static Logger getLogger()
		{
			if(Logger._logger == null)
			{
				lock(sync)
				{
					Logger._logger = new Logger();
					Thread.Sleep(100);
				}
			}
			return Logger._logger;
		}

		override public void dispatch(Object obj) 
		{
			//Console.WriteLine(obj);
			this._sw.WriteLine(obj);
			this._sw.Flush();
		}

		public void kill()
		{
			this._rl.kill();
			this._sw.Close();
		}

		public void setPriority(int priority, bool setting)
		{
			if(priority > (this._prioritySettings.Length - 1))
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Invalid Priority Length");
				return;
			}
			else
			{
				this._prioritySettings[priority] = setting;
				return;
			}
		}

		public void log(int priority, String error)
		{
			if(this._prioritySettings[priority] == true) 
			{
				//Console.WriteLine((Logger.PRIORITY[priority] + "\r\n" + System.DateTime.Now + "\r\n" + error + "\r\n" + Logger.PRIORITY[priority]));
				//	Console.WriteLine("In like flint");
				this._rl.add((Logger.PRIORITY[priority] + "\r\n" + System.DateTime.Now + "\r\n" + error + "\r\n" + Logger.PRIORITY[priority] + "\r\n"));
				//	Console.WriteLine("iOut like flint");
			}
		}

		public void log(int priority, String error, Exception e)
		{
			if(this._prioritySettings[priority] == true) 
			{
				this._rl.add((Logger.PRIORITY[priority] + "\r\n" + System.DateTime.Now + "\r\n" + error + "\r\n" + e.ToString() + "\r\n" + Logger.PRIORITY[priority] + "\r\n"));
			}
		}

	}
}
