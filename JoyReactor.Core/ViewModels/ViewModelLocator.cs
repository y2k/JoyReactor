using System;

namespace JoyReactor.Core.ViewModels
{
    public class ViewModelLocator
    {
        Lazy<FeedViewModel> _feed = new Lazy<FeedViewModel>(() => new FeedViewModel());
        public FeedViewModel Feed
        {
            get { return _feed.Value; }
        }

        Lazy<TagsViewModel> _tags = new Lazy<TagsViewModel>(() => new TagsViewModel());
        public TagsViewModel Tags
        {
            get { return _tags.Value; }
        }

        public PostViewModel Post { get; } = new PostViewModel();

        public GalleryViewModel Gallery { get; } = new GalleryViewModel();

        public TagInformationViewModel TagInformation { get; } = new TagInformationViewModel();
    }
}