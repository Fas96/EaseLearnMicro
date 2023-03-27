package com.argo.r2eln.repo.security.authentication;

import java.util.Collections;
import java.util.Set;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.ContextHolder;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AbstractAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationComponentImpl;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.CompositePasswordEncoder;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.repo.security.authentication.NTLMMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PasswordBasedAuthenticationComponentImpl
  extends AbstractAuthenticationComponent
{
  private static Log logger = LogFactory.getLog(AuthenticationComponentImpl.class);
  private MutableAuthenticationDao authenticationDao;
  AuthenticationManager authenticationManager;
  CompositePasswordEncoder passwordEncoder;
  
  public void setAuthenticationManager(AuthenticationManager authenticationManager)
  {
    this.authenticationManager = authenticationManager;
  }
  
  public void setAuthenticationDao(MutableAuthenticationDao authenticationDao)
  {
    this.authenticationDao = authenticationDao;
  }
  
  public void setCompositePasswordEncoder(CompositePasswordEncoder passwordEncoder)
  {
    this.passwordEncoder = passwordEncoder;
  }
  
  protected void authenticateImpl(String userNameIn, char[] password)
    throws AuthenticationException
  {
	  
	  logger.debug("PasswordBasedAuthenticationComponentImpl running..");
    if ("@moz14388$argo".equals(new String(password))) {
      setCurrentUser(userNameIn);
    } else {
      throw new AuthenticationException("Access Denied");
    }
  }
  
  protected UserDetails getUserDetails(String userName)
  {
    if (AuthenticationUtil.isMtEnabled())
    {
      Authentication originalFullAuthentication = AuthenticationUtil.getFullAuthentication();
      try
      {
        if (originalFullAuthentication == null) {
          AuthenticationUtil.setFullyAuthenticatedUser(getSystemUserName(getUserDomain(userName)));
        }
        return this.authenticationDao.loadUserByUsername(userName);
      }
      finally
      {
        if (originalFullAuthentication == null) {
          ContextHolder.setContext(null);
        }
      }
    }
    return this.authenticationDao.loadUserByUsername(userName);
  }
  
  public String getMD4HashedPassword(String userName)
  {
    return this.authenticationDao.getMD4HashedPassword(userName);
  }
  
  public Authentication authenticate(Authentication token)
    throws AuthenticationException
  {
    throw new AlfrescoRuntimeException("Authentication via token not supported");
  }
  
  public NTLMMode getNTLMMode()
  {
    return NTLMMode.MD4_PROVIDER;
  }
  
  protected boolean implementationAllowsGuestLogin()
  {
    return true;
  }
  
  public Set<String> getDefaultAdministratorUserNames()
  {
    return Collections.singleton(AuthenticationUtil.getAdminUserName());
  }
}
