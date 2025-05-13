import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto

class AdaptadorProductoCliente(
    private val listaProductos: List<Producto>
) : RecyclerView.Adapter<AdaptadorProductoCliente.ProductoViewHolder>() {

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecio)
        val txtVendedor: TextView = view.findViewById(R.id.txtVendedor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_cliente, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.txtNombre.text = producto.nombre
        holder.txtPrecio.text = "${producto.precio} €"
        holder.txtVendedor.text = "Vendedor: ${producto.nombreVendedor}"

        val imagen = producto.imagenPrincipal
        if (!imagen.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imagen)
                .placeholder(R.drawable.galeria)
                .error(R.drawable.galeria)
                .into(holder.imgProducto)
        } else {
            holder.imgProducto.setImageResource(R.drawable.galeria)
        }
    }


    override fun getItemCount(): Int = listaProductos.size
}
