using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using psl.memento.pervasive.hermes.client.util;

namespace psl.memento.pervasive.hermes.client
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class DialogBox : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.TextBox textBox1;
		private System.Windows.Forms.Button button1;
		private MainForm parent;
	
		public DialogBox(MainForm mainForm)
		{
			this.parent = mainForm;
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
			// 
			// label1
			// 
			this.label1.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Regular);
			this.label1.Location = new System.Drawing.Point(8, 16);
			this.label1.Size = new System.Drawing.Size(120, 48);
			this.label1.Text = "Please enter the IP address of the Hermes Server.";
			this.label1.TextAlign = System.Drawing.ContentAlignment.TopCenter;
			// 
			// textBox1
			// 
			this.textBox1.BackColor = System.Drawing.Color.Black;
			this.textBox1.ForeColor = System.Drawing.Color.White;
			this.textBox1.Location = new System.Drawing.Point(8, 72);
			this.textBox1.Size = new System.Drawing.Size(120, 20);
			this.textBox1.Text = "";
			// 
			// button1
			// 
			this.button1.Location = new System.Drawing.Point(16, 96);
			this.button1.Size = new System.Drawing.Size(104, 32);
			this.button1.Text = "Connect";
			this.button1.Click += new System.EventHandler(this.button1_Click);
			// 
			// DialogBox
			// 
			this.BackColor = System.Drawing.Color.Red;
			this.ClientSize = new System.Drawing.Size(136, 135);
			this.Controls.Add(this.button1);
			this.Controls.Add(this.textBox1);
			this.Controls.Add(this.label1);
			this.ForeColor = System.Drawing.Color.Black;
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
			this.Text = "Server IP Address";
			this.Closed += new System.EventHandler(this.parent.connecting);

		}
		#endregion


		private void button1_Click(object sender, System.EventArgs e)
		{
			Logger.getLogger().log(Logger.INFO_PRIORITY, "At this point we have the ip address: " + this.textBox1.Text);
			string ip = this.textBox1.Text;
			//try
			//{
				this.parent._clientHandler.setIP(ip);
			//}
			//catch(System.Exception badIP)
			//{
				//do error handling here for bad ip
			//}
			//this.parent.testClose();
			this.Close();
			//Logger.getLogger().log(Logger.INFO_PRIORITY, "No we should return to our close.");
		}



	}
}
