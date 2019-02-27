# RoundedImageView

[ ![Download](https://api.bintray.com/packages/rishabh876/RoundedImageView/RoundedImageView/images/download.svg) ](https://bintray.com/rishabh876/RoundedImageView/RoundedImageView/_latestVersion)
[ ![Min API](https://img.shields.io/badge/%20Min%20API-14%2B-green.svg) ]()
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<img src="https://i.imgur.com/AYfiMIs.jpg" width="300" />

A RoundedImageView library that supports rounding any corner or circular shape. It supports all kinds of drawable, bitmaps, resources just like a normal ImageView. All scale types are workng. RoundedImageView is extended from AppCompatImageView.

### Limitations
- Android Studio does not display rounded corner in Layout Preview 
- Borders are not supported at the moment. Pull requests are welcomed.
- Shadows are not supported at the moment. 
- RTL support is not present.
- Oval shape is not supported.

### Gradle

```implementation 'com.rishabhharit.roundedimageview:roundedImageView:0.7.0'```

### How to use
``` 
 <com.rishabhharit.roundedimageview.RoundedImageView
    ...
    app:cornerRadius="8dp"
    app:roundedCorners="topRight|bottomLeft"
    ...
 /> 
```

### Circular Shape
To get Circular shape, all you need to do is set cornerRadius to a value that is higher than the width & height of your RoundedImageView
`app:cornerRadius="1000dp"`

### Customizations
`app:roundedCorners` is pretty flexible. It supports all the following variations

`app:roundedCorners="all|top|bottom|right|topLeft|topRight|bottomLeft|bottomRight"` (yes you can use multiple at the same time here separated by | )

Default is value for `app:roundedCorners` is `all`

Default is value for `app:cornerRadius` is `0dp`



[![Medium](https://img.shields.io/badge/Medium-%40RishabhHarit-green.svg)](https://medium.com/@rishabhharit)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-%40RishabhHarit-blue.svg)](https://www.linkedin.com/in/rishabhharit/)
