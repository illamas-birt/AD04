package eus.birt.dam.entidades;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "actividad")
public class Actividad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String localidad;

	@Column(name = "fecha_hora", nullable = false)
	private LocalDateTime fechaHora;

	@Column(name = "plazas_totales", nullable = false)
	private int plazasTotales;

	@Column(name = "plazas_disponibles", nullable = false)
	private int plazasDisponibles;

	@ManyToOne
	@JoinColumn(name = "monitor_id", nullable = false)
	private Monitor monitor;

	public Actividad() {
	}

	public Actividad(String nombre, String localidad, LocalDateTime fechaHora, int plazasTotales, Monitor monitor) {
		this.nombre = nombre;
		this.localidad = localidad;
		this.fechaHora = fechaHora;
		this.plazasTotales = plazasTotales;
		this.plazasDisponibles = plazasTotales;
		this.monitor = monitor;
		monitor.getActividades().add(this);
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getLocalidad() {
		return localidad;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public int getPlazasTotales() {
		return plazasTotales;
	}

	public int getPlazasDisponibles() {
		return plazasDisponibles;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setPlazasDisponibles(int plazasDisponibles) {
		this.plazasDisponibles = plazasDisponibles;
	}
}