package com.ericsson.addroneapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by Kamil on 8/22/2016.
 */
public class AddConnectionDialogFragment extends DialogFragment {

    private AddConnectionDialogListener listener = null;
    private EditText editTextName;
    private EditText editTextIp;
    InputFilter ipFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            StringBuilder stringBuilder = new StringBuilder(editTextIp.getText());
            for (int i = 0; i < source.length(); i++) {
                char c = source.charAt(i);
                if (!(Character.isDigit(c) || c == '.'))
                    return "";
                stringBuilder.append(c);
            }

            String text = stringBuilder.toString();

            if(text.contains("..") || text.startsWith(".")) {
                return "";
            }

            String[] parts = text.split("\\.");
            if (parts.length <= 4) {
                for (String part : parts) {
                    try {
                        if (Integer.parseInt(part) > 255) {
                            return "";
                        }
                    } catch (NumberFormatException e) {
                        return "";
                    }
                }
            } else {
                return "";
            }

            if(parts.length == 4 && text.endsWith(".")) {
                return "";
            }

            return null;
        }
    };
    private EditText editTextPort;
    private InputFilter portFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder stringBuilder = new StringBuilder(editTextPort.getText());
            for (int i = 0; i < source.length(); i++) {
                char c = source.charAt(i);
                if (!(Character.isDigit(c)))
                    return "";
                stringBuilder.append(c);
            }

            try {
                if (Integer.parseInt(stringBuilder.toString()) > 65535) {
                    return "";
                }
            } catch (NumberFormatException e) {
                return "";
            }
            return null;
        }
    };

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
                .setTitle(R.string.add_new_connection)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null);

        final AlertDialog alertDialog = builder.create();
        // override default hiding of dialog after button click
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editTextName.length() > 0 && editTextIp.length() > 0 && editTextPort.length() > 0) {
                            listener.onAddConnection(
                                    editTextName.getText().toString(),
                                    editTextIp.getText().toString(),
                                    Integer.parseInt(editTextPort.getText().toString())
                            );
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(AddConnectionDialogFragment.this.getActivity(), R.string.fill_all_fields, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        editTextName = (EditText) root.findViewById(R.id.edit_text_name);
        editTextIp = (EditText) root.findViewById(R.id.edit_text_ip_address);
        editTextPort = (EditText) root.findViewById(R.id.edit_text_port);

        editTextIp.setFilters(new InputFilter[]{ipFilter});
        editTextPort.setFilters(new InputFilter[]{portFilter});

        return alertDialog;
    }

    interface AddConnectionDialogListener {
        void onAddConnection(String name, String ip, int port);
    }
}
