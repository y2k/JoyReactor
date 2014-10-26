using System;
using HtmlAgilityPack;
using System.Text.RegularExpressions;
using System.Linq;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Parser
{
	public class ChanPostLinker
	{
		private string firstPostId;
		private Dictionary<string, int> subIds;

		public ChanPostLinker (string firstPostId)
		{
			this.firstPostId = firstPostId;
			subIds = new Dictionary<string, int> ();
		}

		public List<PartialPost> Export (HtmlNode postNode, string originalId)
		{
			var ps = new List<string> ();
			HtmlDocument pc = null;
			int index = 0;
			var posts = new List<PartialPost> ();

			foreach (var z in postNode.ChildNodes) {
				if (z.Name == "a" && z.InnerHtml.StartsWith (">>")) {
					if (pc == null) {
						var id = Regex.Match (z.InnerHtml, ">>(\\d+)").Groups [1].Value;
						int cnt;
						if (!subIds.TryGetValue (id, out cnt))
							cnt = 1;
						for (int i = 0; i < cnt; i++) {
							ps.Add (id + "-" + i);
						}
					} else {
						var data = new PartialPost ();
						if (ps.Count == 0 || ps.All (s => s == firstPostId + "-0")) {
							data.Id = originalId + "-0";
							data.ParentIds = null;
							data.Content = pc.DocumentNode.InnerHtml;
						} else {
							data.Id = originalId + "-" + (index++);
							data.ParentIds = ps.ToArray ();
							data.Content = pc.DocumentNode.InnerHtml;
						}
						posts.Add (data);
						ps.Clear ();
						pc = null;
					}
				} else {
					pc = pc ?? new HtmlDocument ();
					pc.DocumentNode.AppendChild (z);
				}
			}
			if (pc != null) {
				var data = new PartialPost ();
				if (ps.Count == 0 || ps.All (s => s == firstPostId + "-0")) {
					data.Id = originalId + "-0";
					data.ParentIds = null;
					data.Content = pc.DocumentNode.InnerHtml;
				} else {
					data.Id = originalId + "-" + (index++);
					data.ParentIds = ps.ToArray ();
					data.Content = pc.DocumentNode.InnerHtml;
				}
				posts.Add (data);
			}
			if (index > 1)
				subIds [originalId] = index;

			return posts;
		}

		public class PartialPost
		{
			public string Content { get; set; }

			public string Id { get; set; }

			public string[] ParentIds { get; set; }

			public override bool Equals (object obj)
			{
				var o = obj as PartialPost;
				if (o == null)
					return false;
				return 
					Content == o.Content &&
				Id == o.Id &&
				CompareArray (ParentIds, o.ParentIds);
			}

			private static bool CompareArray (string[] l, string[] r)
			{
				if (l == null && r == null)
					return true;
				if ((l == null && r != null) || (l != null && r == null))
					return false;
				return Enumerable.SequenceEqual (l, r);
			}
		}
	}
}