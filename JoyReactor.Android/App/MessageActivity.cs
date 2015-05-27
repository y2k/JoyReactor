using System.Collections.ObjectModel;
using Android.App;
using Android.OS;
using Android.Support.V4.Widget;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;
using Humanizer;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using Android.Support.V7.Widget;

namespace JoyReactor.Android.App
{
    [Activity(Label = "@string/activity_private_messages")]			
    public class MessageActivity : BaseActivity
    {
        DrawerLayout panel;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_messages);

            panel = FindViewById<DrawerLayout>(Resource.Id.slidePanel); 
            if (savedInstanceState == null)
                panel.OpenDrawer((int)GravityFlags.Left);
        }

        protected override void OnStart()
        {
            base.OnStart();
            MessengerInstance.Register<MessagesViewModel.SelectThreadMessage>(
                this, _ => panel.CloseDrawer((int)GravityFlags.Left));
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
                        v.FindViewById<TextView>(Resource.Id.userName).Text = s.UserName;
                        v.FindViewById<TextView>(Resource.Id.lastMessage).Text = s.LastMessage;
                        v.FindViewById<TextView>(Resource.Id.time).Text = s.LastMessageTime.Humanize();
                        v.FindViewById<WebImageView>(Resource.Id.userImage).ImageSource = s.UserImage;
                        return v;
                    },
                };
                var progress = view.FindViewById(Resource.Id.progress);
                AddBinding(viewmodel, () => viewmodel.IsBusy, progress, () => progress.Visibility)
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
                var view = inflater.Inflate(Resource.Layout.fragment_messages, null);
                var list = view.FindViewById<RecyclerView>(Resource.Id.list);
                list.SetLayoutManager(new LinearLayoutManager(Activity, LinearLayoutManager.Vertical, true));
                list.SetAdapter(new MessageAdapter(viewmodel.Messages));

                var newMessage = view.FindViewById<EditText>(Resource.Id.newMessage);
                AddBinding(viewmodel, () => viewmodel.NewMessage, newMessage, () => newMessage.Text, BindingMode.TwoWay);

                var progress = view.FindViewById(Resource.Id.progress);
                AddBinding(viewmodel, () => viewmodel.IsBusy, progress, () => progress.Visibility)
                    .ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);

                view.FindViewById(Resource.Id.createMessage).SetCommand("Click", viewmodel.CreateMessageCommand);
                return view;
            }

            class MessageAdapter : RecyclerView.Adapter
            {
                ObservableCollection<PrivateMessage> dataSource { get; set; }

                internal MessageAdapter(ObservableCollection<PrivateMessage> dataSource)
                {
                    this.dataSource = dataSource;
                    dataSource.CollectionChanged += (sender, e) => NotifyDataSetChanged();
                }

                #region implemented abstract members of Adapter

                public override int GetItemViewType(int position)
                {
                    return dataSource[position].Mode == PrivateMessage.ModeInbox ? 0 : 1;
                }

                public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
                {
                    var s = dataSource[position];
                    var convertView = holder.ItemView;
                    convertView.FindViewById<TextView>(Resource.Id.message).Text = s.Message;
                    convertView.FindViewById<TextView>(Resource.Id.created).Text = s.Created.Humanize();
                    
                    string name;
                    if (IsInboxItem(position))
                    {
                        name = (position == ItemCount - 1) || !IsInboxItem(position + 1) ? "inbox_first" : "inbox";
                    }
                    else
                    {
                        name = (position == ItemCount - 1) || IsInboxItem(position + 1) ? "outbox_first" : "outbox";
                    }
                    convertView.FindViewById(Resource.Id.content).Background = VectorDrawable.NewVectorDrawable(name);
                }

                bool IsInboxItem(int position)
                {
                    return GetItemViewType(position) == 0;
                }

                public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
                {
                    return new ViewHolderImpl(CreateView(viewType, parent));
                }

                public override int ItemCount
                {
                    get { return dataSource.Count; }
                }

                #endregion

                View CreateView(int type, ViewGroup parent)
                {
                    var resId = type == 0
                                ? Resource.Layout.item_message_inbox
                                : Resource.Layout.item_message_outbox;
                    return LayoutInflater.From(parent.Context).Inflate(resId, parent, false);
                }

                class ViewHolderImpl : RecyclerView.ViewHolder
                {
                    internal ViewHolderImpl(View view)
                        : base(view)
                    {
                    }
                }
            }
        }

        #endregion
    }
}