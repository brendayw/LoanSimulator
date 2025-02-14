package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoCuentaYaExisteException;

import java.util.List;

public interface ClienteService {
    Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException, ClienteMayorDeEdadException;
    void agregarCuenta(Cuenta cuenta, long dniTitular) throws TipoCuentaYaExisteException, ClientNoExisteException;
    void agregarPrestamo(Prestamo prestamo, Long dniTitular) throws ClientNoExisteException;
    Cliente buscarClientePorDni(Long dni) throws ClientNoExisteException;
    List<Cliente> buscarClientes() throws ClientNoExisteException;
    Cliente desactivarCliente(Long dni) throws ClientNoExisteException;





}
