using Android.Graphics;
using Android.Support.V17.Leanback.Widget;
using Android.Views;
using Android.Widget;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.AndroidTv
{
    class GridItemPresenter : Presenter
    {
        public override ViewHolder OnCreateViewHolder(ViewGroup parent)
        {
            TextView view = new TextView(parent.Context);
            view.LayoutParameters = new ViewGroup.LayoutParams(200, 200);
            view.Focusable = true;
            view.SetBackgroundColor(Color.ParseColor("#3d3d3d"));
            view.SetTextColor(Color.White);
            view.Gravity = GravityFlags.Center;
            return new ViewHolder(view);
        }

        public override void OnBindViewHolder(ViewHolder viewHolder, Java.Lang.Object item)
        {
            ((TextView)viewHolder.View).Text = "" + item;
        }

        public override void OnUnbindViewHolder(ViewHolder viewHolder)
        {
        }
    }
}