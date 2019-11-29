package qualisys.com.gpsapp.gps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;


public class GPSDialog extends DialogFragment {

    private static final String cls = GPSDialog.class.getCanonicalName();

    public static GPSDialog createDialog(int request, String callerTag, String title, String message, String ok, String cancel, Bundle ext) {
        Bundle args = new Bundle();
        args.putInt(cls + ".request", request);
        if (title != null) {
            args.putString(cls + ".title", title);
        }
        args.putString(cls + ".message", message);
        args.putString(cls + ".ok", ok);
        if (cancel != null && !cancel.isEmpty()) {
            args.putString(cls + ".cancel", cancel);
        }
        if (callerTag != null) {
            args.putString(cls + ".callerTag", callerTag);
        }

        if (ext != null) {
            args.putBundle(cls + ".extraBundle", ext);
        }
        GPSDialog dlg = new GPSDialog();
        dlg.setArguments(args);
        dlg.setCancelable(false);
        return dlg;
    }

    @Override
    public Dialog onCreateDialog(Bundle saved) {
        Bundle args = getArguments();
        final int request = args.getInt(cls + ".request");
        String title = null;
        if (args.containsKey(cls + ".title")) {
            title = args.getString(cls + ".title");
        }
        String message = args.getString(cls + ".message");
        String ok = args.getString(cls + ".ok");
        String cancel = null;
        if (args.containsKey(cls + ".cancel")) {
            cancel = args.getString(cls + ".cancel");
        }
        final String callerTag;
        if (args.containsKey(cls + ".callerTag")) {
            callerTag = args.getString(cls + ".callerTag");
        } else {
            callerTag = null;
        }
        Integer icon = null;
        if (args.containsKey(cls + ".icon")) {
            icon = args.getInt(cls + ".icon");
        }
        final Bundle extraBunde = (args.containsKey(cls + ".extraBundle") ? args.getBundle(cls + ".extraBundle") : null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (title != null) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callerTag != null) {
                    android.app.Fragment frg = getFragmentManager().findFragmentByTag(callerTag);
                    if (frg instanceof GPSDialogListener) {
                        ((GPSDialogListener) getFragmentManager().findFragmentByTag(callerTag)).okClickedGpsDialog(request, extraBunde);
                    }
                } else {
                    Activity act = getActivity();
                    if (act instanceof GPSDialogListener) {
                        ((GPSDialogListener) getActivity()).okClickedGpsDialog(request, extraBunde);
                    }
                }

            }
        });
        if (cancel != null) {
            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (callerTag != null) {
                                android.app.Fragment frg = getFragmentManager().findFragmentByTag(callerTag);
                                if (frg instanceof GPSDialogListener) {
                                    ((GPSDialogListener) getFragmentManager().findFragmentByTag(callerTag)).cancelClickedGpsDialog(request, extraBunde);
                                }
                            } else {
                                Activity act = getActivity();
                                if (act instanceof GPSDialogListener) {
                                    ((GPSDialogListener) getActivity()).cancelClickedGpsDialog(request, extraBunde);
                                }
                            }
                        }
                    }
            );
        }

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    public void show(Activity parent) {
        show(parent.getFragmentManager(), parent.getClass().getCanonicalName() + ".shortTag");
    }

    public void show(android.app.Fragment parent) {
        show(parent.getFragmentManager(), parent.getClass().getCanonicalName() + ".shortTag");
    }
}