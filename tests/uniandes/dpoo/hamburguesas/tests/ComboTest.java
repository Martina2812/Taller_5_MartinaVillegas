package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ComboTest {
    private ProductoMenu corral;

    private ProductoMenu papas;

    private ProductoMenu gaseosa;

    private ArrayList<ProductoMenu> items;

    private Combo combo;

    @BeforeEach
    void setUp() throws Exception {
        corral = new ProductoMenu("corral", 14000);
        papas = new ProductoMenu("papas medianas", 5500);
        gaseosa = new ProductoMenu("gaseosa", 5000);

        items = new ArrayList<ProductoMenu>();
        items.add(corral);
        items.add(papas);
        items.add(gaseosa);

        combo = new Combo("combo corral", 0.1, items);
    }

    @Test
    void testGetNombre() {
        assertEquals("combo corral", combo.getNombre());
    }

    @Test
    void testGetPrecioConDescuento() {
        assertEquals( 22050, combo.getPrecio(), "El precio debe ser la suma de los productos menos el descuento.");
    }

    @Test
    void testGetPrecioSinDescuento()
    {
        Combo sinDescuento = new Combo( "sin descuento", 0, items);
        assertEquals( 24500, sinDescuento.getPrecio());
    }

    @Test
    void testGetPrecioDescuentoTotal() {
        Combo gratis = new Combo( "gratis", 1, items);
        assertEquals( 0, gratis.getPrecio());
    }

    @Test
    void testGetPrecioComboVacio() {
        Combo vacio = new Combo("vacio", 0.5, new ArrayList<ProductoMenu>( ) );
        assertEquals( 0, vacio.getPrecio());
    }

    @Test
    void testElComboCopiaLaListaDeItems() {
        items.add( new ProductoMenu("extra", 10000));
        assertEquals( 22050, combo.getPrecio(), "Modificar la lista original no debe afectar al combo.");
    }

    @Test
    void testGenerarTextoFactura() {
        assertEquals( "Combo combo corral\n Descuento: 0.1\n            22050\n", combo.generarTextoFactura());
    }
}
