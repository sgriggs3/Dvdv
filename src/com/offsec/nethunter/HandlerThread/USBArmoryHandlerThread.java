package com.offsec.nethunter.HandlerThread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;

import com.offsec.nethunter.SQL.USBArmorySQL;
import com.offsec.nethunter.models.USBArmoryUSBSwitchModel;
import com.offsec.nethunter.utils.ShellExecuter;


public class USBArmoryHandlerThread extends HandlerThread {

    private Handler handler;
    public static final int SETUSBIFACE = 1;
    public static final int RELOAD_USBIFACE = 2;
    public static final int RELOAD_MOUNTSTATUS = 3;
    public static final int MOUNT_IMAGE = 4;
    public static final int UNMOUNT_IMAGE = 5;
    public static final int GET_USBSWITCH_SQL_DATA = 6;
    public static final int GET_USBNETWORK_SQL_DATA = 7;
    private USBArmoryListener listener;
    private Object resultObject = new Object();
    private ShellExecuter exe = new ShellExecuter();

    public USBArmoryHandlerThread() {
        super("USBArmoryHandlerThread", Process.THREAD_PRIORITY_DEFAULT);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SETUSBIFACE:
                        resultObject = exe.RunAsRootReturnValue(msg.obj.toString());
                        break;
                    case RELOAD_USBIFACE:
                        resultObject = exe.RunAsRootOutput(msg.obj.toString());
                        break;
                    case RELOAD_MOUNTSTATUS:
                        resultObject = exe.RunAsRootOutput(msg.obj.toString());
                        break;
                    case MOUNT_IMAGE:
                        resultObject = exe.RunAsRootReturnValue(msg.obj.toString());
                        break;
                    case UNMOUNT_IMAGE:
                        resultObject = exe.RunAsRootReturnValue(msg.obj.toString());
                        break;
                    case GET_USBSWITCH_SQL_DATA:
                        resultObject = USBArmorySQL.getInstance((Context) msg.obj)
                                .getUSBSwitchColumnData(msg.getData().getString("targetOSName"),
                                        msg.getData().getString("functionName"));
                        break;
                    case GET_USBNETWORK_SQL_DATA:
                        resultObject = USBArmorySQL.getInstance((Context) msg.obj).getUSBNetworkColumnData(msg.arg1);
                        break;
                }
                if (listener != null) {
                    listener.onShellExecuterFinished(resultObject, msg.what);
                }
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }

    public void setOnShellExecuterFinishedListener(USBArmoryListener listener) {
        this.listener = listener;
    }

    public interface USBArmoryListener {
        void onShellExecuterFinished(Object resultObject, int actionCode);
    }
}
