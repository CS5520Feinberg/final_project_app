package edu.northeastern.final_project.fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import edu.northeastern.final_project.R;

import edu.northeastern.final_project.backgroundThreadClass.SearchPhoneNumberThread;
import edu.northeastern.final_project.validation.GenericStringValidation;

public class SearchBoxFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_box,container,false);
        Button searchButton = view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch_search(view);
            }
        });
        return view;
    }

    public void launch_search(View view){

        //startSearchThread
        TextInputLayout textInputLayout = view.findViewById(R.id.search_box_input_layout);

        EditText editText = textInputLayout.getEditText();
        Log.d("Search_Input",""+editText.getText());
        String pattern_regex = "^[1-9]{1}[0-9]{9}";
        Pattern pattern = Pattern.compile(pattern_regex) ;

        if(new GenericStringValidation<Pattern>(pattern).validateString(editText.getText().toString())){
            new SearchPhoneNumberThread(this.getContext(),editText.getText().toString()).execute();

        }else{
            Toast.makeText(this.getContext(),"only ten digit phone number is allowed",Toast.LENGTH_SHORT).show();
        }

    }


}
