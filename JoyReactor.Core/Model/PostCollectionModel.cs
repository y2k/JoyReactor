using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Inject;
using Autofac;
using Microsoft.Practices.ServiceLocation;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;

namespace JoyReactor.Core.Model
{
	class PostCollectionModel : IPostCollectionModel
	{
		private ISQLiteConnection connection = ServiceLocator.Current.GetInstance<ISQLiteConnection> ();
		private bool isFirstExecuteFirstPostForTag = true;

		#region New Methods

		public Task<PostCollectionState> Get (ID id)
		{
			return Task.Run (() => {
				var tagId = ToFlatId (id);
				var result = new PostCollectionState ();
				result.NewItemsCount = connection.SafeExecuteScalar<int> (
					"SELECT COUNT(*) FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
					tagId);
				result.Posts = connection.SafeQuery<Post> (
					"SELECT * " +
					"FROM posts " +
					"WHERE Id IN (" +
					"   SELECT PostId " +
					"   FROM tag_post " +
					"   WHERE TagId IN (" +
					"      SELECT Id " +
					"      FROM tags " +
					"      WHERE TagId = ?))", 
					tagId);
				return result;
			});
		}

		public Task SyncFirstPage (ID id)
		{
			return Task.Run (() => {
				var parser = GetParserForTag (id);
				parser.Cookies = GetSiteCookies (id);
				parser.NewPost += (sender, post) => {
					if (IsFirstPost ())
						ClearDatabaseFromPosts (id);
					SavePostToDatabase (id, post);
				};
				parser.ExtractTag (id.Tag, id.Type, 0);
			});
		}

		private SiteParser GetParserForTag (ID id)
		{
			var parsers = ServiceLocator.Current.GetInstance<SiteParser[]> ();
			return parsers.First (s => s.ParserId == id.Site);
		}

		private bool IsFirstPost ()
		{
			try {
				return isFirstExecuteFirstPostForTag;
			} finally {
				isFirstExecuteFirstPostForTag = false;
			}
		}

		private void ClearDatabaseFromPosts (ID id)
		{
			connection.SafeExecute ("UPDATE tags SET Timestamp = ? WHERE TagId = ?", TimestampNow (), ToFlatId (id));
			// Удаление постов тега
			connection.SafeExecute ("DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)", ToFlatId (id));
			// Удаление связанных тегов
			connection.SafeExecute ("DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)", ToFlatId (id));
		}

		public Task ApplyNewItems (ID id)
		{
			throw new NotImplementedException ();
		}

		#endregion

		#region IPostCollectionModel implementation

		public Task<List<Post>> GetPostsAsync (ID id)
		{
			return Task.Run (() => {
				var sid = ToFlatId (id);
				var sql = "" +
				          "SELECT * " +
				          "FROM posts " +
				          "WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?))";
				return MainDb.Instance.SafeQuery<Post> (sql, sid);
			});
		}

		public Task SyncTask (ID id, SyncFlags flags)
		{
			return Task.Run (() => {

				if (flags == SyncFlags.First)
					InnerSyncFirstPage (id);
				else if (flags == SyncFlags.Next)
					SyncNextPage (id);


			});
		}

		//		public event EventHandler<ID> PostChanged {
		//			add { GlobalHandler += value; }
		//			remove { GlobalHandler -= value; }
		//		}

		public Task<PostCollection> GetListAsync (ID id, SyncFlags flags = SyncFlags.None)
		{
			if (flags == SyncFlags.First)
				Task.Run (() => InnerSyncFirstPage (id)).GetAwaiter ();
			return Task.Run (
				() => {
					var sid = ToFlatId (id);
					var r = new PostCollection ();
					r.AddRange (MainDb.Instance.SafeQuery<Post> ("SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?))", sid));
					return r;
				});
		}

		public Task<List<Post>> GetPostsAsync (ID id, SyncFlags flags = SyncFlags.None)
		{
			return Task.Run (
				() => {
					if (id.Type == ID.TagType.Favorite && id.Tag == null) {
						id.Tag = MainDb.Instance.SafeExecuteScalar<string> ("SELECT Username From profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor);
					}

					if (flags == SyncFlags.First)
						InnerSyncFirstPage (id);
					else if (flags == SyncFlags.Next)
						SyncNextPage (id);

					var sid = ToFlatId (id);
					return MainDb.Instance.SafeQuery<Post> ("SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?))", sid);
				});
		}

		public int GetCount (ID id)
		{
			return MainDb.Instance.SafeExecuteScalar<int> (
				"SELECT COUNT(*) FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
				ToFlatId (id));
		}

		public Task<int> GetCountAsync (ID id)
		{
			return Task.Run (() => GetCount (id));
		}

		#endregion

		#region Private methods

		private string ToFlatId (ID id)
		{
			return id.Site + "-" + id.Type + "-" + id.Tag;
		}

		private void InnerSyncFirstPage (ID id)
		{
//			long ts = MainDb.Instance.SafeExecuteScalar<long> ("SELECT Timestamp FROM tags WHERE TagId = ?", ToFlatId (id));
//			if (Math.Abs (ts - TimestampNow ()) < Constants.TagLifeTime)
//				return;
//
//			var p = parsers.First (s => s.ParserId == id.Site);
//			p.ExtractTagPostCollection (id.Type, id.Tag, 0, GetSiteCookies (id), state => {
//				if (state.State == CollectionExportState.ExportState.Begin) {
//					MainDb.Instance.SafeExecute ("UPDATE tags SET Timestamp = ? WHERE TagId = ?", TimestampNow (), ToFlatId (id));
//					// Удаление постов тега
//					MainDb.Instance.SafeExecute ("DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)", ToFlatId (id));
//					// Удаление связанных тегов
//					MainDb.Instance.SafeExecute ("DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)", ToFlatId (id));
//				} else if (state.State == CollectionExportState.ExportState.TagInfo) {
//					var t = MainDb.Instance.SafeQuery<Tag> ("SELECT * FROM tags WHERE TagId = ?", ToFlatId (id)).FirstOrDefault ()
//					        ?? new Tag { BestImage = state.TagInfo.Image, TagId = ToFlatId (id) };
//					t.NextPage = state.TagInfo.NextPage;
//					if (t.Id == 0)
//						MainDb.Instance.SafeInsert (t);
//					else
//						MainDb.Instance.SafeUpdate (t);
//				} else if (state.State == CollectionExportState.ExportState.PostItem) {
//					SavePostToDatabase (id, state.Post);
//				} else if (state.State == CollectionExportState.ExportState.LikendTag) {
//					// TODO Добавить проверку, что это первая страница
//					int tid = MainDb.Instance.SafeExecuteScalar<int> ("SELECT Id FROM tags WHERE TagId = ?", ToFlatId (id));
//					MainDb.Instance.SafeInsert (new TagLinkedTag {
//						ParentTagId = tid,
//						GroupName = state.LinkedTag.group,
//						Image = state.LinkedTag.image,
//						Title = state.LinkedTag.name,
//						TagId = ToFlatId (new ID {
//							Site = p.ParserId,
//							Type = ID.TagType.Good,
//							Tag = state.LinkedTag.value
//						}),
//					});
//				}
//			});
		}

		private void SyncNextPage (ID id)
		{
//			// TODO Убрать копипаст
//			var p = parsers.First (s => s.ParserId == id.Site);
//			var t = MainDb.Instance.SafeQuery<Tag> ("SELECT * FROM tags WHERE TagId = ?", ToFlatId (id)).First ();
//			p.ExtractTagPostCollection (id.Type, id.Tag, t.NextPage, GetSiteCookies (id), state => {
//				if (state.State == CollectionExportState.ExportState.TagInfo) {
//					t.NextPage = state.TagInfo.NextPage;
//					MainDb.Instance.SafeUpdate (t);
//				} else if (state.State == CollectionExportState.ExportState.PostItem) {
//					SavePostToDatabase (id, state.Post);
//				}
//			});
		}

		private IDictionary<string, string> GetSiteCookies (ID id)
		{
			var p = connection.SafeDeferredQuery<Profile> ("SELECT * FROM profiles WHERE Site = ?", "" + id.Site).FirstOrDefault ();
			return p == null ? new Dictionary<string, string> () : DeserializeObject<Dictionary<string, string>> (p.Cookie);
		}

		private void SavePostToDatabase (ID listId, ExportPost post)
		{
			var p = Convert (listId.Site, post);
			var f = ToFlatId (listId);

			p.Id = connection.SafeExecuteScalar<int> ("SELECT Id FROM posts WHERE PostId = ?", p.PostId);
			if (p.Id == 0)
				connection.SafeInsert (p);
			else
				connection.SafeUpdate (p);

			var tp = new TagPost ();
			tp.PostId = p.Id;
			tp.Status = TagPost.StatusNew;
			tp.TagId = connection.SafeExecuteScalar<int> ("SELECT Id FROM tags WHERE TagId = ?", f);
			connection.SafeInsert (tp);
		}

		private static long TimestampNow ()
		{
			return DateTime.Now.ToFileTimeUtc () / 10000L;
		}

		private static IDictionary<string, string> DeserializeObject<T> (string o)
		{
			return o.Split (';').Select (s => s.Split ('=')).ToDictionary (s => s [0], s => s [1]);
		}

		private Post Convert (ID.SiteParser parserId, ExportPost p)
		{
			return new Post {
				PostId = parserId + "-" + p.Id,
				CommentCount = p.CommentCount,
				Coub = p.Coub,
				Created = p.Created,
				Image = p.Image,
				ImageHeight = p.ImageHeight,
				ImageWidth = p.ImageWidth,
				Rating = p.Rating,
				Title = p.Title,
				UserImage = p.UserImage,
				UserName = p.UserName,
			};
		}

		#endregion
	}
}