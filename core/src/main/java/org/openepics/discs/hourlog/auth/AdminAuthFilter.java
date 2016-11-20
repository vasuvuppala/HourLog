/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *  
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *  
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.discs.hourlog.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for administrative pages
 * 
 * @author vuppala
 */
// @WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {
    
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;
    
    private static final Logger logger = Logger.getLogger(AdminAuthFilter.class.getName());
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
        
        if (authManager == null) {
            logger.log(Level.SEVERE, "authManager is null");
        }      
        
        if (authManager != null && authManager.canManageFacility()) {
            chain.doFilter(request, response);
        } else {
            logger.log(Level.FINE, "not authorized to admin");
            String contextPath = req.getContextPath();
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(contextPath + "/auth/unauthorized.xhtml");           
        } 
    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }
}
