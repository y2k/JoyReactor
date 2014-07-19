using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.WP.ViewModel
{
    /// <summary>
    /// This class contains static references to all the view models in the
    /// application and provides an entry point for the bindings.
    /// </summary>
    public class ViewModelLocator
    {
        public SinglePostViewModel SinglePost
        {
            get { return ServiceLocator.Current.GetInstance<SinglePostViewModel>(); }
        }

        public PostViewModel Post
        {
            get { return ServiceLocator.Current.GetInstance<PostViewModel>(); }
        }

        public ProfileViewModel Profile
        {
            get { return ServiceLocator.Current.GetInstance<ProfileViewModel>(); }
        }

        public MainViewModel Main
        {
            get { return ServiceLocator.Current.GetInstance<MainViewModel>(); }
        }

        public AttachmentsViewModel Attachments
        {
            get { return ServiceLocator.Current.GetInstance<AttachmentsViewModel>(); }
        }

        public static void Cleanup()
        {
            // TODO Clear the ViewModels
        }
    }
}