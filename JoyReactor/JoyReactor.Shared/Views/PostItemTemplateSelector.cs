using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace JoyReactor.Views
{
    class PostItemTemplateSelector : DataTemplateSelector
    {
        public DataTemplate PosterTemplate { get; set; }
        public DataTemplate CommentTemplate { get; set; }

        protected override DataTemplate SelectTemplateCore(object item)
        {
            if (item is Post)
                return PosterTemplate;
            if (item is PostViewModel.CommentViewModel)
                return CommentTemplate;
            throw new ArgumentException("item = " + item);
        }
    }
}