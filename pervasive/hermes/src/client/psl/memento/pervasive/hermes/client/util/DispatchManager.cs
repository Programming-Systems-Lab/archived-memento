using System;
using System.Threading;

namespace psl.memento.pervasive.hermes.client.util
{
	/// <summary>
	/// Summary description for DispatchManager.
	/// </summary>
	public class DispatchManager : WaitHandle
	{
		
		//incoming objects are stored in the array list
		private System.Collections.ArrayList _list;
		//the dispatcher is specified by the class that wants to use the runloop
		//the dispatcher may do a number of different things like write to a stream
		//or to a file
		private Dispatcher _dispatcher;
		private bool _isRunning;
		private bool _isKilled;


		public DispatchManager(Dispatcher ds)
		{
			this._isRunning = false;
			this._isKilled = false;
			this._dispatcher = ds;
			this._list = new System.Collections.ArrayList();
		}

		/// <summary>
		/// Dispatch is called when something shows up in the array list
		/// </summary>
		public void dispatch()
		{
			while(!this._isKilled)
			{
				if(this._isRunning)
				{
					lock(this)
					{
						if(this._list.Count > 0)
						{
							this._dispatcher.dispatch(this._list[0]);
							this._list.RemoveAt(0);
						}
					}
				}
				else
				{
					Thread.Sleep(1000);
				}
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
			this._list.Add(obj);
		}

		public void kill()
		{
			this._isKilled = true;
		}

	}
}
