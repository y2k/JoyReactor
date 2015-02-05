using System;
using Android.App;
using Android.Graphics;
using Android.Support.V17.Leanback.App;

namespace JoyReactor.AndroidTv.Helpers
{
    class BackgroundChanger : IDisposable
    {
        BackgroundManager manager;

        public BackgroundChanger(Activity activity)
        {
            (manager = BackgroundManager.GetInstance(activity)).Attach(activity.Window);
        }

        public void Change(IColorSource colorSource)
        {
            manager.Color = colorSource.GetColor();
        }

        public void Dispose()
        {
            manager.Release();
        }

        internal interface IColorSource
        {
            Color GetColor();
        }
    }
}