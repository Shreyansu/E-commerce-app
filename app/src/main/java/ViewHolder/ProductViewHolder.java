package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce20.R;

import Interface.ItemClickListner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtproductName ,txtproductDescription,txtproductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        imageView =(ImageView) itemView.findViewById(R.id.product_image);
        txtproductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtproductName = (TextView) itemView.findViewById(R.id.product_name);
        txtproductPrice = (TextView) itemView.findViewById(R.id.product_price);

    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View v) {

        listner.onClick(v, getAdapterPosition(), false);

    }
}
