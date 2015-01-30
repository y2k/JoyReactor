using System.Collections.Specialized;
using System.IO;
using System.Linq;
using Android.Graphics;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using Autofac;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;
using XamarinCommons.Image;

namespace JoyReactor.AndroidTv
{
	public class MainFragment : BrowseFragment
	{
		TagsViewModel viewmodel;

		public override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);
			RetainInstance = true;

			viewmodel = new TagsViewModel();
			viewmodel.Tags.CollectionChanged += HandleCollectionChanged;

			Adapter = new ArrayObjectAdapter(new ListRowPresenter());
		}

		void HandleCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
		{
			if (e.Action == NotifyCollectionChangedAction.Add)
			{
				foreach (var i in e.NewItems.Cast<TagsViewModel.TagItemViewModel>())
				{
					ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new PostPresenter());
					for (int j = 0; j < 10; j++)
						listRowAdapter.Add(new PostPresenter.PostWrapper());

					HeaderItem header = new HeaderItem(i.Title, null);
					((ArrayObjectAdapter)Adapter).Add(new ListRow(header, listRowAdapter));
				}
			}
		}

		public override void OnDestroy()
		{
			base.OnDestroy();
			viewmodel.Dispose();
		}

		#region App initialize

		static MainFragment()
		{
			var locator = new DefaultServiceLocator(new AndroidInjectModule());
			ServiceLocator.SetLocatorProvider(() => locator);
		}

		class AndroidInjectModule : Module
		{
			protected override void Load(ContainerBuilder builder)
			{
				builder.RegisterType<BitmapImageDecoder>().As<ImageDecoder>();
				builder.RegisterType<SQLitePlatformAndroid>().As<ISQLitePlatform>();
			}

			class BitmapImageDecoder : ImageDecoder
			{
				public override int GetImageSize(object commonImage)
				{
					return commonImage == null ? 0 : ((Bitmap)commonImage).ByteCount;
				}

				public override object DecoderStream(Stream stream)
				{
					return BitmapFactory.DecodeStream(stream);
				}
			}
		}

		#endregion
	}
}