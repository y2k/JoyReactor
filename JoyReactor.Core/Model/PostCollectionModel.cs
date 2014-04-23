using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Database;
using Ninject;
using JoyReactor.Core.Model.Inject;

namespace JoyReactor.Core.Model
{
	public class PostCollectionModel : IPostCollectionModel
	{
		private ISiteParser[] parsers = InjectService.Instance.Get<ISiteParser[]>();

		#region IPostCollectionModel implementation

		public Task<List<Post>> GetPostsAsync (ID id, SyncFlags flags = SyncFlags.None)
		{
			return Task.Run(
				() => {


					if (flags == SyncFlags.First) SyncFirstPage(id);
					else if (flags == SyncFlags.Next) SyncNextPage(id);

//					try {
//						new ReactorParser().ExtractTagPostCollection(ID.TYPE_GOOD, null, 0, 
//							p => {
//								if (p.State == CollectionExportState.ExportState.PostItem) {
//									MainDb.Instance.InsertOrReplace(Convert(p.PostItem));
//								}
//							});
//					} catch {}

					return MainDb.Instance.Query<Post>("SELECT * FROM Post");
				});
		}

		#endregion

		private void SyncFirstPage (ID id)
		{

			var p = parsers.First (s => s.ParserId == id.Site);

			p.ExtractTagPostCollection (id.Type, id.Tag, 0, state => {

				if (state.State == CollectionExportState.ExportState.PostItem) SavePostToDatabase(state.PostItem);

			});

			throw new NotImplementedException ();
		}

		private void SavePostToDatabase (ExportPost post)
		{
			var p = Convert (post);
			MainDb.Instance.InsertOrReplace (post);
		}

		private void SyncNextPage (ID id)
		{
			throw new NotImplementedException ();
		}

		private Post Convert(ExportPost p)
		{
			return new Post {
				ServerId = "reactor:" + p.id,
				CommentCount = p.commentCount,
				Coub = p.coub,
				Created = p.created,
				PostId = p.id,
				Image = p.image,
				ImageHeight = p.imageHeight,
				ImageWidth = p.imageWidth,
				Rating = p.rating,
				Title = p.title,
				UserImage = p.userImage,
				UserName = p.userName,
			};
		}
	}
}