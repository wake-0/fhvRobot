#!/bin/sh

# This script fetches and builds the driver for Realtek 0bda:8179

echo "Building realtek driver. This may take a while"

sudo apt-get install build-essential git

mkdir ~/RTL8188EU

cd ~/RTL8188EU

git clone git://github.com/lwfinger/rtl8188eu

cd ~/RTL8188EU/rtl8188eu

make

sudo make install

sudo cp -v ~/RTL8188EU/rtl8188eu/rtl8188eufw.bin /lib/firmware/rtlwifi/

sudo depmod -a

sudo update-initramfs -u

sudo modprobe 8188eu

echo "Wireless should be live now"
echo "Configure your wireless using /etc/network/interfaces"