using System;
using System.Drawing;
using System.Drawing.Text;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Windows;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using GameServer.Properties;
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
                          Resources.FHV_Image.GetHbitmap(),
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
