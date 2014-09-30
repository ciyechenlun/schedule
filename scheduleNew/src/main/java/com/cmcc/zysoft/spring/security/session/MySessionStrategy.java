// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.spring.security.session;

import java.util.List;

import javax.annotation.Resource;


import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

import com.cmcc.zysoft.schedule.service.ScheduleTempService;
import com.cmcc.zysoft.spring.security.model.User;

/**
 * 
 * @author AMCC
 *
 */
public class MySessionStrategy extends SessionRegistryImpl {
	@Resource
	private ScheduleTempService scheduleTempService;
	
	@Override
	public List<Object> getAllPrincipals() {
		// TODO Auto-generated method stub
		return super.getAllPrincipals();
	}

	@Override
	public List<SessionInformation> getAllSessions(Object principal,
			boolean includeExpiredSessions) {
		// TODO Auto-generated method stub
		return super.getAllSessions(principal, includeExpiredSessions);
	}

	@Override
	public SessionInformation getSessionInformation(String sessionId) {
		// TODO Auto-generated method stub
		return super.getSessionInformation(sessionId);
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		// TODO Auto-generated method stub
		super.onApplicationEvent(event);
	}

	@Override
	public void refreshLastRequest(String sessionId) {
		// TODO Auto-generated method stub
		super.refreshLastRequest(sessionId);
	}

	@Override
	public void registerNewSession(String sessionId, Object principal) {
		// TODO Auto-generated method stub
		super.registerNewSession(sessionId, principal);
	}

	@Override
	public void removeSessionInformation(String sessionId) {
		// TODO Auto-generated method stub
		SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }
		this.scheduleTempService.sessionDestroyClearTemp(((User)info.getPrincipal()).getUserId());
		super.removeSessionInformation(sessionId);
	}

	
}
