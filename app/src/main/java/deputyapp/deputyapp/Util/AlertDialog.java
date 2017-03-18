package deputyapp.deputyapp.Util;

import android.content.Context;

import AlertCustom.AlertView;
import AlertCustom.OnItemClickListener;

public class AlertDialog {

    public static void showDialogWithoutAlertHeader(Context context,String message, OnItemClickListener onItemClickListener) {
        new AlertView("", message, null, new String[]{"OK"}, null, context, AlertView.Style.Alert,false,true,  onItemClickListener).show();
    }

    public static void showDialogWithHeaderTwoButton(Context context,String heading, String message, OnItemClickListener onItemClickListener) {
        new AlertView(heading, message, "Turn On", null, new String[]{"cancel"}, context, AlertView.Style.Alert,true,false,  onItemClickListener).show();
    }

    public static void showDialogWithAlertHeaderSingleButton(Context context,String header,String message, OnItemClickListener onItemClickListener) {
        new AlertView(header, message, null, new String[]{"OK"}, null, context, AlertView.Style.Alert,true,true,  onItemClickListener).show();
    }
}
