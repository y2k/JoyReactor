using System;
using System.Text;
using Microsoft.Practices.ServiceLocation;
using Refractored.Xam.Settings.Abstractions;

namespace JoyReactor.Core.Model.Parser
{
    public class TagUrlBuilder
    {
        public const string DefaultDomain = "joyreactor.cc";

        readonly TagStateStorage storage = new TagStateStorage();

        public void CorrectTagDomain(string tag, string domain, bool isRoot = false)
        {
            storage.SaveTagState(tag, domain, isRoot);
        }

        public void CorrectIsSecret(string tag)
        {
            CorrectTagDomain(tag, "pornreactor.cc");
        }

        public Uri Build(ID id, int page)
        {
            var tag = id.Tag;
            var url = new StringBuilder("http://");
            url.Append(storage.GetDomain(tag, DefaultDomain));
            if (!storage.IsFandomRootTag(tag))
                url.Append("/tag/").Append(Uri.EscapeUriString(tag));
            if (ID.TagType.Best == id.Type)
                url.Append("/best");
            else if (ID.TagType.All == id.Type)
                url.Append(id.Tag == null ? "/all" : "/new");

            if (page > 0)
                url.Append("/").Append(page);

            return new Uri("" + url);
        }

        class TagStateStorage
        {
            ISettings settings = ServiceLocator.Current.GetInstance<ISettings>();

            public void SaveTagState(string tag, string domain, bool isRoot)
            {
                settings.AddOrUpdateValue(CreateKey(tag), domain + "|" + isRoot);
            }

            public string GetDomain(string tag, string defaultValue)
            {
                var state = settings.GetValueOrDefault<string>(CreateKey(tag));
                return string.IsNullOrEmpty(state) ? defaultValue : state.Split('|')[0];
            }

            public bool IsFandomRootTag(string tag)
            {
                var state = settings.GetValueOrDefault<string>(CreateKey(tag));
                return !string.IsNullOrEmpty(state) && bool.Parse(state.Split('|')[1]);
            }

            string CreateKey(string tag)
            {
                return "reactor.domain.detector.2." + tag;
            }
        }
    }
}