
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.Model.DTO;

using JoyReactor.Android.Widget;
using Android.Graphics;

namespace JoyReactor.Android.App
{
	[Activity (Label = "Gallery")]			
	public class GalleryActivity : BaseActivity
	{
		private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel>();

		private GridView list;

		protected async override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			SetContentView (Resource.Layout.activity_gallery);
			list = FindViewById<GridView> (Resource.Id.list);

			GalleryAdapter a;
			list.Adapter = a = new GalleryAdapter (this);

			var s = await model.GetAttachmentsAsync (Intent.Extras.GetInt (Arg1));
			a.AddAll (s);
		}

		private class GalleryAdapter : ArrayAdapter<CommentAttachment>
		{
			private IImageModel im = ServiceLocator.Current.GetInstance<IImageModel> ();

			public GalleryAdapter(Context c) : base (c, 0) {
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				convertView = convertView ?? View.Inflate (Context, Resource.Layout.item_gallery, null);

				convertView.SetBackgroundColor (Color.Red);

				var i = GetItem (position);
				var web = convertView.FindViewById<WebImageView> (Resource.Id.image);

				web.ImageSource = im.CreateThumbnailUrl(i.Url, 
					(int)(100 * parent.Resources.DisplayMetrics.Density));

				return convertView;
			}
		}
	}
}