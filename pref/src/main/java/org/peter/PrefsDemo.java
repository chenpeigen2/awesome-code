package org.peter;

import java.io.FileOutputStream;
import java.util.prefs.*;


//java.lang.Object
//        java.util.EventObject (implements java.io.Serializable) [not included]
//             java.util.prefs.NodeChangeEvent
//             java.util.prefs.PreferenceChangeEvent [here]
//        java.util.prefs.Preferences [here]
//             java.util.prefs.AbstractPreferences


//Interface Hierarchy
//java.util.EventListener
//        java.util.prefs.NodeChangeListener
//        java.util.prefs.PreferenceChangeListener [here]
//java.util.prefs.PreferencesFactory

public class PrefsDemo {
    public static void main(String[] args) {
        String[] keys = {"sunway", "copyright", "author"};
        String[] values = {"sunway technology company", "copyright 2002",
                "turbochen@163.com"};

        /* 建立一个位于user root下的/com/sunway/spc节点参数项 */

//        ./home/mi/.java/.userPrefs/com/sunway
        Preferences prefsdemo = Preferences.userRoot().node("/com/sunway/spc");

        // 为这个Preferences对象添加监听，监听首选项的改变
        prefsdemo.addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                System.out.println(evt.getKey() + " = " + evt.getNewValue());
            }
        });


        prefsdemo.addNodeChangeListener(new NodeChangeListener() {
            @Override
            public void childAdded(NodeChangeEvent evt) {
                System.out.println(1);
            }

            @Override
            public void childRemoved(NodeChangeEvent evt) {
                System.out.println(2);
            }
        });

        /* 储存参数项 */
        for (int i = 0; i < keys.length; i++) {
            prefsdemo.put(keys[i], values[i]);
        }
        System.out.println("===");
        prefsdemo.putBoolean("flags", true);
        System.out.println("===");
        /* 导出到XML文件 */
        try {
            FileOutputStream fos = new FileOutputStream("prefsdemo.xml");
            prefsdemo.exportNode(fos);
        } catch (Exception e) {
            System.err.println("Cannot export nodes: " + e);
        }

        /* 去掉注释可以清除注册表中的参数项 */
		/*try {
			prefsdemo.removeNode();
		} catch (BackingStoreException e) {
		}*/

    }

}
