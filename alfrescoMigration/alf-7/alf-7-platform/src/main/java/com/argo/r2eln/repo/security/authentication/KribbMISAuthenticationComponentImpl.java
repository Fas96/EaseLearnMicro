package com.argo.r2eln.repo.security.authentication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AbstractAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.argo.r2eln.repo.dao.KribbCommonDAO;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.ContextHolder;

public class KribbMISAuthenticationComponentImpl extends AbstractAuthenticationComponent {

	private static final Log LOG = LogFactory.getLog(KribbMISAuthenticationComponentImpl.class);
	
	private MutableAuthenticationDao authenticationDao;

	private KribbCommonDAO kribbDao;
	
    public void setAuthenticationDao(MutableAuthenticationDao authenticationDao)
    {
       this.authenticationDao = authenticationDao;
    }
  
    public void setKribbCommonDao(KribbCommonDAO dao) {
    	this.kribbDao = dao;
    }
    
    public void authenticateImpl(String userName, char[] password) throws AuthenticationException {

    	Map<String, Object> userDetail = this.kribbDao.selectPerson(userName);
    	
    	if(userDetail==null) {
            String msg = userName + " does not exist is database";
            throw new AuthenticationException(msg);    		
    	}
    	
    	String passwdInDB = (String)userDetail.get("PASSWD");
    	String encPasswd = encPassWd(new String(password));
    	
    	if(passwdInDB==null || encPasswd==null) {
            String msg = "password cannot be null, but the entered password or password in db may be null";
            throw new AuthenticationException(msg);
    	}
    	
        if (passwdInDB.equals(encPasswd)) {
            setCurrentUser(userName);
            this.authenticationDao.updateUser(userName, password);
        } else {
            String msg = "Login request: username not recognized [userName=" + userName + "]";
            LOG.error(msg);
            throw new AuthenticationException(msg);
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
    
    /**
     * The default is not to support token base authentication
     */
    public Authentication authenticate(Authentication token) throws AuthenticationException {
        throw new AlfrescoRuntimeException("Authentication via token not supported");
    }
    
	@Override
	protected boolean implementationAllowsGuestLogin() {
		// TODO Auto-generated method stub
		return false;
	}

	private String encPassWd(String _inPassWord) {
		
		long   ll_GOP  = 378 ;
    	long   ll_x	= 2147483467;
    	long   ll_y   = 8;
    	long   ll_z   = 20;
    	long	make_num = 0, G1, G2;
    	int ll_leng, ll_ascnum;
    	int i, j;

    	String Gchar, v_passwd, inPassWord, Opasswd = "";

    	inPassWord = _inPassWord;
    	byte[] bt = inPassWord.getBytes();
    	ll_leng = inPassWord.length();

    	for ( i = 1; i <= ll_leng; i++)
    	{
        	make_num += (bt[i-1] * (ll_leng - (i - 1)));
    	}

    	G2=(make_num * ll_GOP)%ll_x+ll_y;
    	G1=make_num % ll_z;

    	v_passwd=G1+""+G2;
    	ll_leng=v_passwd.length();

    	for ( i = 1; i <= ll_leng; i++ )
    	{
        	j = ll_leng - (i - 1);
        	Gchar=v_passwd.substring(j-1,j);
        	bt = Gchar.getBytes();
        	ll_ascnum = (bt[0]+(j*3)) % 255;
        	Opasswd = Opasswd +  (char)ll_ascnum;
    	}
    	
    	return Opasswd;
	 }

	
}
