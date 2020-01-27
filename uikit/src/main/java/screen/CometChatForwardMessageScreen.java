package screen;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ConversationListAdapter;
import constant.StringContract;
import listeners.ClickListener;
import listeners.RecyclerTouchListener;
import utils.FontUtils;
import utils.Utils;

/**
 * Purpose - CometChatForwardMessageScreen class is a fragment used to display list of users to which
 * we will forward the message.
 * Created on - 20th December 2019
 *
 * Modified on  - 16th January 2020
 */


public class CometChatForwardMessageScreen extends AppCompatActivity {
    private static final String TAG = "CometChatForward";

    private RecyclerView rvConversationList;

    private HashMap<String,Conversation> userList = new HashMap<>();

    private ConversationListAdapter conversationListAdapter;

    private ConversationsRequest conversationsRequest;

    private EditText etSearch;

    private ImageView clearSearch;

    private String name,avatar;

    private MaterialButton forwardBtn;

    private ChipGroup selectedUsers;

    private String textMessage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_forward_message_screen);
        new FontUtils(this);
        handleIntent();
        init();
    }

    /**
     * This method is used to handle parameter passed to this class.
     */
    private void handleIntent() {
        if (getIntent().hasExtra(StringContract.IntentStrings.TYPE)) {
            String messageType = getIntent().getStringExtra(StringContract.IntentStrings.TYPE);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.TEXT_MESSAGE)){
            textMessage = getIntent().getStringExtra(StringContract.IntentStrings.TEXT_MESSAGE);
        }
    }

    /**
     * This method is used to initialize the views
     */
    public void init() {
        // Inflate the layout 
        MaterialToolbar toolbar = findViewById(R.id.forward_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          if (Utils.changeToolbarFont(toolbar)!=null){
              Utils.changeToolbarFont(toolbar).setTypeface(FontUtils.robotoMedium);
          }
        selectedUsers = findViewById(R.id.selected_user);

        forwardBtn = findViewById(R.id.btn_forward);

        rvConversationList = findViewById(R.id.rv_conversation_list);

        etSearch = findViewById(R.id.search_bar);

        clearSearch = findViewById(R.id.clear_search);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1)
                    clearSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                 if (editable.toString().length()!=0) {
                     if (conversationListAdapter != null)
                         conversationListAdapter.getFilter().filter(editable.toString());
                 }
            }
        });

        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (conversationListAdapter!=null)
                    conversationListAdapter.getFilter().filter(textView.getText().toString());
                clearSearch.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        clearSearch.setOnClickListener(view1 -> {
            etSearch.setText("");
            clearSearch.setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            // Hide the soft keyboard
            inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        });

        rvConversationList.addOnItemTouchListener(new RecyclerTouchListener(this, rvConversationList, new ClickListener() {
            @Override
            public void onClick(View var1, int var2) {

                    Conversation conversation = (Conversation) var1.getTag(R.string.conversation);
                     if (userList!=null&&userList.size()<5){
                    if (!userList.containsKey(conversation.getConversationId())) {
                        userList.put(conversation.getConversationId(), conversation);
                        Chip chip = new Chip(CometChatForwardMessageScreen.this);

                        if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            name = ((User) conversation.getConversationWith()).getName();
                            avatar = ((User) conversation.getConversationWith()).getAvatar();
                        } else {
                            name = ((Group) conversation.getConversationWith()).getName();
                            avatar = ((Group) conversation.getConversationWith()).getIcon();
                        }
                        chip.setText(name);
                        Glide.with(CometChatForwardMessageScreen.this).load(avatar).placeholder(R.drawable.ic_contacts).transform(new CircleCrop()).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                chip.setChipIcon(resource);
                            }
                        });
                        chip.setCloseIconVisible(true);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View vw) {
                                userList.remove(conversation.getConversationId());
                                selectedUsers.removeView(vw);
                                checkUserList();

                            }
                        });
                        selectedUsers.addView(chip,0);
                    }
                    checkUserList();
                }
                else {
                    Toast.makeText(CometChatForwardMessageScreen.this,"You cannot forward message to more than 5 members",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLongClick(View var1, int var2) {

            }
        }));

        //It sends message to selected users present in userList using thread. So UI thread doesn't get heavy.
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 new Thread(() -> {
                     for (int i=0;i<=userList.size()-1;i++) {
                         Conversation conversation= new ArrayList<>(userList.values()).get(i);
                         TextMessage message;
                         String uid;
                         String type;
                         Log.e(TAG, "run: "+conversation.getConversationId());
                         if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                              uid = ((User) conversation.getConversationWith()).getUid();
                              type=CometChatConstants.RECEIVER_TYPE_USER;
                         }
                         else {
                              uid = ((Group) conversation.getConversationWith()).getGuid();
                             type=CometChatConstants.RECEIVER_TYPE_GROUP;
                         }
                         message = new TextMessage(uid, textMessage, type);
                         sendMessage(message);
                          if (i==userList.size()-1){
                              Intent intent=new Intent(CometChatForwardMessageScreen.this,CometChatUnified.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(intent);
                              finish();
                          }
                     }

                 }).start();

            }
        });
        rvConversationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }

            }
        });

    }

    private void sendMessage(TextMessage message) {

        CometChat.sendMessage(message, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.e(TAG, "onSuccess: "+textMessage.getReceiverUid());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         if (item.getItemId()==android.R.id.home){
             onBackPressed();
         }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserList() {
        Log.e(TAG, "checkUserList: "+userList.size() );
        if (userList.size()>0) {
            forwardBtn.setVisibility(View.VISIBLE);
        }
        else {
            forwardBtn.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to fetch conversations
     */
    private void makeConversationList() {

        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversationsList) {
                if (conversationsList.size() != 0) {

                    setAdapter(conversationsList);
                }
            }

            @Override
            public void onError(CometChatException e) {
               Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<Conversation> conversations) {
        if (conversationListAdapter == null) {
            conversationListAdapter = new ConversationListAdapter(this, conversations);
            rvConversationList.setAdapter(conversationListAdapter);
        } else {
            conversationListAdapter.updateList(conversations);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        conversationsRequest = null;
        conversationListAdapter=null;
        makeConversationList();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        CometChat.removeMessageListener(TAG);
        userList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
