package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.service.PrestamoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrestamoControllerTest {
    @Mock private PrestamoService prestamoService;
    @Mock private PrestamoDao prestamoDao;
    @Mock private PrestamoValidator prestamoValidator;
    @InjectMocks private PrestamoController prestamoController;

    private PrestamoRespuesta prestamoRespuesta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //metodo para crear prestamo
    private Prestamo crearPrestamo(long dni, double monto, int plazo, int pagos, double saldo) {
        Prestamo prestamo = new Prestamo();
        prestamo.setDniTitular(dni);
        prestamo.setMonto(monto);
        prestamo.setPlazoMeses(plazo);
        prestamo.setPagosRealizados(pagos);
        prestamo.setSaldoRestante(saldo);
        return prestamo;
    }
    private PrestamoDto crearPrestamoDto(long numeroCliente, double monto, String tipoMoneda, int plazo) {
        PrestamoDto prestamo = new PrestamoDto();
        prestamo.setNumeroCliente(numeroCliente);
        prestamo.setMontoPrestamo(monto);
        prestamo.setTipoMoneda(tipoMoneda);
        prestamo.setPlazoMeses(plazo);
        return prestamo;
    }
    //metodo para crear respuesta de un post
    private PrestamoDetalle respuestaPrestamoDetalle(LoanStatus estado, String mensaje, List<PlanPago> plan) {
        PrestamoDetalle detalle = new PrestamoDetalle();
        detalle.setEstado(estado);
        detalle.setMensaje(mensaje);
        detalle.setPlanPagos(plan);
        return detalle;
    }

    //aprueba el prestamo
    @Test
    void testCrearPrestamo_Success() throws ClientNoExisteException, TipoMonedaNoSoportada, CuentaNoExisteException, CreditScoreException, CampoIncorrecto, PrestamoNoExisteException {
        PrestamoDto prestamoNuevo = crearPrestamoDto(40860006, 1000, "D", 12);

        List<PlanPago> planPagos = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            planPagos.add(new PlanPago(i, 167.0));
        }

        PrestamoDetalle prestamoDetalle = respuestaPrestamoDetalle(
                LoanStatus.APROBADO,
                "EL prestamo fue aprobado.",
                planPagos);

        doNothing().when(prestamoValidator).validatePrestamo(prestamoNuevo);
        when(prestamoService.darAltaPrestamo(prestamoNuevo)).thenReturn(prestamoDetalle);

        PrestamoDetalle resultado = prestamoController.crearPrestamo(prestamoNuevo);

        assertNotNull(resultado);
        assertEquals(prestamoDetalle.getEstado(), resultado.getEstado());
        assertEquals(prestamoDetalle.getMensaje(), resultado.getMensaje());
        assertEquals(prestamoDetalle.getPlanPagos(), resultado.getPlanPagos());
        verify(prestamoValidator, times(1)).validatePrestamo(prestamoNuevo);
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoNuevo);
    }

    //monto incorrecto - agregado despues de rendir
    @Test
    void testCrearPrestamo_FailureMonto() throws CampoIncorrecto, CreditScoreException, CuentaNoExisteException, ClientNoExisteException, TipoMonedaNoSoportada, PrestamoNoExisteException{
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setMontoPrestamo(0.0);

        doNothing().when(prestamoValidator).validatePrestamo(prestamoDto);
        when(prestamoService.darAltaPrestamo(prestamoDto)).thenThrow(new CampoIncorrecto("El monto del prestamo no puede ser 0 o nulo."));

        CampoIncorrecto e = assertThrows(CampoIncorrecto.class, () -> {
            prestamoController.crearPrestamo(prestamoDto);
        });

        assertEquals("El monto del prestamo no puede ser 0 o nulo.",e.getMessage());
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoDto);

    }

    //plazo meses - agregado despues de rendir
    @Test
    void testCrearPrestamo_FailureMeses() throws CampoIncorrecto, CreditScoreException, CuentaNoExisteException, ClientNoExisteException, TipoMonedaNoSoportada, PrestamoNoExisteException{
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setPlazoMeses(0);
        doNothing().when(prestamoValidator).validatePrestamo(prestamoDto);
        when(prestamoService.darAltaPrestamo(prestamoDto)).thenThrow(new CampoIncorrecto("El plazo no puede ser 0 o nulo."));
        CampoIncorrecto e = assertThrows(CampoIncorrecto.class, () -> {
            prestamoController.crearPrestamo(prestamoDto);
        });
        assertEquals("El plazo no puede ser 0 o nulo.",e.getMessage());
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoDto);
    }

    //tipo moneda - agregado despues de rendir
    @Test
    void testCrearPrestamo_FailureMoneda() throws CampoIncorrecto, CreditScoreException, CuentaNoExisteException, ClientNoExisteException, TipoMonedaNoSoportada, PrestamoNoExisteException{
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setTipoMoneda("EUR");
        doNothing().when(prestamoValidator).validatePrestamo(prestamoDto);
        when(prestamoService.darAltaPrestamo(prestamoDto)).thenThrow(new CampoIncorrecto("El tipo de moneda no puede ser nulo."));
        CampoIncorrecto e = assertThrows(CampoIncorrecto.class, () -> {
            prestamoController.crearPrestamo(prestamoDto);
        });
        assertEquals("El tipo de moneda no puede ser nulo.",e.getMessage());
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoDto);
    }

    //falla porque no encuentra el cliente - agregado despues de rendir
    @Test
    void testCrearPrestamo_FailureClienteNoExiste() throws CreditScoreException, ClientNoExisteException, CampoIncorrecto, TipoMonedaNoSoportada, PrestamoNoExisteException, CuentaNoExisteException {
        PrestamoDto prestamoDto = new PrestamoDto();

        doNothing().when(prestamoValidator).validatePrestamo(prestamoDto);
        when(prestamoService.darAltaPrestamo(prestamoDto)).thenThrow(new ClientNoExisteException("El cliente no existe."));

        ClientNoExisteException e = assertThrows(ClientNoExisteException.class, () -> prestamoController.crearPrestamo(prestamoDto));

        assertEquals("El cliente no existe.", e.getMessage());
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoDto);
    }

    //falla score credit insuficiente
    @Test
    void testCrearPrestamo_CreditScoreInsuficiente() throws CreditScoreException, ClientNoExisteException, TipoMonedaNoSoportada, CuentaNoExisteException, CampoIncorrecto, PrestamoNoExisteException {
        PrestamoDto prestamoNuevo = crearPrestamoDto(40860006, 1000, "D", 12);

        doThrow(new CreditScoreException("El préstamo ha sido rechazado debido a una calificación crediticia insuficiente"))
                .when(prestamoValidator).validatePrestamo(prestamoNuevo);

        CreditScoreException e = assertThrows(CreditScoreException.class, () -> {
            prestamoController.crearPrestamo(prestamoNuevo);
        });
        assertEquals("El préstamo ha sido rechazado debido a una calificación crediticia insuficiente", e.getMessage());

        verify(prestamoService, never()).darAltaPrestamo(any(PrestamoDto.class));
        verify(prestamoValidator, times(1)).validatePrestamo(prestamoNuevo);
    }

    //obtener prestamo por id
    @Test
    void testObtenerPrestamoPorId_Success() throws PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);

        when(prestamoService.buscarPrestamoPorId(id)).thenReturn(prestamo);
        Prestamo resultado = prestamoController.obtenerPrestamoPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(prestamoService, times(1)).buscarPrestamoPorId(id);
    }

    @Test
    void testObtenerPrestamoPorId_Failure() throws PrestamoNoExisteException {
        long id = 123456789;
        doThrow(new PrestamoNoExisteException("El préstamo con ID " + id + " no existe.")).when(prestamoService).buscarPrestamoPorId(id);

        PrestamoNoExisteException thrown = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoController.obtenerPrestamoPorId(id);
        });

        assertEquals("El préstamo con ID " + id + " no existe.", thrown.getMessage());
        verify(prestamoService, times(1)).buscarPrestamoPorId(id);
    }

    @Test
    void testObtenerPrestamosPorCliente_Success() throws ClientNoExisteException, PrestamoNoExisteException {
        long dni = 123456789L;
        prestamoRespuesta = new PrestamoRespuesta();
        prestamoRespuesta.setPrestamoResume(Collections.singletonList(new PrestamoResume()));

        when(prestamoService.prestamosPorCliente(dni)).thenReturn(prestamoRespuesta);

        PrestamoRespuesta respuesta = prestamoController.obtenerPrestamosPorCliente(dni);

        assertNotNull(respuesta);
        assertFalse(respuesta.getPrestamoResume().isEmpty());

        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    @Test
    void testObtenerPrestamosPorCliente_FailureSinPrestamos() throws ClientNoExisteException, PrestamoNoExisteException {
        long dni = 123456789L;
        prestamoRespuesta = new PrestamoRespuesta();
        prestamoRespuesta.setPrestamoResume(Collections.emptyList());

        when(prestamoService.prestamosPorCliente(dni)).thenReturn(prestamoRespuesta);

        PrestamoNoExisteException thrown = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoController.obtenerPrestamosPorCliente(dni);
        });

        assertEquals("El cliente no tiene préstamos.", thrown.getMessage());

        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    @Test
    void testObtenerPrestamosPorCliente_FailureClienteNoExiste() throws ClientNoExisteException, PrestamoNoExisteException {
        long dni = 123456789L;

        when(prestamoService.prestamosPorCliente(dni)).thenThrow(new ClientNoExisteException("El cliente no existe."));

        ClientNoExisteException thrown = assertThrows(ClientNoExisteException.class, () -> {
            prestamoController.obtenerPrestamosPorCliente(dni);
        });

        assertEquals("El cliente no existe.", thrown.getMessage());
        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    @Test
    void testObtenerPrestamosPorCliente_PrestamoRespuestaNull() throws ClientNoExisteException, PrestamoNoExisteException {
        long dni = 123456789L;

        when(prestamoService.prestamosPorCliente(dni)).thenReturn(null);

        PrestamoNoExisteException thrown = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoController.obtenerPrestamosPorCliente(dni);
        });

        assertEquals("El cliente no tiene préstamos.", thrown.getMessage());
        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    @Test
    void testObtenerPrestamosPorCliente_PrestamoResumeNull() throws ClientNoExisteException, PrestamoNoExisteException {
        long dni = 123456789L;
        prestamoRespuesta = new PrestamoRespuesta();
        prestamoRespuesta.setPrestamoResume(null);

        when(prestamoService.prestamosPorCliente(dni)).thenReturn(prestamoRespuesta);

        PrestamoNoExisteException thrown = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoController.obtenerPrestamosPorCliente(dni);
        });

        assertEquals("El cliente no tiene préstamos.", thrown.getMessage());

        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }


    //obtener todos los prestamos
    @Test
    void testBuscarPrestamos_Success() throws PrestamoNoExisteException {
        Prestamo prestamo1 = new Prestamo();
        Prestamo prestamo2 = new Prestamo();

        prestamo1.setDniTitular(40860006);
        prestamo2.setDniTitular(14533778);
        List<Prestamo> prestamos = List.of(prestamo1, prestamo2);

        when(prestamoService.buscarPrestamos()).thenReturn(prestamos);
        List<Prestamo> resultado = prestamoController.obtenerPrestamos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prestamo1));
        assertTrue(resultado.contains(prestamo2));
        verify(prestamoService, times(1)).buscarPrestamos();
    }

    //agregado despues de rendir
    //manejo de excepcion correcto
    @Test
    void testBuscarPrestamos_Failure() throws PrestamoNoExisteException {
        when(prestamoService.buscarPrestamos()).thenThrow(new PrestamoNoExisteException("No hay préstamos registrados."));

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoController.obtenerPrestamos());
        assertEquals("No hay préstamos registrados.", e.getMessage());
        verify(prestamoService, times(1)).buscarPrestamos();

    }

    //agregado despues de rendir
    //cliente no existe / no encontrado
    @Test
    void testBuscarPrestamos_FailureCliente() throws PrestamoNoExisteException, ClientNoExisteException {
       long dni = 123L;
       when(prestamoService.prestamosPorCliente(dni)).thenThrow(new ClientNoExisteException("El cliente no existe."));

       ClientNoExisteException e = assertThrows(ClientNoExisteException.class, () -> prestamoController.obtenerPrestamosPorCliente(dni));

       assertEquals("El cliente no existe.", e.getMessage());
       verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    //agregado despues de rendir
    //base de datos (memoria) vacio
    @Test
    void testBuscarPrestamos_FailureNoHayPrestamos() throws PrestamoNoExisteException {
        when(prestamoDao.findAll()).thenReturn(Collections.emptyList());

        when(prestamoService.buscarPrestamos()).thenThrow(new PrestamoNoExisteException("No se encontraron préstamos."));

        PrestamoNoExisteException exception = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoService.buscarPrestamos();
        });

        assertEquals("No se encontraron préstamos.", exception.getMessage());
    }

    //pagar cuota del prestamo
    @Test
    void testPagarCuota_Success() throws ClientNoExisteException, CuentaNoExisteException, PrestamoNoExisteException {
        long idPrestamo = 12345L;
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setMontoPrestamo(5000.0);

        PrestamoRespuesta prestamoRespuestaEsperada = new PrestamoRespuesta();
        List<PrestamoResume> prestamosResumen = new ArrayList<>();

        PrestamoResume prestamoResume = new PrestamoResume(15000.0, 12, 6, 10000.0);
        prestamosResumen.add(prestamoResume);
        prestamoRespuestaEsperada.setPrestamoResume(prestamosResumen);

        when(prestamoService.pagarCuota(prestamoDto, idPrestamo)).thenReturn(prestamoRespuestaEsperada);

        PrestamoRespuesta resultado = prestamoController.pagarCuota(idPrestamo, prestamoDto);

        assertNotNull(resultado, "La respuesta no debe ser nula.");
        assertEquals(1, resultado.getPrestamoResume().size());
        assertEquals(prestamoResume.getMonto(), resultado.getPrestamoResume().get(0).getMonto());
        assertEquals(prestamoResume.getSaldoRestante(), resultado.getPrestamoResume().get(0).getSaldoRestante());

        verify(prestamoService, times(1)).pagarCuota(prestamoDto, idPrestamo);
    }

    //error al pagar la cuota porque no encuentra el prestamo
    @Test
    void testPagarCuota_Error() throws ClientNoExisteException, CuentaNoExisteException, PrestamoNoExisteException {
        long id = 12345L;
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setMontoPrestamo(5000.0);

        when(prestamoService.pagarCuota(prestamoDto, id))
                .thenThrow(new PrestamoNoExisteException("El prestamo no existe."));

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoController.pagarCuota(id, prestamoDto));
        assertEquals("El prestamo no existe.", e.getMessage());
        verify(prestamoService, times(1)).pagarCuota(prestamoDto, id);
    }

    //error al procesar el pago
    @Test
    void testPagaCuota_FailureProcesarPago() throws CuentaNoExisteException, ClientNoExisteException, PrestamoNoExisteException{
        long prestamoId = 123456L;
        PrestamoDto prestamoDto = new PrestamoDto();
        when(prestamoService.pagarCuota(prestamoDto, prestamoId)). thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            prestamoController.pagarCuota(prestamoId, prestamoDto);  // Llamada al controlador
        });

        assertEquals("Error al procesar el pago del préstamo.", exception.getMessage());
    }


    //cerrar prestamo
    @Test
    void testCerrarPrestamo_Success() throws CampoIncorrecto, PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        when(prestamoService.cerrarPrestamo(id)).thenReturn(prestamo);
        Prestamo resultado = prestamoController.cerrarPrestamo(id);
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(prestamoService, times(1)).cerrarPrestamo(id);
    }

    //no encontro el prestamo por id
    //agreguado despues rendir
    @Test
    void testCerrarPrestamo_Failure() throws PrestamoNoExisteException {
        long id = 123L;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);

        when(prestamoService.buscarPrestamoPorId(id)).thenThrow(new PrestamoNoExisteException("El prestamo con ID " + id + " no existe."));

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoController.obtenerPrestamoPorId(id));
        assertEquals("El prestamo con ID " + id + " no existe.", e.getMessage());
        verify(prestamoService, times(1)).buscarPrestamoPorId(id);
    }

}
