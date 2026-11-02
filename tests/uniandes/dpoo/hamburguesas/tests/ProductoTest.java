package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Producto;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ProductoTest {
    private ArrayList<Producto> productos;

    @BeforeEach
    void setUp() throws Exception {
        ProductoMenu corral = new ProductoMenu("corral", 14000);
        ProductoMenu papas = new ProductoMenu("papas", 5500);

        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>();
        items.add(corral);
        items.add(papas);

        ProductoAjustado ajustado = new ProductoAjustado(corral);
        ajustado.agregarIngrediente(new Ingrediente("queso", 2500));
        ajustado.eliminarIngrediente(new Ingrediente("cebolla", 1000));

        productos = new ArrayList<Producto>();
        productos.add(corral);
        productos.add(ajustado);
        productos.add(new Combo("combo", 0.1, items));
    }

    @Test
    void testTodosTienenNombre() {
        for (Producto producto : productos) {
            assertNotNull(producto.getNombre());
            assertFalse(producto.getNombre().isEmpty());
        }
    }

    @Test
    void testTodosTienenPrecioPositivo( ) {
        for(Producto producto : productos) {
            assertTrue( producto.getPrecio() > 0, "El precio de " + producto.getNombre() + " debe ser positivo.");
        }
    }

    @Test
    void testLaFacturaIncluyeNombreYPrecio() {
        for(Producto producto : productos) {
            String texto = producto.generarTextoFactura();
            assertTrue(texto.contains(producto.getNombre()), "La factura de " + producto.getNombre() + " debe incluir el nombre.");
            assertTrue(texto.contains("" + producto.getPrecio()), "La factura de " + producto.getNombre() + " debe incluir el precio.");
        }
    }
}
