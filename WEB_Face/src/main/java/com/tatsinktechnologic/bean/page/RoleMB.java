/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Permission;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.RolePermissionRel;
import com.tatsinktechnologic.dao_controller.PermissionJpaController;
import com.tatsinktechnologic.dao_controller.RoleJpaController;
import com.tatsinktechnologic.dao_controller.RolePermissionRelJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.springframework.ui.context.Theme;

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class RoleMB implements Serializable {

    private Role selectedRole;
    private Role selectedRole_cache;
    private List<Permission> listpermission;
    private DualListModel<Permission> dualListModelpermissions;
    private List<Role> filteredRoles;
    private List<Role> listRole;
    private String permission_string;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private RoleJpaController role_controller;

    @Inject
    private PermissionJpaController perm_controller;

    @Inject
    RolePermissionRelJpaController rolePerm_controller;

    public Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
    }

    public List<Permission> getListpermission() {
        return listpermission;
    }

    public void setListpermission(List<Permission> listpermission) {
        this.listpermission = listpermission;
    }

    public List<Role> getFilteredRoles() {
        return filteredRoles;
    }

    public void setFilteredRoles(List<Role> filteredRoles) {
        this.filteredRoles = filteredRoles;
    }

    public List<Role> getListRole() {
        return listRole;
    }

    public void setListRole(List<Role> listRole) {
        this.listRole = listRole;
    }

    public String getPermission_string() {
        return permission_string;
    }

    public void setPermission_string(String permission_string) {
        this.permission_string = permission_string;
    }

    public boolean isDo_create() {
        return do_create;
    }

    public void setDo_create(boolean do_create) {
        this.do_create = do_create;
    }

    public boolean isDo_view() {
        return do_view;
    }

    public void setDo_view(boolean do_view) {
        this.do_view = do_view;
    }

    public boolean isDo_edit() {
        return do_edit;
    }

    public void setDo_edit(boolean do_edit) {
        this.do_edit = do_edit;
    }

    public boolean isDo_reset() {
        return do_reset;
    }

    public void setDo_reset(boolean do_reset) {
        this.do_reset = do_reset;
    }

    public Role getSelectedRole_cache() {
        return selectedRole_cache;
    }

    public void setSelectedRole_cache(Role selectedRole_cache) {
        this.selectedRole_cache = selectedRole_cache;
    }

    public DualListModel<Permission> getDualListModelpermissions() {
        return dualListModelpermissions;
    }

    public void setDualListModelpermissions(DualListModel<Permission> dualListModelpermissions) {
        this.dualListModelpermissions = dualListModelpermissions;
    }

    @PostConstruct
    public void init() {
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        listRole = role_controller.findRoleEntities();
        listpermission = perm_controller.findPermissionEntities();

        dualListModelpermissions = new DualListModel<Permission>(listpermission, permissionsTarget);

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedRole != null) {
            permissionsTarget = commun_controller.getPermissionByRole(selectedRole.getRoleName());
            for (Permission perm : permissionsTarget) {
                dualListModelpermissions.getSource().remove(perm);
            }

            dualListModelpermissions.setTarget(permissionsTarget);
        } else {
            selectedRole = new Role();
        }
    }

    public String getPermissionRole(String role_name) {
        List<Permission> listPerm = commun_controller.getPermissionByRole(role_name);
        String result = "";
        for (Permission perm : listPerm) {
            result = result + perm.getPermissionStr() + "\n";
        }
        permission_string = result;
        return result;
    }

    public void view(Role rol) {
        List<Permission> permissionsSource = new ArrayList<Permission>();
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        selectedRole = rol;
        selectedRole_cache = SerializationUtils.clone(rol);
        permissionsTarget = commun_controller.getPermissionByRole(selectedRole.getRoleName());

        for (Permission perm : listpermission) {
            if (!permissionsTarget.contains(perm)) {
                permissionsSource.add(perm);
            }
        }

        dualListModelpermissions = new DualListModel<Permission>(permissionsSource, permissionsTarget);

        do_create = false;
        do_view = true;
        do_edit = false;
        do_reset = true;
    }

    public void enableEdit() {
        do_create = false;
        do_view = false;
        do_edit = true;
        do_reset = true;
        selectedRole = selectedRole_cache;

    }

    public void clear() {
        List<Permission> permissionsSource = new ArrayList<Permission>();
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        for (Permission perm : listpermission) {
            permissionsSource.add(perm);
        }

        dualListModelpermissions = new DualListModel<Permission>(permissionsSource, permissionsTarget);

        selectedRole = new Role();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedRole != null) {
            String role_name = selectedRole.getRoleName().trim();
            List<Permission> listpermission = commun_controller.getPermissionByRole(role_name);

            if (listpermission != null && !listpermission.isEmpty()) {
                try {
                    commun_controller.deleteRolePermissionRelByRole(role_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", role_name);
                    e.printStackTrace();
                }
            }

            if (!role_name.equals("root")) {
                try {

                    role_controller.edit(selectedRole);

                    List<Permission> permissionsTarget = dualListModelpermissions.getTarget();
                    int perm_size = permissionsTarget.size();

                    for (int i = 0; i < perm_size; i++) {
                        Permission permission = new Permission();
                        RolePermissionRel rolepermission = new RolePermissionRel();

                        permission.setPermissionStr(permissionsTarget.get(i).getPermissionStr());
                        permission.setPermission_id(permissionsTarget.get(i).getPermission_id());

                        rolepermission.setRole(selectedRole);
                        rolepermission.setPermission(permission);
                        rolePerm_controller.create(rolepermission);
                    }

//                    
//                    for (Permission permission : permissionsTarget) {
//                        RolePermissionRel rolepermission = new RolePermissionRel();
//                        rolepermission.setRole(selectedRole);
//                        rolepermission.setPermission(permission);
//                        rolePerm_controller.create(rolepermission);
//                    }
                    msg = new FacesMessage("Success Update", role_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", role_name);
                    e.printStackTrace();
                }
            } else {
                msg = new FacesMessage("cannot edit this Row", role_name);
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<Permission> permissionsSource = new ArrayList<Permission>();
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        for (Permission perm : listpermission) {
            permissionsSource.add(perm);
        }

        dualListModelpermissions = new DualListModel<Permission>(permissionsSource, permissionsTarget);

        selectedRole = new Role();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
    }

    public String delete() {
        FacesMessage msg = null;
        String role_name = selectedRole.getRoleName().trim();
        List<Permission> listpermission = commun_controller.getPermissionByRole(role_name);

        if (listpermission != null && !listpermission.isEmpty()) {
            try {
                commun_controller.deleteRolePermissionRelByRole(role_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", role_name);
                e.printStackTrace();
            }
        }

        if (!role_name.equals("root")) {
            try {
                role_controller.destroy(selectedRole.getRole_id());

                listRole = role_controller.findRoleEntities();
                msg = new FacesMessage("Success Delete", role_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Cancellation Row", role_name);
                e.printStackTrace();
            }
        } else {
            msg = new FacesMessage("cannot cancel this Row", role_name);
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<Permission> permissionsSource = new ArrayList<Permission>();
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        for (Permission perm : listpermission) {
            permissionsSource.add(perm);
        }

        dualListModelpermissions = new DualListModel<Permission>(permissionsSource, permissionsTarget);

        selectedRole = new Role();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedRole != null) {
            String role_name = selectedRole.getRoleName().trim();
            if (role_name == null || role_name.equals("")) {
                msg = new FacesMessage("You must provide Role Name", "");
            } else {
                Role rol = commun_controller.getOneByRole(role_name.trim());
                if (rol != null) {
                    msg = new FacesMessage("This Role already exist", "");
                } else {
                    try {
                        role_controller.create(selectedRole);

                        commun_controller.deleteRolePermissionRelByRole(selectedRole.getRoleName());
                        rol = commun_controller.getOneByRole(role_name.trim());

                        for (Permission permission : dualListModelpermissions.getTarget()) {
                            RolePermissionRel rolepermission = new RolePermissionRel();
                            rolepermission.setRole(rol);
                            rolepermission.setPermission(permission);
                            rolePerm_controller.create(rolepermission);
                        }
                        msg = new FacesMessage("New Role added", role_name);
                        listRole = role_controller.findRoleEntities();
                        selectedRole = new Role();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", role_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<Permission> permissionsSource = new ArrayList<Permission>();
        List<Permission> permissionsTarget = new ArrayList<Permission>();

        for (Permission perm : listpermission) {
            permissionsSource.add(perm);
        }

        dualListModelpermissions = new DualListModel<Permission>(permissionsSource, permissionsTarget);

        selectedRole = new Role();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((Theme) item).getName()).append("<br />");
        }

        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Items Transferred");
        msg.setDetail(builder.toString());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", ((Permission) event.getObject()).getPermissionStr()));
    }

    public void onUnselect(UnselectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Unselected", ((Permission) event.getObject()).getPermissionStr()));
    }

    public void onReorder() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
    }

}
