package com.example.lnctmeet.fragment;

import android.util.Log;

import com.example.lnctmeet.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OthersFragment extends BaseFragment {

    public Query getQuery() {
        Log.d(getActivity().toString(),"OTHERS fragment");
        return FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo(Constants.TAG, Constants.String_Other)
                .orderBy(Constants.DATE_CREATION, Query.Direction.DESCENDING);
    }
}
