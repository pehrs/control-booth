package com.pehrs.cb.config;


import java.util.Hashtable;
import javax.naming.Context;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration("ldapConfig")
@Getter
public class LdapConfig {

    @Value("${cb.ldap-provider.hostname}")
    private String ldapHostname;

    @Value("${cb.ldap-provider.port}")
    private int ldapPort;

    @Value("${cb.ldap-provider.admin.cn}")
    private String adminDn;

    @Value("${cb.ldap-provider.admin.password}")
    private String adminPassword;

    @Value("${cb.ldap-provider.search.root}")
    private String searchRoot;

    @Value("${cb.ldap-provider.search.limit:10000}")
    private int ldapSearchLimit;

    public @NonNull Hashtable<String, String> getLdapAdminEnv() {
        return getLdapEnv(adminDn, adminPassword);
    }

    public @NonNull Hashtable<String, String> getLdapEnv(String userDn, String password) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,
            String.format("ldap://%s:%d", ldapHostname, ldapPort));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        return env;
    }

}
