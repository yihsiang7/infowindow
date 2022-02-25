# InfoWindow

提供彈出指向某個元件的資訊視窗

![截圖](/screenshot.gif)

# Usage

```kotlin
val anchor = findViewById<TextView>(R.id.tv)
val contentView = LayoutInflater.from(this).inflate(R.layout.content, null, false)
val infoWindow = InfoWindow(contentView, Gravity.TOP).apply { show(anchor, 10f /*optional*/) }

// in Activity onDestroy or Fragment onDestroyView call dismiss()
infoWindow.dismiss()
```