package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ProductoAjustadoTest {

	private ProductoMenu base;

    private Ingrediente queso;

    private Ingrediente cebolla;

    private ProductoAjustado sinAjustes;

    @BeforeEach
    void setUp() throws Exception {
        base = new ProductoMenu("Hamburguesa Clásica", 20000);
        queso = new Ingrediente("Queso", 3000);
        cebolla = new Ingrediente("Cebolla", 2000);
        sinAjustes = new ProductoAjustado(base);
    }

    @Test
    void testGetNombre() {
        assertEquals("Hamburguesa Clásica", sinAjustes.getNombre(), "El nombre debe ser el del producto base.");
    }

    @Test
    void testGetPrecioSinAjustes() {
        assertEquals(20000, sinAjustes.getPrecio(), "Sin ajustes el precio debe ser el del producto base.");
    }

    @Test
    void testGetPrecioConAgregados() {
        sinAjustes.agregarIngrediente(queso);
        sinAjustes.agregarIngrediente(queso);
        sinAjustes.agregarIngrediente(cebolla);
        assertEquals(20000 + 3000 + 3000 + 2000, sinAjustes.getPrecio(), "Cada ingrediente agregado (incluso repetido) debe sumar su costo.");
    }

    @Test
    void testGetPrecioConEliminadosNoCambia() {
        sinAjustes.eliminarIngrediente(cebolla);
        assertEquals(20000, sinAjustes.getPrecio(), "Quitar ingredientes no debe reducir el precio.");
    }

    @Test
    void testGetPrecioConAgregadosYEliminados() {
        sinAjustes.agregarIngrediente(queso);
        sinAjustes.eliminarIngrediente(cebolla);
        assertEquals( 23000, sinAjustes.getPrecio());
    }

    @Test
    void testGenerarTextoFacturaSinAjustes() {
        assertEquals( "Hamburguesa Clásica\n            20000\n", sinAjustes.generarTextoFactura());
    }

    @Test
    void testGenerarTextoFacturaSoloAgregados() {
        sinAjustes.agregarIngrediente(queso);
        sinAjustes.agregarIngrediente(cebolla);
        assertEquals("Hamburguesa Clásica\n" + "    +Queso                3000\n" + "    +Cebolla                2000\n" + "            25000\n", sinAjustes.generarTextoFactura());
    }

    @Test
    void testGenerarTextoFacturaSoloEliminados() {
        sinAjustes.eliminarIngrediente(cebolla);
        assertEquals( "Hamburguesa Clásica\n" + "    -Cebolla\n" + "            20000\n", sinAjustes.generarTextoFactura());
    }

    @Test
    void testGenerarTextoFacturaConAgregadosYEliminados() {
        sinAjustes.agregarIngrediente(queso);
        sinAjustes.eliminarIngrediente(cebolla);
        assertEquals("Hamburguesa Clásica\n" + "    +Queso                3000\n" + "    -Cebolla\n" + "            23000\n", sinAjustes.generarTextoFactura());
    }

}

