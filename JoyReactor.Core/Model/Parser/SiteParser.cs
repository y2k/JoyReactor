using System;
using System.Collections.Generic;
using JoyReactor.Core.Model.Web.Parser.Data;
using JoyReactor.Core.Model.Parser.Data;

namespace JoyReactor.Core.Model.Parser
{
	public abstract class SiteParser
	{
		public IDictionary<string, string> Cookies { get; set; }

		public abstract ID.SiteParser ParserId { get; }

		public virtual IDictionary<string, string> Login (string username, string password)
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

		public virtual void ExtractTag (string tag, ID.TagType type, int? currentPageId)
		{
			throw new NotImplementedException ();
		}

		#region Events

		public event EventHandler<ExportPostInformation> NewPostInformation;
		public event EventHandler<ExportPostComment> NewComment;
		public event EventHandler<ExportPost> NewPost;
		public event EventHandler<ExportTagInformation> NewTagInformation;
		public event EventHandler<ExportLinkedTag> NewLinkedTag;

		protected void OnNewComment (ExportPostComment comment)
		{
			var handler = NewComment;
			if (handler != null)
				handler (this, comment);
		}

		protected void OnNewPostInformation (ExportPostInformation postInfo)
		{
			var handler = NewPostInformation;
			if (handler != null)
				handler (this, postInfo);
		}

		protected void OnNewPost (ExportPost post)
		{
			var handler = NewPost;
			if (handler != null)
				handler (this, post);
		}

		protected void OnNewTagInformation (ExportTagInformation information)
		{
			var handler = NewTagInformation;
			if (handler != null)
				handler (this, information);
		}

		protected void OnNewLinkedTag (ExportLinkedTag tag)
		{
			var handler = NewLinkedTag;
			if (handler != null)
				handler (this, tag);
		}

		#endregion

		// ==============================================================================
	}

	public class ProfileExport
	{
		public string Username { get; set; }

		public Uri Image { get; set; }

		public float Rating { get; set; }

		public List<TagExport> ReadingTags { get; set; } = new List<TagExport>();

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

		public ExportTagInformation TagInfo { get; set; }

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