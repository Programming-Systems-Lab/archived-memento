using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;
using System.Threading;
using psl.memento.pervasive.hermes.client.ClientHandler;
using psl.memento.pervasive.hermes.xml.objects;
using psl.memento.pervasive.hermes.xml.messages;
using psl.memento.pervasive.hermes.client.util;

namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class MainForm : System.Windows.Forms.Form
	{
		public System.Windows.Forms.ListBox listBox1;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Button button1;
		public System.Windows.Forms.ListBox listBox2;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Button button2;
		private System.Windows.Forms.Button button3;
		public System.Windows.Forms.StatusBar statusBar1;
		private System.Windows.Forms.Button button4;
		private System.Windows.Forms.Panel panel1;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Button button5;
		private System.Windows.Forms.Button button6;
		private System.Windows.Forms.Panel panel2;
		private System.Windows.Forms.Button button7;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.Timer timer1;
		private System.Windows.Forms.ProgressBar progressBar1;
		public client.ClientHandler.ClientHandler _clientHandler;
		//this is used to temporarliy hold on to a chat request
		private ChatRequest _cr;

		public MainForm()
		{
			this._clientHandler = new ClientHandler.ClientHandler(this);
				
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
			this.listBox1 = new System.Windows.Forms.ListBox();
			this.label1 = new System.Windows.Forms.Label();
			this.button1 = new System.Windows.Forms.Button();
			this.listBox2 = new System.Windows.Forms.ListBox();
			this.label2 = new System.Windows.Forms.Label();
			this.button2 = new System.Windows.Forms.Button();
			this.button3 = new System.Windows.Forms.Button();
			this.statusBar1 = new System.Windows.Forms.StatusBar();
			this.button4 = new System.Windows.Forms.Button();
			this.panel1 = new System.Windows.Forms.Panel();
			this.button5 = new System.Windows.Forms.Button();
			this.label3 = new System.Windows.Forms.Label();
			this.button6 = new System.Windows.Forms.Button();
			this.panel2 = new System.Windows.Forms.Panel();
			this.progressBar1 = new System.Windows.Forms.ProgressBar();
			this.label4 = new System.Windows.Forms.Label();
			this.button7 = new System.Windows.Forms.Button();
			this.timer1 = new System.Windows.Forms.Timer();
			// 
			// listBox1
			// 
			this.listBox1.Location = new System.Drawing.Point(16, 40);
			this.listBox1.Size = new System.Drawing.Size(200, 69);
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(16, 8);
			this.label1.Size = new System.Drawing.Size(128, 24);
			this.label1.Text = "Chat Buddies";
			// 
			// button1
			// 
			this.button1.Location = new System.Drawing.Point(8, 112);
			this.button1.Size = new System.Drawing.Size(72, 24);
			this.button1.Text = "Chat";
			this.button1.Click += new System.EventHandler(this.button1_Click);
			// 
			// listBox2
			// 
			this.listBox2.Location = new System.Drawing.Point(16, 160);
			this.listBox2.Size = new System.Drawing.Size(200, 56);
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(16, 144);
			this.label2.Size = new System.Drawing.Size(104, 16);
			this.label2.Text = "Current Chat";
			// 
			// button2
			// 
			this.button2.Location = new System.Drawing.Point(88, 112);
			this.button2.Size = new System.Drawing.Size(80, 24);
			this.button2.Text = "Stop Chat";
			this.button2.Click += new System.EventHandler(this.button2_Click);
			// 
			// button3
			// 
			this.button3.Location = new System.Drawing.Point(8, 224);
			this.button3.Size = new System.Drawing.Size(72, 24);
			this.button3.Text = "Disconnect";
			this.button3.Click += new System.EventHandler(this.button3_Click);
			// 
			// statusBar1
			// 
			this.statusBar1.Location = new System.Drawing.Point(0, 259);
			this.statusBar1.Size = new System.Drawing.Size(234, 16);
			this.statusBar1.Text = "Pending Connection";
			// 
			// button4
			// 
			this.button4.Enabled = false;
			this.button4.Location = new System.Drawing.Point(96, 8);
			this.button4.Size = new System.Drawing.Size(96, 24);
			this.button4.Text = "Talk";
			// 
			// panel1
			// 
			this.panel1.BackColor = System.Drawing.Color.Gray;
			this.panel1.Controls.Add(this.button5);
			this.panel1.Controls.Add(this.label3);
			this.panel1.Controls.Add(this.button6);
			this.panel1.Location = new System.Drawing.Point(24, 64);
			this.panel1.Size = new System.Drawing.Size(184, 120);
			this.panel1.Visible = false;
			// 
			// button5
			// 
			this.button5.Location = new System.Drawing.Point(16, 56);
			this.button5.Size = new System.Drawing.Size(64, 32);
			this.button5.Text = "Accept";
			this.button5.Click += new System.EventHandler(this.button5_Click);
			// 
			// label3
			// 
			this.label3.Location = new System.Drawing.Point(16, 8);
			this.label3.Size = new System.Drawing.Size(160, 16);
			this.label3.Text = "Incoming Call.";
			// 
			// button6
			// 
			this.button6.Location = new System.Drawing.Point(104, 56);
			this.button6.Size = new System.Drawing.Size(64, 32);
			this.button6.Text = "Decline";
			this.button6.Click += new System.EventHandler(this.button6_Click);
			// 
			// panel2
			// 
			this.panel2.BackColor = System.Drawing.Color.Goldenrod;
			this.panel2.Controls.Add(this.progressBar1);
			this.panel2.Controls.Add(this.label4);
			this.panel2.Controls.Add(this.button7);
			this.panel2.Location = new System.Drawing.Point(32, 40);
			this.panel2.Size = new System.Drawing.Size(176, 152);
			this.panel2.Visible = false;
			// 
			// progressBar1
			// 
			this.progressBar1.Location = new System.Drawing.Point(16, 64);
			this.progressBar1.Size = new System.Drawing.Size(144, 16);
			// 
			// label4
			// 
			this.label4.Location = new System.Drawing.Point(8, 8);
			this.label4.Size = new System.Drawing.Size(160, 40);
			this.label4.Text = "Waiting for chat buddy response...";
			// 
			// button7
			// 
			this.button7.Location = new System.Drawing.Point(40, 112);
			this.button7.Size = new System.Drawing.Size(88, 24);
			this.button7.Text = "Cancel";
			this.button7.Click += new System.EventHandler(this.button7_Click);
			// 
			// timer1
			// 
			this.timer1.Interval = 50;
			this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
			// 
			// MainForm
			// 
			this.ClientSize = new System.Drawing.Size(234, 275);
			this.Controls.Add(this.panel1);
			this.Controls.Add(this.button4);
			this.Controls.Add(this.statusBar1);
			this.Controls.Add(this.button3);
			this.Controls.Add(this.button2);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.listBox2);
			this.Controls.Add(this.button1);
			this.Controls.Add(this.label1);
			this.Controls.Add(this.listBox1);
			this.Controls.Add(this.panel2);
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

		private void button3_Click(object sender, System.EventArgs e)
		{
			//Logger.getLogger().log(Logger.DEBUG_PRIORITY, "Ok?  Good!@!!!!!");
			this._clientHandler.disconnect();
		}

		private void button5_Click(object sender, System.EventArgs e)
		{
			this.panel1.Visible = false;
		}

		private void button6_Click(object sender, System.EventArgs e)
		{
			this.panel1.Visible = false;
			this.panel1.SendToBack();
			ThreadPool.QueueUserWorkItem(new WaitCallback(this._clientHandler._crh.chatReject), this._cr._chatID.ToString());
			
		}

		private void button1_Click(object sender, System.EventArgs e)
		{
			if(this.listBox1.SelectedItem == null)
			{
				System.Windows.Forms.MessageBox.Show("You must select a buddy to chat with.", "Error");
			}
			this.timer1.Enabled = true;
			this.panel2.BringToFront();
			this.panel2.Visible = true;
			//this.panel2.BringToFront();
			ChatBuddy cb = (ChatBuddy)this.listBox1.SelectedItem;
			//if this returns true we are connected and the client should be moved to the other box
			this._clientHandler.requestChat(cb);

		}

		public void chatInviteReject(object reason)
		{
	
			System.Windows.Forms.MessageBox.Show("Cannot connect with chat buddy. Reason:\r\n" + (string)reason, "Error");
		
			
			this.timer1.Enabled = false;
			this.panel2.Visible = false;
			this.panel2.SendToBack();
		}

		private void button2_Click(object sender, System.EventArgs e)
		{
			if(!this._clientHandler._client._isChatting)
			{
				System.Windows.Forms.MessageBox.Show("You are not currently chatting.", "Error");
			}
		}

		private void button7_Click(object sender, System.EventArgs e)
		{
			this.panel2.Visible = false;
			this.timer1.Enabled = false;
		}

		private void timer1_Tick(object sender, System.EventArgs e)
		{
			int i = this.progressBar1.Value;
			if(i == this.progressBar1.Maximum)
				this.progressBar1.Value = 0;
			else
				this.progressBar1.Value++;
		}

		public void incomingChatInvite(object chatRequest)
		{
			this._cr = (ChatRequest)chatRequest;
		
			this.label3.Text = "Incoming Chat Request from Buddy: \r\n" + this._cr._chatBuddy.getChatName();
			this.panel1.Visible = true;
			this.panel1.BringToFront();
			
		}

	}
}
