package edu.school21;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.db.DbConfig;
import edu.school21.entities.Company;
import edu.school21.entities.User;
import edu.school21.manager.OrmManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            DbConfig dbConfig = new DbConfig();

            HikariConfig hikariConfig = configureHikari(dbConfig);

            try (HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
                 OrmManager ormManager = new OrmManager(hikariDataSource, "edu.school21.entities")) {

                User firstUser = new User(1L, "Ivan", "Ivanov", 33);
                User secondUser = new User(2L, "Sava", "Panov", 26);

                Company firstCompany = new Company(1L, "Sber", 20000);
                Company secondCompany = new Company(2L, "CFT", 6000);

                ormManager.begin();

                ormManager.save(firstUser);
                ormManager.save(secondUser);
                ormManager.save(firstCompany);
                ormManager.save(secondCompany);

                firstUser.setAge(150);
                ormManager.update(firstUser);

                secondCompany.setCompanyName("VTB");
                ormManager.update(secondCompany);

                User foundUser = ormManager.findById(1L, User.class);

                if (foundUser != null) {
                    log.info("Founded entity: {}", foundUser);
                } else {
                    log.info("The object not found!");
                }

                ormManager.commit();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static HikariConfig configureHikari(DbConfig dbConfig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbConfig.getUrl());
        hikariConfig.setUsername(dbConfig.getUsername());
        hikariConfig.setPassword(dbConfig.getPassword());
        return hikariConfig;
    }
}
