using System;
using System.Collections;
using System.Threading;
using System.Security;

namespace Utilities.runloop
{
	/// <summary>
	/// Handles the actual thread
	/// </summary>
	public class DispatchManager
	{
		//instance var

		private System.Collections.Queue _queue;
		private Dispatcher _dispatcher;
		private bool _isRunning;
		private bool _isKilled;

		/// <summary>
		/// Constructor for Dispatch Mananger
		/// </summary>
		/// <param name="dispatcher"></param>
		/// <param name="queue"></param>
		public DispatchManager(Dispatcher dispatcher)
		{
			this._isRunning = false;
			this._isKilled = false;
			this._dispatcher = dispatcher;
			//Console.Write(dispatcher);
			this._queue = Queue.Synchronized(new Queue());
		}

		/// <summary>
		/// Dispatch starts the queue on its way for good.
		/// </summary>
		public void dispatch()
		{

			while(!this._isKilled)
			{
				if(this._isRunning)
				{
					lock(this)
					{
						if(this._queue.Count > 0)
						{
							this._dispatcher.dispatch(this._queue.Dequeue());
						}
					}
				}
				
				else
				{
					Console.WriteLine("Here we die");
					Thread.CurrentThread.Suspend();
					Console.WriteLine("Here we live");
				}
				//Console.WriteLine("Here we loop");
			}
		}

		/// <summary>
		/// Method for seeting our runloop to go.
		/// </summary>
		/// <param name="running"></param>
		public void setRunning(bool running)
		{
			this._isRunning = running;
		}

		public void enqueue(Object obj)
		{
			this._queue.Enqueue(obj);
		}

		public void kill()
		{
			this._isKilled = true;
		}
	}
}
