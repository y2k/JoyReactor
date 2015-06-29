using Android.App;
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
        ScreenOrientation = global::Android.Content.PM.ScreenOrientation.Portrait)]			
    public class FullscreenGalleryActivity : BaseActivity
    {
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

            var imageView = FindViewById<LargeImageViewer>(Resource.Id.imageViewer);
            Bindins
                .Add(viewmodel, () => viewmodel.ImagePath)
                .WhenSourceChanges(() => imageView.SetImage(viewmodel.ImagePath));
        }
    }
}