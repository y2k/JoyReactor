using Android.App;
using Android.OS;
using Android.Views;
using JoyReactor.Android.App.Base;
using Android.Graphics;
using JoyReactor.Core.ViewModels;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;

namespace JoyReactor.Android.App
{
	[Activity (Label = "Private messages")]			
	public class MessageActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.activity_messages);
		}

		public class ThreadListFragment : BaseFragment
		{
			MessageThreadsViewModel viewmodel;

			public override void OnCreate (Bundle savedInstanceState)
			{
				base.OnCreate (savedInstanceState);
				RetainInstance = true;
				(viewmodel = new MessageThreadsViewModel ()).Initialize ();
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				return new ListView (Activity) {
					Adapter = new ObservableAdapter<MessageThreadsViewModel.MessageThreadItem> {
						DataSource = viewmodel.Threads,
						GetTemplateDelegate = (i, s, v) => {
							v = v ?? View.Inflate (Activity, Resource.Layout.item_message_thread, null);
							v.FindViewById<TextView> (Resource.Id.username).Text = s.Username;
							v.FindViewById<TextView> (Resource.Id.created).Text = s.LastMessage;
							v.SetClick (((sender, e) => s.OpenThreadCommand.Execute (null)));
							return v;
						},
					},
				};
			}
		}

		public class MessageListFragment : BaseFragment
		{
			MessagesViewModel viewmodel;

			public override void OnCreate (Bundle savedInstanceState)
			{
				base.OnCreate (savedInstanceState);
				RetainInstance = true;
				viewmodel = new MessagesViewModel ();
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				return new ListView (Activity) {
					Adapter = new ObservableAdapter<MessagesViewModel.MessageItem> {
						DataSource = viewmodel.Messages,
						GetTemplateDelegate = (i, s, v) => {
							var text = (TextView)v ?? new Button (Activity);
							text.Text = s.Message;
							return text;
						},
					},
				};
			}
		}
	}
}