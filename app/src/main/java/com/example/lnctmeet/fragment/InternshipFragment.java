package com.example.lnctmeet.fragment;

import android.util.Log;

import com.example.lnctmeet.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class InternshipFragment extends BaseFragment {

    @Override
    public Query getQuery() {
        Log.d(getActivity().toString(),"internship fragment");
        return FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo(Constants.TAG, Constants.String_Internship)
                .orderBy(Constants.DATE_CREATION, Query.Direction.DESCENDING);
    }
}
