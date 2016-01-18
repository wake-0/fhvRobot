using System;
using System.Windows;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using FontFamily = System.Windows.Media.FontFamily;

namespace GameServer.Images
{
    public static class ResourceService
    {
        public static ImageSource FhvImageSource
        {
            get
            {
                return Imaging.CreateBitmapSourceFromHBitmap(
                          Properties.Resources.FHV_Image.GetHbitmap(),
                          IntPtr.Zero,
                          Int32Rect.Empty,
                          BitmapSizeOptions.FromEmptyOptions());
            }
        }

        public static FontFamily DigitalFontFamily
        {
            get { return new FontFamily("Quartz MS"); }
        }
    }
}
