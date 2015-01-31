using System.Linq;
using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using System.Threading.Tasks;
using Android.Graphics.Drawables;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;

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
            ArrayObjectAdapter adapter;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);

                CreateUi();
                viewmodel = new PostViewModel();

                viewmodel.ViewModelParts.CollectionChanged += (sender, e) =>
                {
                    if (e.NewItems != null)
                        foreach (var i in e.NewItems.OfType<Post>())
                        {
                            adapter.Clear();
                            deltailsRow = new DetailsOverviewRow(new DetailsDescriptionPresenter.Wrapper { Post = i });
                            deltailsRow.AddAction(new Action(1, "Fullscreen"));
                            deltailsRow.AddAction(new Action(2, "Rate"));
                            adapter.Add(deltailsRow);

                            ReloadImage(i);
                        }
                };

                viewmodel.Initialize(Activity.Intent.GetIntExtra(PostId, 0));
            }

            void ReloadImage(Post i)
            {
                new ImageModel().Load(deltailsRow, i.Image, 300, bitmap =>
                    {
                        deltailsRow.SetImageBitmap(Activity, (Bitmap)bitmap);
                        adapter.NotifyArrayItemRangeChanged(0, 1);
                    });
            }

            void CreateUi()
            {
                var dor = new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter()) { StyleLarge = true };

                Adapter = adapter = new ArrayObjectAdapter(dor);

                deltailsRow = new DetailsOverviewRow(new DetailsDescriptionPresenter.Wrapper());
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
                    var post = ((Wrapper)item).Post;
                    if (post != null)
                    {
                        vh.Title.Text = post.Title;
                        vh.Subtitle.Text = post.UserName;
                        vh.Body.Text = post.Created.DateTimeFromUnixTimestampMs() + "\n" + post.Content;
                    }
                }

                internal class Wrapper : Java.Lang.Object
                {
                    internal Post Post { get; set; }
                }
            }
        }
    }
}