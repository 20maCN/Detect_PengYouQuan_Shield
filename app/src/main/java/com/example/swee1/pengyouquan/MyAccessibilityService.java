package com.example.swee1.pengyouquan;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swee1 on 2017/5/11.
 */

public class MyAccessibilityService extends AccessibilityService {
    private boolean mutex = false;
    private myDB helper;
    private AccessibilityNodeInfo nodeInfoTmp;
    private int jumpTime = 700;
    @Override
    protected void onServiceConnected() {
        //当启动服务的时候就会被调用
        super.onServiceConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        if (className.equals("com.tencent.mm.ui.LauncherUI") && !mutex) {
            mutex = true;
            Log.d("yeah","进入wx");
            if (findElementByID("com.tencent.mm:id/bue",event,1)) {
                Log.d("yeah","点击通讯录");
                nodeInfoTmp.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
                if ( rootNodeInfo != null ) {
                    try {
                        helper = new myDB(this);
                        helper.delete();
                        test(rootNodeInfo);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void pressBack() throws InterruptedException {
        Thread.sleep(jumpTime);
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        clickNodeByID(rootNodeInfo,"com.tencent.mm:id/h3");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clickNodeByID(AccessibilityNodeInfo rootNodeInfo,String NodeId) {
        if (rootNodeInfo!=null && rootNodeInfo.findAccessibilityNodeInfosByViewId(NodeId).size() > 0 ) {
            AccessibilityNodeInfo clickNode =  rootNodeInfo.findAccessibilityNodeInfosByViewId(NodeId).get(0);
            clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkPictures() throws InterruptedException {
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if ( rootNodeInfo != null ) {
            if ( rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a0k").size() > 0 ) {
                String friendID =  rootNodeInfo.findAccessibilityNodeInfosByViewId("android:id/text1").get(0).getText().toString();
                String description = "疑似屏蔽了你";
                helper = new myDB(this);
                if (!helper.ifHasData(friendID)) {
                    helper.insert2DB(helper.numOfData()+1,friendID,description);
                }
            }
            else if ( rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/crw").size() > 0 ) {
                String friendID =  rootNodeInfo.findAccessibilityNodeInfosByViewId("android:id/text1").get(0).getText().toString();
                String description =  rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/crw").get(0).getText().toString();
                helper = new myDB(this);
                if ( !helper.ifHasData(friendID)) {
                    helper.insert2DB(helper.numOfData()+1,friendID,description);
                }
            }
//            else {
//                // "查看正常\n";
//            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void jumpToDetail() throws InterruptedException {
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if ( rootNodeInfo != null ) {
            List<AccessibilityNodeInfo> pictures = rootNodeInfo.findAccessibilityNodeInfosByText("个人相册");
            if ( pictures.size() > 0 ) {
                AccessibilityNodeInfo clickNode =  pictures.get(0);
                clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Thread.sleep(jumpTime*2);
                checkPictures();
                pressBack();
            }
        }
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void test(AccessibilityNodeInfo rootNodeInfo) throws InterruptedException {
        if ( rootNodeInfo != null && rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hy").size() > 0 ) {
            AccessibilityNodeInfo listView =  rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hy").get(0);
            int len = listView.getChildCount();
            List<AccessibilityNodeInfo> friendList = new ArrayList<>();
            for ( int i = 0; i < len; i++ ) {
                friendList.add(listView.getChild(i));
                Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
            for ( int i = 0; i < friendList.size(); i++ ) {
                AccessibilityNodeInfo clickNode = friendList.get(i);
                clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Thread.sleep(jumpTime);
                jumpToDetail();
                pressBack();
                Thread.sleep(jumpTime);
            }

            listView.performAction( AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            if ( rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ae8").size()> 0 ) {
                Toast.makeText(this, "扫描完毕，请关闭服务后到主界面查看结果", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);//跳转到辅助功能设置页
                startActivity(intent);
                return;
            }
            test(getRootInActiveWindow());
        }
    }

    //根据控件id模拟点击控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean findElementByID(String clickID, AccessibilityEvent event, int needNumber) { // needNumber 是用来提取某一个控件的
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if ( nodeInfo!= null ) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickID);
//            Log.d("viewNumber", String.valueOf(list.size()));
            if ( list.size() > 0 ) {
                nodeInfoTmp = list.get(needNumber);
                return true;
            }
            else return false;
        }
        else {
//            Log.d("nodeInfo","null");
            return false;
        }
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
