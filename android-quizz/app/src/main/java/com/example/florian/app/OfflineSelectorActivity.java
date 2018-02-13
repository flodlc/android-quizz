package com.example.florian.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import entities.User;
import services.UserManager;

/**
 * Created by Florian on 14/12/2017.
 */
public class OfflineSelectorActivity extends Fragment {

    private User user;

    public static OfflineSelectorActivity newInstance(Bundle args) {
        OfflineSelectorActivity f = new OfflineSelectorActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        this.user = b.getParcelable("user");
        View view = inflater.inflate(R.layout.offline_selector, container, false);
        setIntent((Button) view.findViewById(R.id.offlineButton), OffLineQuizzActivity.class);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String record = UserManager.getData(getActivity(), "record");
        record = record.equals("") ? "0" : record;
        ((TextView) getView().findViewById(R.id.record)).setText(record);
    }

    private void setIntent(Button button, final Class className) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), className);
                Bundle b = new Bundle();
                b.putParcelable("user", user);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }
}