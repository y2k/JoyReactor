﻿using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.Android.App
{
    [Activity(Label = "@string/profile", ParentActivity = typeof(HomeActivity), WindowSoftInputMode = SoftInput.AdjustResize)]			
    public class ProfileActivity : BaseActivity
    {
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentViewForFragment();
            SupportActionBar.SetDisplayHomeAsUpEnabled(true);
            if (bundle == null)
                SetRootFragment(new ProfileFragment());

            MessengerInstance.Register<LoginViewModel.NavigateToProfileMessage>(
                this, m => SetRootFragment(new ProfileFragment()));
            MessengerInstance.Register<ProfileViewModel.NavigateToLoginMessage>(
                this, m => SetRootFragment(new LoginFragment()));
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();
            Messenger.Default.Unregister(this);
        }

        public class ProfileFragment : BaseFragment
        {
            ProfileViewModel viewmodel;

            public async override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);
                RetainInstance = true;
                await (viewmodel = new ProfileViewModel()).Initialize();
            }

            public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                var view = inflater.Inflate(Resource.Layout.fragment_profile, container, false);

                var progress = view.FindViewById(Resource.Id.progress);
                Bindings.Add(viewmodel, () => viewmodel.IsLoading, progress, () => progress.Visibility)
					.ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);

                var username = view.FindViewById<TextView>(Resource.Id.username);
                Bindings.Add(viewmodel, () => viewmodel.UserName, username, () => username.Text);

                var rating = view.FindViewById<TextView>(Resource.Id.rating);
                Bindings.Add(viewmodel, () => viewmodel.Rating, rating, () => rating.Text);

                var avatar = view.FindViewById<WebImageView>(Resource.Id.avatar);
                Bindings.Add(viewmodel, () => viewmodel.Avatar, avatar, () => avatar.ImageSource);

                var stars = view.FindViewById<RatingBar>(Resource.Id.stars);
                Bindings.Add(viewmodel, () => viewmodel.Stars, stars, () => stars.Rating);

                var nextStarProgress = view.FindViewById<ProgressBar>(Resource.Id.nextStarProgress);
                Bindings
                    .Add(viewmodel, () => viewmodel.NextStarProgress, nextStarProgress, () => nextStarProgress.Progress)
                    .ConvertSourceToTarget(s => (int)(100 * s));

                view.FindViewById(Resource.Id.logout).SetCommand("Click", viewmodel.LogoutCommand);
                return view;
            }
        }

        public class LoginFragment : BaseFragment
        {
            LoginViewModel viewmodel;
            Binding progressBinding;

            public override void OnCreate(Bundle savedInstanceState)
            {
                base.OnCreate(savedInstanceState);
                RetainInstance = true;
                viewmodel = new LoginViewModel();

                MessengerInstance.Register<LoginViewModel.LoginFailMessage>(
                    this, _ => Toast.MakeText(Activity, Resource.String.login_error, ToastLength.Long).Show());
            }

            public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                var view = inflater.Inflate(Resource.Layout.fragment_login, null);

                var username = view.FindViewById<EditText>(Resource.Id.username);
                viewmodel.SetBinding(() => viewmodel.Username, username, () => username.Text, BindingMode.TwoWay);

                var password = view.FindViewById<EditText>(Resource.Id.password);
                viewmodel.SetBinding(() => viewmodel.Password, password, () => password.Text, BindingMode.TwoWay);

                var progress = view.FindViewById(Resource.Id.progress);
                progressBinding = viewmodel
					.SetBinding(() => viewmodel.IsBusy, progress, () => progress.Visibility)
					.ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);

                view.FindViewById(Resource.Id.login).SetCommand("Click", viewmodel.LoginCommand);
                return view;
            }
        }
    }
}