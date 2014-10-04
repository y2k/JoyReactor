using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportComment
	{
		public static ExportComment.ExportAttachment[] EmptyAttachments = new ExportComment.ExportAttachment[0];

//		public string parentId { get; set; }
		public string[] ParentIds { get; set; }

		public string Id { get; set; }
		public string Text { get; set; }
		public long Created { get; set; }
		public string UserName { get; set; }
		public string UserImage { get; set; }
		public ExportComment.ExportAttachment[] Attachments { get; set; }
        public float Rating { get; set; }

		public class ExportAttachment
		{
			public string Image { get; set; }
			public int Width { get; set; }
			public int Height { get; set; }
		}
    }
}