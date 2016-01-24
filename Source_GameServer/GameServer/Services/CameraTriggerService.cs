using AForge.Video.DirectShow;
using Clifton.Collections.Generic;
using Emgu.CV;
using Emgu.CV.CvEnum;
using Emgu.CV.Structure;
using Emgu.CV.Util;
using GameServer.Interfaces;
using PostSharp.Patterns.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;
using System.Windows.Threading;

namespace GameServer.Services
{
    interface ICameraTriggerServiceFrameCallback
    {
        void FrameUpdate(IImage rawFrame, IImage diffFrame);
    }

    [NotifyPropertyChanged]
    class CameraTriggerService : ITriggerSystem
    {
        private const int CAPTURE_FRAME_WIDTH = 640;
        private const int CAPTURE_FRAME_HEIGHT = 480;
        private const int MOVING_AVERAGE_FRAMES = 100;

        private const float TRIGGER_THRESHOLD_FACTOR = 2.0f;
        private const int CONSECUTIVE_TRIGGERS_NEEDED = 2;

        private static CameraTriggerService instance;

        private Capture capture;
        private bool captureInProgress;
        private Mat lastImage;
        private Mat overlayImage;
        private CircularList<int> diffHistory;
        private int consecutiveTriggers;
        private long lastTriggerTime;

        public List<System.Drawing.Point> RegionOfInterestPoints { get; set; }

        public event EventHandler TriggerRaised;

        public int CaptureWidth { get { return CAPTURE_FRAME_WIDTH; } }
        public int CaptureHeight { get { return CAPTURE_FRAME_HEIGHT; } }
        public ICameraTriggerServiceFrameCallback FrameCallback { get; set; }

        /// <summary>
        /// This list represents all available capture devices. The values of the list
        /// are human readable strings which may be used in a GUI.
        /// </summary>
        public List<string> CaptureDevices { get; set; }

        /// <summary>
        /// Specifies the used camera device for capturing. The value represents
        /// the index of the CaputureDevices property.
        /// </summary>
        public int SelectedDeviceIndex { get; internal set; }

        /// <summary>
        /// The value specifies the minimal time between two trigger events.
        /// This value ensures that the service does not raise two trigger events
        /// which correspond to the same event in the real world, e.g.
        /// a robot slowly passes the start/finish line (and maybe stops while
        /// on the start/finish line).
        /// </summary>
        public long TriggerBlockTimeMs { get; set; }

        private CameraTriggerService() {
            SelectedDeviceIndex = -1;
            CaptureDevices = new List<string>();
            RegionOfInterestPoints = new List<System.Drawing.Point>();
            diffHistory = new CircularList<int>(MOVING_AVERAGE_FRAMES);
            FilterInfoCollection fc = new FilterInfoCollection(FilterCategory.VideoInputDevice);

            for (int i = 0; i < fc.Count; i++)
            {
                CaptureDevices.Add(fc[i].Name);
            }
            // NOTE: We always use the first found capture device
            // In cases where we have an internal device (integrated webcam)
            // and an external device (which is the prefered device) we maybe
            // have to implement some logic that the prefered device is taken by default!
            SetCameraDevice(0);

            // NOTE: The default value of 10s should be a good enough default value
            TriggerBlockTimeMs = 10 * 1000;

            // NOTE: We specify a default ROI which is a rectangle in the middle of the
            // captured frame
            RegionOfInterestPoints.Add(new System.Drawing.Point(CAPTURE_FRAME_WIDTH / 2 - 20, 0));
            RegionOfInterestPoints.Add(new System.Drawing.Point(CAPTURE_FRAME_WIDTH / 2 + 20, 0));
            RegionOfInterestPoints.Add(new System.Drawing.Point(CAPTURE_FRAME_WIDTH / 2 + 20, CAPTURE_FRAME_HEIGHT));
            RegionOfInterestPoints.Add(new System.Drawing.Point(CAPTURE_FRAME_WIDTH / 2 - 20, CAPTURE_FRAME_HEIGHT));
        }

        public static CameraTriggerService Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new CameraTriggerService();
                }
                return instance;
            }
        }

        public bool IsSystemActive
        {
            get
            {
                return captureInProgress;
            }
        }

        public void SetCameraDevice(int cameraDeviceIndex)
        {
            if (cameraDeviceIndex < 0 || cameraDeviceIndex > this.CaptureDevices.Count - 1)
            {
                throw new ArgumentOutOfRangeException();
            }
            if (cameraDeviceIndex == SelectedDeviceIndex) return;
            SelectedDeviceIndex = cameraDeviceIndex;

            try
            {
                if (capture != null && captureInProgress)
                {
                    capture.ImageGrabbed -= ProcessFrame;
                    capture.Pause();
                    captureInProgress = false;
                    capture.Stop();
                }
                capture = new Capture(cameraDeviceIndex);
                capture.SetCaptureProperty(CapProp.FrameWidth, CAPTURE_FRAME_WIDTH);
                capture.SetCaptureProperty(CapProp.FrameHeight, CAPTURE_FRAME_HEIGHT);
                capture.SetCaptureProperty(CapProp.Fps, 15);
                capture.ImageGrabbed += ProcessFrame;
            }
            catch
            {
                throw new NotSupportedException("Device does not support capturing.");
            }
        }

        private void ProcessFrame(object sender, EventArgs e)
        {
            Mat image = new Mat();
            Mat diffImage = new Mat();
            capture.Retrieve(image);
            if (lastImage != null)
            {
                CvInvoke.AbsDiff(image, lastImage, diffImage);
            }
            Image<Gray, byte> mask = new Image<Gray, byte>(image.Width, image.Height);
            if (lastImage != null)
            {
                VectorOfPoint vp = new VectorOfPoint(RegionOfInterestPoints.ToArray());
                CvInvoke.Polylines(image, vp, true, new Bgr(0, 0, 255).MCvScalar, 2);
                if (vp.Size >= 3)
                {
                    CvInvoke.FillConvexPoly(mask, vp, new MCvScalar(255));

                    overlayImage = new Mat((int)lastImage.Height, (int)lastImage.Width, DepthType.Cv8U, 3);
                    diffImage.CopyTo(overlayImage, mask);

                    byte[] data = new byte[overlayImage.Width * overlayImage.Height * 3];

                    GCHandle handle = GCHandle.Alloc(data, GCHandleType.Pinned);
                    using (Mat m2 = new Mat(overlayImage.Size, DepthType.Cv8U, 3, handle.AddrOfPinnedObject(), overlayImage.Width * 3))
                        CvInvoke.BitwiseNot(overlayImage, m2);
                    handle.Free();

                    CheckTrigger(data, overlayImage.Width, overlayImage.Height);
                }
            }
            if (FrameCallback != null)
            {
                FrameCallback.FrameUpdate(
                        image,
                        overlayImage
                    );
            }

            lastImage = image;
        }

        private void CheckTrigger(byte[] data, int width, int height)
        {
            int diffs = 0;
            // Find min x
            int minX = width;
            int minY = height;
            int maxX = 0;
            int maxY = 0;
            foreach (System.Drawing.Point p in this.RegionOfInterestPoints)
            {
                if (p.X < minX) minX = p.X;
                if (p.X > maxX) maxX = p.X;
                if (p.Y < minY) minY = p.Y;
                if (p.Y > maxY) maxY = p.Y;
            }
            for (int w = minX; w < maxX; w++)
            {
                for (int h = minY; h < maxY; h++)
                {
                    int b = data[((width * h + w) * 3) + 0];
                    int g = data[((width * h + w) * 3) + 1];
                    int r = data[((width * h + w) * 3) + 2];
                    if (b + g + r <= 255 * 3 - 40)
                    {
                        diffs++;
                    }
                }
            }

            Console.WriteLine("Diffs=" + diffs);
            if (diffs > DiffHistoryAverage() * TRIGGER_THRESHOLD_FACTOR)
            {
                if (++consecutiveTriggers > CONSECUTIVE_TRIGGERS_NEEDED)
                {
                    consecutiveTriggers = 0;
                    long milliseconds = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
                    if (lastTriggerTime + this.TriggerBlockTimeMs < milliseconds)
                    {
                        lastTriggerTime = milliseconds;
                        Console.WriteLine("TRIGGER! TRIGGER! TRIGGER!");
                        if (TriggerRaised != null)
                        {
                            TriggerRaised(this, null);
                        }
                    }
                }
            }
            else
            {
                consecutiveTriggers = 0;
            }
            this.diffHistory.Value = diffs;
            this.diffHistory.Next();
        }

        private float DiffHistoryAverage()
        {
            float total = 0;
            foreach (int v in diffHistory)
            {
                total += v;
            }
            return total / diffHistory.Count;
        }

        public bool ActivateSystem()
        {
            if (capture != null && captureInProgress == false)
            {
                capture.Start();
                captureInProgress = true;
                return true;
            }
            return false;
        }

        public bool DeactivateSystem()
        {
            if (capture != null && captureInProgress)
            {
                capture.Pause();
                captureInProgress = false;
                lastImage = null;
                consecutiveTriggers = 0;
                if (FrameCallback != null)
                {
                    FrameCallback.FrameUpdate(null, null);
                }
                return true;
            }
            return false;
        }
    }
}
