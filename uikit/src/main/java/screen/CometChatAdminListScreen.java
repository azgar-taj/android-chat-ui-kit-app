package screen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import adapter.GroupMemberAdapter;
import constant.StringContract;
import listeners.ClickListener;
import listeners.RecyclerTouchListener;
import utils.FontUtils;
import utils.Utils;

/**
 * Purpose - CometChatAdminListScreen.class is a screen used to display List of admin's of a particular
 * group. It also helps to perform action like remove as admin, add as admin on group members.
 * <p>
 * Created on - 20th December 2019
 * <p>
 * Modified on  - 16th January 2020
 */

public class CometChatAdminListScreen extends Fragment {

    private RecyclerView adminList;

    private String ownerId;

    private String guid;    //It is guid of group whose members are been fetched.

    private GroupMembersRequest groupMembersRequest;    //Used to fetch group members list.

    private ArrayList<GroupMember> members = new ArrayList<>();

    private GroupMemberAdapter adapter;

    private String TAG = "CometChatAdminListScreen";

    private String loggedInUserScope;

    private User loggedInUser = CometChat.getLoggedInUser();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            new FontUtils(getActivity());

        handleArguments();
    }

    private void handleArguments() {

        if (getArguments() != null) {
            guid = getArguments().getString(StringContract.IntentStrings.GUID);
            loggedInUserScope = getArguments().getString(StringContract.IntentStrings.MEMBER_SCOPE);
            ownerId = getArguments().getString(StringContract.IntentStrings.GROUP_OWNER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comet_chat_admin_list_screen, container, false);
        adminList = view.findViewById(R.id.adminList);
        setHasOptionsMenu(true);
        RelativeLayout rlAddMember = view.findViewById(R.id.rl_add_Admin);
        MaterialToolbar toolbar = view.findViewById(R.id.admin_toolbar);

        if (Utils.changeToolbarFont(toolbar) != null) {
            Utils.changeToolbarFont(toolbar).setTypeface(FontUtils.robotoMedium);
        }
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new GroupMemberAdapter(getContext(), members, null);
        adminList.setAdapter(adapter);

        if (loggedInUserScope != null && loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN)) {
            rlAddMember.setVisibility(View.VISIBLE);
        }
        getAdminList(guid);

        rlAddMember.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CometChatGroupMemberListScreenActivity.class);
            intent.putExtra(StringContract.IntentStrings.GUID, guid);
            startActivity(intent);
        });
        adminList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), adminList, new ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                GroupMember groupMember = (GroupMember) var1.getTag(R.string.user);
                if (ownerId != null && loggedInUser.getUid().equals(ownerId) && loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) && !groupMember.getUid().equals(loggedInUser.getUid())) {
                    if (getActivity() != null) {
                        MaterialAlertDialogBuilder alert_dialog = new MaterialAlertDialogBuilder(getActivity());
                        alert_dialog.setTitle("Remove");
                        alert_dialog.setMessage("Remove " + groupMember.getName() + " as a Admin");
                        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateMemberScope(groupMember, var1);
                            }
                        });
                        alert_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert_dialog.create();
                        alert_dialog.show();
                    }
                } else {
                    String message;
                    if (groupMember.getUid().equals(loggedInUser.getUid()))
                        message = "You cannot perform action on yourself";
                    else
                        message = "Only group owner can remove admin.";

                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                }
            }
        }));
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null)
                getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMemberScope(GroupMember groupMember, View view) {

        CometChat.updateGroupMemberScope(groupMember.getUid(), guid, CometChatConstants.SCOPE_PARTICIPANT,
                new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (adapter != null)
                            adapter.removeGroupMember(groupMember);

                        Snackbar.make(view, groupMember.getName() + " is removed from admin privilege",
                                Snackbar.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(CometChatException e) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Action failed", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * This method is used to fetch Admin List.
     *
     * @param groupId is a unique id of group. It is used to fetch admin list of particular group.
     */
    private void getAdminList(String groupId) {
        if (groupMembersRequest == null) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(groupId).setLimit(100).build();
        }
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                ArrayList<GroupMember> memberList = new ArrayList<>();
                for (GroupMember groupMember : groupMembers) {

                    if (groupMember.getScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                        memberList.add(groupMember);
                    }
                }
                adapter.addAll(memberList);
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(adminList, "Admin list retrieval failed", Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        groupMembersRequest = null;
        if (guid != null)
            getAdminList(guid);
        addGroupListener();

    }

    @Override
    public void onPause() {
        super.onPause();
        CometChat.removeGroupListener(TAG);
    }

    private void addGroupListener() {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                if (adapter != null)
                    adapter.removeGroupMember(Utils.UserToGroupMember(leftUser, false, CometChatConstants.SCOPE_PARTICIPANT));
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                if (adapter != null)
                    adapter.removeGroupMember(Utils.UserToGroupMember(kickedUser, false, CometChatConstants.SCOPE_PARTICIPANT));
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group) {
                if (adapter != null) {
                    if (action.getNewScope().equals(CometChatConstants.SCOPE_ADMIN))
                        adapter.addGroupMember(Utils.UserToGroupMember(updatedUser, true, action.getNewScope()));
                    else if (action.getOldScope().equals(CometChatConstants.SCOPE_ADMIN))
                        adapter.removeGroupMember(Utils.UserToGroupMember(updatedUser, false, CometChatConstants.SCOPE_PARTICIPANT));
                }
            }
        });
    }
}
