using System;
using System.IO;
using System.Threading;
using psl.memento.pervasive.hermes.util.runloop;



namespace psl.memento.pervasive.hermes.util.log
{
	/// <summary>
	/// Logger is used to log messages for the server.
	/// it uses a runloop so that the calling class gets quick returns on 
	/// log calls.
	/// it also allows multiple levels of debugging.
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
		protected StreamWriter _sw;
		protected String _outputFileName;
		protected bool[] _prioritySettings;

		
		/// <summary>
		/// Protected Constructor
		/// </summary>
		protected Logger()
		{
			
			this._sw = new StreamWriter(Console.OpenStandardOutput());
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

		/// <summary>
		/// this class gets the logger object
		/// </summary>
		/// <returns></returns>
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

/// <summary>
/// this is the dispatch that actually puts the obj (which is a string) into the log file
/// or the console.
/// </summary>
/// <param name="obj"></param>
		override public void dispatch(Object obj) 
		{
			//Console.WriteLine(obj);
			this._sw.WriteLine(obj);
			this._sw.Flush();
		}


		public void kill()
		{
			this._rl.kill();
		}
		/// <summary>
		/// setPriority allows user to turn on and off output
		/// </summary>
		/// <param name="priority"></param>
		/// <param name="setting"></param>
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

		/// <summary>
		/// Default output is console
		/// </summary>
		public void setDefaultOutput()
		{
			this._sw = new StreamWriter(Console.OpenStandardOutput());
		}

		/// <summary>
		/// Allows for setting of log file destination.  Creates new file with passed file name and date time stamp on it.
		/// </summary>
		/// <param name="fileName"></param>

		public static void setLogFile(String fileName)
		{
			//create the file

			FileStream newLog = null;

			try
			{
				//append hour.minute.month.day.year.log to fileName
				fileName += "." + System.DateTime.Now.Hour + "." + 
					System.DateTime.Now.Minute + "." + System.DateTime.Now.Month + 
					"." + System.DateTime.Now.Day + "." + System.DateTime.Now.Year + ".log";
				newLog = File.Create(fileName);
			}
			catch(ArgumentNullException ane)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", ane);
				Logger.getLogger().setDefaultOutput();
			}
			catch(ArgumentException ae)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", ae);
				Logger.getLogger().setDefaultOutput();
			}
			catch(UnauthorizedAccessException uae)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", uae);
				Logger.getLogger().setDefaultOutput();
			}
			catch(PathTooLongException ptle)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", ptle);
				Logger.getLogger().setDefaultOutput();
			}
			catch(NotSupportedException nse)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", nse);
				Logger.getLogger().setDefaultOutput();
			}
			catch(IOException ioe)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", ioe);
				Logger.getLogger().setDefaultOutput();
			}
	
			//else we made it without too much trouble
			//we now create our streamwriter
			try
			{
				Logger.getLogger()._sw = new StreamWriter(newLog); 
			}		
				//we should not be catching anything here as the above catchs should have handled it.
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.INFO_PRIORITY, "Could not set logger to outfile.  Using default", e);
				Logger.getLogger().setDefaultOutput();	
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
				this._rl.add((Logger.PRIORITY[priority] + "\r\n" + System.DateTime.Now + "\r\n" + error + "\r\n" + "\r\n" + e.Message + "\r\n" + e.StackTrace + "\r\n" + Logger.PRIORITY[priority] + "\r\n"));
			}
		}
	
	}

}
