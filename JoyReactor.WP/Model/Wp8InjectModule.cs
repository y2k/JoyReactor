using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.WindowsPhone;
using JoyReactor.Core.Model.Image;
using JoyReactor.WP.ViewModel;

namespace JoyReactor.WP.Model
{
    public class Wp8InjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<MvxWindowsPhoneSQLiteConnectionFactory>().As<ISQLiteConnectionFactory>();

            b.RegisterType<PathImageDecoder>().As<IImageDecoder>();
            b.RegisterType<PathMemoryCache>().As<IMemoryCache>();

            b.RegisterType<MainViewModel>().AsSelf().SingleInstance();
            b.RegisterType<ProfileViewModel>().AsSelf().SingleInstance();
            b.RegisterType<PostViewModel>().AsSelf().SingleInstance();
            b.RegisterType<AttachmentsViewModel>().AsSelf().SingleInstance();
            b.RegisterType<SinglePostViewModel>().AsSelf().SingleInstance();
        }

        #endregion
    }
}