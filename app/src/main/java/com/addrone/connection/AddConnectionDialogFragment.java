package com.addrone.connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.addrone.R;
import com.addrone.model.ConnectionInfo;

import java.util.regex.Pattern;

/**
 * Created by Kamil on 8/22/2016.
 */
public class AddConnectionDialogFragment extends DialogFragment implements
        AdapterView.OnItemSelectedListener {

    private static final String DEBUG_TAG = "AdDrone:" + AddConnectionDialogFragment.class.getSimpleName();

    private AddConnectionDialogListener listener = null;
    private EditText editTextName;
    private EditText editTextIp;

    private String name = null;
    private ConnectionInfo connectionInfo = null;

    private Pattern ipPattern = Pattern.compile("(?:[0-9]+\\.){3}[0-9]+");

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
    private Spinner spinnerType;

    public void setInitialConnection(String name, ConnectionInfo connectionInfo) {
        this.name = name;
        this.connectionInfo = connectionInfo;
    }

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

                            if (! ipPattern.matcher(editTextIp.getText().toString()).matches()) {
                                Toast.makeText(getActivity(), R.string.invalid_ip_format, Toast.LENGTH_LONG).show();
                            } else {
                                if (getTag().equalsIgnoreCase("MODIFY_DIALOG")) {
                                    listener.onModifyConnection(
                                            name,
                                            editTextName.getText().toString(),
                                            new ConnectionInfo(ConnectionInfo.Type.TCP_CLIENT,
                                                    editTextIp.getText().toString(),
                                                    Integer.parseInt(editTextPort.getText().toString())));
                                } else {
                                    listener.onAddConnection(
                                            editTextName.getText().toString(),
                                            new ConnectionInfo(ConnectionInfo.Type.TCP_CLIENT,
                                                    editTextIp.getText().toString(),
                                                    Integer.parseInt(editTextPort.getText().toString())));
                                }
                                alertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(AddConnectionDialogFragment.this.getActivity(), R.string.fill_all_fields, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        editTextName = (EditText) root.findViewById(R.id.edit_text_name);
        spinnerType = (Spinner) root.findViewById(R.id.spinner_type);
        editTextIp = (EditText) root.findViewById(R.id.edit_text_ip_address);
        editTextPort = (EditText) root.findViewById(R.id.edit_text_port);

        String[] items = new String[]{ConnectionInfo.Type.TCP_CLIENT.toString(),
                ConnectionInfo.Type.USB.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);

        if (name != null && connectionInfo != null){
            editTextName.setText(name);
            spinnerType.setSelection(connectionInfo.getType().equals(ConnectionInfo.Type.TCP_CLIENT) ? 1 : 0);
            editTextIp.setText(connectionInfo.getIpAddress());
            editTextPort.setText(String.valueOf(connectionInfo.getPort()));
            editTextIp.setEnabled(false);
            editTextPort.setEnabled(false);
        }

        editTextIp.setFilters(new InputFilter[]{ipFilter});
        editTextPort.setFilters(new InputFilter[]{portFilter});

        return alertDialog;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                Log.e(DEBUG_TAG, "Selected: " + position);
                editTextIp.setEnabled(true);
                editTextPort.setEnabled(true);
                break;

            case 1:
                Log.e(DEBUG_TAG, "Selected: " + position);
                editTextIp.setEnabled(false);
                editTextPort.setEnabled(false);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    interface AddConnectionDialogListener {
        void onAddConnection(String name, ConnectionInfo connectionInfo);
        void onModifyConnection(String name, String newName, ConnectionInfo connectionInfo);
    }
}
