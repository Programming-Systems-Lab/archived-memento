using System;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;
using psl.memento.pervasive.hermes.client.ClientHandler;

namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class MainForm : System.Windows.Forms.Form
	{
		public client.ClientHandler.ClientHandler clientHandler;

		public MainForm()
		{
			this.clientHandler = new client.ClientHandler.ClientHandler();
				
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
			System.Console.WriteLine("made it this far old man.");
			DialogBox db = new DialogBox(this);
			db.ShowDialog();
			db.Closed += new System.EventHandler(this.connecting);

		}

		public void connecting(object sender, System.EventArgs e)
		{

		}
	}
}
