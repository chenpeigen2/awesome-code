package bp;

import com.google.gson.Gson;

import java.util.Map;

public class BpTest {
    static String str = """
             {                                //编译成动态库，类似于Android.mk中的BUILD_SHARED_LIBRARY
                name: "libbluetooth_jni",        //编译出的模块的名称，类似于Android.mk中的LOCAL_MODULE
                srcs: [                                         //源文件，类似于Android.mk中的LOCAL_SRC_FILES
                    "com_android_bluetooth_btservice_AdapterService.cpp",
                    "com_android_bluetooth_hfp.cpp",
                    "com_android_bluetooth_hfpclient.cpp",
                    "com_android_bluetooth_a2dp.cpp",
                    "com_android_bluetooth_a2dp_sink.cpp",
                    "com_android_bluetooth_avrcp.cpp",
                    "com_android_bluetooth_avrcp_controller.cpp",
                    "com_android_bluetooth_hid.cpp",
                    "com_android_bluetooth_hidd.cpp",
                    "com_android_bluetooth_hdp.cpp",
                    "com_android_bluetooth_pan.cpp",
                    "com_android_bluetooth_gatt.cpp",
                    "com_android_bluetooth_sdp.cpp",
                ],
                include_dirs: [                                //用户指定的头文件查找路径，类似于Android.mk中的LOCAL_C_INCLUDES
                    "libnativehelper/include/nativehelper",
                    "system/bt/types",
                ],
                shared_libs: [                                //编译所依赖的动态库，类似于Android.mk中的LOCAL_SHARED_LIBRARIES
                    "libandroid_runtime",
                    "libchrome",
                    "libnativehelper",
                    "libcutils",
                    "libutils",
                    "liblog",
                    "libhardware",
                ],
                static_libs: [                                //编译所依赖的静态库，类似于Android.mk中的LOCAL_STATIC_LIBRARIES
                    "libbluetooth-types",
                ],
                cflags: [                                        ///编译flag,类似于Android.mk中的LOCAL_CFLAGS
                    "-Wall",
                    "-Wextra",
                    "-Wno-unused-parameter",
                ]
            }
                        """;

    public static void main(String[] args) {
        Gson gson = new Gson();
        var m = gson.fromJson(str, Map.class);
        m.forEach((o, o2) -> {
            System.out.println("---------");
            System.out.println(o);
            System.out.println(o2);
        });
    }
}
