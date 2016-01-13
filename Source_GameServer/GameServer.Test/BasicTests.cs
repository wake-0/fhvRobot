using System;
using GameServer.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace GameServer.Test
{
    [TestClass]
    public class BasicTests
    {
        [TestMethod]
        public void BitConverterTest()
        {
            var value = 1;
            var byteArr = BitConverter.GetBytes(value);       

            Assert.IsNotNull(byteArr);
            Assert.AreEqual(4, byteArr.Length);
            Assert.AreEqual(1, byteArr[0]);
            Assert.AreEqual(0, byteArr[1]);
            Assert.AreEqual(0, byteArr[2]);
            Assert.AreEqual(0, byteArr[3]);

            value = 255;
            byteArr = BitConverter.GetBytes(value);

            Assert.IsNotNull(byteArr);
            Assert.AreEqual(4, byteArr.Length);
            Assert.AreEqual(255, byteArr[0]);
            Assert.AreEqual(0, byteArr[1]);
            Assert.AreEqual(0, byteArr[2]);
            Assert.AreEqual(0, byteArr[3]);

            value = 256;
            byteArr = BitConverter.GetBytes(value);

            Assert.IsNotNull(byteArr);
            Assert.AreEqual(4, byteArr.Length);
            Assert.AreEqual(0, byteArr[0]);
            Assert.AreEqual(1, byteArr[1]);
            Assert.AreEqual(0, byteArr[2]);
            Assert.AreEqual(0, byteArr[3]);

            value = 17777;
            byteArr = BitConverter.GetBytes(value);

            Assert.IsNotNull(byteArr);
            Assert.AreEqual(4, byteArr.Length);
            Assert.AreEqual(0x71, byteArr[0]);
            Assert.AreEqual(0x45, byteArr[1]);
            Assert.AreEqual(0, byteArr[2]);
            Assert.AreEqual(0, byteArr[3]);

        }

        [TestMethod]
        public void LengthConverterConvertLength()
        {
            var arr = LengthConverter.ConvertLength(35);
            Assert.AreEqual(1, arr.Length);
            Assert.AreEqual(35, arr[0]);

            arr = LengthConverter.ConvertLength(255);
            Assert.AreEqual(1, arr.Length);
            Assert.AreEqual(255, arr[0]);

            arr = LengthConverter.ConvertLength(17777);
            Assert.AreEqual(2, arr.Length);
            Assert.AreEqual(0x45, arr[0]);
            Assert.AreEqual(0x71, arr[1]);
        }
    }
}
