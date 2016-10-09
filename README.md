# ZXingDemo
一个使用zxing框架来实现二维码扫描和生成二维码的Demo
# 添加权限
    <!--权限添加-->
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.FLASHLIGHT"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <!--GooglePlay过滤条件-->
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
# 扫描视图布局
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <SurfaceView
        android:id="@+id/qrcode_preview_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center" />

    <cn.edu.gdaib.zxingdemo.view.ViewfinderView
        android:id="@+id/qrcode_viewfinder_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    ......
</RelativeLayout>
