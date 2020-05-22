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

### Use GetMe in the Activity as a fragment in the container
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
#### Full implementation code <a href="https://github.com/lincollincol/GetMe/blob/master/app/src/main/java/linc/com/getmeexample/ExampleGetMeActivity.kt">here in the example app</a>
#### Result app from implementation above
<p align="center">
<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/GetMeMainMenu.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/InnerMenuGetMe.png" width="200" height="450">&#10240 &#10240 &#10240 &#10240<img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/SelectionGetMe.png" width="200" height="450">
</p>
<p align="center">
  <img src="https://github.com/lincollincol/GetMe/blob/master/screenshots/RotateGetMe.png" width="450" height="200">
</p>

## What about using GetMe inside fragment? See example app <a href="https://github.com/lincollincol/GetMe/tree/master/app/src/main/java/linc/com/getmeexample">here</a> 

## Documentation
### GetMe constructor parameters


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
