using System;
using System.Collections;
using System.Threading;
using System.Security.Permissions;


namespace psl.memento.pervasive.hermes.util.runloop
{
	/// <summary>
	/// Runloop is a thread which allows queueing of objects and the dispatch of these objects.
	/// </summary>
	/// 
	public class Runloop
	{
		//instance variables

		private DispatchManager _dispatchManager;
		/// <summary>
		/// DMT is dispatch manager thread
		/// </summary>
		private Thread _dmt;

		/// <summary>
		/// Takes dispatcher object.
		/// </summary>
		/// <param name="dispatcher"></param>
		public Runloop(Dispatcher dispatcher)
		{
			//Console.Write((dispatcher == null) + "what");
			this._dispatchManager = new DispatchManager(dispatcher);
			this._dispatchManager.setRunning(false);
			this._dmt = new Thread(new ThreadStart(this._dispatchManager.dispatch));
			this._dmt.Start();
		}

		/// <summary>
		/// Start gets the runloop going and dispatching objects
		/// </summary>
		public void start()
		{
			this._dispatchManager.setRunning(true);
		}
	
		public void add(Object obj)
		{
			lock(this._dispatchManager._queue)
			{
		
				this._dispatchManager.enqueue(obj);
				//Console.WriteLine(this._dmt.ThreadState);
				if(this._dmt.ThreadState == ThreadState.Suspended) 
				{
					this._dmt.Resume();
				}
			}
		}
		

		public void stop()
		{
			this._dispatchManager.setRunning(false);
		}

		public void kill()
		{
			lock(this)
			{
				this._dispatchManager.kill();
				//		Console.WriteLine(this._dmt.IsAlive);
				if(this._dmt.ThreadState == ThreadState.Suspended) 
				{
					this._dmt.Resume();
				}
			}
		}
			
	}
		
}
