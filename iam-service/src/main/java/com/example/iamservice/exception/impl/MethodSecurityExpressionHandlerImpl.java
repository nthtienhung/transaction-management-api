package com.example.iamservice.exception.impl;

import com.example.iamservice.util.security.SecurityExpressionRootImpl;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public class MethodSecurityExpressionHandlerImpl extends DefaultMethodSecurityExpressionHandler {
    private final PermissionEvaluator permissionEvaluator;

    public MethodSecurityExpressionHandlerImpl(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
        super.setPermissionEvaluator(permissionEvaluator);
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        SecurityExpressionRootImpl root = new SecurityExpressionRootImpl(authentication,permissionEvaluator);
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
