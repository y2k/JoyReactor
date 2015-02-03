using System.Collections.Generic;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Gallery
{
    // TODO: востановить галерею
    [Activity(Label = "Gallery", ParentActivity = typeof(HomeActivity))]			
    public class GalleryActivity : BaseActivity
    {
        //		PostModel model = new PostModel ();
        //
        //		GridView list;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

//			SetContentView (Resource.Layout.activity_gallery);
//			list = FindViewById<GridView> (Resource.Id.list);
//
//			GalleryAdapter a;
//			list.Adapter = a = new GalleryAdapter (this);
//
//			var s = await model.GetAttachmentsAsync (Intent.Extras.GetInt (Arg1));
//			a.AddAll (s);
        }

        class GalleryAdapter : ArrayAdapter<Attachment>
        {
            public GalleryAdapter(Context c)
                : base(c, 0)
            {
            }

            public override View GetView(int position, View convertView, ViewGroup parent)
            {
                convertView = convertView ?? View.Inflate(Context, Resource.Layout.item_gallery, null);

                var i = GetItem(position);
                var web = convertView.FindViewById<WebImageView>(Resource.Id.image);

//				web.ImageSource = im.CreateThumbnailUrl (i.Url, 
//					(int)(100 * parent.Resources.DisplayMetrics.Density));
                web.ImageSizeDip = 100;
                web.ImageSource = i.Url;

                return convertView;
            }
        }
    }
}