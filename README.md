# RoundedImageView

<img src="https://i.imgur.com/AYfiMIs.jpg" width="300" />

A RoundedImageView library that supports rounding any corner or circular shape. It supports all kinds of drawable, bitmaps, resources just like a normal ImageView. RoundedImageView is extended from AppCompatImageView.

### Limitations
- Android Studio does not display rounded corner in Layout Preview 
- Borders are not supported at the moment. Pull requests are welcomed.
- Shadows are not supported at the moment. 
- RTL support is not present.

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

`app:roundedCorners="all|top|right|topLeft|topRight|bottomLeft|bottomRight"` (yes you can use multiple at the same time here separated by | )

Default is value for `app:roundedCorners` is `all`

Default is value for `app:cornerRadius` is `0dp`



[![Medium](https://img.shields.io/badge/Medium-%40RishabhHarit-green.svg)](https://medium.com/@rishabhharit)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-%40RishabhHarit-blue.svg)](https://www.linkedin.com/in/rishabhharit/)
