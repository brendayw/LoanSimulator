package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;
import ar.edu.utn.frbb.tup.service.CuentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuenta")
public class CuentaController {
    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    //crea cuenta
    @PostMapping
    public Cuenta crearCuenta(@RequestBody CuentaDto cuentaDto) throws CuentaYaExisteException, CampoIncorrecto, TipoCuentaYaExisteException, ClientNoExisteException, CuentaNoSoportadaException, TipoMonedaNoSoportada {
        cuentaValidator.validateCuenta(cuentaDto);
        return cuentaService.darDeAltaCuenta(cuentaDto);
    }

    //obtiene todas las cuentas
    @GetMapping
    public List<Cuenta> buscarCuentas() throws CuentaNoExisteException {
        return cuentaService.buscarCuentas();
    }

    //busca cuenta por numero de cuenta
    @GetMapping("/{id}")
    public Cuenta obtenerCuentaPorId(@PathVariable long id) throws CuentaNoExisteException {
        return cuentaService.buscarCuentaPorId(id);
    }

    //busca cuenta de cliente por dni de cliente
    @GetMapping("/cliente/{dni}")
    public List<Cuenta> obtenerCuentasPorCliente(@PathVariable long dni) throws ClientNoExisteException, CuentaNoExisteException {
        return cuentaService.buscarCuentaPorCliente(dni);
    }


    //desactiva cuenta
    @DeleteMapping("/{id}")
    public Cuenta desactivarCuenta(@PathVariable long id) throws CuentaNoExisteException {
        return cuentaService.desactivarCuenta(id);
    }
}