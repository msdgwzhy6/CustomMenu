package myapplication.nomasp.com.custommenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Created by nomasp on 2015/09/24.
 */

public class LeftMenu extends Fragment{

    @Override
      public View onCreateView(LayoutInflater inflater,
                               @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left, container,false);
        v.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Hello nomasp");
            }
        });
        return v;
    }
}

