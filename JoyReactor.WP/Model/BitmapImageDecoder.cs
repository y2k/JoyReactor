using System;
using JoyReactor.Core.Model.Image;
using System.IO;
using System.Windows.Controls;
using System.Windows.Media.Imaging;
using PCLStorage;

namespace JoyReactor.WP.Model
{
    public class PathImageDecoder : IImageDecoder
    {
        #region ImageDecoder implementation

        public object Decode(IFile file)
        {
            return new Uri(file.Path);
        }

        public int GetImageSize(ImageWrapper commonImage)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}