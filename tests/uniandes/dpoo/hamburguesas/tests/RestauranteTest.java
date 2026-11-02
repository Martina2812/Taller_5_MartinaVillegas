package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

class RestauranteTest {
    private static final String DIR = "./data/prueba/";

    private Restaurante restaurante;

    private File facturaCreada;

    @BeforeEach
    void setUp() throws Exception {
        restaurante = new Restaurante();
        facturaCreada = null;

        new File( "./facturas" ).mkdirs();
    }

    @AfterEach
    void tearDown() throws Exception {
        if(facturaCreada != null)
            facturaCreada.delete();
    }

    private String leerArchivo(File archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String linea = reader.readLine();
        while(linea != null)
        {
            contenido.append(linea + "\n");
            linea = reader.readLine();
        }
        reader.close( );
        return contenido.toString();
    }

    private void cargarTodo() throws Exception {
        restaurante.cargarInformacionRestaurante(new File( DIR + "ingredientes.txt"), new File(DIR + "menu.txt"), new File(DIR + "combos.txt"));
    }

    @Test
    void testRestauranteVacio() {
        assertNull(restaurante.getPedidoEnCurso());
        assertTrue(restaurante.getPedidos().isEmpty());
        assertTrue(restaurante.getIngredientes().isEmpty());
        assertTrue(restaurante.getMenuBase().isEmpty());
        assertTrue(restaurante.getMenuCombos().isEmpty());
    }

    @Test
    void testIniciarPedido() throws Exception {
        restaurante.iniciarPedido("Ana", "Calle 1");
        Pedido pedido = restaurante.getPedidoEnCurso();
        assertNotNull(pedido);
        assertEquals("Ana", pedido.getNombreCliente());
        assertTrue(restaurante.getPedidos().isEmpty(), "Un pedido en curso aún no está cerrado.");
    }

    @Test
    void testIniciarPedidoYaHayUnoEnCurso() throws Exception {
        restaurante.iniciarPedido("Ana", "Calle 1");
        Pedido original = restaurante.getPedidoEnCurso();

        assertThrows(YaHayUnPedidoEnCursoException.class, ( ) -> restaurante.iniciarPedido("Luis", "Calle 2"));
        assertSame(original, restaurante.getPedidoEnCurso( ), "El pedido en curso no debe cambiar.");
    }

    @Test
    void testCerrarSinPedidoEnCurso() {
        assertThrows(NoHayPedidoEnCursoException.class, () -> restaurante.cerrarYGuardarPedido());
    }

    @Test
    void testCerrarYGuardarPedido() throws Exception {
        cargarTodo();
        restaurante.iniciarPedido("Ana", "Calle 1");
        Pedido pedido = restaurante.getPedidoEnCurso();
        pedido.agregarProducto( restaurante.getMenuBase().get(0));
        String textoEsperado = pedido.generarTextoFactura();
        facturaCreada = new File("./facturas/factura_" + pedido.getIdPedido() + ".txt");

        restaurante.cerrarYGuardarPedido();

        assertNull(restaurante.getPedidoEnCurso());
        assertTrue(facturaCreada.exists(), "La factura debe quedar en ./facturas/factura_<id>.txt");
        assertEquals(textoEsperado, leerArchivo(facturaCreada));
    }

    @Test
    void testSePuedeIniciarOtroPedidoTrasCerrar() throws Exception {
        restaurante.iniciarPedido("Ana", "Calle 1");
        int id1 = restaurante.getPedidoEnCurso().getIdPedido();
        restaurante.cerrarYGuardarPedido();
        new File("./facturas/factura_" + id1 + ".txt").delete();

        restaurante.iniciarPedido("Luis", "Calle 2");
        int id2 = restaurante.getPedidoEnCurso().getIdPedido();
        facturaCreada = new File("./facturas/factura_" + id2 + ".txt");
        restaurante.cerrarYGuardarPedido();

        assertNotEquals(id1, id2);
    }

    @Test
    void testCargarInformacion( ) throws Exception {
        cargarTodo( );

        assertEquals( 3, restaurante.getIngredientes( ).size( ) );
        assertEquals( "lechuga", restaurante.getIngredientes( ).get( 0 ).getNombre( ) );
        assertEquals( 1000, restaurante.getIngredientes( ).get( 0 ).getCostoAdicional( ) );
        assertEquals( "queso", restaurante.getIngredientes( ).get( 2 ).getNombre( ) );

        assertEquals( 3, restaurante.getMenuBase( ).size( ) );
        ProductoMenu corral = restaurante.getMenuBase( ).get( 0 );
        assertEquals( "corral", corral.getNombre( ) );
        assertEquals( 14000, corral.getPrecio( ) );

        assertEquals( 2, restaurante.getMenuCombos( ).size( ) );
        Combo combo = restaurante.getMenuCombos( ).get( 0 );
        assertEquals( "combo corral", combo.getNombre( ) );
        assertEquals( 22050, combo.getPrecio( ), "(14000 + 5500 + 5000) con 10% de descuento" );
        Combo sencillo = restaurante.getMenuCombos( ).get( 1 );
        assertEquals( "combo sencillo", sencillo.getNombre( ) );
        assertEquals( 18135, sencillo.getPrecio( ), "(14000 + 5500) con 7% de descuento" );
    }

    @Test
    void testCargarArchivosDelProyecto( ) throws Exception {
        restaurante.cargarInformacionRestaurante( new File( "./data/ingredientes.txt" ), new File( "./data/menu.txt" ), new File( "./data/combos.txt" ) );
        assertEquals( 15, restaurante.getIngredientes( ).size( ) );
        assertEquals( 22, restaurante.getMenuBase( ).size( ) );
        assertEquals( 4, restaurante.getMenuCombos( ).size( ) );
    }

    @Test
    void testIngredienteRepetido() {
        assertThrows( IngredienteRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes_repetidos.txt" ),
                new File( DIR + "menu.txt" ), new File( DIR + "combos.txt" ) ) );
    }

    @Test
    void testProductoRepetido() {
        assertThrows( ProductoRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes.txt" ),
                new File( DIR + "menu_repetido.txt" ), new File( DIR + "combos.txt" ) ) );
    }

    @Test
    void testComboRepetido() {
        assertThrows( ProductoRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes.txt" ),
                new File( DIR + "menu.txt" ), new File( DIR + "combos_repetidos.txt" ) ) );
    }

    @Test
    void testComboConProductoFaltante() {
        assertThrows( ProductoFaltanteException.class, ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes.txt" ),
                new File( DIR + "menu.txt" ), new File( DIR + "combos_faltante.txt" ) ) );
    }

    @Test
    void testNumeroInvalido() {
        assertThrows( NumberFormatException.class, ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes_invalidos.txt" ),
                new File( DIR + "menu.txt" ), new File( DIR + "combos.txt" ) ) );
    }

    @Test
    void testIngredientesInexistente() {
        File noExiste = new File( DIR + "no_existe.txt" );
        assertThrows( FileNotFoundException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( noExiste, new File( DIR + "menu.txt" ), new File( DIR + "combos.txt" ) ) );
    }

    @Test
    void testMenuInexistente() {
        File noExiste = new File( DIR + "no_existe.txt" );
        assertThrows( FileNotFoundException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes.txt" ), noExiste, new File( DIR + "combos.txt" ) ) );
    }

    @Test
    void testCombosInexistente() {
        File noExiste = new File( DIR + "no_existe.txt" );
        assertThrows( FileNotFoundException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( new File( DIR + "ingredientes.txt" ), new File( DIR + "menu.txt" ), noExiste ) );
    }
}
