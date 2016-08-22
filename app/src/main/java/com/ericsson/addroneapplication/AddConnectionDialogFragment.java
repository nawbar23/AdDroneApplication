package com.ericsson.addroneapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by Kamil on 8/22/2016.
 */
public class AddConnectionDialogFragment extends DialogFragment {

    interface AddConnectionDialogListener {
        void onAddConnection(String name, String ip, int port);
    }

    private AddConnectionDialogListener listener = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Try casting parent activity to listener to ensure that it is properly hooked up.
        try {
            listener = (AddConnectionDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddConnectionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final ViewGroup root = new FrameLayout(getActivity());

        builder.setView(inflater.inflate(R.layout.dialog_add_connection, root))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editTextName = (EditText) root.findViewById(R.id.edit_text_name);
                        EditText editTextIP = (EditText) root.findViewById(R.id.edit_text_ip_address);
                        EditText editTextPort = (EditText) root.findViewById(R.id.edit_text_port);

                        listener.onAddConnection(
                                editTextName.getText().toString(),
                                editTextIP.getText().toString(),
                                Integer.parseInt(editTextPort.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddConnectionDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
