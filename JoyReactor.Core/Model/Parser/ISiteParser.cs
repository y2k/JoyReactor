using System;
using System.Collections.Generic;
using JoyReactor.Core.Model.Parser.Data;

namespace JoyReactor.Core.Model.Parser
{
	public interface ISiteParser
	{
		ID.SiteParser ParserId { get; }

		IDictionary<string, string> Login(string username, string password);

		void ExtractTagPostCollection (ID.TagType type, string tag, int lastLoadedPage, Action<CollectionExportState> callback);

		void ExtractPost (string postId, Action<PostExportState> callback);

		ProfileExport Profile (string username);
	}

	public class ProfileExport 
	{
		public string Username { get;set; }
		public Uri Image { get;set; }
		public float Rating { get;set; }

		public List<TagExport> ReadingTags { get; set; }

		public class TagExport 
		{
			public string Tag { get; set; }
			public string Title { get; set; }
		}
	}

	public class CollectionExportState
	{
		public enum ExportState { Begin, TagInfo, PostItem, LikendTag };

		public ExportState State { get; set; }
		public ExportTag TagInfo { get; set; }
		public ExportPost Post { get; set; }
		public ExportLinkedTag LinkedTag { get; set; }
	}

	public class PostExportState
	{
		public enum ExportState { Begin, Info, Comment, Tag, LinkedTag, LinkedPost }

		public ExportState State { get; set; }
		public string image {get;set;}
		public int imageWidth {get;set;}
		public int imageHeight {get;set;}
		public string userName {get;set;}
		public string userImage {get;set;}
		public string title {get;set;}
		public long created {get;set;}
		public float rating {get;set;}
		public string coub {get;set;}

		public ExportComment Comment { get; set; }
		public ExportPreviewPost LinkedPost { get; set; }
	}
}