package pl.agh.edu.iisg.io.vmms.vmmsbackend;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.sun.deploy.security.CertificateHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@PropertySource("ldap.properties")
public class LdapConfiguration {

    @Autowired
    Environment env;

    @Bean
    public LdapContextSource contextSource() {
        System.setProperty("javax.net.ssl.keyStore", env.getRequiredProperty("jks.file"));
        System.setProperty("javax.net.ssl.trustStore", env.getRequiredProperty("jks.file"));
        System.setProperty("javax.net.ssl.trustAnchors", env.getRequiredProperty("jks.file"));
        System.setProperty("javax.net.ssl.trustStorePassword", env.getRequiredProperty("jks.password"));
        System.setProperty("javax.net.ssl.keyStorePassword", env.getRequiredProperty("jks.password"));
        DefaultTlsDirContextAuthenticationStrategy strategy= new DefaultTlsDirContextAuthenticationStrategy();
        strategy.setHostnameVerifier(new CertificateHostnameVerifier());
        LdapContextSource contextSource= new LdapContextSource();
        String[] ldaps = getLdapHostNames();
        if(! env.getRequiredProperty("ldap.protocol").equals("ldaps")){
            contextSource.setAuthenticationStrategy(strategy);
        }
        contextSource.setUrls(ldaps);
        contextSource.setUserDn(env.getRequiredProperty("spring.ldap.dn"));
        contextSource.setPassword(env.getRequiredProperty("spring.ldap.password"));
        contextSource.setPooled(false);
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    private String[] getLdapHostNames(){
        DnsSrvResolver resolver = DnsSrvResolvers.newBuilder()
                .dnsLookupTimeoutMillis(3000)
                .retainingDataOnFailures(true)
                .build();
        String ldap = "ldap";
        String port = env.getRequiredProperty("ldap.startTLS.port");
        if(env.getRequiredProperty("ldap.protocol").equals("ldaps")) {
            ldap = "ldaps";
            port = env.getRequiredProperty("ldap.ldaps.port");
        }
        final String l = ldap; //to be able to use variable in map
        final String p = port;
        return resolver.resolve("_ldap._tcp." + env.getRequiredProperty("ldap.discovery_url")).stream()
                .map(record -> record.host())
                //remove trailing dot which cosed exception
                .map(host -> l +"://" + host.substring(0, host.length()-1) + ":" + p)
                .toArray(String[]::new);
    }

    @Bean
    public LdapTemplate ldapTemplate() throws Exception{
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource());
        ldapTemplate.afterPropertiesSet();
        return ldapTemplate;
    }
}
