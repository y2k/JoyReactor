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
	}

	public class CollectionExportState
	{
		public enum ExportState { Begin, TagInfo, PostItem, LikendTagItem };

		public ExportState State { get; set; }
		public ExportTag TagInfo { get; set; }
		public ExportPost PostItem { get; set; }
		public ExportLinkedTag LinkedTagItem { get; set; }
	}
}