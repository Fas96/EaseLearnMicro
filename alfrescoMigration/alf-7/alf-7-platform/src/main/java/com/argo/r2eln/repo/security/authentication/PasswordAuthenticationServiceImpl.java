package com.argo.r2eln.repo.security.authentication;

import java.util.Collections;
import java.util.Set;
import org.alfresco.repo.management.subsystems.ActivateableBean;
import org.alfresco.repo.security.authentication.AbstractAuthenticationService;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationComponent.UserNameValidationMode;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.util.Pair;

public class PasswordAuthenticationServiceImpl
  extends AbstractAuthenticationService
  implements ActivateableBean
{
  AuthenticationComponent authenticationComponent;
  TicketComponent ticketComponent;
  private String domain;
  private boolean allowsUserCreation = true;
  private boolean allowsUserDeletion = true;
  private boolean allowsUserPasswordChange = true;
  
  public void setTicketComponent(TicketComponent ticketComponent)
  {
    this.ticketComponent = ticketComponent;
  }
  
  public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
  {
    this.authenticationComponent = authenticationComponent;
  }
  
  public boolean isActive()
  {
    return (!(this.authenticationComponent instanceof ActivateableBean)) || (((ActivateableBean)this.authenticationComponent).isActive());
  }
  
  public void authenticate(String userName, char[] password)
    throws AuthenticationException
  {
    try
    {
      String tenant = getPrevalidationTenantDomain();
      
      clearCurrentSecurityContext();
      preAuthenticationCheck(userName);
      this.authenticationComponent.authenticate(userName, password);
      if (tenant == null)
      {
        Pair<String, String> userTenant = AuthenticationUtil.getUserTenant(userName);
        tenant = (String)userTenant.getSecond();
      }
      TenantContextHolder.setTenantDomain(tenant);
    }
    catch (AuthenticationException ae)
    {
      clearCurrentSecurityContext();
      throw ae;
    }
    this.ticketComponent.clearCurrentTicket();
    getCurrentTicket();
  }
  
  public String getCurrentUserName()
    throws AuthenticationException
  {
    return this.authenticationComponent.getCurrentUserName();
  }
  
  public void invalidateUserSession(String userName)
    throws AuthenticationException
  {
    this.ticketComponent.invalidateTicketByUser(userName);
  }
  
  public Set<String> getUsersWithTickets(boolean nonExpiredOnly)
  {
    return this.ticketComponent.getUsersWithTickets(nonExpiredOnly);
  }
  
  public void invalidateTicket(String ticket)
    throws AuthenticationException
  {
    this.ticketComponent.invalidateTicketById(ticket);
  }
  
  public int countTickets(boolean nonExpiredOnly)
  {
    return this.ticketComponent.countTickets(nonExpiredOnly);
  }
  
  public int invalidateTickets(boolean expiredOnly)
  {
    return this.ticketComponent.invalidateTickets(expiredOnly);
  }
  
  public void validate(String ticket)
    throws AuthenticationException
  {
    String currentUser = null;
    try
    {
      String tenant = getPrevalidationTenantDomain();
      
      clearCurrentSecurityContext();
      currentUser = this.ticketComponent.validateTicket(ticket);
      this.authenticationComponent.setCurrentUser(currentUser, AuthenticationComponent.UserNameValidationMode.NONE);
      if (tenant == null)
      {
        Pair<String, String> userTenant = AuthenticationUtil.getUserTenant(currentUser);
        tenant = (String)userTenant.getSecond();
      }
      TenantContextHolder.setTenantDomain(tenant);
    }
    catch (AuthenticationException ae)
    {
      clearCurrentSecurityContext();
      throw ae;
    }
  }
  
  protected String getPrevalidationTenantDomain()
  {
    return null;
  }
  
  public String getCurrentTicket()
    throws AuthenticationException
  {
    String userName = getCurrentUserName();
    
    String ticket = this.ticketComponent.getCurrentTicket(userName, false);
    if (ticket == null) {
      return getNewTicket();
    }
    return ticket;
  }
  
  public String getNewTicket()
  {
    String userName = getCurrentUserName();
    try
    {
      preAuthenticationCheck(userName);
    }
    catch (AuthenticationException e)
    {
      clearCurrentSecurityContext();
      throw e;
    }
    return this.ticketComponent.getNewTicket(userName);
  }
  
  public void clearCurrentSecurityContext()
  {
    this.authenticationComponent.clearCurrentSecurityContext();
    this.ticketComponent.clearCurrentTicket();
  }
  
  public boolean isCurrentUserTheSystemUser()
  {
    return this.authenticationComponent.isSystemUserName(getCurrentUserName());
  }
  
  public void authenticateAsGuest()
    throws AuthenticationException
  {
    String defaultGuestName = AuthenticationUtil.getGuestUserName();
    if ((defaultGuestName == null) || (defaultGuestName.length() == 0)) {
      throw new AuthenticationException("Guest authentication not supported");
    }
    preAuthenticationCheck(defaultGuestName);
    this.authenticationComponent.setGuestUserAsCurrentUser();
    String guestUser = this.authenticationComponent.getCurrentUserName();
    this.ticketComponent.clearCurrentTicket();
    this.ticketComponent.getCurrentTicket(guestUser, true);
  }
  
  public boolean guestUserAuthenticationAllowed()
  {
    return this.authenticationComponent.guestUserAuthenticationAllowed();
  }
  
  public boolean getAllowsUserCreation()
  {
    return this.allowsUserCreation;
  }
  
  public void setAllowsUserCreation(boolean allowsUserCreation)
  {
    this.allowsUserCreation = allowsUserCreation;
  }
  
  public boolean getAllowsUserDeletion()
  {
    return this.allowsUserDeletion;
  }
  
  public void setAllowsUserDeletion(boolean allowsUserDeletion)
  {
    this.allowsUserDeletion = allowsUserDeletion;
  }
  
  public boolean getAllowsUserPasswordChange()
  {
    return this.allowsUserPasswordChange;
  }
  
  public void setAllowsUserPasswordChange(boolean allowsUserPasswordChange)
  {
    this.allowsUserPasswordChange = allowsUserPasswordChange;
  }
  
  public String getDomain()
  {
    return this.domain;
  }
  
  public void setDomain(String domain)
  {
    this.domain = domain;
  }
  
  public Set<String> getDomains()
  {
    return Collections.singleton(getDomain());
  }
  
  public Set<String> getDomainsThatAllowUserCreation()
  {
    if (getAllowsUserCreation()) {
      return Collections.singleton(getDomain());
    }
    return Collections.emptySet();
  }
  
  public Set<String> getDomainsThatAllowUserDeletion()
  {
    if (getAllowsUserDeletion()) {
      return Collections.singleton(getDomain());
    }
    return Collections.emptySet();
  }
  
  public Set<String> getDomiansThatAllowUserPasswordChanges()
  {
    if (getAllowsUserPasswordChange()) {
      return Collections.singleton(getDomain());
    }
    return Collections.emptySet();
  }
  
  public Set<TicketComponent> getTicketComponents()
  {
    return Collections.singleton(this.ticketComponent);
  }
  
  public Set<String> getDefaultAdministratorUserNames()
  {
    return this.authenticationComponent.getDefaultAdministratorUserNames();
  }
  
  public Set<String> getDefaultGuestUserNames()
  {
    return this.authenticationComponent.getDefaultGuestUserNames();
  }
  
  public boolean authenticationExists(String userName)
  {
    return true;
  }
  
  public boolean getAuthenticationEnabled(String userName)
    throws AuthenticationException
  {
    return true;
  }
}
