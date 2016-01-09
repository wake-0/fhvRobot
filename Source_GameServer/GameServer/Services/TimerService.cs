using System;
using System.Timers;
using PostSharp.Patterns.Model;

namespace GameServer.Services
{
    [NotifyPropertyChanged]
    public class TimerService
    {
        public event EventHandler<TimeTrackedEventArgs> TimeTracked;

        private Timer timer;
        private DateTime startTime;
        private DateTime endTime;

        public TimerState TimerState { get; private set; }
        public TimeSpan CurrentSpan { get; private set; }

        public TimerService()
        {
            CurrentSpan = new TimeSpan();
            TimerState = TimerState.Stopped;

            timer = new Timer(50);
            timer.Elapsed += TimerElapsed;
        }

        private void TimerElapsed(object sender, ElapsedEventArgs e)
        {
            CalculateCurrentSpan(e.SignalTime, startTime);
        }

        private void CalculateCurrentSpan(DateTime end, DateTime start)
        {
            CurrentSpan = end.Subtract(start);
        }

        public void ToggleStartStop()
        {
            // Toggle tracking
            if (TimerState == TimerState.Stopped)
            {
                // Same behavior for reset and stop
                startTime = DateTime.Now;
                timer.Start();
                TimerState = TimerState.Tracking;
            }
            else
            {
                endTime = DateTime.Now;
                timer.Stop();

                // Call event that the time was tracked
                OnTimeTracked();

                // Reset time after tracking
                Reset();
            }
        }

        public void Reset()
        {
            // Stop tracking and reset time
            timer.Stop();
            TimerState = TimerState.Stopped;

            startTime = DateTime.Now;
            endTime = startTime;

            CalculateCurrentSpan(endTime, startTime);
        }

        private void OnTimeTracked()
        {
            if (TimeTracked == null) { return; }

            TimeTracked(this, new TimeTrackedEventArgs(startTime, endTime));
        }
    }
}
