using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using Android.OS;
using Android.Support.V4.App;
using GalaSoft.MvvmLight.Helpers;
using GalaSoft.MvvmLight.Messaging;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.Android.App.Base
{
    public class BaseFragment : Fragment
    {
        public const string Arg1 = "arg1";
        public const string Arg2 = "arg2";
        public const string Arg3 = "arg3";
        public const string Arg4 = "arg4";

        List<Action> onResumeEvents = new List<Action>();
        List<Action> onPauseEvents = new List<Action>();

        BindingManager bindinManager = new BindingManager();

        public IMessenger MessengerInstance
        {
            get { return Messenger.Default; }
        }

        protected static T NewFragment<T>(params object[] args) where T : Fragment
        {
            var bundle = new Bundle();
            for (int i = 0; i < args.Length; i++)
            {
                var a = args[i];
                var key = "arg" + (i + 1);

                if (a is string)
                    bundle.PutString(key, (string)a);
                else if (a is int)
                    bundle.PutInt(key, (int)a);
                else if (a is long)
                    bundle.PutLong(key, (long)a);
            }

            var f = Activator.CreateInstance<T>();
            f.Arguments = bundle;
            return f;
        }

        protected void AddLifeTimeEvent(Action onResume, Action onPause)
        {
            onResumeEvents.Add(onResume);
            onPauseEvents.Add(onPause);
        }

        public override void OnResume()
        {
            base.OnResume();
            onResumeEvents.ForEach(s => s());
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            onResumeEvents.Clear();
            onPauseEvents.Clear();
            MessengerInstance.Unregister(this);
            bindinManager.Destroy();
        }

        public override void OnPause()
        {
            base.OnPause();
            onPauseEvents.ForEach(s => s());
        }

        protected Binding<TS, TT> AddBinding<TS, TT>(object source, Expression<Func<TS>> sourceExpression, object target, Expression<Func<TT>> targetExpression = null, BindingMode mode = BindingMode.Default)
        {
            return bindinManager.AddBinding(source, sourceExpression, target, targetExpression, mode);
        }

        class BindingManager {

            List<Binding> bindings = new List<Binding>();

            internal Binding<TS, TT> AddBinding<TS, TT>(object source, Expression<Func<TS>> sourceExpression, object target, Expression<Func<TT>> targetExpression, BindingMode mode)
            {
                var binding = source.SetBinding(sourceExpression, target, targetExpression, mode);
                bindings.Add(binding);
                return binding;
            }

            public void Destroy()
            {
                bindings.Clear();
            }
        }
    }
}