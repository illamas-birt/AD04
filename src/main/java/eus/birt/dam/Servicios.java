package eus.birt.dam;

import eus.birt.dam.entidades.*;
import eus.birt.dam.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Servicios {

	private void validarTexto(String texto, String campo) {
		if (texto == null || texto.trim().isEmpty()) {
			throw new IllegalArgumentException(campo + " no puede estar vacío.");
		}
	}

	// 1. Crear monitor
	public Long crearMonitor(String nombre, String especialidad) {
		validarTexto(nombre, "El nombre");
		validarTexto(especialidad, "La especialidad");
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tr = em.getTransaction();
		try {
			tr.begin();
			Monitor monitor = new Monitor(nombre.trim(), especialidad.trim());
			em.persist(monitor);
			tr.commit();
			return monitor.getId();
		} catch (RuntimeException e) {
			if (tr.isActive())
				tr.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	// 2. Crear actividad
	public void crearActividad(String nombre, String localidad, LocalDateTime fecha, int plazas, Long idMonitor) {
		validarTexto(nombre, "El nombre");
		validarTexto(localidad, "La localidad");
		if (fecha == null) {
			throw new IllegalArgumentException("La fecha es obligatoria.");
		}
		if (plazas <= 0) {
			throw new IllegalArgumentException("Las plazas deben ser positivas.");
		}

		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tr = em.getTransaction();
		try {
			tr.begin();
			Monitor monitor = em.find(Monitor.class, idMonitor);
			if (monitor == null) {
				throw new IllegalArgumentException("No existe ese monitor.");
			}

			Actividad actividad = new Actividad(nombre.trim(), localidad.trim(), fecha, plazas, monitor);
			em.persist(actividad);
			tr.commit();
		} catch (RuntimeException e) {
			if (tr.isActive())
				tr.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	// 3. Buscar actividades disponibles
	public List<Actividad> buscarActividades(String localidad, LocalDate fecha, int plazasMinimas) {
		validarTexto(localidad, "La localidad");
		if (fecha == null || plazasMinimas <= 0) {
			throw new IllegalArgumentException("La fecha y el número de plazas deben ser válidos.");
		}

		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		try {
			LocalDateTime inicio = fecha.atStartOfDay();
			LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
			return em
					.createQuery("SELECT a FROM Actividad a JOIN FETCH a.monitor "
							+ "WHERE LOWER(a.localidad) = LOWER(:localidad) " + "AND a.fechaHora >= :inicio "
							+ "AND a.fechaHora < :fin " + "AND a.plazasDisponibles >= :plazas "
							+ "ORDER BY a.fechaHora", Actividad.class)
					.setParameter("localidad", localidad.trim()).setParameter("inicio", inicio).setParameter("fin", fin)
					.setParameter("plazas", plazasMinimas).getResultList();
		} finally {
			em.close();
		}
	}

	// 4. Crear cliente
	public Long crearCliente(String nombre, String email) {
		validarTexto(nombre, "El nombre");
		validarTexto(email, "El email");
		email = email.trim();
		if (!email.contains("@")
				|| !email.substring(email.indexOf("@")).contains(".")
				|| email.startsWith("@")
				|| email.endsWith(".")) {
			throw new IllegalArgumentException("El formato del email no es válido.");
		}

		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tr = em.getTransaction();

		try {
			tr.begin();
			Long existentes = em
					.createQuery("SELECT COUNT(c) FROM Cliente c " + "WHERE LOWER(c.email) = LOWER(:email)", Long.class)
					.setParameter("email", email).getSingleResult();
			if (existentes > 0) {
				throw new IllegalArgumentException("Ese email ya está registrado.");
			}

			Cliente cliente = new Cliente(nombre.trim(), email);
			em.persist(cliente);
			tr.commit();
			return cliente.getId();
		} catch (RuntimeException e) {
			if (tr.isActive())
				tr.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	// 5. Crear reserva
	public String crearReserva(Long idCliente, Long idActividad, int plazas) {
		if (idCliente == null || idActividad == null || idCliente <= 0 || idActividad <= 0 || plazas <= 0) {
			throw new IllegalArgumentException("Los IDs y las plazas deben ser válidos.");
		}
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tr = em.getTransaction();
		try {
			tr.begin();
			Cliente cliente = em.find(Cliente.class, idCliente);
			Actividad actividad = em.find(Actividad.class, idActividad);

			if (cliente == null || actividad == null) {
				throw new IllegalArgumentException("Cliente o actividad inexistente.");
			}
			if (actividad.getPlazasDisponibles() < plazas) {
				throw new IllegalArgumentException("No hay suficientes plazas.");
			}

			Reserva reserva = new Reserva(LocalDateTime.now(), plazas, actividad, cliente);
			actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - plazas);
			em.persist(reserva);
			tr.commit();
			return "Reserva ID: " + reserva.getId() + "\nPlazas restantes: " + actividad.getPlazasDisponibles();
		} catch (RuntimeException e) {
			if (tr.isActive())
				tr.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	// 6. Cancelar reserva
	public String cancelarReserva(Long idReserva) {
		if (idReserva == null || idReserva <= 0) {
			throw new IllegalArgumentException("ID de reserva no válido.");
		}
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tr = em.getTransaction();
		try {
			tr.begin();
			Reserva reserva = em.find(Reserva.class, idReserva);
			if (reserva == null) {
				throw new IllegalArgumentException("No existe esa reserva.");
			}
			Actividad actividad = reserva.getActividad();
			int recuperadas = reserva.getNumeroPlazas();
			actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() + recuperadas);
			em.remove(reserva);
			tr.commit();
			return "Plazas recuperadas: " + recuperadas + "\nPlazas disponibles: " + actividad.getPlazasDisponibles();
		} catch (RuntimeException e) {
			if (tr.isActive())
				tr.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	// 7. Listar todas las actividades
	public List<Actividad> listarActividades() {
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		try {
			return em.createQuery("SELECT a FROM Actividad a JOIN FETCH a.monitor " + "ORDER BY a.fechaHora",
					Actividad.class).getResultList();
		} finally {
			em.close();
		}
	}
}