using Android.App;
using Android.OS;
using Android.Support.V17.Leanback.App;
using JoyReactor.Core.ViewModels;
using Android.Support.V17.Leanback.Widget;

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
            SetContentView (Resource.Layout.Main);
            if (bundle == null)
                FragmentManager.BeginTransaction ().Add (Resource.Id.container, new PostFragment ()).Commit ();

            // Create your application here
        }

        public class PostFragment : DetailsFragment {
 
            PostViewModel viewmodel;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);

                viewmodel = new PostViewModel();
                viewmodel.Initialize(Activity.Intent.GetIntExtra(PostId, 0));

                CreateUi();

            }

            void CreateUi()
            {
                var row = new DetailsOverviewRow(null);
                row.AddAction(new Action(1, "Action"));


                var ps = new ClassPresenterSelector();
                var dor = new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
            }

            class DetailsDescriptionPresenter : Presenter {

                public override void OnBindViewHolder(ViewHolder viewHolder, Java.Lang.Object item)
                {
                    throw new System.NotImplementedException();
                }
                public override ViewHolder OnCreateViewHolder(Android.Views.ViewGroup parent)
                {
                    throw new System.NotImplementedException();
                }
                public override void OnUnbindViewHolder(ViewHolder viewHolder)
                {
                    throw new System.NotImplementedException();
                }
            }
        }
    }
}