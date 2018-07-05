package com.example.michael.stakswipe;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddTagDialog extends DialogFragment{
    private String tagToAdd;//the tag that will be added to the list

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddTagDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String tag);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
     AddTagDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the AddTagDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddTagDialogListener so we can send events to the host
            mListener =  (AddTagDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddTagDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View addTag = inflater.inflate(R.layout.add_tag, null);
        builder.setView(addTag)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText text = addTag.findViewById(R.id.tag_edit);
                        tagToAdd = text.getText().toString();
                        mListener.onDialogPositiveClick(AddTagDialog.this, tagToAdd);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(AddTagDialog.this);
                        AddTagDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();

    }

}
