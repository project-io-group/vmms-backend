package pl.agh.edu.iisg.io.vmms.vmmsbackend.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class UserLdapService {

    private LdapTemplate ldapTemplate;
    private Environment env;

    @Autowired
    public UserLdapService(LdapTemplate ldapTemplate, Environment env){
        this.ldapTemplate = ldapTemplate;
        this.env = env;
    }

    public List<User> getUsers() {
        String filter = env.getRequiredProperty("spring.ldap.user.filter");
        return ldapTemplate.find(query().base(env.getRequiredProperty("spring.ldap.user.base"))
                .searchScope(SearchScope.ONELEVEL).filter(filter), User.class);
    }

    public List<User> getAdmins() {
        String filter = env.getRequiredProperty("spring.ldap.admin.filter");
        return ldapTemplate
                .find(query().base(env.getRequiredProperty("spring.ldap.admin.base"))
                .searchScope(SearchScope.ONELEVEL).filter(filter), User.class);
    }


}
