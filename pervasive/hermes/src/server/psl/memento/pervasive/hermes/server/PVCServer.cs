using System;
using System.Collections;
using System.Threading;

namespace psl.memento.pervasive.hermes.server
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class PVCServer
	{
		private Hashtable _clients;

		public PVCServer()
		{
			this._clients = new Hashtable();

			//
			// TODO: Add constructor logic here
			//
		}
/// <summary>
/// Called by the the listener.
/// </summary>
/// <param name="ch"></param>

		public void addClient(ClientHandler ch)
		{
			new Thread(new ThreadStart(ch.start));
		}
	}
}
