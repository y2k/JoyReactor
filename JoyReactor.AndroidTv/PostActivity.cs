using Android.App;
using Android.OS;
using Android.Support.V17.Leanback.App;
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
            }
        }
    }
}