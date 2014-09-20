using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportComment
	{
		public static ExportComment.ExportAttachment[] EmptyAttachments = new ExportComment.ExportAttachment[0];

//		public string parentId { get; set; }
		public string[] ParentIds { get; set; }

		public string id { get; set; }
		public string text { get; set; }
		public long Created { get; set; }
		public string userName { get; set; }
		public string userImage { get; set; }
		public ExportComment.ExportAttachment[] Attachments { get; set; }
        public float rating { get; set; }

		public class ExportAttachment
		{
			public string Image { get; set; }
			public int Width { get; set; }
			public int Height { get; set; }
		}
    }
}