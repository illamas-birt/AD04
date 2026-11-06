package eus.birt.dam.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("dbactiva");

    public static EntityManagerFactory getEntityManagerFactory() {
        return FACTORY;
    }

    public static void cerrar() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
