/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.handler;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Permission;
import com.tatsinktechnologic.dao_controller.PermissionJpaController;
import com.tatsinktechnologic.dao_controller.RolePermissionRelJpaController;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.RolePermissionRel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.inject.Inject;

/**
 *
 * @author olivier.tatsinkou
 */
public class UnmappedResourceHandler extends ResourceHandlerWrapper {

    private ResourceHandler wrapped;
    private static int passage = 0;

//    @Inject
//    EntityManagerFactoryBean emf_bean;
    @Inject
    private PermissionJpaController permission_cont;

    @Inject
    private Commun_Controller commun_repo;

    @Inject
    RolePermissionRelJpaController rolePerm_controller;

    public UnmappedResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    public List<String> getXhtmls(FacesContext context) {
        List<String> result = getResources(context, "/", ".xhtml");
        return result;
    }

    private List<String> getResources(FacesContext context1, String path, String suffix) {
        ExternalContext context = context1.getCurrentInstance().getExternalContext();
        Set<String> resources = context.getResourcePaths(path);
        List<String> filteredResources = new ArrayList<String>();
        if (resources != null) {
            for (String resource : resources) {
                if (resource.endsWith(".xhtml")) {
                    filteredResources.add(resource);
                } else if (resource.endsWith("/")) {
                    filteredResources.addAll(getResources(context1, resource, suffix));
                }
            }
        }

        return filteredResources;
    }

    @Override
    public Resource createResource(final String resourceName, final String libraryName) {
        final Resource resource = super.createResource(resourceName, libraryName);

        if (resource == null) {
            return null;
        }

        return new ResourceWrapper() {

            @Override
            public String getRequestPath() {
                ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                String mapping = externalContext.getRequestServletPath();

                if (externalContext.getRequestPathInfo() == null) {
                    mapping = mapping.substring(mapping.lastIndexOf('.'));
                }

                String path = super.getRequestPath();

                if (mapping.charAt(0) == '/') {
                    return path.replaceFirst(mapping, "");
                } else if (path.contains("?")) {
                    return path.replace(mapping + "?", "?");
                } else {
                    return path.substring(0, path.length() - mapping.length());
                }
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getResourceName() {
                return resource.getResourceName();
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getLibraryName() {
                return resource.getLibraryName();
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getContentType() {
                return resource.getContentType();
            }

            @Override
            public Resource getWrapped() {
                return resource;
            }
        };
    }

    @Override
    public boolean isResourceRequest(FacesContext context) {

        if (passage == 0) {
            List<String> listResource = getXhtmls(context);
            Role rol = commun_repo.getOneByRole("root");
            for (String resource : listResource) {
                Permission perm = commun_repo.getOneByPermission(resource);

                if (perm == null) {
                    Permission newPerm = new Permission();
                    newPerm.setPermissionStr(resource);
                    try {
                        permission_cont.create(newPerm);

                        RolePermissionRel rolepermission = new RolePermissionRel();
                        rolepermission.setRole(rol);
                        rolepermission.setPermission(newPerm);
                        rolePerm_controller.create(rolepermission);
                    } catch (Exception e) {

                    }

                }
            }

        }
        passage++;

        return ResourceHandler.RESOURCE_IDENTIFIER.equals(context.getExternalContext().getRequestServletPath());
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        String resourceName = externalContext.getRequestPathInfo();
        String libraryName = externalContext.getRequestParameterMap().get("ln");
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, libraryName);

        if (resource == null) {
            super.handleResourceRequest(context);
            return;
        }

        if (!resource.userAgentNeedsUpdate(context)) {
            externalContext.setResponseStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        externalContext.setResponseContentType(resource.getContentType());

        for (Entry<String, String> header : resource.getResponseHeaders().entrySet()) {
            externalContext.setResponseHeader(header.getKey(), header.getValue());
        }

        ReadableByteChannel input = null;
        WritableByteChannel output = null;

        try {
            input = Channels.newChannel(resource.getInputStream());
            output = Channels.newChannel(externalContext.getResponseOutputStream());

            for (ByteBuffer buffer = ByteBuffer.allocateDirect(10240); input.read(buffer) != -1; buffer.clear()) {
                output.write((ByteBuffer) buffer.flip());
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignore) {
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

}
