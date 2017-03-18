package deputyapp.deputyapp.Util;

import android.content.Context;

import AlertCustom.AlertView;
import AlertCustom.OnItemClickListener;

public class AlertDialog {

    public static void showDialogWithoutAlertHeader(Context context,String message, OnItemClickListener onItemClickListener) {
        new AlertView("", message, null, new String[]{"OK"}, null, context, AlertView.Style.Alert,false,true,  onItemClickListener).show();
    }
}
