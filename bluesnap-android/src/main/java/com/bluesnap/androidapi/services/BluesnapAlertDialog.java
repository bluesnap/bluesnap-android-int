package com.bluesnap.androidapi.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import com.bluesnap.androidapi.R;

/**
 * Created by roy.biber on 28/06/2016.
 */
public class BluesnapAlertDialog {

    public static void setDialog(@NonNull Context activity, @NonNull String dialogMessage, @NonNull String dialogTitle) {
        setDialog(activity, dialogMessage, dialogTitle, new BluesnapAlertDialog.BluesnapDialogCallback() {
            @Override
            public void setPositiveDialog() {
            }

            @Override
            public void setNegativeDialog() {
            }
        }, activity.getString(R.string.OK), activity.getString(R.string.CANCEL));
    }

    public static void setDialog(@NonNull Context activity, @NonNull String dialogMessage, @NonNull String dialogTitle, @NonNull final BluesnapDialogCallback dialogCallback) {
        setDialog(activity, dialogMessage, dialogTitle, dialogCallback, activity.getString(R.string.OK), activity.getString(R.string.CANCEL));
    }

    public static void setDialog(@NonNull Context activity, @NonNull String dialogMessage, @NonNull String dialogTitle, @NonNull final BluesnapDialogCallback dialogCallback, String positive, String negative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(dialogMessage).setTitle(dialogTitle);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogCallback.setPositiveDialog();
            }
        });

        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogCallback.setNegativeDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface BluesnapDialogCallback {
        void setPositiveDialog();

        void setNegativeDialog();
    }
}
