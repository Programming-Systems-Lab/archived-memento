using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;
using psl.memento.pervasive.hermes.client.ClientHandler;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.client.util;

namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class MainForm : System.Windows.Forms.Form
	{
		public client.ClientHandler.ClientHandler _clientHandler;

		public MainForm()
		{
			this._clientHandler = new client.ClientHandler.ClientHandler();
				
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//
		}
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			base.Dispose( disposing );
		}
		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			// 
			// MainForm
			// 
			this.ClientSize = new System.Drawing.Size(194, 275);
			this.Text = "Hermes";
			this.Load += new System.EventHandler(this.MainForm_Load);
			this.Closed += new System.EventHandler(this.MainForm_Close);

		}
		#endregion

		/// <summary>
		/// The main entry point for the application.
		/// </summary>

		static void Main() 
		{
			Application.Run(new MainForm());
		}

		private void MainForm_Load(object sender, System.EventArgs e)
		{
			//System.Console.WriteLine("made it this far old man.");
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Start up this bad boy and go.");
			//Logger.getLogger().log(Logger.INFO_PRIORITY, "Everything is going now.");
			DialogBox db = new DialogBox(this);
			db.ShowDialog();
			//db.Closed += new System.EventHandler(this.testClose);
			//db.Closed += new System.EventHandler(this.connecting);
		}

		private void MainForm_Close(object sender, System.EventArgs e)
		{
			Logger.getLogger().log(Logger.INFO_PRIORITY, "Debugger is closing down.");
			Logger.getLogger().kill();
		}

		public void connecting(object sender, System.EventArgs e)
		{
			Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Now we are gtoing to try and connect.");
			try
			{
				//the first thing we do is send off a connection xml object.
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Now we are trying to connect");
				this._clientHandler.connect();
			}
			catch(Exception eeee)
			{
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Problem connecting.", eeee);
			}
			//this._clientHandler.connect();
			
		}
	}
}
