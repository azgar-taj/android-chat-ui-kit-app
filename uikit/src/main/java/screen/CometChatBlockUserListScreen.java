package screen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.BlockedUsersRequest;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.BlockedListAdapter;
import listeners.ClickListener;
import listeners.RecyclerTouchListener;
import listeners.StickyHeaderDecoration;
import utils.FontUtils;
import utils.Utils;

/**
 * Purpose - CometChatBlockUserListScreen.class is a screen used to display List of blocked users.
 * It also helps to perform action like unblock user.
 *
 * Created on - 20th December 2019
 *
 * Modified on  - 16th January 2020
 *
 */


public class CometChatBlockUserListScreen extends Fragment {
    private static final String TAG = "CometChatGroupMember";

    private int LIMIT = 100;

    private BlockedListAdapter blockedUserAdapter;

    private BlockedUsersRequest blockedUserRequest;

    private RecyclerView rvUserList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity()!=null)
        new FontUtils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.block_user_screen, container, false);
        setHasOptionsMenu(true);
        rvUserList = view.findViewById(R.id.rv_blocked_user_list);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_blocked_user);

         if (getActivity()!=null) {
             ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
             if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                 ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         }

        if (Utils.changeToolbarFont(toolbar)!=null){
            Utils.changeToolbarFont(toolbar).setTypeface(FontUtils.robotoMedium);
        }



        rvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fetchBlockedUser();
                }

            }
        });

        // It unblock users when click on item in rvUserList
        rvUserList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvUserList, new ClickListener() {

            @Override
            public void onClick(View var1, int var2) {
                User user = (User)var1.getTag(R.string.user);
                if (getActivity()!=null) {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                    alert.setTitle("Unblock User");
                    alert.setMessage("Would you like to unblock " + user.getName());
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            unBlockUser(user, var1);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            }
        }));

        fetchBlockedUser();

        return view;
    }

    private void unBlockUser(User user, View var1) {

        ArrayList<String> uids = new ArrayList<>();
        uids.add(user.getUid());
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                blockedUserAdapter.removeUser(user);
                Snackbar.make(var1,user.getName()+" is unblocked",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(var1,"Unblock operation failed",Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
    }

    /**
     * This method is used to fetch list of blocked users.
     *
     * @see BlockedUsersRequest
     */
    private void fetchBlockedUser() {
        if (blockedUserRequest == null) {
            blockedUserRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setLimit(LIMIT).build();
        }
        blockedUserRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (users.size() > 0) {
                    setAdapter(users);

                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
                Snackbar.make(rvUserList,"Blocked user retrieval failed",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            if (getActivity() != null)
                getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to set Adapter for rvUserlist to display blocked users.
     *
     * @param users
     */
    private void setAdapter(List<User> users) {
        if (blockedUserAdapter==null){
            blockedUserAdapter=new BlockedListAdapter(getContext(),users);
            rvUserList.setAdapter(blockedUserAdapter);
        }else {
            blockedUserAdapter.updateList(Utils.userSort(users));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        CometChat.removeUserListener(TAG);
        CometChat.removeMessageListener(TAG);
    }
}
