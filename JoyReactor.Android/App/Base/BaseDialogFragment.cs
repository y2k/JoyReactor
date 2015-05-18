using Android.Support.V4.App;
using GalaSoft.MvvmLight.Helpers;
using System.Linq.Expressions;
using System;
using GalaSoft.MvvmLight.Messaging;

namespace JoyReactor.Android.App.Base
{
	public class BaseDialogFragment : DialogFragment
	{
        BaseFragment.BindingManager bindingManager = new BaseFragment.BindingManager();

        protected Binding<TS, TT> AddBinding<TS, TT>(object source, Expression<Func<TS>> sourceExpression, object target, Expression<Func<TT>> targetExpression = null, BindingMode mode = BindingMode.Default)
        {
            return bindingManager.AddBinding(source, sourceExpression, target, targetExpression, mode);
        }

        public IMessenger MessengerInstance
        {
            get { return Messenger.Default; }
        }

        public override void OnDestroyView()
        {
            base.OnDestroyView();
            bindingManager.Destroy();
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            MessengerInstance.Unregister(this);
        }
	}
}