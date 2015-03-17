using System;
using Android.Content;
using Android.Support.V4.App;
using Android.Support.V7.App;
using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Android.App.Gallery;
using JoyReactor.Android.App.Posts;
using JoyReactor.Core.ViewModels;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.Android.App.Base
{
    public class BaseActivity : ActionBarActivity
    {
        public const string Arg1 = "arg1";
        public const string Arg2 = "arg2";
        public const string Arg3 = "arg3";
        public const string Arg4 = "arg4";

        public IMessenger MessengerInstance
        {
            get { return Messenger.Default; }
        }

        protected static Intent NewIntent(Type activityType, params object[] args)
        {
            var t = new Intent(App.Instance, activityType);
            for (int i = 0; i < args.Length; i++)
            {
                var a = args[i];
                var key = "arg" + (i + 1);

                if (a is string)
                    t.PutExtra(key, (string)a);
                else if (a is int)
                    t.PutExtra(key, (int)a);
                else if (a is long)
                    t.PutExtra(key, (long)a);
            }
            return t;
        }

        public void NavigateToGallery(int postId)
        {
            StartActivity(NewIntent(typeof(GalleryActivity), postId));
        }

        public void NavigateToFullscreenGallery(int postId, int initPosition)
        {
            StartActivity(NewIntent(typeof(FullscreenGalleryActivity), postId, initPosition));
        }

        public void SetContentViewForFragment()
        {
            SetContentView(Resource.Layout.layout_activity_container);
        }

        public void SetRootFragment(Fragment fragment)
        {
            SupportFragmentManager.BeginTransaction()
				.Replace(Resource.Id.container, fragment)
				.Commit();
        }

        protected override void OnStart()
        {
            base.OnStart();
            MessengerInstance.Register<PostNavigationMessage>(this, s => 
				StartActivity(PostActivity.NewIntent(s.PostId)));
        }

        protected override void OnStop()
        {
            base.OnStop();
            MessengerInstance.Unregister(this);
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();
            MessengerInstance.Unregister(this);
        }
    }
}