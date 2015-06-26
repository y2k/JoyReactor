using System;
using System.Linq;
using System.Threading.Tasks;
using System.Xml.Linq;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.ViewModels
{
    public class UpdateNotificationViewModel : ViewModel
    {
        bool _updateAvailable;

        public bool UpdateAvailable
        { 
            get { return _updateAvailable; }
            set { Set(ref _updateAvailable, value); }
        }

        public Command OpenCommand { get; private set; }

        public UpdateNotificationViewModel()
        {
            OpenCommand = new Command(() => NavigationService.NavigateTo("https://github.com/y2k/JoyReactor/releases"));
            CheckForUpdates();
        }

        async void CheckForUpdates()
        {
            try
            {
                UpdateAvailable = await GetNewVersion() > GetCurrentVersion();
            }
            catch
            {
            }
        }

        Version GetCurrentVersion()
        {
            return ServiceLocator.Current.GetInstance<IPlatform>().GetVersion();
        }

        async Task<Version> GetNewVersion()
        {
            var doc = await new WebDownloader().GetXmlAsync(new Uri("https://github.com/y2k/JoyReactor/releases.atom"));
            return doc.Root.Elements()
                .Where(s => s.Name.LocalName == "entry")
                .Select(s => s.Descendants().First(a => a.Name.LocalName == "title"))
                .Max(s => new Version(s.Value));
        }
    }
}