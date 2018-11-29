package com.kairanpa.se.lecshare;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Set;

import model.User;

public class AvatarFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_avatar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initGachaButton();
        initAvatar();
        initShop();
        updateMoneyText();
        initBackButton();
        initToolbar();
    }

    void initGachaButton()
    {
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
                else
                {
                    gachaButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    final String result = randomBox.get((int) (Math.random() * randomBox.size()));
                    user.getInventory().put(result, true);
                    user.addMoney(-100);
                    updateMoneyText();
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

    void initAvatar()
    {
        ImageView avatar = getView().findViewById(R.id.avatar_avatar);
        String pictureName = user.getAvatar();
        Log.d("test", "picture name is " + pictureName);
        switch (pictureName)
        {
            case "blue":
                avatar.setImageResource(R.drawable.avatar_blue);
                break;
            case "green":
                avatar.setImageResource(R.drawable.avatar_green);
                break;
            case "grey":
                avatar.setImageResource(R.drawable.avatar_grey);
                break;
            case "red":
                avatar.setImageResource(R.drawable.avatar_red);
                break;
            case "brown bunny":
                avatar.setImageResource(R.drawable.avatar_brown_bunny);
                break;
            case "pinky":
                avatar.setImageResource(R.drawable.avatar_pinky);
                break;
            case "wingky":
                avatar.setImageResource(R.drawable.avatar_wingky);
                break;
            case "zombie":
                avatar.setImageResource(R.drawable.avatar_zombie);
                break;
        }
    }

    void initShop()
    {
        initItem(R.id.avatar_item_blue, "blue", 1000, R.drawable.avatar_blue);
        initItem(R.id.avatar_item_green, "green",1000, R.drawable.avatar_green);
        initItem(R.id.avatar_item_grey, "grey", 1000, R.drawable.avatar_grey);
        initItem(R.id.avatar_item_red, "red", 1000, R.drawable.avatar_red);
        initItem(R.id.avatar_item_brown_bunny, "brown bunny", 1000, R.drawable.avatar_brown_bunny);
        initItem(R.id.avatar_item_pinky, "pinky", 1000, R.drawable.avatar_pinky);
        initItem(R.id.avatar_item_wingky, "wingky", 1000, R.drawable.avatar_wingky);
        initItem(R.id.avatar_item_zombie, "zombie", 1000, R.drawable.avatar_zombie);
    }

    void initItem(final int id, final String name, final int price, final int avatarId)
    {
        LinearLayout itemLayout = getView().findViewById(id);
        ImageView itemPicture = itemLayout.findViewById(R.id.shop_item_picture);
        itemPicture.setImageResource(avatarId);
        TextView itemName = itemLayout.findViewById(R.id.shop_item_name);
        TextView itemPrice = itemLayout.findViewById(R.id.shop_item_price);
        final Button buyButton = itemLayout.findViewById(R.id.shop_item_buy_button);
        final TextView equipped = itemLayout.findViewById(R.id.shop_item_equipped);
        final ProgressBar progressBar = itemLayout.findViewById(R.id.shop_item_progress_bar);
        itemName.setText(name);
        itemPrice.setText("Price : " + price);
        if (user.getInventory().get(name))
        {
            buyButton.setText("Use");
        }
        if (user.getAvatar().equals(name))
        {
            resetBuyButton();
            buyButton.setVisibility(View.GONE);
            equipped.setVisibility(View.VISIBLE);
        }
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (user.getInventory().get(name))
                {
                    resetBuyButton();
                    buyButton.setVisibility(View.GONE);
                    equipped.setVisibility(View.VISIBLE);
                    ImageView avatar = getView().findViewById(R.id.avatar_avatar);
                    avatar.setImageResource(avatarId);
                    user.setAvatar(name);
                    fbStore.collection("User").document(user.getDocumentId()).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    buyButton.setEnabled(true);
                                    Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                                    Log.d("test", "update equip success");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            buyButton.setEnabled(true);
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("test", "equip fail : " + e.getMessage());
                        }
                    });
                }
                else
                {
                    if (user.getMoney() < price)
                    {
                        buyButton.setEnabled(true);
                        Toast.makeText(getContext(), "not enough money", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    user.getInventory().put(name, true);
                    user.addMoney(-price);
                    updateMoneyText();
                    fbStore.collection("User").document(user.getDocumentId()).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    buyButton.setEnabled(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    buyButton.setText("Use");
                                    Toast.makeText(getContext(), "buy success", Toast.LENGTH_SHORT).show();
                                    Log.d("test", "update buy success");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            buyButton.setEnabled(true);
                            Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("test", "update buy error : " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    void resetBuyButton()
    {
        int[] ids = {R.id.avatar_item_blue, R.id.avatar_item_green, R.id.avatar_item_grey, R.id.avatar_item_red};
        for (int i = 0; i < ids.length; i++)
        {
            LinearLayout layout = getView().findViewById(ids[i]);
            Button buyButton = layout.findViewById(R.id.shop_item_buy_button);
            TextView equipped = layout.findViewById(R.id.shop_item_equipped);
            buyButton.setVisibility(View.VISIBLE);
            equipped.setVisibility(View.GONE);
        }
    }

    void updateMoneyText()
    {
        TextView moneyText = getView().findViewById(R.id.avatar_money);
        moneyText.setText("Money : " + user.getMoney());
    }

    void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.avatar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.avatar_toolbar);
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
}
