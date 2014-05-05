using GalaSoft.MvvmLight;
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

        private ISubscriptionCollectionModel model = InjectService.Instance.Get<ISubscriptionCollectionModel>();

        /// <summary>
        /// Initializes a new instance of the MainViewModel class.
        /// </summary>
        public MainViewModel()
        {
            Tags = new ObservableCollection<Tag>();
            Initialize();
        }

        public async void Initialize()
        {
            if (IsInDesignMode)
            {
                // Code runs in Blend --> create design time data.
                Tags.Add(new Tag { Title = "Tag 1" });
                Tags.Add(new Tag { Title = "Tag 2" });
                Tags.Add(new Tag { Title = "Tag 3" });
            }
            else
            {
                // Code runs "for real"
                Tags.Clear();
                var tags = await model.GetMainSubscriptionsAsync();
                tags.ForEach(Tags.Add);
            }
        }
    }
}