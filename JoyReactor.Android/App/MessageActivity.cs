using Android.App;
using Android.OS;
using Android.Support.V4.Widget;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Android.App
{
    [Activity(Label = "@string/activity_private_messages")]			
    public class MessageActivity : BaseActivity
    {
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.activity_messages);
            if (bundle == null)
                FindViewById<SlidingPaneLayout>(Resource.Id.slidePanel).OpenPane();
        }

        #region Threads

        public class ThreadListFragment : BaseFragment
        {
            MessageThreadsViewModel viewmodel;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);
                RetainInstance = true;
                (viewmodel = new MessageThreadsViewModel()).Initialize();
            }

            public override void OnDestroy()
            {
                base.OnDestroy();
                viewmodel.Cleanup();
            }

            public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                var view = inflater.Inflate(Resource.Layout.fragment_message_threads, null);
                var list = view.FindViewById<ListView>(Resource.Id.list);
                list.ItemClick += (sender, e) => viewmodel.SelectedIndex = e.Position;
                list.Adapter = new ObservableAdapter<MessageThreadItem>
                {
                    DataSource = viewmodel.Threads,
                    GetTemplateDelegate = (position, s, v) =>
                    {
                        v = v ?? View.Inflate(Activity, Resource.Layout.item_message_thread, null);
                        v.FindViewById<TextView>(Resource.Id.username).Text = s.UserName;
                        v.FindViewById<TextView>(Resource.Id.lastMessage).Text = s.LastMessage;
                        return v;
                    },
                };
                var progress = view.FindViewById(Resource.Id.progress);
                viewmodel
					.SetBinding(() => viewmodel.IsBusy, progress, () => progress.Visibility)
					.ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);
                return view;
            }
        }

        #endregion

        #region Messages

        public class MessageListFragment : BaseFragment
        {
            MessagesViewModel viewmodel;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);
                RetainInstance = true;
                viewmodel = new MessagesViewModel();
            }

            public override void OnDestroy()
            {
                base.OnDestroy();
                viewmodel.Cleanup();
            }

            public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                var view = inflater.Inflate(Resource.Layout.fragment_message_threads, null);
                view.FindViewById<ListView>(Resource.Id.list).Adapter = new ObservableAdapter<PrivateMessage>
                {
                    DataSource = viewmodel.Messages,
                    GetTemplateDelegate = (i, s, v) =>
                    {
                        v = v ?? View.Inflate(Activity, Resource.Layout.item_message, null);
                        v.FindViewById<TextView>(Resource.Id.message).Text = s.Message;
                        v.FindViewById<TextView>(Resource.Id.created).Text = s.Created.ToLongDateString() + " " + s.Created.ToLongTimeString();
                        return v;
                    },
                };
                return view;
            }
        }

        #endregion
    }
}