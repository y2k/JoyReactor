using System;

namespace JoyReactor.Models
{
    class PathImage : IDisposable
    {
        public Uri PathUri { get; set; }

        public void Dispose()
        {
            // Ignore
        }
    }
}