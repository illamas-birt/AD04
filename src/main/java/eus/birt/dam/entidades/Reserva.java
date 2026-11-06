package eus.birt.dam.entidades;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fecha_reserva", nullable = false)
	private LocalDateTime fechaReserva;

	@Column(name = "numero_plazas", nullable = false)
	private int numeroPlazas;

	@ManyToOne
	@JoinColumn(name = "actividad_id", nullable = false)
	private Actividad actividad;

	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	public Reserva() {
	}

	public Reserva(LocalDateTime fechaReserva, int numeroPlazas, Actividad actividad, Cliente cliente) {
		this.fechaReserva = fechaReserva;
		this.numeroPlazas = numeroPlazas;
		this.actividad = actividad;
		this.cliente = cliente;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getFechaReserva() {
		return fechaReserva;
	}

	public int getNumeroPlazas() {
		return numeroPlazas;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public Cliente getCliente() {
		return cliente;
	}
}