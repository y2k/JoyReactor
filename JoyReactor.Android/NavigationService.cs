using System;
using Android.App;
using Android.Content;
using JoyReactor.Android.App.Gallery;
using JoyReactor.Core.ViewModels.Common;

namespace JoyReactor.Android
{
    public class NavigationService : BaseNavigationService
    {
        Activity activity;

        public NavigationService(Activity activity)
        {
            this.activity = activity;
        }

        public override T GetArgument<T>()
        {
            return (T)(object)activity.Intent.GetStringExtra("arg");
        }

        public override void ImageFullscreen(string imageUrl)
        {
            var intent = new Intent(activity, typeof(FullscreenGalleryActivity)).PutExtra("arg", imageUrl);
            activity.StartActivity(intent);
        }
    }
}