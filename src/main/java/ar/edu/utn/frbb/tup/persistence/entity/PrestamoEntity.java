package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.PlanPago;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoEntity extends BaseEntity {
    private final long id;
    private final long numeroCliente;
    private final double montoSolicitado;
    private final double monto; // monto con interes
    private final String tipoMoneda;
    private final int plazoMeses;
    private String estado;
    private double saldoRestante;
    private int pagosRealizados;
    private final List<PlanPago> planPagos = new ArrayList<>();

    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId());
        this.id = prestamo.getId();
        this.numeroCliente = prestamo.getDniTitular();
        this.montoSolicitado = prestamo.getMontoSolicitado();
        this.monto = prestamo.getMonto();
        this.tipoMoneda = prestamo.getMoneda() != null ? prestamo.getMoneda().getDescripcion() : null;
        this.plazoMeses = prestamo.getPlazoMeses();
        this.estado = prestamo.getLoanStatus() != null ? prestamo.getLoanStatus().getDescripcion() : null;
        if (prestamo.getPlanDePagos() != null && !prestamo.getPlanDePagos().isEmpty()) {
            for (PlanPago pago : prestamo.getPlanDePagos()) {
                this.planPagos.add(pago);
            }
        }
        this.saldoRestante = prestamo.getSaldoRestante();
        this.pagosRealizados = prestamo.getPagosRealizados();
    }

    public Prestamo toPrestamo() {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(this.id);
        prestamo.setDniTitular(this.numeroCliente);
        prestamo.setMontoSolicitado(this.montoSolicitado);
        prestamo.setMonto(this.monto);
        prestamo.setMoneda(TipoMoneda.fromString(this.tipoMoneda));
        prestamo.setPlazoMeses(this.plazoMeses);
        prestamo.setLoanStatus(LoanStatus.fromString(this.estado));
        prestamo.setPlanDePagos(this.planPagos);
        prestamo.setPagosRealizados(this.pagosRealizados);
        prestamo.setSaldoRestante(this.saldoRestante);
        return prestamo;
    }

    //getters y setters
    public long getNumeroCliente() {
        return numeroCliente;
    }

    public double getMonto() {
        return monto;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<PlanPago> getPlanPagos() {
        return planPagos;
    }

    public double getSaldoRestante() {
        return saldoRestante;
    }
    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public int getPagosRealizados() {
        return pagosRealizados;
    }
    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }
}
