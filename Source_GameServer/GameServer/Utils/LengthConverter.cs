using System;
using System.Linq;

namespace GameServer.Utils
{
    public static class LengthConverter
    {
        public static byte[] ConvertLength(int length)
        {
            if (length < 0 || length > 65535) throw new ArgumentException("Length to big.");

            var value = BitConverter.GetBytes(length);
            if (length <= 255)
            {
                return new[] { value[0] };
            }

            var newArray = value.Take(2).Reverse().ToArray();
            return newArray;
        }

    }
}
