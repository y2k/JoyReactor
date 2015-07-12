using Android.App;
using Android.Media;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Gallery
{
    [Activity(
        Label = "FullscreenGalleryActivity",
        Theme = "@style/AppTheme.Gallery")]
    public class FullscreenGalleryActivity : BaseActivity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_container);
            if (savedInstanceState == null)
                SupportFragmentManager
                    .BeginTransaction()
                    .Add(Resource.Id.container, new FullscreenGalleryFragment())
                    .Commit();
        }

        public class FullscreenGalleryFragment : BaseFragment
        {
            GalleryViewModel viewmodel;
            VideoView videoView;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);
                RetainInstance = true;
                viewmodel = Scope.New<GalleryViewModel>();
            }

            public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                var view = inflater.Inflate(Resource.Layout.activity_fullscreen_gallery, container, false);

                var progress = view.FindViewById<ProgressBar>(Resource.Id.progress);
                Bindings
                    .Add(viewmodel, () => viewmodel.Progress)
                    .WhenSourceChanges(() =>
                    {
                        progress.Indeterminate = viewmodel.Progress == 0;
                        progress.Progress = viewmodel.Progress;
                        progress.Visibility = viewmodel.Progress < 100 ? ViewStates.Visible : ViewStates.Gone;
                    });

                videoView = view.FindViewById<VideoView>(Resource.Id.videoView);
                videoView.SetOnPreparedListener(new PreparedListenerImlp());

                Bindings
                    .Add(viewmodel, () => viewmodel.ImagePath)
                    .WhenSourceChanges(() => SetMedia(viewmodel));

                return view;
            }

            void SetMedia(GalleryViewModel viewmodel)
            {
                if (viewmodel.ImagePath == null)
                    return;
                if (viewmodel.IsVideo)
                {
                    videoView.SetVideoPath(viewmodel.ImagePath);
                    videoView.Start();
                }
                else
                {
                    videoView.Visibility = ViewStates.Gone;
                    var imageView = View.FindViewById<LargeImageViewer>(Resource.Id.imageViewer);
                    imageView.SetImage(viewmodel.ImagePath);
                }
            }

            class PreparedListenerImlp : Java.Lang.Object, MediaPlayer.IOnPreparedListener
            {
                public void OnPrepared(MediaPlayer mp)
                {
                    mp.Looping = true;
                }
            }
        }
    }
}