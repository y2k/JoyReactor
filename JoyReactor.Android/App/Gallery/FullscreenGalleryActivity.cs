using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;
using Android.Media;

namespace JoyReactor.Android.App.Gallery
{
    [Activity(
        Label = "FullscreenGalleryActivity",
        Theme = "@style/AppTheme.Gallery",
        ScreenOrientation = global::Android.Content.PM.ScreenOrientation.Portrait)]			
    public class FullscreenGalleryActivity : BaseActivity
    {
        VideoView videoView;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_fullscreen_gallery);

            var viewmodel = Scope.New<GalleryViewModel>();

            var progress = FindViewById<ProgressBar>(Resource.Id.progress);
            Bindins
                .Add(viewmodel, () => viewmodel.Progress)
                .WhenSourceChanges(() =>
                {
                    progress.Indeterminate = viewmodel.Progress == 0;
                    progress.Progress = viewmodel.Progress;
                    progress.Visibility = viewmodel.Progress < 100 ? ViewStates.Visible : ViewStates.Gone;
                });

            videoView = FindViewById<VideoView>(Resource.Id.videoView);
            videoView.SetOnPreparedListener(new PreparedListenerImlp());

            Bindins
                .Add(viewmodel, () => viewmodel.ImagePath)
                .WhenSourceChanges(() => SetMedia(viewmodel));
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
                var imageView = FindViewById<LargeImageViewer>(Resource.Id.imageViewer);
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