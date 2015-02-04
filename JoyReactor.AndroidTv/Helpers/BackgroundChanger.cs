using System;
using Android.Support.V17.Leanback.App;
using Android.App;
using Android.Graphics;

namespace JoyReactor.AndroidTv.Helpers
{
    public class BackgroundChanger : IDisposable
    {
        BackgroundManager manager;

        public BackgroundChanger(Activity activity)
        {
            (manager = BackgroundManager.GetInstance(activity)).Attach(activity.Window);
        }

        public void Change(int color)
        {
            try
            {
                manager.Color = Color.ParseColor("#" + color.ToString("X"));
            }
            catch
            {
            }
        }

        public void Dispose()
        {
            manager.Release();
        }
    }
}