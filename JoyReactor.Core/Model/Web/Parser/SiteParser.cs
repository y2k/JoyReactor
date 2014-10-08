using System;
using System.Collections.Generic;
using JoyReactor.Core.Model.Web.Parser.Data;
using JoyReactor.Core.Model.Parser.Data;

namespace JoyReactor.Core.Model.Parser
{
	public abstract class SiteParser
	{
		public abstract ID.SiteParser ParserId { get; }

		public virtual IDictionary<string, string> Login (string username, string password)
		{
			throw new NotImplementedException ();
		}

		public virtual void ExtractTagPostCollection (ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			throw new NotImplementedException ();
		}

		public virtual ProfileExport Profile (string username)
		{
			throw new NotImplementedException ();
		}

		// ==============================================================================

		public virtual void ExtractPost (string postId)
		{
			throw new NotImplementedException ();
		}

		public event EventHandler<ExportPostInformation> NewPost;
		public event EventHandler<ExportPostComment> NewComment;

		protected void OnNewComment (ExportPostComment comment)
		{
			var handler = NewComment;
			if (handler != null)
				handler (this, comment);
		}

		protected void OnNewPost (ExportPostInformation postInfo)
		{
			var handler = NewPost;
			if (handler != null)
				handler (this, postInfo);
		}

		// ==============================================================================
	}

	public class ProfileExport
	{
		public string Username { get; set; }

		public Uri Image { get; set; }

		public float Rating { get; set; }

		public List<TagExport> ReadingTags { get; set; }

		public class TagExport
		{
			public string Tag { get; set; }

			public string Title { get; set; }
		}
	}

	public class CollectionExportState
	{
		public enum ExportState
		{
			Begin,
			TagInfo,
			PostItem,
			LikendTag}

		;

		public ExportState State { get; set; }

		public ExportTag TagInfo { get; set; }

		public ExportPost Post { get; set; }

		public ExportLinkedTag LinkedTag { get; set; }
	}

	public class PostExportState
	{
		public enum ExportState
		{
			Begin,
			Info,
			Comment,
			Tag,
			LinkedTag,
			LinkedPost

		}

		public ExportState State { get; set; }

		[Obsolete]
		public string Image { get; set; }

		[Obsolete]
		public int ImageWidth { get; set; }

		[Obsolete]
		public int ImageHeight { get; set; }

		public ExportPostAttachment[] Attachments { get; set; }

		public string UserName { get; set; }

		public string UserImage { get; set; }

		public string Title { get; set; }

		public string Content { get; set; }

		public long Created { get; set; }

		public float Rating { get; set; }

		public string Coub { get; set; }

		public ExportComment Comment { get; set; }

		public ExportPreviewPost LinkedPost { get; set; }
	}
}