
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace JoyReactor.AndroidTv
{
    [Activity(Label = "PostActivity")]			
    public class PostActivity : Activity
    {
        public const string SharedElementName = "hero";

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Create your application here
        }
    }
}