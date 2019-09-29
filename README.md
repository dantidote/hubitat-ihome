# hubitat-ihome
First pass at writing a driver for ihome on hubitat

To Use:
  Install ihome app
  Install ihomeswitch driver
  Launch ihome app
  Login (add username & password)
  Discover Devices, select the ones you want installed
  Profit

This is currently in at least a semi working state.  
* I only have single devices and I know it will only work for those.
* I think I should be polling for status
* I wish the password field was actually obscured (Hubitat bug?) ..  maybe also throw away after login?  does the api key ever rotate?
* General cleanup..  so much guesswork due to lacking hubitat docs
* You have to manually delete devices if you change the selection, I don't remove them yet other than I think full uninstall of ihome app

