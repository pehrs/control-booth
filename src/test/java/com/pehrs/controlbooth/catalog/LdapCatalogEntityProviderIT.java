package com.pehrs.controlbooth.catalog;

import java.util.Base64;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.junit.jupiter.api.Test;

class LdapCatalogEntityProviderIT {

  @Test
  public void test() throws NamingException {

    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, "ldap://localhost:389");
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=control-booth,dc=org");
    env.put(Context.SECURITY_CREDENTIALS, "admin");
    // env.put("java.naming.ldap.attributes.binary", "jpegPhoto");

    DirContext ctx = new InitialDirContext(env);

    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//    searchControls.setReturningAttributes(new String[]{
//        "uid",
//        "cn",
//        "displayName",
//        "memberUid",
//        "objectClass",
//        "jpegPhoto"
//    });
    searchControls.setCountLimit(10000);
    NamingEnumeration<SearchResult> namingEnumeration =
        ctx.search("dc=control-booth,dc=org", "(objectClass=*)", searchControls);
    while (namingEnumeration.hasMoreElements()) {
      SearchResult sr = namingEnumeration.next();
      System.out.println("=====================");
      System.out.println("dn: " + sr.getNameInNamespace());
      System.out.println("name: " + sr.getName());
      System.out.println("attributes: " + sr.getAttributes());
      System.out.println("objectClass: " + sr.getAttributes().get("objectClass"));
      System.out.println("memberUid: " + sr.getAttributes().get("memberUid"));

      Attribute members = sr.getAttributes().get("memberUid");
      if (members != null) {
        NamingEnumeration<String> memberEnumeration = (NamingEnumeration<String>) members.getAll();
        while (memberEnumeration.hasMoreElements()) {
          String memberUid = memberEnumeration.next();
          System.out.println("  member: " + memberUid);
        }
      }

      Attribute jpegPhoto = sr.getAttributes().get("jpegPhoto");
      if (jpegPhoto != null) {
        byte[] jpegBytes = (byte[]) jpegPhoto.get();
        byte[] encoded = Base64.getEncoder().encode(jpegBytes);
        String b64 = new String(encoded);
        System.out.println("jpeg: " + b64);
      }

    }

    ctx.close();

  }

}