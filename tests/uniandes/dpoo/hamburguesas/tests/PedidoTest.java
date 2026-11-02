package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class PedidoTest {
    private static final String ENCABEZADO = "Cliente: Ana\nDirección: Calle 1 # 2-3\n----------------\n";

    private Pedido pedido;

    private ProductoMenu corral;

    private ProductoMenu papas;

    @BeforeEach
    void setUp() throws Exception {
        pedido = new Pedido("Ana", "Calle 1 # 2-3");
        corral = new ProductoMenu("corral", 14000);
        papas = new ProductoMenu("papas", 5500);
    }

    @Test
    void testDatosBasicos( )
    {
        assertEquals( "Ana", pedido.getNombreCliente( ) );
    }

    @Test
    void testIdsSeIncrementan( )
    {
        Pedido siguiente = new Pedido( "Luis", "Otra calle" );
        Pedido ultimo = new Pedido( "Eva", "Otra calle 2" );
        assertEquals( pedido.getIdPedido( ) + 1, siguiente.getIdPedido( ) );
        assertEquals( pedido.getIdPedido( ) + 2, ultimo.getIdPedido( ) );
    }

    @Test
    void testPedidoVacioPrecioTotal( )
    {
        assertEquals( 0, pedido.getPrecioTotalPedido( ) );
    }

    @Test
    void testPedidoVacioFactura( )
    {
        String esperado = ENCABEZADO + "----------------\n" + "Precio Neto:  0\n" + "IVA:          0\n" + "Precio Total: 0\n";
        assertEquals( esperado, pedido.generarTextoFactura( ) );
    }

    @Test
    void testPrecioTotalConProductos( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );
        // neto 19500, IVA 3705
        assertEquals( 23205, pedido.getPrecioTotalPedido( ) );
    }

    @Test
    void testFacturaConProductos( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );
        String esperado = ENCABEZADO + "corral\n            14000\n" + "papas\n            5500\n" + "----------------\n" + "Precio Neto:  19500\n" + "IVA:          3705\n"
                + "Precio Total: 23205\n";
        assertEquals( esperado, pedido.generarTextoFactura( ) );
    }

    @Test
    void testIVATruncaDecimales( )
    {
        pedido.agregarProducto( new ProductoMenu( "raro", 101 ) );

        assertEquals( 120, pedido.getPrecioTotalPedido( ) );
    }

    private void llenarPedidoMixto( )
    {
        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>( );
        items.add( corral );
        items.add( papas );
        Combo combo = new Combo( "combo", 0.1, items ); 

        ProductoAjustado ajustado = new ProductoAjustado( corral );
        ajustado.agregarIngrediente( new Ingrediente( "queso", 2500 ) ); 

        pedido.agregarProducto( combo );
        pedido.agregarProducto( ajustado );
    }

    @Test
    void testPrecioTotalMixto( )
    {
        llenarPedidoMixto( );

        assertEquals( 34050 + 6469, pedido.getPrecioTotalPedido( ) );
    }

    @Test
    void testFacturaMixta( )
    {
        llenarPedidoMixto( );
        String esperado = ENCABEZADO + "Combo combo\n Descuento: 0.1\n            17550\n" + "corral\n    +queso                2500\n            16500\n" + "----------------\n"
                + "Precio Neto:  34050\n" + "IVA:          6469\n" + "Precio Total: 40519\n";
        assertEquals( esperado, pedido.generarTextoFactura( ) );
    }

    private String leerArchivo( File archivo ) throws IOException
    {
        StringBuilder contenido = new StringBuilder( );
        BufferedReader reader = new BufferedReader( new FileReader( archivo ) );
        String linea = reader.readLine( );
        while( linea != null )
        {
            contenido.append( linea + "\n" );
            linea = reader.readLine( );
        }
        reader.close( );
        return contenido.toString( );
    }

    @Test
    void testGuardarFactura( ) throws Exception
    {
        pedido.agregarProducto( corral );
        File archivo = new File( "factura_temporal.txt" );

        pedido.guardarFactura( archivo );

        assertTrue( archivo.exists( ), "El archivo de la factura debe existir." );
        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivo ), "El archivo debe contener exactamente el texto de la factura." );
        archivo.delete( );
    }

    @Test
    void testGuardarFacturaSobrescribe( ) throws Exception
    {
        File archivo = new File( "factura_temporal.txt" );
        PrintWriter writer = new PrintWriter( archivo );
        writer.println( "contenido viejo que es mas largo que la factura de un pedido vacio ............" );
        writer.close( );

        pedido.guardarFactura( archivo );

        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivo ) );
        archivo.delete( );
    }

    @Test
    void testGuardarFacturaEnRutaInvalida( )
    {
        File archivo = new File( "carpeta_que_no_existe/factura.txt" );
        assertThrows( FileNotFoundException.class, ( ) -> pedido.guardarFactura( archivo ) );
        assertFalse( archivo.exists( ) );
    }
}
