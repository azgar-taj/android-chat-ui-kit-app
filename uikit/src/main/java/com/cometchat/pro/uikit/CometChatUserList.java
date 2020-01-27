package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.User;

import java.util.List;

import listeners.ClickListener;
import listeners.OnItemClickListener;
import listeners.RecyclerTouchListener;
import listeners.UserItemClickListener;
import viewmodel.UserListViewModel;

/**
 * Purpose - CometChatUserList class is a subclass of recyclerview and used as component by
 * developer to display list of users. Developer just need to fetchUsers at their end
 * and pass it to this component to display list of Users. It helps user to create conversation
 * list easily and saves their time.
 * @see User
 *
 * Created on - 20th December 2019
 *
 * Modified on  - 16th January 2020
 *
 */

@BindingMethods(value = {@BindingMethod(type = CometChatUserList.class, attribute = "app:userlist", method = "setUserList")})
public class CometChatUserList extends RecyclerView {

    private Context context;

    private UserListViewModel userListViewModel;

    public CometChatUserList(@NonNull Context context) {
        super(context);
        this.context = context;
        setViewModel();
    }

    public CometChatUserList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttributes(attrs);
        setViewModel();
    }

    public CometChatUserList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        getAttributes(attrs);
        setViewModel();
    }

    private void getAttributes(AttributeSet attributeSet) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.CometChatUserList, 0, 0);
    }

    /**
     *  This methods sets the list of users provided by the developer
     *
     * @param userList list of users
     *
     */
    public void setUserList(List<User> userList) {
        if (userListViewModel != null)
            userListViewModel.setUsersList(userList);
    }

    private void setViewModel() {
        if (userListViewModel == null) {
            userListViewModel = new UserListViewModel(context,this);
        }
    }

    public void add(int index,User user){
        if (userListViewModel!=null)
            userListViewModel.add(index,user);
    }

    /**
     * Method helps in adding the user to list
     *
     * @param user to be added in the list
     */
    public void add(User user){
        if (userListViewModel!=null)
            userListViewModel.add(user);
    }

    /**
     *  This methods updates the particular user provided by the developer
     *
     * @param user object of the user to be updated
     *
     */
    public void update(User user){
        if (userListViewModel!=null)
            userListViewModel.update(user);

    }

    public void update(int index,User user){
        if (userListViewModel!=null)
            userListViewModel.update(index,user);
    }

    public void remove(int index){
        if (userListViewModel!=null)
            userListViewModel.remove(index);
    }

    /**
     *   Removes user from the list based on user provided
     *
     * @param user of the user to be removed
     *
     */
    public void remove(User user){
        if (userListViewModel!=null){
            userListViewModel.remove(user);
        }
    }

    /**
     *   This method provides click event callback to the developer.
     *
     * @param clickListener object of <code><UserItemClickListener<code/> class
     */
    public void setItemClickListener(OnItemClickListener<User> clickListener){

        this.addOnItemTouchListener(new RecyclerTouchListener(context, this, new ClickListener() {

            @Override
            public void onClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                clickListener.OnItemClick(user,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                clickListener.OnItemLongClick(user,var2);
            }
        }));
    }


}
