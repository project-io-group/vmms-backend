package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.vibur.dbcp.ViburDBCPDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;

@Profile("dev")
@Configuration
public class EmbeddedPostgresConfiguration {

    @Value("${spring.datasource.url}")
    private String URL;
    @Value("${spring.datasource.username}")
    private String USERNAME;
    @Value("${spring.datasource.password}")
    private String PASSWORD;

    @Bean(destroyMethod = "stop")
    public PostgresProcess postgresProcess() throws IOException {

        String url = URL.split("//", 2)[1];
        String host = url.split(":", 2)[0];
        int port = Integer.valueOf(url.split(":", 2)[1].split("/", 2)[0]);
        String dbName = url.split(":", 2)[1].split("/", 2)[1];

        PostgresConfig postgresConfig = new PostgresConfig(
                Version.V9_6_8,
                new AbstractPostgresConfig.Net(host, port),
                new AbstractPostgresConfig.Storage(dbName),
                new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials(USERNAME, PASSWORD)
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

        ds.setJdbcUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);

        ds.start();
        return ds;
    }
}
