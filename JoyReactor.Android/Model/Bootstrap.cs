using System;
using Android.Widget;
using Android.Views;

namespace JoyReactor.Android.Model
{
    public class Bootstrap
    {
        public Bootstrap()
        {
            new EditText(null).TextChanged += Stub;
            new Button(null).Click += Stub;
            new CheckBox(null).CheckedChange += Stub;
            new View(null).Visibility = new View(null).Visibility;
            new ViewAnimator(null).DisplayedChild = new ViewAnimator(null).DisplayedChild;
        }

        void Stub(object sender, EventArgs e)
        {
        }
    }
}