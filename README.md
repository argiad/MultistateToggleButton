# MultistateToggleButton

Draft for Multi State Toggle Button, created for commercial project 

<img src="https://github.com/argiad/MultistateToggleButton/blob/master/Screenshot_1522563599.png" width="350"/>


```xml
        <com.steegler.multistatetogglebutton.MultistateToggleButton
            android:id="@+id/mtbSport"
            android:layout_width="match_parent"
            android:layout_height="40dp"
            android:layout_marginTop="10dp"
            app:entries="@array/power_state"
            app:stretch="true">
```
```kotlin
    private lateinit var mtbSport: MultistateToggleButton
    private lateinit var mtbThrottle: MultistateToggleButton

    ...

    override fun initWidgets(fragmentView: View) {

        mtbSport = fragmentView.findViewById(R.id.mtbSport)
        mtbThrottle = fragmentView.findViewById(R.id.mtbThrottle)
        mtbSport.callback = this
        mtbThrottle.callback = this
        
        // Set textfont
        ResourcesCompat.getFont(mContext, R.font.simple_light)?.notNull {
            mtbSport.textFont = it
            mtbThrottle.textFont = it
        }
        mtbSport.selectedPosition = 1
        mtbThrottle.selectedPosition = 2
    }

    // implementation of MultistateToggleButton.MTBInterface  
    override fun onStateChanged(view: View, index: Int) {
        when (view.id) {
            R.id.mtbSport -> {
                log("Sport $index")
            }
            R.id.mtbThrottle -> {
                log("Throttle $index")
            }
        }

    }
```



MIT License

Copyright (c) 2018 Artem Mkrtchyan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
