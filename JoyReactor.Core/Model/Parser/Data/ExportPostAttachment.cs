using System;

namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportPostAttachment
	{
		public string Url { get; set; }
		public int Type { get; set; }

		public string Image { get; set; }
		public int Width { get; set; }
		public int Height { get; set; }
	}
}