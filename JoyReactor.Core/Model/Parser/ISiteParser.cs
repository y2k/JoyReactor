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
}