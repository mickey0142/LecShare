package com.kairanpa.se.lecshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Set;

import model.User;

public class GachaFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    Button _backBtn;
    String result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        result = bundle.getString("gacha result");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gacha, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setGif();
        initBackButton();
        initToolbar();
        initTryAgain();
    }


    void setGif()
    {
        ImageView iv = getView().findViewById(R.id.gacha_picture);
        GlideApp.with(getContext()).asGif().load(R.drawable.open_gacha)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                Log.d("test", "load gif failed");
                return false;
            }

            @Override
            public boolean onResourceReady(final GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e)
                        {
                            Log.d("test", "catch InterruptException" + e.getMessage());
                        }
                        while(true) {
                            if(!resource.isRunning()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAfterFinish();
                                    }
                                });
                                Log.d("test", "animation finish");
                                break;
                            }
                        }
                    }
                }).start();
                return false;
            }
        })
                .into(iv);
    }
    void setAfterFinish()
    {
        LinearLayout layout = getView().findViewById(R.id.gacha_after_finish_layout);
        layout.setVisibility(View.VISIBLE);
        ImageView iv = getView().findViewById(R.id.gacha_picture);
        iv.setVisibility(View.GONE);
        ImageView afterPicture = getView().findViewById(R.id.gacha_after_finish_picture);
        switch (result)
        {
            case "blue":
                afterPicture.setImageResource(R.drawable.avatar_blue);
                break;
            case "green":
                afterPicture.setImageResource(R.drawable.avatar_green);
                break;
            case "grey":
                afterPicture.setImageResource(R.drawable.avatar_grey);
                break;
            case "red":
                afterPicture.setImageResource(R.drawable.avatar_red);
                break;
            case "brown bunny":
                afterPicture.setImageResource(R.drawable.avatar_brown_bunny);
                break;
            case "pinky":
                afterPicture.setImageResource(R.drawable.avatar_pinky);
                break;
            case "wingky":
                afterPicture.setImageResource(R.drawable.avatar_wingky);
                break;
            case "zombie":
                afterPicture.setImageResource(R.drawable.avatar_zombie);
                break;
            case "spinny":
                afterPicture.setImageResource(R.drawable.avatar_spinny);
                break;
            case "feddy":
                afterPicture.setImageResource(R.drawable.avatar_feddy);
                break;
            case "gold ufo":
                afterPicture.setImageResource(R.drawable.avatar_gold_ufo);
                break;
            case "judy":
                afterPicture.setImageResource(R.drawable.avatar_judy);
                break;
            case "marico":
                afterPicture.setImageResource(R.drawable.avatar_marico);
                break;
            case "penny":
                afterPicture.setImageResource(R.drawable.avatar_penny);
                break;
            case "ufo":
                afterPicture.setImageResource(R.drawable.avatar_ufo);
                break;
            case "helicopty":
                afterPicture.setImageResource(R.drawable.avatar_helicopty);
                break;
            case "spikey":
                afterPicture.setImageResource(R.drawable.avatar_spikey);
                break;
        }
        afterPicture.setVisibility(View.VISIBLE);
        TextView resultText = getView().findViewById(R.id.gacha_text);
        resultText.setText("you got " + result);
        resultText.setVisibility(View.VISIBLE);
        Button _tryAgain = getView().findViewById(R.id.gacha_try_again);
        _tryAgain.setVisibility(View.VISIBLE);
        _backBtn = getView().findViewById(R.id.gacha_back_button);
        _backBtn.setVisibility(View.VISIBLE);
    }

    void initBackButton()
    {
        _backBtn = getView().findViewById(R.id.gacha_back_button);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_view, new ProfileFragment()).commit();
            }
        });
    }


    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.gacha_toolbar);
        mTool.inflateMenu(R.menu.fragment_menu);
        mTool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Log.d("test", "itemId : " + itemId);
                if (itemId == R.id.menu_home)
                {
                    Log.d("test", "press home");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, homeFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_profile)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    bundle.putSerializable("Target object", user);
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_upload)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment uploadFragment = new UploadFragment();
                    uploadFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, uploadFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_search_user)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment fragment = new SearchUserFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_logout)
                {
                    Log.d("test", "press logout");
                    fbAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .commit();
                }
                return false;
            }
        });
    }

    void initTryAgain(){
        final ProgressBar progressBar = getView().findViewById(R.id.avatar_progress_bar);
        final Button gachaButton = getView().findViewById(R.id.avatar_gacha_button);
        gachaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean gotAll = true;
                Set<String> keys = user.getInventory().keySet();
                ArrayList<String> randomBox = new ArrayList<>();
                for (String key : keys)
                {
                    if (!user.getInventory().get(key))
                    {
                        gotAll = false;
                        randomBox.add(key);
                    }
                }
                if (gotAll)
                {
                    Toast.makeText(getContext(), "you already have every item", Toast.LENGTH_SHORT).show();
                }
                else if (user.getMoney() < 100)
                {
                    Toast.makeText(getContext(), "not enough money", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    gachaButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    final String result = randomBox.get((int) (Math.random() * randomBox.size()));
                    user.getInventory().put(result, true);
                    user.addMoney(-100);
                    fbStore.collection("User").document(user.getDocumentId()).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    gachaButton.setEnabled(true);
                                    Log.d("test", "gacha random success result is : " + result);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("User object", user);
                                    bundle.putString("gacha result", result);
                                    Fragment fragment = new GachaFragment();
                                    fragment.setArguments(bundle);
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    gachaButton.setEnabled(true);
                                    Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("test", "gacha random update fail : " + e.getMessage());
                                }
                            });
                }
            }
        });
    }
}
