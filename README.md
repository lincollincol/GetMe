# GetMe<img align="right" src="https://github.com/lincollincol/GetMe/blob/master/screenshots/logoPrev.png" width="200" height="200">
![GitHub](https://img.shields.io/github/license/lincollincol/GetMe?style=flat-square)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/lincollincol/GetMe?style=flat-square)
![GitHub All Releases](https://img.shields.io/github/downloads/lincollincol/GetMe/total?color=%23ffaa&style=flat-square)

![GitHub followers](https://img.shields.io/github/followers/lincollincol?style=social)
![GitHub stars](https://img.shields.io/github/stars/lincollincol/GetMe?style=social)
![GitHub forks](https://img.shields.io/github/forks/lincollincol/GetMe?style=social)

Library for fast and easy working with files in android. The main idea of Get Me - provide files and directories to the user without writing much code. Get Me also have many interesting functions like filters, support lifecycle, support back pressed, animations etc. These functions will be documented below. Get Me is very simple and flexible, you can make sure by checking some demos in this repository (GetMeExample app).

## Download GetMe

### Gradle
``` groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
  implementation 'com.github.lincollincol:GetMe:1.0.4'
}

  ```
### Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.lincollincol</groupId>
  <artifactId>GetMe</artifactId>
  <version>1.0.4</version>
</dependency>
```

## How to implement GetMe . . . ?

Add permissions to the Manifest.xml
``` xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

Grant permissions with <a href="https://github.com/Karumi/Dexter">Dexter</a> or code below:  
``` kotlin
ActivityCompat.requestPermissions(
    this, 
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ), 
    REQUEST_CODE
)
```

### Use GetMe in the Activity as a fragment in the container. Full implementation code <a href="https://github.com/lincollincol/GetMe/blob/master/app/src/main/java/linc/com/getmeexample/ExampleGetMeActivity.kt">here in the example app</a>
* Create container for fragmens(FrameLayou etc.) in your xml
* Create Activity and implement interfaces:
``` kotlin
    CloseFileManagerCallback, 
    FileManagerCompleteCallback,
    SelectionTrackerCallback, // Optional
    SelectionActionMode.MenuItemClickListener // Optional
```
* Create a global GetMe instance in your Activity class
``` kotlin
private lateinit var getMe: GetMe
```


* Initialize global GetMe instance
``` kotlin
// Inside your onCreate
getMe = GetMe(
            supportFragmentManager, // <- FragmentManager param
            R.id.getMeContainer, // <- Container id (FrameLayout or other ViewGroup xml id)
            GetMeFilesystemSettings(GetMeFilesystemSettings.ACTION_SELECT_FILE), // <- GetMe filesystem settings. This functional documented below
            GetMeInterfaceSettings(GetMeInterfaceSettings.SELECTION_MIXED), // <- GetMe interface settings. This functional documented below
            closeFileManagerCallback = this, // Callback for handling back pressed
            fileManagerCompleteCallback = this, // Callbck for handling result
            selectionTrackerCallback = this, // Callback for handling selection (Optional)
            okView = buttonGet, // View that request result after click. After click on this view, GetMe will call fileManagerCompleteCallback and pass selected files or directories into parameters.
            backView = buttonBack, // View that perform back click.
            firstClearSelectionAfterBack = true // When user use selection and set this parameter true - GetMe will clear selection at first when backView will be clicked and then after second click return to previous directory.
        ) 
```


* Prevent reseting GetMe after restoring activity instance
``` kotlin
// Inside your onCreate
    if(savedInstanceState == null) {
        getMe.show() // <- When you call show - GetMe put internal fragment into container that you pass into paramenters when initialize GetMe instance.
    }
```


* Handle screen rotation with onSaveInstanceState and onRestoreInstanceState
``` kotlin
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        getMe.onRestoreInstanceState(savedInstanceState) // <- When you call onRestoreInstanceState GetMe restore all saved states such as path to directory, recycler view position and selection. 
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getMe.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState) // <- When you call onSaveInstanceState GetMe save all states such as path to directory, recycler view position and selection.
    }
```


* Handle onBackPressed. Override base onBackPressed method and put getMe.onBackPressed() instead of super.onBackPressed(). When you call getMe.onBackPressed(), GetMe will check if current state(path) is not root and navigate us to previous directory. In case when current state(path) is root - GetMe call closeFileManagerCallback in which you should handle your back pressed with super.onBackPressed() or other application logic.
``` kotlin
    override fun onBackPressed() {
        getMe.onBackPressed() // <- Handle back clicks 
    }
```


* Implement default or custom application back pressed logic in onCloseFileManager() method from CloseFileManagerCallback. IMPORTANT: call getMe.close() at first and then implement your back pressed logic inside the lambda expression.
``` kotlin
    override fun onCloseFileManager() {
        // Remove GetMe from fragment manager
        getMe.close {
            // TODO implement back click logic here after calling getMe.close()
            finish()
        }
    }
```


* Handle result files. To handle result files you should implement onFilesSelected() method from FileManagerCompleteCallback. This method will be called when user select files or directories and click on okView. This also can be called when user click on the file.
``` kotlin
    override fun onFilesSelected(selectedFiles: List<File>) {
        println(selectedFiles) // <- Selected files or directories.
    }
```

* Handle selection with action mode and selection tracker (Optional). To handle selection, you should implement onSelectionTrackerCreated() method from SelectionTrackerCallback.
``` kotlin
    private var actionMode: ActionMode? = null

    override fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntityModel>) {
        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<FilesystemEntityModel>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                
                // Handle selection with action mode
                if(selectionTracker.hasSelection()) {
                    if(actionMode == null)
                        actionMode = startSupportActionMode(SelectionActionMode( // <- Or your custom ActionMode.Callback
                          selectionTracker, 
                          this@ExampleGetMeActivity
                        ))
                    actionMode?.title = "Selected ${selectionTracker.selection.size()}"
                }else {
                    actionMode?.finish()
                    actionMode = null
                }
                
            }
        })
    }
```
You can also use my SelectionActionMode call back from here <a href="https://github.com/lincollincol/GetMe/blob/master/app/src/main/java/linc/com/getmeexample/fragments/SelectionActionMode.kt">SelectionActionMode callback code</a>

* Implement menu item click. getMe.performOkClick() function documented below in the README
``` kotlin
    override fun onMenuItemClicked(item: MenuItem?) {
        getMe.performOkClick() // <- Perform ok click when user click on menu item in the acion bar
    }
```

#### And that's all. Accept my congratulations - you implement default GetMe in your application. Now build and run your app.  

#### Result app from implementation above
<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeMainMenu.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/InnerMenuGetMe.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/SelectionGetMe.png" width="200" height="450">
</p>
<p align="center">
  <img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/RotateGetMe.png" width="450" height="200">
</p>

## What about using GetMe inside fragment as a subfragment? See example app <a href="https://github.com/lincollincol/GetMe/tree/master/app/src/main/java/linc/com/getmeexample">here</a> 
The code inside your *parent fragment* will look the same as in *Activity*, **but** you need to replace onRestoreInstanceState() with
``` kotlin 
    // This method should be inside your parent fragment that use GetMe
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null)
            getMe.onRestoreInstanceState(savedInstanceState)
    }
```
And save parent fragment state inside activity
``` kotlin
    // This methods should be inside your Activity
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, supportFragmentManager.getFragment(savedInstanceState, "FRA")!!)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        supportFragmentManager.putFragment(
            outState,
            "FRA",
            supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!
        )
        super.onSaveInstanceState(outState)
    }
```
So, now let's handle back clicks. Fragments don't have OnBackPressed, so we should create custom callback which will be called from Activity onBackPressed.
``` kotlin
    interface FileManagerFragment {
        fun onBackPressed()
    }
```
Implement this callback in the parent GetMe fragment
``` kotlin
    override fun onBackPressed() { // FileManagerFragment implementation inside GetMe parent fragment
        getMe.onBackPressed()
    }
```
And call it from Activity
``` kotlin
    override fun onBackPressed() { // Base onBackPressed method implementation inside Activity 
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if(fragment != null && fragment is FileManagerFragment)
            fragment.onBackPressed() // Call onBackPressed() from FileManagerFragment callback
    }
```
And that's all. Now you know how to implement GetMe inside fragment as a subfragment. If something went wrong, you can see <a href="https://github.com/lincollincol/GetMe/tree/master/app/src/main/java/linc/com/getmeexample">example app code here</a>

## Documentation
### GetMe constructor parameters <a href="https://github.com/lincollincol/GetMe/blob/master/getme/src/main/java/linc/com/getme/GetMe.kt">from GetMe library source</a>
``` kotlin
class GetMe (
    private var fragmentManager: FragmentManager?,
    private var fragmentContainer: Int?,
    private var getMeFilesystemSettings: GetMeFilesystemSettings?,
    private var getMeInterfaceSettings: GetMeInterfaceSettings?,
    private var closeFileManagerCallback: CloseFileManagerCallback?,
    private var fileManagerCompleteCallback: FileManagerCompleteCallback?,
    private var selectionTrackerCallback: SelectionTrackerCallback? = null,
    private var okView: View? = null,
    private var backView: View? = null,
    private var firstClearSelectionAfterBack: Boolean = false,
    @StyleRes private var style: Int = GET_ME_DEFAULT_STYLE,
    @LayoutRes private var fileLayout: Int = GET_ME_DEFAULT_FILE_LAYOUT
) {
// Implementation
. . .
```
* **FragmentManager (GetMe param)** - supportFragmentManager from Activtiy, fragmentManager or childFragmentManager from Fragment. This parameter help GetMe start internal fragment inside fragment container.  

* **FragmentContainer** - container (FrameLayout or other ViewGroup) id from xml. GetMe will be launched inside this container.

* **GetMeFilesystemSettings (GetMe param) <a href="https://github.com/lincollincol/GetMe/blob/master/getme/src/main/java/linc/com/getme/domain/entities/GetMeFilesystemSettings.kt">(GetMeFilesystemSettings source)</a>** - settings class that provide filter functions and start from path function.
``` kotlin
class GetMeFilesystemSettings(
    internal val actionType: Int,
    internal val mainContent: MutableList<String>? = null,
    internal val exceptContent: MutableList<String>? = null,
    internal val path: String? = null,
    internal val allowBackPath: Boolean = false
) : Parcelable {
// Implementation
. . .
```
* ***ActionType (GetMeFilesystemSettings param)*** - constant, that note which content will be selected during the GetMe lifecycle:

```ACTION_SELECT_DIRECTORY``` - select directory (will display all directories **without** files)  
```ACTION_SELECT_FILE``` - select files (will display files and directories)

* ***MainContent (GetMeFilesystemSettings param)*** - list of file extensions (pdf, mp3, . . .) which will be available during GetMe lifecycle. GetMe will filter unnecessary files and display all files with extensions that you pass in the list.

* ***ExceptContent (GetMeFilesystemSettings param)*** - list of file extensions (pdf, mp3, . . .) which will be excepted during GetMe lifecycle. GetMe will except files with extensions that you pass in the list and display other files.  

**[!WARNING] When you try to use both MainContent and ExceptContent, GetMe will ignore ExceptContent, because MainContent have bigger priority**

* ***Path (GetMeFilesystemSettings param)*** - path (string) to the **directory** and only **directory**, which GetMe will open at first. If you pass path to the **file**, GetMe throws ```GetMeInvalidPathException```.

* ***AllowBackPath (GetMeFilesystemSettings param)*** - boolean (true/false) value, which is responsible for creating back path (path to root).  
**--** For example you pass ```"/storage/emulated/0/MyFiles/Music"``` **path** and pass ```true``` as an **allowBackPath**. GetMe will be launched from ```"/storage/emulated/0/MyFiles/Music"``` path and add previous directories into stack. When you click **back** button, GetMe you navigate to previous directory from the initial path:  
```"/storage/emulated/0/MyFiles/Music"``` ==> ```"/storage/emulated/0/MyFiles"```.  
**--** If you pass ```false``` as an **allowBackPath**, GetMe will use ```"/storage/emulated/0/MyFiles/Music"``` **path** as a **root** and when you click **back** button, GetMe will be closed.  

**[!WARNING] AllowBackPath using only with Path parameter**

* **GetMeInterfaceSettings <a href="https://github.com/lincollincol/GetMe/blob/master/getme/src/main/java/linc/com/getme/ui/GetMeInterfaceSettings.kt">(GetMeInterfaceSettings source)</a>**
``` kotlin
class GetMeInterfaceSettings(
    internal val selectionType: Int = SELECTION_SINGLE,
    internal val selectionMaxSize: Int = SELECTION_SIZE_DEFAULT,
    internal val enableOverScroll: Boolean = false,
    internal var adapterAnimation: Int = ANIMATION_ADAPTER_DISABLE,
    internal var animationFirstOnly: Boolean = true,
    internal var actionType: Int = 0
) : Parcelable {
// Implementation
. . .
```
* ***SelectionType (GetMeInterfaceSettings param)*** - constant, that note which selection type GetMe will use:  
```SELECTION_SINGLE``` - selection without ```SelectionTracker```. Use this selection when you need to select only one file or directory.  
**--** If you want to select file, you should click on it and handle result in the onFilesSelected() callback method.  
**--** If you want to select directory, you should use okView: open directory and click on okView. Handle result in the onFilesSelected() callback method. okView, in this case, can compare with *"Open this directory"* expression.  
```SELECTION_MULTIPLE``` - selection with ```SelectionTracker```. Use this selection when you need to select **multiple** items (files or directories). You **must** use ```okView``` with this selection type, because when you select few items you need to call onFilesSelected() to handle result files.  
```SELECTION_MIXED``` - selection with ```SelectionTracker```. Use this selection when you need to select **multiple** items (files or directories) and **single** item. This selection type is ```SINGLE``` and ```MULTIPLE``` in one type. You can select one file by click and you can use ```SelectionTracker``` with ```okView``` to select multiple items. 

* ***SelectionMaxSize (GetMeInterfaceSettings param)*** - numeric value which set limit for ```SelectionTracker```.  
For example if you pass 10 as a selectionMaxSize in the parameters, GetMe will limit selection size and you can selet only 10 items. 
<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeMaxSelectionSize.gif" width="220" height="450">
</p>  

* ***EnableOverScroll (GetMeInterfaceSettings param)*** - boolean value which set *OverScroll* animation if you pass ```true``` as a enableOverScroll parameter.  
<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeOverscrollAnimation.gif" width="220" height="450">
</p>  

* ***AdapterAnimation (GetMeInterfaceSettings param)*** - recycler view adapter animations. See more about recycler view animations <a href="https://github.com/wasabeef/recyclerview-animators">here</a>.  
Available animations in the GetMe:
```
ANIMATION_ADAPTER_DISABLE
ANIMATION_ADAPTER_FADE_IN
ANIMATION_ADAPTER_SCALE_IN
ANIMATION_ADAPTER_SCALE_IN_BOTTOM
ANIMATION_ADAPTER_SLIDE_IN_LEFT
ANIMATION_ADAPTER_SLIDE_IN_RIGHT
```
<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeAdapterAnimations.gif" width="220" height="450">
</p>  

* ***AnimationFirstOnly (GetMeInterfaceSettings param)*** - play recycler view adapter animation only one time when you pass ```true``` as a parameter.  

**[!WARNING] AnimationFirstOnly using only with AdapterAnimation parameter**

* ***ActionType (GetMeInterfaceSettings param)*** - internal variable. **Skip this parameter**.

* **CloseFileManagerCallback (GetMe param)** - This callback have ```onCloseFileManager()``` method that will be **called** when you **try close GetMe with back button**. Use this method to implement back pressed logic inside your activity or fragment instead of ```onBackPressed()```. This callback must implement ```Activity``` or ```Fragment``` that use GetMe.
``` kotlin
    interface CloseFileManagerCallback {
        fun onCloseFileManager()
    }
```

* **FileManagerCompleteCallback (GetMe param)** - This callback have ```onFilesSelected()``` method will be called when you click on **okView** or when you **click on file** (file clicks available only with selection types ```SELECTION_SINGLE``` or ```SELECTION_MIXED```). Use this callback in your activity or fragment for handle result files. This callback must implement ```Activity``` or ```Fragment``` that use GetMe.
``` kotlin
    interface FileManagerCompleteCallback {
        fun onFilesSelected(selectedFiles: List<File>)
    }
```

* **SelectionTrackerCallback (GetMe param)** - This callback return ```SelectionTracker``` instance if you use ```SELECTION_MULTIPLE``` or ```SELECTION_MIXED``` selection types. Use this callback in your activity or fragment for more flexible work with selection: clear, selecd, deselect etc. This is optional callback, you shouldn't implement it.
``` kotlin
    interface SelectionTrackerCallback {
        fun onSelectionTrackerCreated(selectionTracker: SelectionTracker<FilesystemEntityModel>)
    }
```
**[!WARNING] If you want to use SelectionTrackerCallback, make sure that you implement this Google library for selection  
```implementation "androidx.recyclerview:recyclerview-selection:1.1.0-rc01"```**

* **OkView (GetMe param)** - view (Button, ImageView, . . .) which will call onFilesSelected() from FileManagerCompleteCallback to return result files into your ```Activity``` or ```Fragment```. Look at screen or downlaod app, you can see **left** blue FloationActionButton with back arrow - this is **okView**.

* **BackView (GetMe param)** - view (Button, ImageView, . . .) which navigate you to previous directory and call onCloseFileManager() from CloseFileManagerCallback to close GetMe and let you implement custom logic inside onCloseFileManager(). Look at screen or downlaod app, you can see **right** blue FloationActionButton with down arrow - this is **backView**.

* **FirstClearSelectionAfterBack (GetMe param)** - boolean value which set back view click type: if you pass ```true``` as a firstClearSelectionAfterBack parameter and you are using selection, back button first clear selection and then, after second click, navigate you to previous directory.  

**[!WARNING] FirstClearSelectionAfterBack using only with backView**

* **Style (GetMe param)**  - custom style reference which will be applied inside GetMe. If you want to override GetMe style, you should create new ```<style>``` in the ```styles.xml``` and add items with GetMe attributes names:
``` xml
    <style name="MyCustomGetMeTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="color_background_default_get_me"><my_color></item>
        <item name="color_background_selected_get_me"><my_color></item>
        <item name="color_background_ripple_get_me"><my_color></item>
        <item name="color_background_circle_get_me"><my_color></item>
        <item name="color_background_icon_get_me"><my_color></item>
        <item name="color_background_selection_check_icon_get_me"><my_color></item>
        <item name="color_background_selection_icon_get_me"><my_color>/item>
        <item name="color_background_empty_directory_icon_get_me"><my_color></item>
        <item name="color_text_empty_directory_get_me"><my_color></item>
        <item name="color_text_get_me"><my_color></item>
        <item name="size_text_empty_directory"><my_size></item>
        <item name="string_empty_directory_get_me"><my_string></item>
    </style>
```

**<a href="https://github.com/lincollincol/GetMe/tree/master/screenshots">See full screenshot with attributes name here</a>**

<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/CustomStyleGetMe.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/StyleNames.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeEmptyDirectory.png" width="200" height="450">
</p>  

* **FileLayout (GetMe param)** - custom layout reference which will be applied inside GetMe. If you want to override GetMe layout, you should create new ```item_my_castom_get_me_name.xml``` and add view's with the same types and id's. If you don't need some view, you can not add it to the markup.  

**Custom layout from the screenshot below <a href="https://github.com/lincollincol/GetMe/blob/master/app/src/main/res/layout/item_get_me_custom.xml">here in the example app res/layout</a>**  
**Default GetMe <a href="https://github.com/lincollincol/GetMe/blob/master/getme/src/main/res/layout/item_filesystem_entity_get_me.xml">item markup here</a>**  

**[!WARNING] If you try to use view's with different types (Example: LinearLayout instead of ConstraintLayout), you will get ClassCastException. Be careful with it**

**<a href="https://github.com/lincollincol/GetMe/blob/master/screenshots/LayoutIds.png">See full screenshots with view's id's here</a>**

<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/CustomLayoutGetMe.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/LayoutIds.png" width="200" height="450">
</p>

### GetMe methods <a href="https://github.com/lincollincol/GetMe/blob/master/getme/src/main/java/linc/com/getme/GetMe.kt">from GetMe library source</a>
* **show()** - create internal GetMeFragment and launch it inside container (parameter value).  
* **close()** - clear references and pop GetMeFragment from back stack.  
* **performOkClick()** - performs ok click for calling onFilesSelected() from FileManagerCompleteCallback to get result files without creating okView. You can call this method inside your custom app click listeners etc.  
* **performBackClick()** - performs back click for navigating directory or calling onCloseFileManager from CloseFileManagerCallback if current state equals to root. This method also used in cases when you don't need to create any back buttons. 
* **isRoot()** - return true or false value. Check if current state equals to root.  
* **getCurrentPath()** - return current path as a string. For example "/storage/emulated/0/Music". You can use it to implement displaying current path state or something like this.
* **onBackPressed()** - handle back button clicks.  
* **onSaveInstanceState()** - save current GetMe state.  
* **onRestoreInstanceState()** - restore last saved GetMe state.  

## Download example app (.apk)
<a href="https://github.com/lincollincol/GetMe/blob/master/screenshots/app-debug.apk">Here you can download example app app-debug.apk and test GetMe</a>

## Feedback
<a href="http://mail.google.com/">linc.apps.sup@gmail.com</a>

## Third part libraries
<a href="https://github.com/wasabeef/recyclerview-animators">Recycler view animations</a>

## License
```
   Copyright 2020 - present lincollincol

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
