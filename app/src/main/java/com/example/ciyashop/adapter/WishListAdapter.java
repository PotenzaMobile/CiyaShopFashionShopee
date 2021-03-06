package com.example.ciyashop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ciyashop.library.apicall.GetApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.ciyashop.R;
import com.example.ciyashop.activity.CartActivity;
import com.example.ciyashop.activity.ProductDetailActivity;
import com.example.ciyashop.customview.MaterialRatingBar;
import com.example.ciyashop.customview.textview.TextViewRegular;
import com.example.ciyashop.helper.DatabaseHelper;
import com.example.ciyashop.interfaces.OnItemClickListner;
import com.example.ciyashop.javaclasses.CheckIsVariationAvailable;
import com.example.ciyashop.model.Cart;
import com.example.ciyashop.model.CategoryList;
import com.example.ciyashop.model.Variation;
import com.example.ciyashop.utils.BaseActivity;
import com.example.ciyashop.utils.Config;
import com.example.ciyashop.utils.Constant;
import com.example.ciyashop.utils.CustomToast;
import com.example.ciyashop.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 17-11-2017.
 */

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.RecentViewHolder> implements OnResponseListner, OnItemClickListner {


    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private DatabaseHelper databaseHelper;

    AlertDialog alertDialog;
    private CustomToast toast;
    private boolean isDialogOpen = false;
    private int VariationPage = 1;
    private List<Variation> variationList = new ArrayList<>();
    private int defaultVariationId;


    public WishListAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
        this.toast = new CustomToast(activity);

    }

    public void addAll(List<CategoryList> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    public int getListsize() {
        return list.size();
    }

    @Override
    public RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row_wishlist, parent, false);

        return new RecentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecentViewHolder holder, final int position) {
        holder.llDelete.setVisibility(View.VISIBLE);
        holder.tvCart.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        holder.ivCart.setColorFilter(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).type.equals(RequestParamUtils.external)) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
                    activity.startActivity(browserIntent);
                } else {
                    Constant.CATEGORYDETAIL = list.get(position);
                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                    intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                    activity.startActivity(intent);
                }
            }
        });

        //Add product in cart if add to cart enable from admin panel

        if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }

        Picasso.get().load(list.get(position).appthumbnail).into(holder.ivImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
            holder.tvName.setText(Html.fromHtml(list.get(position).name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
//            tvProductName.setText(categoryList.name + "");
            holder.tvName.setText(Html.fromHtml(list.get(position).name + ""));
        }

        holder.tvPrice.setTextSize(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }

        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, list.size());
                databaseHelper.deleteFromWishList(list.get(position).id + "");
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        if (Constant.IS_ADD_TO_CART_ACTIVE) {
            if (Config.IS_CATALOG_MODE_OPTION) {
                holder.llAddCart.setVisibility(View.GONE);
            } else {
                holder.llDelete.setVisibility(View.VISIBLE);

                if (!list.get(position).inStock) {

                    holder.tvCart.setText(activity.getString(R.string.out_of_stock));

                }
                if (!list.get(position).type.equals("variable")) {
                    if (databaseHelper != null) {
                        if (databaseHelper.getProductFromCartById(list.get(position).id + "") != null) {
                            holder.tvCart.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                            holder.ivCart.setColorFilter(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                        } else {
                            holder.ivCart.setColorFilter(R.color.black);
                            holder.tvCart.setTextColor(activity.getResources().getColor(R.color.black));
                        }
                    }
                }
                holder.llAddCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).inStock) {
                            if (list.get(position).type.equals("variable")) {

                                isDialogOpen = true;
                                callApi(position);

                            } else if (list.get(position).type.equals("simple")) {
                                holder.tvCart.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                                holder.ivCart.setColorFilter(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                                Cart cart = new Cart();
                                cart.setQuantity(1);
                                cart.setVariation("{}");
                                cart.setProduct(new Gson().toJson(list.get(position)));
                                cart.setVariationid(0);
                                cart.setProductid(list.get(position).id + "");
                                cart.setBuyNow(0);
                                cart.setManageStock(list.get(position).manageStock);

                                if (databaseHelper.getProductFromCartById(list.get(position).id + "") != null) {
                                    databaseHelper.addToCart(cart);
                                    Intent intent = new Intent(activity, CartActivity.class);
                                    intent.putExtra("buynow", 0);
                                    activity.startActivity(intent);
                                } else {
                                    databaseHelper.addToCart(cart);
                                    ((BaseActivity) activity).showCart();
                                    toast = new CustomToast(activity);
                                    toast.showToast(activity.getString(R.string.item_added_to_your_cart));
                                    toast.showBlackbg();
                                    onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, list.size());
                                    databaseHelper.deleteFromWishList(list.get(position).id + "");
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        } else {
                            toast = new CustomToast(activity);
                            toast.showToast(activity.getString(R.string.out_of_stock));
                            toast.showBlackbg();
                        }
                    }
                });
            }
        } else {
            holder.llAddCart.setVisibility(View.GONE);
        }
    }

    public void callApi(int position) {

        ((BaseActivity) activity).showProgress("");
        if (VariationPage == 1) {
            variationList = new ArrayList<>();
        }
        GetApi getApi = new GetApi(activity, "getVariation_" + position, this, ((BaseActivity) activity).getlanuage());
        getApi.callGetApi(new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + list.get(position).id + "/" + new URLS().WOO_VARIATION + "?page=" + VariationPage);
    }

    public void showDialog(final int position) {

        RecyclerView rvProductVariation;
        ProductVariationAdapter productVariationAdapter;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);
        rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);
        productVariationAdapter = new ProductVariationAdapter(activity, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productVariationAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        productVariationAdapter.addAll(list.get(position).attributes);
        productVariationAdapter.addAllVariationList(variationList);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        alertDialog.show();

        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (alertDialog != null) {
                    alertDialog.show();
                }
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.get(position).attributes)) {
                    toast = new CustomToast(activity);
                    toast.showToast(activity.getString(R.string.combition_doesnt_exist));
                } else {
                    toast.cancelToast();
                    alertDialog.dismiss();
                    if (databaseHelper.getVariationProductFromCart(getCartVariationProduct(position))) {
                        //tvCart.setText(getResources().getString(R.string.go_to_cart));
                    } else {
                        //tvCart.setText(getResources().getString(R.string.add_to_Cart));
                    }

                    //changePrice();

                    if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.get(position).attributes)) {
                        toast = new CustomToast(activity);
                        toast.showToast(activity.getString(R.string.variation_not_available));
                        toast.showRedbg();
                    } else {
                        if (getCartVariationProduct(position) != null) {
                            Cart cart = getCartVariationProduct(position);
                            if (databaseHelper.getVariationProductFromCart(cart)) {
                                Intent intent = new Intent(activity, CartActivity.class);
                                intent.putExtra("buynow", 0);
                                activity.startActivity(intent);
                            } else {
                                databaseHelper.addVariationProductToCart(getCartVariationProduct(position));
                                ((BaseActivity) activity).showCart();
//                                toast.showBlackbg();
                                toast = new CustomToast(activity);
                                toast.showToast(activity.getString(R.string.item_added_to_your_cart));
                                onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, list.size());
                                databaseHelper.deleteFromWishList(list.get(position).id + "");
                                list.remove(position);
                                notifyDataSetChanged();

                            }
                        } else {
                            toast = new CustomToast(activity);
                            toast.showRedbg();
                            toast.showToast(activity.getString(R.string.variation_not_available));

                        }
                    }
                }
            }
        });
        alertDialog.show();
    }

    public void getDefaultVariationId() {
        Log.e("default variation id ", "called");
        List<String> list = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < ProductDetailActivity.combination.size(); i++) {
                String value = ProductDetailActivity.combination.get(i);
                String[] valuearray = new String[0];
                if (value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                list.add(ProductDetailActivity.combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        defaultVariationId = new CheckIsVariationAvailable().getVariationid(variationList, list);
        CategoryList.Image image = new CategoryList().getImageInstance();
        image.src = CheckIsVariationAvailable.imageSrc;
    }

    public Cart getCartVariationProduct(int position) {

        Log.e("getCartVariation", "called");
        List<String> lists = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < ProductDetailActivity.combination.size(); i++) {
                String value = ProductDetailActivity.combination.get(i);
                String[] valuearray = new String[0];
                if (value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                lists.add(ProductDetailActivity.combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setVariation(object.toString());
        list.get(position).priceHtml = CheckIsVariationAvailable.pricehtml;
        list.get(position).price = CheckIsVariationAvailable.price + "";

        if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
            list.get(position).appthumbnail = CheckIsVariationAvailable.imageSrc;
        }
        if (!list.get(position).manageStock) {
            list.get(position).manageStock = CheckIsVariationAvailable.isManageStock;
        }

        //list.get(position).images.set(0,"")
        list.get(position).images.clear();

        cart.setVariationid(new CheckIsVariationAvailable().getVariationid(variationList, lists));
        cart.setProductid(list.get(position).id + "");
        cart.setBuyNow(0);
        cart.setManageStock(list.get(position).manageStock);
        cart.setStockQuantity(CheckIsVariationAvailable.stockQuantity);
        cart.setProduct(new Gson().toJson(list.get(position)));

        if (cart.getVariationid() != defaultVariationId) {

            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.get(position).images.add(image);

        } else {
            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.get(position).images.add(image);

        }
        cart.setProduct(new Gson().toJson(list.get(position)));
        return cart;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onItemClick(int position, String value, int outerpos) {

    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        @BindView(R.id.ll_Delete)
        LinearLayout llDelete;

        @BindView(R.id.iv_cart)
        ImageView ivCart;

        @BindView(R.id.tvCart)
        TextViewRegular tvCart;

        @BindView(R.id.ll_AddCart)
        LinearLayout llAddCart;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        public RecentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onResponse(String response, String methodName) {

        if (methodName.contains("getVariation_")) {
            ((BaseActivity) activity).dismissProgress();

            String currentString = methodName;
            String[] separated = currentString.split("_");
            String str = separated[0];
            String strPositon = separated[1];
            int positions = Integer.parseInt(strPositon);

            Log.e("onResponse: ", "position=" + positions);

            JSONArray jsonArray = null;
            if (response != null && response.length() > 0) {
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonResponse = jsonArray.get(i).toString();
                        Variation variationRider = new Gson().fromJson(
                                jsonResponse, new TypeToken<Variation>() {
                                }.getType());
                        variationList.add(variationRider);
                    }
                    if (jsonArray.length() == 10) {
                        //more product available
                        VariationPage++;
                        callApi(positions);
                    } else {
                        showDialog(positions);
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                if (jsonArray == null || jsonArray.length() != 10) {
                    getDefaultVariationId();
                }
            }

        }
    }
}
