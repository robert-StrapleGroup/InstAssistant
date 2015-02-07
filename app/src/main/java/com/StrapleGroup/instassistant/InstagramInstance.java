package com.StrapleGroup.instassistant;

import android.content.Context;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;

/**
 * Created by Robert on 2015-02-07.
 */
public class InstagramInstance {

    Context context;
    private static InstagramInstance instance = null;
    Instagram instagram;

    public InstagramInstance(Context context){
        this.context = context;
    }

    public boolean startService(Token aToken){
        instagram = new Instagram(aToken);
        return true;
    }

    public Instagram getInstagram(){
        if(instagram != null){
            return instagram;
        }else return null;
    }

    public static InstagramInstance getInstance(Context context){
        if(instance == null){
            instance = new InstagramInstance(context.getApplicationContext());
        }
        return instance;
    }

}
