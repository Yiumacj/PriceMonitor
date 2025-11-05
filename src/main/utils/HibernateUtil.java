package main.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(String url, String user, String pass) {
        if (sessionFactory == null) {
            try {
                Configuration cfg = new Configuration().configure();
                cfg.setProperty("hibernate.connection.url", url);
                cfg.setProperty("hibernate.connection.username", user);
                cfg.setProperty("hibernate.connection.password", pass);

                sessionFactory = cfg.buildSessionFactory();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка инициализации Hibernate", e);
            }
        }
        return sessionFactory;
    }
}
