using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using psl.memento.pervasive.hermes.client.util;
using System.Runtime.InteropServices;


namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// This form is used to get the desired client chat name 
	/// and the server ip address. 
	/// after the user selects connect the parent is informed and
	/// the server is connected to.
	/// </summary>
	public class DialogBox : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.TextBox textBox1;
		private System.Windows.Forms.Button button1;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.StatusBar statusBar1;
		private System.Windows.Forms.Button button2;
		private System.Windows.Forms.TextBox textBox2;
		[DllImport("coredll.dll")]
		private static extern bool SipShowIM(int dwFlag);
		private int _dwFlag;
		private MainForm mom;
	
		public DialogBox(MainForm mainForm)
		{
			this.mom = mainForm;
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
			this.label1 = new System.Windows.Forms.Label();
			this.textBox1 = new System.Windows.Forms.TextBox();
			this.button1 = new System.Windows.Forms.Button();
			this.label2 = new System.Windows.Forms.Label();
			this.textBox2 = new System.Windows.Forms.TextBox();
			this.statusBar1 = new System.Windows.Forms.StatusBar();
			this.button2 = new System.Windows.Forms.Button();
			// 
			// label1
			// 
			this.label1.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Regular);
			this.label1.Location = new System.Drawing.Point(60, 16);
			this.label1.Size = new System.Drawing.Size(120, 48);
			this.label1.Text = "Please enter the IP address of the Hermes Server.";
			this.label1.TextAlign = System.Drawing.ContentAlignment.TopCenter;
			// 
			// textBox1
			// 
			this.textBox1.BackColor = System.Drawing.Color.Black;
			this.textBox1.ForeColor = System.Drawing.Color.White;
			this.textBox1.Location = new System.Drawing.Point(60, 72);
			this.textBox1.Size = new System.Drawing.Size(120, 20);
			this.textBox1.Text = "128.59.14.168";
			// 
			// button1
			// 
			this.button1.Location = new System.Drawing.Point(68, 144);
			this.button1.Size = new System.Drawing.Size(104, 32);
			this.button1.Text = "Connect";
			this.button1.Click += new System.EventHandler(this.button1_Click);
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(68, 96);
			this.label2.Size = new System.Drawing.Size(104, 16);
			this.label2.Text = "Enter Chat Name";
			this.label2.TextAlign = System.Drawing.ContentAlignment.TopCenter;
			// 
			// textBox2
			// 
			this.textBox2.Location = new System.Drawing.Point(60, 120);
			this.textBox2.Size = new System.Drawing.Size(120, 20);
			this.textBox2.Text = "Bubba";
			// 
			// statusBar1
			// 
			this.statusBar1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Underline);
			this.statusBar1.Location = new System.Drawing.Point(0, 268);
			this.statusBar1.Size = new System.Drawing.Size(240, 32);
			this.statusBar1.Text = "Welcome Friend";
			// 
			// button2
			// 
			this.button2.Location = new System.Drawing.Point(168, 280);
			this.button2.Size = new System.Drawing.Size(64, 16);
			this.button2.Text = "keyboard";
			this.button2.Click += new System.EventHandler(this.changeKeyboard);
			// 
			// DialogBox
			// 
			this.BackColor = System.Drawing.Color.Red;
			this.ClientSize = new System.Drawing.Size(240, 300);
			this.Controls.Add(this.button2);
			this.Controls.Add(this.statusBar1);
			this.Controls.Add(this.textBox2);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.button1);
			this.Controls.Add(this.textBox1);
			this.Controls.Add(this.label1);
			this.ForeColor = System.Drawing.Color.Black;
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
			this.Text = "Server IP Address";

			this.Closed += new System.EventHandler(this.mom.connecting);
		}
		#endregion

			// ##############  if you edit this form the following line needs to be pased at the end of the
			//initialize form method above this line.
			//	this.Closed += new System.EventHandler(this.mom.connecting);

		private void button1_Click(object sender, System.EventArgs e)
		{
			Logger.getLogger().log(Logger.INFO_PRIORITY, "At this point we have the ip address: " + this.textBox1.Text);
			string ip = this.textBox1.Text;
			string chatName = this.textBox2.Text;
			//try
			//{
				this.mom._clientHandler.setIP(ip);
				this.mom._clientHandler.setChatName(chatName);
			//}
			//catch(System.Exception badIP)
			//{
				//do error handling here for bad ip
			//}
			//this.mom.testClose();
			this.Close();
			//Logger.getLogger().log(Logger.INFO_PRIORITY, "No we should return to our close.");
		}

		private void changeKeyboard(object sender, System.EventArgs e)
		{
			if(this._dwFlag == 1)
				this._dwFlag = 0;
			else
				this._dwFlag = 1;

			SipShowIM(this._dwFlag);

		}



	}
}
