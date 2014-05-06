using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using System.Collections.ObjectModel;

namespace JoyReactor.WP.ViewModel
{
    /// <summary>
    /// This class contains properties that the main View can data bind to.
    /// <para>
    /// Use the <strong>mvvminpc</strong> snippet to add bindable properties to this ViewModel.
    /// </para>
    /// <para>
    /// You can also use Blend to data bind with the tool's support.
    /// </para>
    /// <para>
    /// See http://www.galasoft.ch/mvvm
    /// </para>
    /// </summary>
    public class MainViewModel : ViewModelBase
    {
        public ObservableCollection<Tag> Tags { get; set; }

        public ObservableCollection<Post> Posts { get; set; }

        public RelayCommand<Tag> OpenTagCommand { get; set; }

        private ISubscriptionCollectionModel model = InjectService.Instance.Get<ISubscriptionCollectionModel>();
        private IPostCollectionModel posModel = InjectService.Instance.Get<IPostCollectionModel>();

        /// <summary>
        /// Initializes a new instance of the MainViewModel class.
        /// </summary>
        public MainViewModel()
        {
            Tags = new ObservableCollection<Tag>();
            Posts = new ObservableCollection<Post>();

            if (IsInDesignMode)
            {
                Tags.Add(new Tag { Title = "Tag 1" });
                Tags.Add(new Tag { Title = "Tag 2" });
                Tags.Add(new Tag { Title = "Tag 3" });
            }
            else
            {
                OpenTagCommand = new RelayCommand<Tag>(s => Initialize(s));
                Initialize(null);
            }
        }

        public async void Initialize(Tag tag)
        {
            if (tag == null)
            {
                Tags.Clear();
                var tags = await model.GetMainSubscriptionsAsync();
                tags.ForEach(Tags.Add);
            }

            Posts.Clear();
            var posts = await posModel.GetPostsAsync(tag == null ? ID.REACTOR_GOOD : ID.Parser(tag.TagId), SyncFlags.First);
            posts.ForEach(Posts.Add);
        }
    }
}