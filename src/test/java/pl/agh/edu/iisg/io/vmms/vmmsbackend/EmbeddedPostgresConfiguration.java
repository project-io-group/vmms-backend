package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class EmbeddedPostgresConfiguration {

    @Bean(destroyMethod = "stop")
    public PostgresProcess postgresProcess() throws IOException {

        PostgresConfig postgresConfig = new PostgresConfig(
                Version.V9_6_8,
                new AbstractPostgresConfig.Net("localhost", 5432),
                new AbstractPostgresConfig.Storage("vmms"),
                new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("vmms_app", "vmms")
        );

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        PostgresExecutable exec = runtime.prepare(postgresConfig);
        PostgresProcess process = exec.start();

        return process;
    }

    @Bean(destroyMethod = "close")
    @DependsOn("postgresProcess")
    DataSource dataSource(PostgresProcess postgresProcess) {

        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        PostgresConfig postgresConfig = postgresProcess.getConfig();
        dataSource.setUser(postgresConfig.credentials().username());
        dataSource.setPassword(postgresConfig.credentials().password());
        dataSource.setPortNumber(postgresConfig.net().port());
        dataSource.setServerName(postgresConfig.net().host());
        dataSource.setDatabaseName(postgresConfig.storage().dbName());

        return dataSource;
    }
}
