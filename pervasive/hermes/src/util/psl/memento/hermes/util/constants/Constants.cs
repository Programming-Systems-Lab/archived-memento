using System;
using System.IO;
using System.Xml;
using System.Collections;
using Utilities.exceptions;
using Utilities.log;

namespace Utilities.constants
{
	/// <summary>
	/// Constants class parses the constants.xml file and puts all the elements in a hash table.
	/// This hash table is used to access the constants.
	/// </summary>
	public class Constants
	{
		
		private Hashtable CONSTANTS;
		private static Constants _con = new Constants(); 

		private Constants()
		{
			//open up the constants.xml file
			try
			{
				CONSTANTS = new Hashtable();

				//constants.xml must be in the cbas dir
				StreamReader sr = new StreamReader("constants.xml");
				XmlDocument doc = new XmlDocument();
				//load the doc
				doc.Load(sr);
				//we can now get all the string elements
				
				XmlNodeList elemsList = doc.GetElementsByTagName("constant");

				for(int i=0; i < elemsList.Count; i++)
				{
					String type = elemsList[i].FirstChild.InnerText;

					//get the right type and parse if necessary
					if(type == "int") 
					{
						System.Int32 nodeValue = System.Int32.Parse(elemsList[i].FirstChild.NextSibling.InnerText);
						CONSTANTS.Add(elemsList[i].FirstChild.InnerText, nodeValue);
					}
					else if(type == "double")
					{
						System.Double nodeValue = System.Double.Parse(elemsList[i].FirstChild.NextSibling.InnerText);
						CONSTANTS.Add(elemsList[i].FirstChild.InnerText, nodeValue);
					}
					else 
					{
						CONSTANTS.Add(elemsList[i].FirstChild.InnerText, elemsList[i].FirstChild.NextSibling.InnerText);
					}
				}
			}
			catch(Exception e)
			{
				Logger.getLogger().log(Logger.FATAL_PRIORITY, "Trouble parsing constant file", e);
				throw new ConstantNotFoundException("Cannot Parse this file", e);
			}

		}

		/// <summary>
		/// Used to parse a constant from the constant.xml file that is of the type string.
		/// </summary>
		/// <param name="sConstName">The name of the String constant</param>
		/// <returns></returns>
	
		public static string getSConst(String sConstName)
		{
			try
			{
				Logger.getLogger().log(Logger.DEBUG_PRIORITY, "is null? " + (Constants._con.CONSTANTS[sConstName] == null));
				if(Constants._con.CONSTANTS[sConstName] == null)
				{
					throw new ConstantNotFoundException("String lookup returned null");
				}
				return (String)Constants._con.CONSTANTS[sConstName];
			}
			catch(Exception e)
			{
				ConstantNotFoundException cnf = new ConstantNotFoundException("Constant not found in XML constants file", e);
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Cannot find string constant", cnf);
				throw cnf;
			}

		}

		/// <summary>
		/// Used to parse a constant form the constant.xml file that is of the type int
		/// </summary>
		/// <param name="constName"></param>
		/// <returns></returns>
		public static int getIntConst(String constName)
		{
			try
			{
				return Int32.Parse(((String)Constants._con.CONSTANTS[constName]));
			}
			catch(Exception e)
			{
				ConstantNotFoundException cnf = new ConstantNotFoundException("Constant not found in XML constants file", e);
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Cannot find int constant", cnf);
				throw cnf;
			}

		}

		
		/// <summary>
		/// Used to parse a constant form the constant.xml file that is of the type double
		/// </summary>
		/// <param name="constName"></param>
		/// <returns></returns>
		public static double getDoubleConst(String constName)
		{
			try
			{
				return Double.Parse(((String)Constants._con.CONSTANTS[constName]));
			}
			catch(Exception e)
			{
				ConstantNotFoundException cnf = new ConstantNotFoundException("Constant not found in XML constants file", e);
				Logger.getLogger().log(Logger.EXCEPTION_PRIORITY, "Cannot find double constant", cnf);
				throw cnf;
			}

		}

	}
}
