# crasher
[![Release](https://jitpack.io/v/kakadu-dev/crasher.svg)](https://jitpack.io/#kakadu-dev/crasher)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

Lightweight library, without any dependencies for send fatal crashes to developer email

## Use:
```java
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Add it:
        Crasher.init(this,"developers@kakadu.bz");
    }
}
```

