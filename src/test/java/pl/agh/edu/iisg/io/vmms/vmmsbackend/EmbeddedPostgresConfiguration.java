package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.vibur.dbcp.ViburDBCPDataSource;
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

        ViburDBCPDataSource ds = new ViburDBCPDataSource();

        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/vmms");
        ds.setUsername("vmms_app");
        ds.setPassword("vmms");

        ds.start();
        return ds;
    }
}
