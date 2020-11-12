package com.atul.android.ecommerce;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.android.ecommerce.adapter.ProductsAdapter;
import com.atul.android.ecommerce.databinding.ActivityMainBinding;
import com.atul.android.ecommerce.dialog.ProductAdderDialog;
import com.atul.android.ecommerce.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    List<Product> list;
    ProductsAdapter adapter;
    private SearchView searchView;
    public boolean isDragModeOn = false;

    SharedPreferences sharedPreferences;
    String sharedPreferencesFile = ".ECommerce";
    private Gson gson;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(sharedPreferencesFile, MODE_PRIVATE);
        gson = new Gson();

        setupProductsList();

    }

    private void setupProductsList() {
        list = loadData();
        list.add(new Product("Apple", 50, 5));
        adapter = new ProductsAdapter(MainActivity.this, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        dragAndDropProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.search_product).getActionView();

        //Meta data
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_product:
                addProduct();
                return true;

            case R.id.sort_products:
                sortProductsByName();
                return true;
            case R.id.drag_products:
                toggleDragAndDropButton(item);
                isDragModeOn = !isDragModeOn;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleDragAndDropButton(MenuItem item) {
        Drawable icon = item.getIcon();
        if (isDragModeOn) {
            icon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        } else {
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        item.setIcon(icon);

        if (isDragModeOn) {
            itemTouchHelper.attachToRecyclerView(null);
        } else {
            itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        }
    }

    private void sortProductsByName() {
        Collections.sort(adapter.productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "List Sorted!", Toast.LENGTH_SHORT).show();
    }

    private void addProduct() {
        new ProductAdderDialog(ProductAdderDialog.PRODUCT_ADD).showDialog(MainActivity.this , new ProductAdderDialog.OnProductAddListener() {
            @Override
            public void onProductAdded(Product product) {
                adapter.productList.add(product);
                adapter.allProductsList.add(product);
                adapter.notifyItemInserted(adapter.productList.size() - 1);
            }

            @Override
            public void onCancelled() {
                Toast.makeText(MainActivity.this, "Cancel Pressed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.product_edit:
                editProduct();
                return true;

            case R.id.product_remove:
                removeProduct();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void removeProduct() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you really want to delete this product?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Product productToBeDeleted = adapter.productList.get(adapter.lastSelectedItemPosition);
                        adapter.productList.remove(adapter.lastSelectedItemPosition);
                        adapter.allProductsList.remove(productToBeDeleted);
//                        list.remove(productToBeDeleted);
                        adapter.notifyItemRemoved(adapter.lastSelectedItemPosition);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void editProduct() {
        Product productToBeEdited = adapter.productList.get(adapter.lastSelectedItemPosition);
        ProductAdderDialog productAdderDialog = new ProductAdderDialog(ProductAdderDialog.PRODUCT_EDIT);
        productAdderDialog.product = productToBeEdited;
        productAdderDialog.showDialog(MainActivity.this,  new ProductAdderDialog.OnProductAddListener() {
            @Override
            public void onProductAdded(Product product) {
                adapter.productList.set(adapter.lastSelectedItemPosition, product);
                adapter.notifyItemChanged(adapter.lastSelectedItemPosition);
                Toast.makeText(MainActivity.this, "Editted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
                Toast.makeText(MainActivity.this, "Cancel Pressed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(adapter.productList);
        editor.putString("list", json);
        editor.apply();
    }

    private ArrayList<Product> loadData() {
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        List<Product> list = gson.fromJson(json, type);
        if (list == null) {
            return new ArrayList<>();
        }
        return (ArrayList<Product>) list;
    }

    private void dragAndDropProducts() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(adapter.productList, fromPosition, toPosition);
                binding.recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        itemTouchHelper = new ItemTouchHelper(simpleCallback);

    }

}
