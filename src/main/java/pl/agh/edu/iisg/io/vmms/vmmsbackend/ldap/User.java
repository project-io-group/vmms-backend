package pl.agh.edu.iisg.io.vmms.vmmsbackend.ldap;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(
        base = "cn=pcoip-manager-users,cn=groups,cn=accounts,dc=dev,dc=iisg,dc=agh,dc=edu,dc=pl",
        objectClasses = { "posixAccount" })
@Data
@NoArgsConstructor
public final class User {
    @Id
    private Name id;

    private @Attribute(name = "ipaUniqueID") String uniqueId;
    private @Attribute(name = "cn") String fullName;
    private @Attribute(name = "givenName") String givenName;
    private @Attribute(name = "sn") String lastName;
    private @Attribute(name = "displayName") String displayName;
    private @Attribute(name = "mail") String mail;
    private @Attribute(name = "memberOf") String memberOf;
}
