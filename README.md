# DoctorHelper | Google Glass Application #
---

## What is this repository for? ##

> Quick summary
>
Google Glass Application used for networking communications between the Choreographer
>
* Versión v1.0
>
- Author: Hanqing (Aaron) Zhao, <aaron.elite1993@gmail.com>

### How do I get set up? ###

* Summary of set up
>
Android Studio is needed, with XE22 Google Glass real deivce
>
GDK (Glass Development Kit), API level 19
>
Choreographer system run on another machine
* Configuration
Using Gradle as build tool
>
in terminal: ./gradlew build will generate the build apk
>
Or in Android Studio "run" will automatically build
>
* Dependencies
Google Dagger 2 DI framework
>
Retrofit 2.0 and OkHttp
>
ButterKnife 8.0
>
* Database configuration
>
Android Native SQLite embedded database
>
* How to run tests
>
No tests :(
>
* Deployment instructions
>
Google Glass should be used in a proper way, refer to Google Glass Developer site for instructions

### User Guide ###
#### Choreographer ####
* First of all, set up the Choreographer application on Windows machine
* Make sure Windows firewall open for the Choreographer with ports 8000 and 1515
* Change the IPv4 addresses in Choreographer's config.xml (where property of a Service is SessionKey or AuthData or RemoteModems) to the known Glass IPv4 address
_To know the current IPv4 address of Glass, tap on main time panel and swipe backward to find 'Google', say 'my IP address', Google will search the result for you with a website_
* Install Google USB Driver in Android SDK Manager of Android Studio, if you are a Windows user, find external disk information on My Computer -> Management -> Administration of Devices, install the driver for plugged-in Glass. If you are a Mac user, nothing else to do [same as Linux].
#### Glassware ####
* Change the IPv4 address of the Choreographer in the Glassware as well, in Choreographer class find constructor method, replace the AuthModule argument with IPv4 address of the Choreographer along with port 8000
* Open debug mode in Glass: when you are on main time panel, swipe backward to see Settings then tap, swipe forward to find Device Info, tap again, swipe backward to find Turn on Debug
* Connect to WIFI: find Settings -> Wifi, tap and select a Wifi, it pops up a screen scanner, open <https://glass.google.com/setup>, follow instructions to setup Wifi with your Google account.
----
* Other steps to make application work could follow with the report chapter written as follow:

1.	User wakes up (turn on) the Glassware by saying “ok, Glass (pause), show me a demo” or tap the time card of Glass to select “show a demo”
2.	After the first step, user enters main panel with left showing the connectivity of WIFI, right showing the connectivity of Choreographer (Loading), before sensors are loaded, a spinning progress bar is shown indicating the behaviour of loading data. 
3.	Loading process represents authentication, if it succeeds, Choreographer text turns green and popup a menu (dismiss in 2 second) indicating connection succeed. Then a list of possible sensors attached to Choreographer will show under the Choreographer text. 
4.	Sensor list will get refreshed every once in a while to make sure the real-time requests been sent and responded. 
5.	Popup a menu bar notifying disconnection if either WIFI or Choreographer is disconnected, user can only see local stored messages by tapping the main panel and click “Local Messages” 
6.	Sensor list designed with highlighted text background in order that user can swipe forward to scroll down the list for one item or swipe backward to scroll up the list for one item. When a user select a sensor, tap the touchpad with two fingers will launch the message panel (distinct from tapping with one finger to view local messages), printing desired data coming from that sensor one by one. 
7.	User sees the printed messages either from local data or from online on the message panel, delivering the messages like “Temperature: xx”, showing the semantic meaning of data. They will display in a list view, updating down in a period of time not too frequent to cause users panic and uncomfortable. 