using System.Collections.ObjectModel;
using System.Linq;
using Android.App;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
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
            ArrayObjectAdapter adapter;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);

                viewmodel = new PostViewModel();
                CreateUi();

                viewmodel.ViewModelParts.CollectionChanged += (sender, e) =>
                {
                    if (e.NewItems != null)
                        foreach (var i in e.NewItems.OfType<Post>())
                        {
                            deltailsRow = new DetailsOverviewRow(new PostInformationPresenter.Wrapper { Post = i });
                            deltailsRow.AddAction(new Action(1, "Fullscreen"));
                            deltailsRow.AddAction(new Action(2, "Rate"));

                            adapter.RemoveItems(0, 1);
                            adapter.Add(0, deltailsRow);

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
                var ps = new ClassPresenterSelector();
                var dor = new DetailsOverviewRowPresenter(new PostInformationPresenter()) { StyleLarge = true };
                ps.AddClassPresenter(Java.Lang.Class.FromType(typeof(DetailsOverviewRow)), dor);
                ps.AddClassPresenter(Java.Lang.Class.FromType(typeof(ListRow)), new ListRowPresenter());

                Adapter = adapter = new ArrayObjectAdapter(ps);

                deltailsRow = new DetailsOverviewRow(new PostInformationPresenter.Wrapper());
                deltailsRow.AddAction(new Action(1, "Fullscreen"));
                deltailsRow.AddAction(new Action(2, "Rate"));

                adapter.Add(deltailsRow);
                adapter.Add(new ListRow(new HeaderItem("Related Posts", null), new RelatedPostAdapter(viewmodel.RelatedPost)));
            }

            public override void OnDestroy()
            {
                base.OnDestroy();
                viewmodel.Cleanup();
            }

            class PostInformationPresenter : AbstractDetailsDescriptionPresenter
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

            class RelatedPostAdapter : ObjectAdapter
            {
                ObservableCollection<RelatedPost> posts;

                internal RelatedPostAdapter(ObservableCollection<RelatedPost> posts)
                    : base(new RelatedPostPresenter())
                {
                    this.posts = posts;
                    posts.CollectionChanged += (sender, e) => 
                        NotifyChanged();
                }

                public override Java.Lang.Object Get(int position)
                {
                    return new RelatedPostPresenter.Wrapper { Item = posts[position] };
                }

                public override int Size()
                {
                    return posts.Count;
                }

                class RelatedPostPresenter : Presenter
                {
                    ImageModel loader = new ImageModel();

                    public override void OnBindViewHolder(ViewHolder viewHolder, Java.Lang.Object item)
                    {
                        var relatedPost = ((Wrapper)item).Item;
                        var image = ((ImageCardView)viewHolder.View);
                        image.SetMainImageDimensions(300, 300);
                        loader.Load(image, relatedPost.Image, 300, 
                            bitmap => image.MainImage = bitmap == null ? null : new BitmapDrawable((Bitmap)bitmap));
                    }

                    public override ViewHolder OnCreateViewHolder(Android.Views.ViewGroup parent)
                    {
                        var image = new ImageCardView(parent.Context) { Focusable = true };
                        return new ViewHolder(image);
                    }

                    public override void OnUnbindViewHolder(ViewHolder viewHolder)
                    {
                        // Ignore
                    }

                    internal class Wrapper : Java.Lang.Object
                    {
                        internal RelatedPost Item { get; set; }
                    }
                }
            }
        }
    }
}