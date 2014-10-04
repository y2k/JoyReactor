using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Web.Parser.Data
{
	public class ExportPostComment
	{
		public string[] ParentIds { get; set; }

		public string Id { get; set; }

		public string Content { get; set; }

		public DateTime Created { get; set; }

		public ExportUser User { get; set; }

		public ExportAttachment[] Attachments { get; set; }

		public float Rating { get; set; }
	}
}