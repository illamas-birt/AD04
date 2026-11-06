package eus.birt.dam;

import eus.birt.dam.entidades.Actividad;
import eus.birt.dam.util.JPAUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static final Scanner sc = new Scanner(System.in);
	private static final Servicios servicios = new Servicios();

	public static void main(String[] args) {

		int opcion;

		do {
			System.out.println("=== ACTIVA · Gestión de actividades ===\n");
			System.out.println("1. Crear monitor");
			System.out.println("2. Crear actividad");
			System.out.println("3. Buscar actividades disponibles");
			System.out.println("4. Crear cliente");
			System.out.println("5. Crear reserva");
			System.out.println("6. Cancelar reserva");
			System.out.println("7. Listar actividades");
			System.out.println("8. Salir");
			System.out.print("\nSelecciona una opción: ");
			opcion = leerEntero();
			try {
				switch (opcion) {
				case 1:
					crearMonitor();
					break;
				case 2:
					crearActividad();
					break;
				case 3:
					buscarActividades();
					break;
				case 4:
					crearCliente();
					break;
				case 5:
					crearReserva();
					break;
				case 6:
					cancelarReserva();
					break;
				case 7:
					listarActividades();
					break;
				case 8:
					System.out.println("Saliendo de ACTIVA...");
					break;
				default:
					System.out.println("Opción no válida.");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
			
			if (opcion != 8) {
			    System.out.println("\nPresiona ENTER para continuar...");
			    sc.nextLine();
			}

		} while (opcion != 8);

		JPAUtil.cerrar();
		sc.close();
	}

	private static void crearMonitor() {
		System.out.print("Nombre del monitor: ");
		String nombre = sc.nextLine();

		System.out.print("Especialidad: ");
		String especialidad = sc.nextLine();

		Long id = servicios.crearMonitor(nombre, especialidad);
		System.out.println("Monitor creado correctamente. ID: " + id);
	}

	private static void crearActividad() {
		System.out.print("Nombre de la actividad: ");
		String nombre = sc.nextLine();

		System.out.print("Localidad: ");
		String localidad = sc.nextLine();

		System.out.print("Fecha y hora (dd/MM/yyyy HH:mm): ");
		LocalDateTime fecha = LocalDateTime.parse(sc.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

		System.out.print("Número de plazas: ");
		int plazas = leerEntero();

		System.out.print("ID del monitor: ");
		Long idMonitor = leerLong();

		servicios.crearActividad(nombre, localidad, fecha, plazas, idMonitor);
		System.out.println("Actividad creada correctamente.");
	}

	private static void buscarActividades() {
		System.out.print("Localidad: ");
		String localidad = sc.nextLine();

		System.out.print("Fecha (dd/MM/yyyy): ");
		LocalDate fecha = LocalDate.parse(
			    sc.nextLine(),
			    DateTimeFormatter.ofPattern("dd/MM/yyyy")
			);

		System.out.print("Mínimo de plazas disponibles: ");
		int plazas = leerEntero();

		List<Actividad> actividades = servicios.buscarActividades(localidad, fecha, plazas);
		mostrarActividades(actividades);
	}

	private static void crearCliente() {
		System.out.print("Nombre del cliente: ");
		String nombre = sc.nextLine();

		System.out.print("Email: ");
		String email = sc.nextLine();

		Long id = servicios.crearCliente(nombre, email);
		System.out.println("Cliente creado correctamente. ID: " + id);
	}

	private static void crearReserva() {
		System.out.print("ID del cliente: ");
		Long idCliente = leerLong();

		System.out.print("ID de la actividad: ");
		Long idActividad = leerLong();

		System.out.print("Número de plazas: ");
		int plazas = leerEntero();

		System.out.println(servicios.crearReserva(idCliente, idActividad, plazas));
	}

	private static void cancelarReserva() {
		System.out.print("ID de la reserva: ");
		Long idReserva = leerLong();

		System.out.println(servicios.cancelarReserva(idReserva));
	}

	private static void listarActividades() {
		List<Actividad> actividades = servicios.listarActividades();
		mostrarActividades(actividades);
	}

	private static void mostrarActividades(List<Actividad> actividades) {
		if (actividades.isEmpty()) {
			System.out.println("No se encontraron actividades.");
			return;
		}

		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		for (Actividad a : actividades) {
			System.out.println("\nID: " + a.getId());
			System.out.println("Actividad: " + a.getNombre());
			System.out.println("Localidad: " + a.getLocalidad());
			System.out.println("Fecha: " + a.getFechaHora().format(formato));
			System.out.println("Monitor: " + a.getMonitor().getNombre());
			System.out.println("Plazas: " + a.getPlazasDisponibles() + "/" + a.getPlazasTotales());
		}
	}

	private static int leerEntero() {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.print("Introduce un número válido: ");
			}
		}
	}

	private static Long leerLong() {
		while (true) {
			try {
				return Long.parseLong(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.print("Introduce un ID válido: ");
			}
		}
	}
}
