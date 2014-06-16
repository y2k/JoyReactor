using JoyReactor.Core.Model.DTO;
using JoyReactor.WP.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace JoyReactor.WP.View
{
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