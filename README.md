# hubitat-ihome
First pass at writing a driver for ihome on hubitat
Using docs from: https://scott.stevensononthe.net/2017/01/reverse-engineering-the-ihome-isp5-smartplug-communications/

To Use:
  1. Install ihome app
  2. Install ihomeswitch driver
  3. Launch ihome app
  4. Login (add username & password)
  5. Discover Devices, select the ones you want installed
  6. Profit

This is currently in at least a semi working state.  
* I only have single switch devices and I know it will only work for those.
* I think I should be polling for status
* I wish the password field was actually obscured (Hubitat bug?) ..  maybe also throw away after login?  does the api key ever rotate?
* General cleanup..  so much guesswork due to lacking hubitat docs
* You have to manually delete devices if you change the selection, I don't remove them yet other than I think full uninstall of ihome app
* I think this might list other non-switch ihome devices that would be silly to try and control
* This does not paginate the device discovery url probably at some point you won't be able to select your device
* I found that the method to turn on that Scott had listed didn't work for me (the value updated, the device didn't turn on/off).  I found another endpoint in evrythngs APIs and it seems to work

