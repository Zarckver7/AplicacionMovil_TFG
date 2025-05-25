import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurumverus.Cliente.Dialogs.BottomSheetPedidoDialog
import com.example.aurumverus.R
import com.example.aurumverus.modelos.Producto

class AdaptadorProductoCliente(
    // Muestra productos al cliente para comprar
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
        // Al hacer clic, muestra un diálogo para hacer pedido
        val producto = listaProductos[position]
        holder.txtNombre.text = producto.nombre
        holder.txtPrecio.text = "${producto.precio} €"
        holder.txtVendedor.text = "Vendedor: ${producto.nombreVendedor}"
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (context is FragmentActivity) {
                val dialog = BottomSheetPedidoDialog(producto)
                dialog.show(context.supportFragmentManager, "PedidoDialog")
            }
        }


        val imagen = producto.imagenPrincipal
        if (!imagen.isNullOrEmpty()) {
            // Muestra la imagen del producto
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
