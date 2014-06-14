using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using JoyReactor.WP.Common;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.WP.View
{
    public partial class PostPage : BasePage
    {
        public PostPage()
        {
            InitializeComponent();
        }

        public class PostItemTemplateSelector : DataTemplateSelector
        {
            public DataTemplate Comment { get; set; }

            public override DataTemplate SelectTemplate(object item, DependencyObject container)
            {
                if (item is Comment) return Comment;
                return null;
            }
        }
    }
}