using System;

namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportPost
	{
		[Obsolete]
		public string Image { get; set; }
		[Obsolete]
		public int ImageWidth { get; set; }
		[Obsolete]
		public int ImageHeight { get; set; }

		public string UserName { get; set; }
		public string Title { get; set; }
		public string Id { get; set; }
		public long Created { get; set; }

		public string UserImage { get; set; }
		public float Rating { get; set; }
		public string Coub { get; set; }
		public int CommentCount { get; set; }
	}
}