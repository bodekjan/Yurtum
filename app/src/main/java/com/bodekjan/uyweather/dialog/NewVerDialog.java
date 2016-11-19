package com.bodekjan.uyweather.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bodekjan.uyweather.R;


/**
 * Created by bodekjan on 2016/4/8.
 */
public class NewVerDialog extends DialogFragment {
    private Button submit;
    public static final String EXTRA_DATA = "sgwgqwg1fgw12f";
    public static NewVerDialog newInstance(int var){
        Bundle args=new Bundle();
        NewVerDialog fragment=new NewVerDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog=new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_new, null, false);
        submit=(Button)view.findViewById(R.id.dialog_ver_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK);
            }
        });
        dialog.setContentView(view);
        return dialog;
    }
    private void sendResult(int resultCode){
        if(getTargetFragment()==null)
            return;
        Intent i=new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        this.dismiss();
    }
}