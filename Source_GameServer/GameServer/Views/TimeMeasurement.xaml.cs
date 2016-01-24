using Emgu.CV;
using GameServer.Services;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media.Imaging;
using System.Windows.Threading;

namespace GameServer.Views
{
    /// <summary>
    /// Interaction logic for TimeMeasurement.xaml
    /// </summary>
    public partial class TimeMeasurement : Window, ICameraTriggerServiceFrameCallback
    {

        public TimeMeasurement()
        {
            InitializeComponent();

            foreach (string s in CameraTriggerService.Instance.CaptureDevices)
            {
                cmbCaptureDevices.Items.Add(s);
            }
            cmbCaptureDevices.SelectedIndex = CameraTriggerService.Instance.SelectedDeviceIndex;
        }

        private void imgTestCapture_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Point p = e.GetPosition(imgTestCapture);
            double x = p.X;
            double y = p.Y;
            Console.WriteLine("[Capture ROI] Mouse click: x=" + x + ", y=" + y + ", width=" + imgTestCapture.Width + ", height=" + imgTestCapture.Height);
            CameraTriggerService.Instance.RegionOfInterestPoints.Add(new System.Drawing.Point((int)(x * CameraTriggerService.Instance.CaptureWidth / imgTestCapture.Width), (int)(y * CameraTriggerService.Instance.CaptureHeight / imgTestCapture.Height)));
        }

        private void Button_ClearROI(object sender, RoutedEventArgs e)
        {
            CameraTriggerService.Instance.RegionOfInterestPoints.Clear();
        }

        private void cmbCaptureDevices_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            try {
                CameraTriggerService.Instance.SetCameraDevice(cmbCaptureDevices.SelectedIndex);
            }
            catch
            {
                MessageBox.Show("Could not set camera device");
            }
        }

        public void FrameUpdate(IImage rawFrame, IImage diffFrame)
        {
            this.Dispatcher.Invoke(DispatcherPriority.Render, (Action)(() => {
                try
                {
                    imgTestCapture.Source = Converters.BitmapSourceConvert.ToBitmapSource(rawFrame);
                    imgProcessedCapture.Source = Converters.BitmapSourceConvert.ToBitmapSource(diffFrame);
                }
                catch (Exception ex) { Console.WriteLine(ex.Message); }
            }));
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            CameraTriggerService.Instance.FrameCallback = null;
        }

        private void Window_Initialized(object sender, EventArgs e)
        {
            CameraTriggerService.Instance.FrameCallback = this;
        }
    }
}
