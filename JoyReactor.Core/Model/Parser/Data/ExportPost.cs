using System;

namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportPost
	{
		public string image { get; set; }
		public int imageWidth { get; set; }
		public int imageHeight { get; set; }

		public string userName { get; set; }
		public string title { get; set; }
		public string id { get; set; }
		public long created { get; set; }

		public string userImage { get; set; }
		public float rating { get; set; }
		public string coub { get; set; }
		public int commentCount { get; set; }
	}
}