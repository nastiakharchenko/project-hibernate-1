package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "*");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        List<Player> resultList = null;
        try(Session session = sessionFactory.openSession()){
            NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM player", Player.class);
            query.setFirstResult(pageNumber);
            query.setMaxResults(pageSize);
            resultList = query.list();
        }
        return resultList;
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
            Query<Long> query = session.createNamedQuery("COUNT_PLAYERS", Long.class);
            return query.uniqueResult().intValue();
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.saveOrUpdate(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> result = null;
        try(Session session = sessionFactory.openSession()){
            result = Optional.ofNullable(session.get(Player.class, id));
        }
        return result;
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.delete(player);
            session.getTransaction().commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}