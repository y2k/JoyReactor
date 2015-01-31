using System.Linq;
using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.AndroidTv
{
    [Activity(Label = "PostActivity")]			
    public class PostActivity : Activity
    {
        public const string SharedElementName = "hero";
        public const string PostId = "post-id";

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.Main);
            if (bundle == null)
                FragmentManager.BeginTransaction().Add(Resource.Id.container, new PostFragment()).Commit();

            // Create your application here
        }

        public class PostFragment : DetailsFragment
        {
 
            PostViewModel viewmodel;
            DetailsOverviewRow deltailsRow;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);

                CreateUi();
                viewmodel = new PostViewModel();

                viewmodel.ViewModelParts.CollectionChanged += (sender, e) =>
                {
                    if (e.NewItems != null)
                        foreach (var i in e.NewItems.OfType<PostViewModel.PosterViewModel>().Where(s => s != null))
                            new ImageModel().Load(
                                deltailsRow, 
                                i.Image, 
                                300, 
                                bitmap => 
                                    deltailsRow.SetImageBitmap(Activity, (Bitmap)bitmap));
                };

                viewmodel.Initialize(Activity.Intent.GetIntExtra(PostId, 0));
            }

            void CreateUi()
            {
                var dor = new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter()) { StyleLarge = true };

                var adapter = new ArrayObjectAdapter(dor);
                Adapter = adapter;

                deltailsRow = new DetailsOverviewRow(new DetailsDescriptionPresenter.Wrapper());

                deltailsRow.SetImageBitmap(Activity, Bitmap.CreateBitmap(200, 200, Bitmap.Config.Argb8888));

                deltailsRow.AddAction(new Action(1, "Fullscreen"));
                deltailsRow.AddAction(new Action(2, "Rate"));
                adapter.Add(deltailsRow);
            }

            public override void OnDestroy()
            {
                base.OnDestroy();
                viewmodel.Cleanup();
            }

            class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter
            {

                protected override void OnBindDescription(ViewHolder vh, Java.Lang.Object item)
                {
                    vh.Body.Text = "Body";
                    vh.Subtitle.Text = "Subtitle";
                    vh.Title.Text = "Title";
                }

                internal class Wrapper : Java.Lang.Object
                {

                    internal PostViewModel.PosterViewModel Post { get; set; }
                }
            }
        }
    }
}