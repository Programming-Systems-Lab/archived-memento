using System;

namespace psl.memento.pervasive.hermes.util.runloop
{
	/// <summary>
	/// Dispatcher is used by runloop to dispatch objects via the dispatch call.
	/// </summary>
	abstract public class Dispatcher
	{
		/// <summary>
		/// Dispatch is called from the runloop on an object dequeued.
		/// </summary>
		/// <param name="objectForDispatch"></param>
		abstract public void dispatch(Object objectForDispatch);
	}
}
