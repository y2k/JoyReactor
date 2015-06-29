using System;

namespace JoyReactor.Core.ViewModels.Common
{
    public abstract class BaseNavigationService
    {
        public static BaseNavigationService Instance { get; set; }

        public abstract T GetArgument<T>();

        public abstract void ImageFullscreen(string imageUrl);
    }
}