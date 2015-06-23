using Android.Support.V4.App;
using GalaSoft.MvvmLight.Messaging;

namespace JoyReactor.Android.App.Base
{
	public class BaseDialogFragment : DialogFragment
	{
        protected readonly BindingManager Bindings = new BindingManager();

        public IMessenger MessengerInstance
        {
            get { return Messenger.Default; }
        }

        public override void OnDestroyView()
        {
            base.OnDestroyView();
            Bindings.Destroy();
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            MessengerInstance.Unregister(this);
        }
	}
}