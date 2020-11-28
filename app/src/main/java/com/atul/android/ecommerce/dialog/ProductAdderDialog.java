package com.atul.android.ecommerce.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atul.android.ecommerce.R;
import com.atul.android.ecommerce.databinding.ProductAdderDialogBinding;
import com.atul.android.ecommerce.model.Product;

import java.util.regex.Pattern;

public class ProductAdderDialog {

    ProductAdderDialogBinding b;
    public Product product;

    public static final byte PRODUCT_ADD = 0, PRODUCT_EDIT = 1;
    byte productType;

    public ProductAdderDialog(byte type) {
        productType = type;
    }

    public void showDialog(Context context, OnProductAddListener listener) {
        b = ProductAdderDialogBinding.inflate(
                LayoutInflater.from(context)
        );

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(productType == PRODUCT_ADD ? "Add Product" : "Edit Product")
                .setView(b.getRoot())
                .setPositiveButton(productType == PRODUCT_ADD ? "ADD" : "EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(areProductDetailsValid(productType)) {
                            listener.onProductAdded(product);
                        } else {
                            Toast.makeText(context, "Invalid Details!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelled();
                    }
                })
                .show();
        setupRadioGroup();
        if (productType == PRODUCT_EDIT) {
            preFillPreviousDetails();
        }
    }

    private void preFillPreviousDetails() {
        b.name.setText(product.name);
        b.radioGroupItemBased.check(product.type == Product.WEIGHT_BASED ? R.id.radio_button_weight_based : R.id.radio_button_varient_based);
        if (product.type == Product.WEIGHT_BASED) {
            b.pricePerKg.setText(product.pricePerKg + "");
            b.minQuantity.setText("" + product.convertMinQtyToWeight(product.minQty));
        } else {
            b.varients.setText(product.varientsString());
        }
    }

    private boolean areProductDetailsValid(int type) {
        String name = b.name.getText().toString().trim();
        if(name.isEmpty()) {
            return false;
        }
        switch (b.radioGroupItemBased.getCheckedRadioButtonId()) {
            case R.id.radio_button_weight_based:
                String pricePerKg = b.pricePerKg.getText().toString().trim();
                String minQty = b.minQuantity.getText().toString().trim();
                if (pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)")) {
                    return false;
                }
                if (type == PRODUCT_ADD) {
                    product = new Product(name, Integer.parseInt(pricePerKg), extractMinimumQuantity(minQty));
                } else {
                    product.makeWeightProduct(name, Integer.parseInt(pricePerKg), extractMinimumQuantity(minQty));
                }
                return true;

            case R.id.radio_button_varient_based:
                String varients = b.varients.getText().toString().trim();
                if (type == PRODUCT_ADD) {
                    product = new Product(name);
                } else {
                    product.makeVarientProduct(name);
                }
                return areVarientsDetailsValid(varients);
        }
        return false;
    }

    private boolean areVarientsDetailsValid(String varients) {
        if (varients.length() == 0) {
            return false;
        }
        String[] vs = varients.split("\n");
        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)+,\\d+$");
        for (String varient : vs) {
            if (!pattern.matcher(varient).matches()) {
                return false;
            }
        }
        product.fromVarientStrings(vs);
        return true;
    }

    private float extractMinimumQuantity(String minQty) {
        if (minQty.contains("kg")) {
            return Float.parseFloat(minQty.replace("kg", ""));
        } else {
            return Float.parseFloat(minQty.replace("g", "")) / 1000f;
        }
    }

    private void setupRadioGroup() {
        b.radioGroupItemBased.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_weight_based) {
                    b.weightBasedRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                } else {
                    b.variantsRoot.setVisibility(View.VISIBLE);
                    b.weightBasedRoot.setVisibility(View.GONE);
                }
            }
        });
    }

    public interface OnProductAddListener {
        void onProductAdded(Product product);
        void onCancelled();
    }

}
