using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.ViewModels
{
    public class GalleryViewModel : ViewModelBase
    {
        public ObservableCollection<string> Images { get; } = new ObservableCollection<string>();

        public async Task Initialize(int postId)
        {
            await new PostModel().GetPostAsync(postId);
            var attachments = await new PostModel().GetAttachmentsAsync(postId);
            Images.ReplaceAll(attachments.Select(s => s.Url));
        }
    }
}