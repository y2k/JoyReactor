using System.Collections.Generic;
using JoyReactor.Core.Model;
using System;
using System.Linq;
using JoyReactor.Core.Model.DTO;
using System.Threading.Tasks;

namespace JoyReactor.Core.Controllers
{
	public class TagInformationController
	{
		public List<ItemController> Items { get; set; } = new List<ItemController>();

		public Action InvalidateUiCallback { get; set; }

		TagCollectionModel model = new TagCollectionModel ();

		public async Task ChangeCurrentTag (ID currentTagId)
		{
			Items.Clear ();
			InvalidateUiCallback ();

			var tags = await model.GetTagLinkedTagsAsync (currentTagId);

			Items.AddRange (tags.Select (ConvertToController));
			InvalidateUiCallback ();
		}

		ItemController ConvertToController (TagLinkedTag tag)
		{
			return new ItemController (tag);
		}

		public class ItemController
		{
			public string Group { get { return tag.GroupName; } }

			public string Title { get { return tag.Title; } }

			public string Image { get { return tag.Image; } }

			readonly TagLinkedTag tag;

			public ItemController (TagLinkedTag tag)
			{
				this.tag = tag;
			}
		}
	}
}